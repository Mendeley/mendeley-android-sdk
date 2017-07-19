package com.mendeley.sdk.request.endpoint;

import android.net.Uri;
import android.util.JsonReader;

import com.mendeley.sdk.AuthManager;
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
 * Class with the implementation of typical {@link Request}s against the /subject_areas endpoint.
 * {@see http://dev.mendeley.com/methods/#subject-areas}
 */
public class SubjectAreasEndpoint {
	public static String SUBJECT_AREAS_URL = MENDELEY_API_BASE_URL + "subject_areas/";

    public static class GetSubjectAreasRequest extends GetAuthorizedRequest<List<String>> {
        public GetSubjectAreasRequest(AuthManager authManager, ClientCredentials clientCredentials) {
            super(Uri.parse(SUBJECT_AREAS_URL), authManager, clientCredentials);
        }

        @Override
        protected List<String> manageResponse(InputStream is) throws JSONException, IOException, ParseException {
            final JsonReader reader = new JsonReader(new InputStreamReader(new BufferedInputStream(is)));
            return JsonParser.subjectAreasFromJson(reader);
        }

        @Override
        protected void appendHeaders(Map<String, String> headers) {
            headers.put("Content-type", "application/vnd.mendeley-subject-area.1+json");
            headers.put("Accept", "application/vnd.mendeley-subject-area.1+json");
        }
    }
}
