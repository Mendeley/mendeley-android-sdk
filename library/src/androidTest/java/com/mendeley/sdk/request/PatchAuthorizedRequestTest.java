package com.mendeley.sdk.request;

import android.net.Uri;
import android.test.suitebuilder.annotation.SmallTest;

import com.mendeley.sdk.util.DateUtils;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.mendeley.sdk.util.IOUtils.readInputStream;

public class PatchAuthorizedRequestTest extends AuthorizedRequestTest {

    private String patchedBody;
    private final Date currentDate = new Date(System.currentTimeMillis());

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        patchedBody = "the patched body";
    }

    @Override
    protected AuthorizedRequest<JSONObject> createRequest() {
        return new PatchAuthorizedRequest<JSONObject>(Uri.parse("https://httpbin.org/patch"), currentDate, getAuthManager(), getClientCredentials()) {

            @Override
            protected RequestBody getBody() {
                return RequestBody.create(MediaType.parse("text/plain"), patchedBody);
            }

            @Override
            protected JSONObject manageResponse(InputStream is) throws Exception {
                String responseString = readInputStream(is);
                return new JSONObject(responseString);
            }
        };
    }


    @SmallTest
    public void test_run_writesThePatchPayload() throws Exception {

        final JSONObject httpBinResponse = getRequest().run().resource;
        final String actual = httpBinResponse.getString("data");

        assertTrue("Patched and returned content", actual.contains(patchedBody));
    }

    @SmallTest
    public void test_run_sendsTheUnmodifiedIfHeader() throws Exception {
        final JSONObject httpBinResponse = getRequest().run().resource;
        final JSONObject headersJson = httpBinResponse.getJSONObject("headers");

        final String actual = headersJson.getString("If-Unmodified-Since");
        assertEquals("If-Unmodified-Since header sent", actual, DateUtils.formatMendeleyApiTimestamp(currentDate));

    }
}
