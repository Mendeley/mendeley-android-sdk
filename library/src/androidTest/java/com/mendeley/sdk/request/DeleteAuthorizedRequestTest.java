package com.mendeley.sdk.request;

import android.net.Uri;

import org.json.JSONObject;

import java.io.InputStream;

import static com.mendeley.sdk.util.IOUtils.readInputStream;

public class DeleteAuthorizedRequestTest extends AuthorizedRequestTest {

    @Override
    protected AuthorizedRequest<JSONObject> createRequest() {
        return new DeleteAuthorizedRequest<JSONObject>(Uri.parse("https://httpbin.org/delete"), getAuthManager(), getClientCredentials()) {
            @Override
            protected JSONObject manageResponse(InputStream is) throws Exception {
                String responseString = readInputStream(is);
                return new JSONObject(responseString);
            }
        };
    }


}
