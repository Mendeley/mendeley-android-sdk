package com.mendeley.sdk.request;

import android.net.Uri;
import android.test.suitebuilder.annotation.LargeTest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.mendeley.sdk.util.IOUtils.readInputStream;

public class PostAuthorizedRequestTest extends AuthorizedRequestTest {

    private String postedBody;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        postedBody = "the posted body";
    }

    @Override
    protected AuthorizedRequest<JSONObject> createRequest() {
        return new PostAuthorizedRequest<JSONObject>(Uri.parse("https://httpbin.org/post"), getAuthManager(), getClientCredentials()) {

            @Override
            protected RequestBody getBody() throws JSONException {
                return RequestBody.create(MediaType.parse("text/plain"), postedBody);
            }

            @Override
            protected JSONObject manageResponse(InputStream is) throws Exception {
                String responseString = readInputStream(is);
                return new JSONObject(responseString);
            }

        };
    }


    @LargeTest
    public void test_run_writesThePostPayload() throws Exception {

        final JSONObject httpBinResponse = getRequest().run().resource;
        final String actual = httpBinResponse.getString("data");

        assertTrue("Posted and returned content", actual.contains(postedBody));
    }

}
