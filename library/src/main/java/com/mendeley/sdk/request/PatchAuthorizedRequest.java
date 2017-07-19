package com.mendeley.sdk.request;

import android.net.Uri;

import com.mendeley.sdk.ClientCredentials;
import com.mendeley.sdk.AuthManager;
import com.mendeley.sdk.Request;
import com.mendeley.sdk.util.DateUtils;

import org.json.JSONException;

import java.io.OutputStream;
import java.util.Date;
import java.util.Map;

import okhttp3.RequestBody;

/**
 * Request against the Mendeley API using the PATCH method.
 */
public abstract class PatchAuthorizedRequest<ResultType> extends OkHttpAuthorizedRequest<ResultType> {

    private final Date ifModifiedSinceDate;

    public PatchAuthorizedRequest(Uri url, Date ifModifiedSinceDate, AuthManager authManager, ClientCredentials clientCredentials) {
        super(url, authManager, clientCredentials);
        this.ifModifiedSinceDate = ifModifiedSinceDate;
    }


    @Override
    protected final void setMethod(okhttp3.Request.Builder requestBld) throws Exception {
        requestBld.patch(getBody());
    }

    protected abstract RequestBody getBody() throws JSONException;

    @Override
    protected void appendHeaders(Map<String, String> headers) {
        super.appendHeaders(headers);
        if (ifModifiedSinceDate != null) {
            headers.put("If-Unmodified-Since", DateUtils.formatMendeleyApiTimestamp(ifModifiedSinceDate));
        }
    }


    /**
     * Implementation of {@link CancellableOutputStream} that uses the {@link Request} to determine
     * whether it has been cancelled.
     */
    private class MyCancelableOutputStream extends CancellableOutputStream {
        public MyCancelableOutputStream(OutputStream delegate) {
            super(delegate);
        }

        @Override
        protected boolean isCancelled() {
            return PatchAuthorizedRequest.this.isCancelled();
        }
    }

}
