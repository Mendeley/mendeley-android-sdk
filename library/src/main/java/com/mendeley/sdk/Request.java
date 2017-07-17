package com.mendeley.sdk;

import android.net.Uri;
import android.os.AsyncTask;

import com.mendeley.sdk.exceptions.MendeleyException;
import com.mendeley.sdk.exceptions.UserCancelledException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executor;

/**
 * Class modeling an HTTP request against the Mendeley API.
 *
 * @param <ResultType>
 */
public abstract class Request<ResultType> {

    // RFC 7231 format, used for Dates in HTTP headers.
    private final static SimpleDateFormat httpHeaderDateFormat;

    public static final String MENDELEY_API_BASE_URL = BuildConfig.WEB_API_BASE_URL;
    protected static final int CONNECTION_TIMEOUT = 1500;
    protected static final int READ_TIMEOUT = 15000 ;

    // Number of times to retry failed HTTP requests due to IOExceptions.
    protected static final int MAX_HTTP_RETRIES = 0;

    static {
        httpHeaderDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss 'GMT'", Locale.US);
        httpHeaderDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private static Date parseHeaderDate(String serverDateStr) {
        try {
            synchronized (httpHeaderDateFormat) {
                return httpHeaderDateFormat.parse(serverDateStr);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not parse server date header", e);
        }
    }

    private final Uri uri;
    private boolean cancelled;
    private boolean finishedRun;

    /**
     * Constructor
     *
     * @param uri URI the request will be executed against
     */
    public Request(Uri uri) {
        this.uri = uri;
    }

    /**
     * @return the URL of the request
     */
    public final Uri getUrl() {
        return uri;
    }

    /**
     * Launches the request.
     * <p/>
     * Calling this method will block the calling thread.
     * As this method performs network work, client code should never execute this method in the
     * UI thread. For this, there are several alternatives:
     *
     * <ul>
     * <li>Using the {@link Request#runAsync(RequestCallback)} method</li>
     * <li>Executing this method in an {@link AsyncTask}, {@link android.app.IntentService} or using any other threading mechanism </li>
     * </ul>
     *
     * @return the response of the request
     * @throws UserCancelledException if the request was cancelled by the user
     * @throws MendeleyException if the request could not be completed for any unexpected reason
     */
    public final Response run() throws MendeleyException {
        try {
            return doRun();
        } finally {
            finishedRun = true;
        }
    }

    /**
     * Template method that effectively performs the work of the request.
     * Implement
     *
     *
     * @return the response
     * @throws MendeleyException semantic exception representing the error.
     *         The actual error may be found wrapped in {@link MendeleyException#getCause()}
     */
    protected abstract Response doRun() throws MendeleyException;

    /**
     * Runs the request asynchronously.
     * This method will not block the caller thread, and the network operation will be performed
     * in the same thread Asynctasks use.
     *
     * {@link AsyncTask#SERIAL_EXECUTOR}
     *
     * @param callback used the get the result asynchronously. The SDK will call methods over this
     *                 callback upon its completion.
     */
    public final void runAsync(final RequestCallback<ResultType> callback) {
        runAsync(callback, AsyncTask.SERIAL_EXECUTOR);
    }

    /**
     * Runs the request asynchronously.
     * This method will not block the caller thread, and the network operation will be performed
     * in the passed executor.
     *
     * @param callback used the get the result asynchronously. The SDK will call methods over this
     *                 callback upon its completion.
     * @param executor where the network operation will run at
     */
    public final void runAsync(final RequestCallback<ResultType> callback, Executor executor) {
        final RequestAsyncTask task = new RequestAsyncTask(callback);
        task.executeOnExecutor(executor);
    }

    /**
     * Tells this request to cancel its work and to return as soon as possible.
     */
    public final void cancel() {
        cancelled = true;
    }

    /**
     * @return whether or not this request has been cancelled
     */
    public final boolean isCancelled() {
        return cancelled;
    }

    /**
     * @return true if the request has finished with success, finished with error or has been cancelled.
     *          False if it has not been run or is still running.
     */
    public boolean isDone() {
        return finishedRun || cancelled;
    }

    /**
     * Response of the @{link Request}
     */
    public class Response {

        /**
         * Actual resource returned by the Mendeley API
         */
        public final ResultType resource;

        /**
         * Link to the next page of the request.
         * May be null.
         * Will be present only in paginated GET request with a long response.
         */
        public final Uri next;

        /**
         * Date of the request in the Mendeley server clock.
         */
        public final Date serverDate;

        public Response(ResultType resource, Date serverDate, Uri next) {
            this.resource = resource;
            this.next = next;
            this.serverDate = serverDate;
        }

        public Response(ResultType resource, String serverDateStr, Uri next) {
            this(resource, parseHeaderDate(serverDateStr), next);
        }

        public Response(ResultType resource, String serverDateStr) {
            this(resource, serverDateStr, null);
        }

    }


    /**
     * Callback used when the {@link Request} is executed asynchronously.
     * If the request is run with {@link Request#runAsync(RequestCallback)}, the SDK will invoke
     * one method in this object upon completion.
     *
     * @param <ResultType>
     */
    public interface RequestCallback<ResultType> {

        /**
         * Invoked when the request finishes successfully.
         *
         * @param resource actual resource returned by the Mendeley API
         * @param next Link to the next page of the request.
         *             May be null.
         *             Will be present only in paginated GET request with a long response.
         * @param serverDate date of the request in the Mendeley server clock.
         */
        void onSuccess(ResultType resource, Uri next, Date serverDate);

        /**
         * Invoked when the request finishes with an error.
         * @param mendeleyException semantic exception representing the error. The actual error
         *                          may be found wrapped in {@link MendeleyException#getCause()}
         */
        void onFailure(MendeleyException mendeleyException);

        /**
         * Invoked when the request is cancelled.
         */
        void onCancelled();
    }

    /**
     * {@link AsyncTask} to execute the {@link Request} asynchronously
     */
    private final class RequestAsyncTask extends AsyncTask<Void, Void, RequestResponseMaybe> {

        private final RequestCallback<ResultType> callback;

        public RequestAsyncTask(RequestCallback<ResultType> callback) {
            this.callback = callback;
        }

        @Override
        protected RequestResponseMaybe doInBackground(Void... params) {
            try {
                return new RequestResponseMaybe(Request.this.run());
            } catch (MendeleyException e) {
                return new RequestResponseMaybe(e);
            }
        }

        @Override
        protected final void onPostExecute(RequestResponseMaybe maybe) {
            super.onPostExecute(maybe);

            if (Request.this.isCancelled()) {
                callback.onCancelled();
            } else if (maybe.error != null) {
                callback.onFailure(maybe.error);
            } else {
                callback.onSuccess(maybe.response.resource, maybe.response.next, maybe.response.serverDate);
            }
        }
    }


    /**
     * Class for holding the result or the exception of the request.
     */
    private class RequestResponseMaybe {

        public final Response response;
        public final MendeleyException error;

        public RequestResponseMaybe(Response response) {
            this(response, null);
        }

        public RequestResponseMaybe(MendeleyException error) {
            this(null, error);
        }

        private RequestResponseMaybe(Response response, MendeleyException error) {
            this.response = response;
            this.error = error;
        }
    }

}
