package com.mendeley.sdk.request;


import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.mendeley.sdk.exceptions.MendeleyException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public abstract class AuthorizedRequestTest extends SignedInTest {

    private AuthorizedRequest<JSONObject> request;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        request = createRequest();
    }

    protected abstract AuthorizedRequest<JSONObject> createRequest();

    protected final AuthorizedRequest<JSONObject> getRequest() {
        return request;
    }

    @LargeTest
    public void test_sendsTheAuthTokenAsAHeader() throws MendeleyException, JSONException {

        final JSONObject httpBinResponse = getRequest().run().resource;
        final JSONObject headersJson = httpBinResponse.getJSONObject("headers");

        final String actual = headersJson.getString("Authorization");

        assertEquals("Authorization token sent", actual, "Bearer " + getAuthManager().getAccessToken());
    }

    @SmallTest
    public void test_run_returnsTheServerDate() throws Exception {
        final Date serverDate = getRequest().run().serverDate;
        assertTrue("Request reads server date", Math.abs(new Date().getTime() - serverDate.getTime()) < 1000 * 60 * 60 * 2);
    }

}
