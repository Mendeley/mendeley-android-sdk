package com.mendeley.sdk.request.endpoint;

import android.net.Uri;
import android.text.TextUtils;
import android.util.JsonReader;

import com.mendeley.sdk.AuthManager;
import com.mendeley.sdk.ClientCredentials;
import com.mendeley.sdk.Request;
import com.mendeley.sdk.model.Profile;
import com.mendeley.sdk.request.DeleteAuthorizedRequest;
import com.mendeley.sdk.request.GetAuthorizedRequest;
import com.mendeley.sdk.request.JsonParser;
import com.mendeley.sdk.request.PatchAuthorizedRequest;
import com.mendeley.sdk.request.PostAuthorizedRequest;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.mendeley.sdk.Request.MENDELEY_API_BASE_URL;

/**
 * Class with the implementation of typical {@link Request}s against the /profiles endpoint.
 * {@see http://dev.mendeley.com/methods/#profiles}
 */
public class ProfilesEndpoint {
	public static final String PROFILES_URL = MENDELEY_API_BASE_URL + "profiles/";
    public static final String PROFILE_CONTENT_TYPE = "application/vnd.mendeley-profiles.1+json";
    public static final String PROFILE_NEW_CONTENT_TYPE = "application/vnd.mendeley-new-profile.1+json";
    public static final String PROFILE_AMENDMENT_CONTENT_TYPE = "application/vnd.mendeley-profile-amendment.1+json";

    public ProfilesEndpoint() {
    }

    public static class GetProfileRequest extends GetAuthorizedRequest<Profile> {
        public GetProfileRequest(String profileId, AuthManager authManager, ClientCredentials clientCredentials) {
            super(Uri.parse(ProfilesEndpoint.PROFILES_URL + profileId), authManager, clientCredentials);
        }

        @Override
        protected synchronized Profile manageResponse(InputStream is) throws JSONException, IOException, ParseException {
            final JsonReader reader = new JsonReader(new InputStreamReader(new BufferedInputStream(is)));
            return JsonParser.profileFromJson(reader);
        }

        @Override
        protected void appendHeaders(Map<String, String> headers) {
            headers.put("Content-type", PROFILE_CONTENT_TYPE);
            headers.put("Accept", PROFILE_CONTENT_TYPE);
        }
    }

    public static class PostProfileRequest extends PostAuthorizedRequest<Profile> {
        private final Profile profile;
        private final String password;

        public PostProfileRequest(AuthManager authManager, ClientCredentials clientCredentials, Profile profile, String password) {
            super(Uri.parse(PROFILES_URL), authManager, clientCredentials);
            this.profile = profile;
            this.password = password;
        }

        @Override
        protected void appendHeaders(Map<String, String> headers) {
            headers.put("Content-type", PROFILE_CONTENT_TYPE);
            headers.put("Accept", PROFILE_CONTENT_TYPE);
        }

        @Override
        protected RequestBody getBody() throws JSONException {
            return RequestBody.create(MediaType.parse(PROFILE_NEW_CONTENT_TYPE), JsonParser.profileToJson(profile, password).toString());
        }

        @Override
        protected Profile manageResponse(InputStream is) throws Exception {
            final JsonReader reader = new JsonReader(new InputStreamReader(is));
            return JsonParser.profileFromJson(reader);
        }
    }

    public static class PatchMeProfileRequest extends PatchAuthorizedRequest<Profile> {
        private final Profile profile;

        public PatchMeProfileRequest(Profile profile, AuthManager authManager, ClientCredentials clientCredentials) {
            super(Uri.parse(ProfilesEndpoint.PROFILES_URL + "me"), null, authManager, clientCredentials);
            this.profile = profile;
        }

        @Override
        protected RequestBody getBody() throws JSONException {
            if (!TextUtils.isEmpty(profile.email)) {
                throw new IllegalArgumentException("Email can't be patched using this endpoint/request");
            }
            return RequestBody.create(MediaType.parse(PROFILE_AMENDMENT_CONTENT_TYPE), JsonParser.profileToJsonAmendment(profile).toString());
        }

        @Override
        protected Profile manageResponse(InputStream is) throws Exception {
            final JsonReader reader = new JsonReader(new InputStreamReader(is));
            return JsonParser.profileFromJson(reader);
        }

        @Override
        protected void appendHeaders(Map<String, String> headers) {
            headers.put("Content-type", PROFILE_CONTENT_TYPE);
            headers.put("Accept", PROFILE_CONTENT_TYPE);
        }
    }

    public static class DeleteProfileRequest extends DeleteAuthorizedRequest<Void> {

        public DeleteProfileRequest(String profileId, AuthManager authManager, ClientCredentials clientCredentials) {
            super(Uri.parse(PROFILES_URL + "/" + profileId), authManager, clientCredentials);
        }
    }

}
