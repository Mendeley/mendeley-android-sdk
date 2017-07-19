package com.mendeley.sdk.request.endpoint;

import android.net.Uri;
import android.util.JsonReader;

import com.mendeley.sdk.ClientCredentials;
import com.mendeley.sdk.AuthManager;
import com.mendeley.sdk.Request;
import com.mendeley.sdk.model.Document;
import com.mendeley.sdk.request.DeleteAuthorizedRequest;
import com.mendeley.sdk.request.GetAuthorizedRequest;
import com.mendeley.sdk.request.JsonParser;
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
 * Class with the implementation of typical {@link Request}s against the /trash endpoint.
 * {@see http://dev.mendeley.com/methods/#trash}
 */
public class TrashEndpoint {
    public static String BASE_URL = MENDELEY_API_BASE_URL + "trash";

    public static class GetTrashedDocumentsRequest extends GetAuthorizedRequest<List<Document>> {
        public static Uri getTrashDocumentsUrl(DocumentEndpoint.DocumentRequestParameters params) {
            Uri uri = Uri.parse(TrashEndpoint.BASE_URL);
            return (params != null) ? params.appendToUi(uri) : uri;
        }

        public GetTrashedDocumentsRequest(Uri url, AuthManager authManager, ClientCredentials clientCredentials) {
            super(url, authManager, clientCredentials);
        }

        public GetTrashedDocumentsRequest(DocumentEndpoint.DocumentRequestParameters parameters, AuthManager authManager, ClientCredentials clientCredentials) {
            super(getTrashDocumentsUrl(parameters), authManager, clientCredentials);
        }

        @Override
        protected List<Document> manageResponse(InputStream is) throws JSONException, IOException, ParseException {
            final JsonReader reader = new JsonReader(new InputStreamReader(new BufferedInputStream(is)));
            return JsonParser.documentsFromJson(reader);
        }

        @Override
        protected void appendHeaders(Map<String, String> headers) {
            headers.put("Content-type", DocumentEndpoint.DOCUMENTS_CONTENT_TYPE);
            headers.put("Accept", DocumentEndpoint.DOCUMENTS_CONTENT_TYPE);
        }
    }

    public static class DeleteTrashedDocumentRequest extends DeleteAuthorizedRequest<Void> {
        public DeleteTrashedDocumentRequest(String documentId, AuthManager authManager, ClientCredentials clientCredentials) {
            super(Uri.parse(BASE_URL).buildUpon().appendPath(documentId).build(), authManager, clientCredentials);
        }
    }

    public static class RestoreTrashedDocumentRequest extends PostAuthorizedRequest<Void> {
        public RestoreTrashedDocumentRequest(String documentId, AuthManager authManager, ClientCredentials clientCredentials) {
            super(Uri.parse(BASE_URL).buildUpon().appendPath(documentId).appendPath("restore").build(), authManager, clientCredentials);
        }

        @Override
        protected Void manageResponse(InputStream is) throws Exception {
            return null;
        }

        @Override
        protected RequestBody getBody() throws JSONException {
            return RequestBody.create(MediaType.parse("text/plain"), "");
        }
    }
}
