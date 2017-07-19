package com.mendeley.sdk.request;

import android.net.Uri;

import com.mendeley.sdk.ClientCredentials;
import com.mendeley.sdk.AuthManager;

import org.json.JSONException;

import java.io.InputStream;

/**
 * Request against the Mendeley API using the GET method.
 */
public abstract class GetAuthorizedRequest<ResultType> extends OkHttpAuthorizedRequest<ResultType> {

    public GetAuthorizedRequest(Uri url, AuthManager authManager, ClientCredentials clientCredentials) {
        super(url, authManager, clientCredentials);
    }

    @Override
    protected final void setMethod(okhttp3.Request.Builder requestBld) throws JSONException, Exception {
        requestBld.get();
    }

    @Override
    protected abstract ResultType manageResponse(InputStream is) throws Exception;

}

