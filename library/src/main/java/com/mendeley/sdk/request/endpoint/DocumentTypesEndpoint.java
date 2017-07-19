package com.mendeley.sdk.request.endpoint;

import android.net.Uri;
import android.util.JsonReader;

import com.mendeley.sdk.AuthManager;
import com.mendeley.sdk.ClientCredentials;
import com.mendeley.sdk.request.GetAuthorizedRequest;
import com.mendeley.sdk.request.JsonParser;
import com.mendeley.sdk.Request;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import static com.mendeley.sdk.Request.MENDELEY_API_BASE_URL;

/**
 * Class with the implementation of typical {@link Request}s against the /document_types endpoint.
 * {@see http://dev.mendeley.com/methods/#documents}
 */
public class DocumentTypesEndpoint {

    public static String DOCUMENT_TYPES_BASE_URL = MENDELEY_API_BASE_URL + "document_types";
    private static final String DOCUMENT_TYPES_CONTENT_TYPE = "application/vnd.mendeley-document-type.1+json";

    public static class GetDocumentTypesRequest extends GetAuthorizedRequest<Map<String, String>> {
        public GetDocumentTypesRequest(AuthManager authManager, ClientCredentials clientCredentials) {
            super(Uri.parse(DOCUMENT_TYPES_BASE_URL), authManager, clientCredentials);
        }

        protected Map<String, String> manageResponse(InputStream is) throws JSONException, IOException {
            final JsonReader reader = new JsonReader(new InputStreamReader(new BufferedInputStream(is)));
            return JsonParser.stringsMapFromJson(reader);
        }

        @Override
        protected void appendHeaders(Map<String, String> headers) {
            headers.put("Content-type", DOCUMENT_TYPES_CONTENT_TYPE);
            headers.put("Accept", DOCUMENT_TYPES_CONTENT_TYPE);
        }
    }

}
