package com.mendeley.sdk.request.endpoint;


import android.test.suitebuilder.annotation.SmallTest;

import com.mendeley.sdk.AuthManager;
import com.mendeley.sdk.model.Profile;
import com.mendeley.sdk.request.SignedInTest;
import com.mendeley.sdk.testUtils.AssertUtils;

public class RefreshTokenRequestTest extends SignedInTest {

    @SmallTest
    public void test_run_obtainsAnUpdatedTheAccessToken() throws Exception {

        final Profile expected = getRequestFactory().newGetMyProfileRequest().run().resource;

        // GIVEN an invalid access token
        final String invalidAccessToken = "invalid";
        final AuthManager authManager = getAuthManager();

        authManager.saveTokens(invalidAccessToken, authManager.getRefreshToken(), authManager.getTokenType(), 1000);

        // and the request
        final OAuthTokenEndpoint.RefreshTokenRequest refreshRequest = new OAuthTokenEndpoint.RefreshTokenRequest(authManager, getClientCredentials());

        // WHEN running the request
        refreshRequest.run();

        // THEN we have new access token
        assertNotSame("Access token updated", invalidAccessToken, authManager.getAccessToken());

        // ...that indeed allows as to perform valid request
        final Profile actual = getRequestFactory().newGetMyProfileRequest().run().resource;
        AssertUtils.assertProfile(expected, actual);
    }

}
