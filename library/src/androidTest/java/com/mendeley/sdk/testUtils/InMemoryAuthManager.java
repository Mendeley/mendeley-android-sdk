package com.mendeley.sdk.testUtils;

import com.mendeley.sdk.AuthManager;

import java.util.Calendar;
import java.util.Date;

/**
 * Internal version of MendeleySdk.
 * <p>
 * This is used to run integration tests on the SDK, in which sign is handled via the resource owner
 * password flow.
 * <p>
 * Developer applications should not use this class.
 */
public class InMemoryAuthManager implements AuthManager {

    private String accessToken; // null if not set
    private String refreshToken;
    private Date expiresAt;
    private String tokenType;

    @Override
    public void saveTokens(String accessToken, String refreshToken, String tokenType, int expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = generateExpiresAtFromExpiresIn(expiresIn);
        this.tokenType = tokenType;
    }

    @Override
    public void clearTokens() {
        accessToken = null;
    }


    @Override
    public Date getAuthTokenExpirationDate() {
        return expiresAt;
    }

    @Override
    public String getTokenType() {
        return tokenType;
    }

    @Override
    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }


    private Date generateExpiresAtFromExpiresIn(int expiresIn) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, expiresIn);
        return c.getTime();
    }
}
