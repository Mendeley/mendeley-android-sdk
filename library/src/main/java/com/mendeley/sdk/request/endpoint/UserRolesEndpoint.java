package com.mendeley.sdk.request.endpoint;

import android.net.Uri;
import android.util.JsonReader;

import com.mendeley.sdk.AuthTokenManager;
import com.mendeley.sdk.ClientCredentials;
import com.mendeley.sdk.Request;
import com.mendeley.sdk.request.GetAuthorizedRequest;
import com.mendeley.sdk.request.JsonParser;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import static com.mendeley.sdk.Request.MENDELEY_API_BASE_URL;

/**
 * Class with the implementation of typical {@link Request}s against the /user_roles endpoint.
 * {@see http://dev.mendeley.com/methods/#user_roles}
 */
public class UserRolesEndpoint {
	public static String USER_ROLES_URL = MENDELEY_API_BASE_URL + "user_roles/";

    public static class GetUserRolesRequest extends GetAuthorizedRequest<List<String>> {
        public GetUserRolesRequest(AuthTokenManager authTokenManager, ClientCredentials clientCredentials) {
            super(Uri.parse(USER_ROLES_URL), authTokenManager, clientCredentials);
        }

        @Override
        protected List<String> manageResponse(InputStream is) throws JSONException, IOException, ParseException {
            final JsonReader reader = new JsonReader(new InputStreamReader(new BufferedInputStream(is)));
            return JsonParser.userRolesFromJson(reader);
        }

        @Override
        protected void appendHeaders(Map<String, String> headers) {
            headers.put("Content-type", "application/vnd.mendeley-user-role.1+json");
        }
    }
}
