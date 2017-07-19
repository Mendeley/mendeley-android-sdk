package com.mendeley.sdk.request;

import android.net.Uri;
import android.text.TextUtils;

import com.mendeley.sdk.AuthManager;
import com.mendeley.sdk.ClientCredentials;
import com.mendeley.sdk.Request;
import com.mendeley.sdk.exceptions.HttpResponseException;
import com.mendeley.sdk.exceptions.MendeleyException;
import com.mendeley.sdk.request.endpoint.OAuthTokenEndpoint;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * {@link Request} against the Mendeley Web API that is performed using a valid OAuth access token.
 *
 * This class is responsible to refresh the access token if it has expired.
 *
 * @param <ResultType>
 */
public abstract class AuthorizedRequest<ResultType> extends Request<ResultType> {

    // Only use tokens which don't expire in the next 5 mins:
    private final static int MIN_TOKEN_VALIDITY_SEC = 300;

    protected final AuthManager authManager;
    protected final ClientCredentials clientCredentials;

    /**
     * Constructor
     *
     * @param url URI the request will be executed against
     * @param authManager used to get the access token
     * @param clientCredentials used to refresh the access token, if needed
     */
    public AuthorizedRequest(Uri url, AuthManager authManager, ClientCredentials clientCredentials) {
        super(url);
        this.authManager = authManager;
        this.clientCredentials = clientCredentials;
    }

    @Override
    public final Response doRun() throws MendeleyException {
        if (TextUtils.isEmpty(authManager.getAccessToken())) {
            // Must call startSignInProcess first - caller error!
            throw new MendeleyException("No access token found");
        }

        if (willExpireSoon()) {
            refreshExpiredToken();
        }
        try {
            return doRunAuthorized();
        } catch (HttpResponseException e) {
            if (e.httpReturnCode == 401 && e.getMessage().contains("Token has expired")) {
                // The refresh-token-in-advance logic did not work for some reason: force a refresh now
                refreshExpiredToken();
                return doRunAuthorized();
            } else {
                throw e;
            }
        }
    }

    private void refreshExpiredToken() throws MendeleyException {
        new OAuthTokenEndpoint.RefreshTokenRequest(authManager, clientCredentials).run();
    }

    /**
     * Template method to be implemented by extending classes.
     * This method is guaranteed to be run with a valid access token.
     *
     * @return Response
     * @throws MendeleyException
     */
    protected abstract Response doRunAuthorized() throws MendeleyException;

    // TODO: consider dropping this to reduce complexity
    /**
     * Checks if the current access token will expire soon (or isn't valid at all).
     */
    private boolean willExpireSoon() {
        if (TextUtils.isEmpty(authManager.getAccessToken()) || authManager.getAuthTokenExpirationDate() == null) {
            return true;
        }
        Date now = new Date();
        Date expires = authManager.getAuthTokenExpirationDate();
        long timeToExpiryMs = expires.getTime() - now.getTime();
        long timeToExpirySec = TimeUnit.MILLISECONDS.toSeconds(timeToExpiryMs);
        return timeToExpirySec < MIN_TOKEN_VALIDITY_SEC;
    }
}
