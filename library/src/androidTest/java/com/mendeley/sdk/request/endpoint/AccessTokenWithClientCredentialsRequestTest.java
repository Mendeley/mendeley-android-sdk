package com.mendeley.sdk.request.endpoint;

import android.content.res.AssetManager;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.mendeley.sdk.ClientCredentials;
import com.mendeley.sdk.testUtils.ClientCredentialsFromAssetsFactory;
import com.mendeley.sdk.testUtils.InMemoryAuthManager;

import junit.framework.Assert;

import java.util.Map;


public class AccessTokenWithClientCredentialsRequestTest extends AndroidTestCase {

    private ClientCredentials clientCredentials;
    private InMemoryAuthManager authTokenManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        final AssetManager assetManager =  getContext().getAssets();
        clientCredentials = ClientCredentialsFromAssetsFactory.create(assetManager);

        authTokenManager = new InMemoryAuthManager();
    }

    @SmallTest
    public void test_run_obtainsAnAccessToken() throws Exception {

        // GIVEN the request
        final OAuthTokenEndpoint.AccessTokenWithClientCredentialsRequest request = new OAuthTokenEndpoint.AccessTokenWithClientCredentialsRequest(authTokenManager, clientCredentials);

        // WHEN running it
        request.run();

        // THEN we have an access token that lets us perform non-user-related requests
        final Map<String, String> resource = new DocumentTypesEndpoint.GetDocumentTypesRequest(authTokenManager, clientCredentials).run().resource;
        Assert.assertNotNull("Empty response", resource);
    }

}