package com.mendeley.sdk.request.endpoint;

import android.net.Uri;
import android.util.JsonReader;

import com.mendeley.sdk.AuthTokenManager;
import com.mendeley.sdk.ClientCredentials;
import com.mendeley.sdk.Request;
import com.mendeley.sdk.model.Document;
import com.mendeley.sdk.model.Folder;
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
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.mendeley.sdk.Request.MENDELEY_API_BASE_URL;

/**
 * Class with the implementation of typical {@link Request}s against the /catalog endpoint.
 * {@see http://dev.mendeley.com/methods/#catalog}
 */
public class CatalogEndpoint {

	public static final String CATALOG_BASE_URL = MENDELEY_API_BASE_URL + "catalog";
    public static final String CATALOG_CONTENT_TYPE = "application/vnd.mendeley-document.1+json";

    public static class GetCatalogDocumentRequest extends GetAuthorizedRequest<List<Document>> {

        private static Uri getGetCatalogDocumentUrl(String identifier, String value) {
            return Uri.parse(CATALOG_BASE_URL).buildUpon().appendQueryParameter(identifier, value).build();
        }

        public GetCatalogDocumentRequest(String identifier, String value, AuthTokenManager authTokenManager, ClientCredentials clientCredentials) {
            super(getGetCatalogDocumentUrl(identifier, value), authTokenManager, clientCredentials);
        }

        @Override
        protected List<Document> manageResponse(InputStream is) throws JSONException, IOException, ParseException {
            final JsonReader reader = new JsonReader(new InputStreamReader(new BufferedInputStream(is)));
            return JsonParser.documentsFromJson(reader);
        }

        @Override
        protected void appendHeaders(Map<String, String> headers) {
            headers.put("Content-type", CATALOG_CONTENT_TYPE);
            headers.put("Accept", CATALOG_CONTENT_TYPE);
        }
    }

}
