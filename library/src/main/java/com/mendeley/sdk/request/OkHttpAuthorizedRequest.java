package com.mendeley.sdk.request;

import android.net.Uri;

import com.mendeley.sdk.AuthTokenManager;
import com.mendeley.sdk.ClientCredentials;
import com.mendeley.sdk.Request;
import com.mendeley.sdk.exceptions.HttpResponseException;
import com.mendeley.sdk.exceptions.MendeleyException;
import com.mendeley.sdk.exceptions.UserCancelledException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

/**
 * Base implementation of {@link Request} using {@link OkHttpClient} as the HTTP client.
 */
public abstract class OkHttpAuthorizedRequest<ResultType> extends AuthorizedRequest<ResultType> {

    private static OkHttpClient sOkHttpClient;

    static  {
        sOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .followRedirects(true)
                .build();

    }

    private RequestProgressListener progressListener;

    public OkHttpAuthorizedRequest(Uri url, AuthTokenManager authTokenManager, ClientCredentials clientCredentials) {
        super(url, authTokenManager, clientCredentials);
    }

    @Override
    public final Response doRunAuthorized() throws MendeleyException {
        return doRun(getUrl(), 0, true);
    }

    private Response doRun(Uri url, int currentRetry, boolean addOauthToken) throws MendeleyException {
        ResponseBody responseBody = null;

        try {
            okhttp3.Request.Builder requestBld = new okhttp3.Request.Builder();
            requestBld.url(url.toString());
            setMethod(requestBld);

            if (addOauthToken) {
                requestBld.addHeader("Authorization", "Bearer " + authTokenManager.getAccessToken());
            }



            final Map<String, String> requestHeaders = new HashMap<String, String>();
            appendHeaders(requestHeaders);
            for (String key : requestHeaders.keySet()) {
                requestBld.addHeader(key, requestHeaders.get(key));
            }

            final okhttp3.Request okHttpRequest =  requestBld.build();
            final okhttp3.Response okHttpResponse = sOkHttpClient.newCall(okHttpRequest).execute();

            final int responseCode = okHttpResponse.code();

            responseBody = okHttpResponse.body();
            if (responseCode / 100 != 2) {
                throw new HttpResponseException(responseCode, okHttpResponse.message(), url.toString(), responseBody.string(), okHttpResponse.header("X-Mendeley-Trace-Id"));
            }

            // wrapping the input stream of the connection in:
            // -- CancellableInputStream to stop reading if the request has been cancelled
            // -- ProgressPublisherInputStream to publish progress as the file is being read
            final InputStream is = new MyCancellableInputStream(new MyProgressPublisherInputStream(responseBody.byteStream(), responseBody.contentLength()));
            final Map<String, List<String>> responseHeaders = okHttpResponse.headers().toMultimap();
            return new Response(manageResponse(is), getServerDateString(responseHeaders), getNextPage(responseHeaders));
        } catch (CancellationException ce) {
            throw new UserCancelledException(ce);
        } catch (MendeleyException me) {
            throw me;
        } catch (ParseException pe) {
            throw new MendeleyException("Could not parse a date in the JSON response " + url, pe);
        } catch (IOException ioe) {
            // If the issue is due to IOException, retry up to MAX_HTTP_RETRIES times
            if (currentRetry <  MAX_HTTP_RETRIES) {
                return doRun(url, currentRetry + 1, addOauthToken);
            } else {
                throw new MendeleyException("IO error performing the request " + url, ioe);
            }
        } catch (Exception e) {
            throw new MendeleyException("Error performing the request " + url, e);
        } finally {
            if (responseBody != null) {
                if (responseBody.byteStream() != null) {
                    try {
                        responseBody.byteStream().close();
                    } catch (IOException ignored) {
                    }
                }
                responseBody.close();
            }
        }
    }

    protected abstract void setMethod(okhttp3.Request.Builder requestBld) throws Exception;

    /**
     * Sets a listener to be notified of progress
     * @param progressListener
     */
    public final void setProgressListener(RequestProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    protected void appendHeaders(Map<String, String> headers) {
    }

    protected abstract ResultType manageResponse(InputStream is) throws Exception;


    private String getServerDateString(Map<String, List<String>> headersMap) throws IOException {
        final List<String> dateHeaders = headersMap.get("Date");
        if (dateHeaders != null) {
            return headersMap.get("Date").get(0);
        }
        return null;
    }

    private Uri getNextPage(Map<String, List<String>> responseHeaders) {
        final List<String> links = responseHeaders.get("Link");
        if (links != null) {
            for (String link : links) {
                try {
                    String linkString = link.substring(link.indexOf("<") + 1, link.indexOf(">"));
                    if (link.contains("next")) {
                        return Uri.parse(linkString);
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }
            }
        }
        return null;
    }

    /**
     * Implementation of {@link CancellableInputStream} that stops reading when the request has been cancelled
     */
    private class MyCancellableInputStream extends CancellableInputStream {
        public MyCancellableInputStream(InputStream delegate) {
            super(delegate);
        }

        @Override
        protected boolean isCancelled() {
            return OkHttpAuthorizedRequest.this.isCancelled();
        }
    }

    /**
     * Implementation of {@link ProgressPublisherInputStream} that pipes the progress to the
     * {@link RequestProgressListener}
     */
    private class MyProgressPublisherInputStream extends ProgressPublisherInputStream {
        public MyProgressPublisherInputStream(InputStream inputStream, long contentLength) {
            super(inputStream, contentLength);
        }

        @Override
        protected void onProgress(long progress) {
            if (progressListener != null) {
                progressListener.onProgress(progress);
            }
        }
    }


    /**
     * To be implemented by classes that want to listen the progress of the download
     */
    public interface  RequestProgressListener {
        /**
         * @param progress in [0-100]
         */
        void onProgress(long progress);
    }

}
