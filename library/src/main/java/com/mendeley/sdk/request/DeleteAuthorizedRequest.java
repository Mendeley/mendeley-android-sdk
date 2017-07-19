package com.mendeley.sdk.request;

import android.net.Uri;

import com.mendeley.sdk.AuthManager;
import com.mendeley.sdk.ClientCredentials;

import java.io.InputStream;

import okhttp3.Request;

/**
 * A NetworkProcedure specialised for making HTTP DELETE requests.
 */
public class DeleteAuthorizedRequest<ResultType> extends OkHttpAuthorizedRequest<ResultType> {

    public DeleteAuthorizedRequest(Uri url, AuthManager authManager, ClientCredentials clientCredentials) {
        super(url, authManager, clientCredentials);
    }

    @Override
    protected final void setMethod(Request.Builder requestBld) throws Exception {
        requestBld.delete();
    }

    @Override
    protected ResultType manageResponse(InputStream is) throws Exception {
        return null;
    }
}
