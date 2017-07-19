package com.mendeley.sdk.request.endpoint;

import android.net.Uri;
import android.util.JsonReader;

import com.mendeley.sdk.AuthManager;
import com.mendeley.sdk.ClientCredentials;
import com.mendeley.sdk.Request;
import com.mendeley.sdk.model.Document;
import com.mendeley.sdk.request.DeleteAuthorizedRequest;
import com.mendeley.sdk.request.GetAuthorizedRequest;
import com.mendeley.sdk.request.JsonParser;
import com.mendeley.sdk.request.PatchAuthorizedRequest;
import com.mendeley.sdk.request.PostAuthorizedRequest;
import com.mendeley.sdk.util.DateUtils;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.mendeley.sdk.Request.MENDELEY_API_BASE_URL;

/**
 * Class with the implementation of typical {@link Request}s against the /documents endpoint.
 * {@see http://dev.mendeley.com/methods/#documents}
 */
public class DocumentEndpoint {

    public static String DOCUMENTS_BASE_URL = MENDELEY_API_BASE_URL + "documents";
    public static String DOCUMENTS_CONTENT_TYPE = "application/vnd.mendeley-document.1+json";


    public static class GetDocumentsRequest extends GetAuthorizedRequest<List<Document>> {
        public GetDocumentsRequest(Uri url, AuthManager authManager, ClientCredentials clientCredentials) {
            super(url, authManager, clientCredentials);
        }

        public GetDocumentsRequest(DocumentEndpoint.DocumentRequestParameters params, AuthManager authManager, ClientCredentials clientCredentials) {
            super(params != null ? params.appendToUi(Uri.parse(DOCUMENTS_BASE_URL)) : Uri.parse(DOCUMENTS_BASE_URL), authManager, clientCredentials);
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

    public static class GetDocumentRequest extends GetAuthorizedRequest<Document> {

        private static Uri getGetDocumentUrl(String documentId, DocumentRequestParameters.View view) {
            StringBuilder url = new StringBuilder();
            url.append(DOCUMENTS_BASE_URL);
            url.append("/").append(documentId);

            if (view != null) {
                url.append("?").append("view=" + view);
            }

            return Uri.parse(url.toString());
        }

        public GetDocumentRequest(String documentId, DocumentRequestParameters.View view, AuthManager authManager, ClientCredentials clientCredentials) {
            super(getGetDocumentUrl(documentId, view), authManager, clientCredentials);
        }

        @Override
        protected Document manageResponse(InputStream is) throws JSONException, IOException, ParseException {
            final JsonReader reader = new JsonReader(new InputStreamReader(new BufferedInputStream(is)));
            return JsonParser.documentFromJson(reader);
        }

        @Override
        protected void appendHeaders(Map<String, String> headers) {
            headers.put("Content-type", DOCUMENTS_CONTENT_TYPE);
            headers.put("Accept", DOCUMENTS_CONTENT_TYPE);
        }
    }

    public static class PostDocumentRequest extends PostAuthorizedRequest<Document> {

        final private Document doc;

        public PostDocumentRequest(Document doc, AuthManager authManager, ClientCredentials clientCredentials) {
            super(Uri.parse(DOCUMENTS_BASE_URL), authManager, clientCredentials);
            this.doc = doc;
        }

        @Override
        protected Document manageResponse(InputStream is) throws Exception {
            final JsonReader reader = new JsonReader(new InputStreamReader(is));
            return JsonParser.documentFromJson(reader);
        }

        @Override
        protected void appendHeaders(Map<String, String> headers) {
            headers.put("Content-type", DOCUMENTS_CONTENT_TYPE);
            headers.put("Accept", DOCUMENTS_CONTENT_TYPE);
        }

        @Override
        protected RequestBody getBody() throws JSONException {
            return RequestBody.create(MediaType.parse(DOCUMENTS_CONTENT_TYPE), JsonParser.documentToJson(doc).toString());
        }
    }

    public static class PatchDocumentAuthorizedRequest extends PatchAuthorizedRequest<Document> {

        private final Document document;

        public PatchDocumentAuthorizedRequest(String documentId, Document document, Date date, AuthManager authManager, ClientCredentials clientCredentials) {
            super(Uri.parse(DOCUMENTS_BASE_URL + "/" + documentId), date, authManager, clientCredentials);
            this.document = document;
        }

        @Override
        protected RequestBody getBody() throws JSONException {
            return RequestBody.create(MediaType.parse(DOCUMENTS_CONTENT_TYPE), JsonParser.documentToJson(document).toString());
        }

        @Override
        protected Document manageResponse(InputStream is) throws Exception {
            final JsonReader reader = new JsonReader(new InputStreamReader(is));
            return JsonParser.documentFromJson(reader);
        }

        @Override
        protected void appendHeaders(Map<String, String> headers) {
            headers.put("Accept", DOCUMENTS_CONTENT_TYPE);
        }

    }

    public static class TrashDocumentRequest extends PostAuthorizedRequest<Void> {
        public TrashDocumentRequest(String documentId, AuthManager authManager, ClientCredentials clientCredentials) {
            super(Uri.parse(DOCUMENTS_BASE_URL + "/" + documentId + "/trash"), authManager, clientCredentials);
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

    public static class DeleteDocumentRequest extends DeleteAuthorizedRequest<Void> {

        public DeleteDocumentRequest(String documentId, AuthManager authManager, ClientCredentials clientCredentials) {
            super(Uri.parse(DOCUMENTS_BASE_URL + "/" + documentId), authManager, clientCredentials);
        }
    }

    /**
     * Parameters for requests to retrieve documents.
     * <p>
     * Uninitialised properties will be ignored.
     */
    public static class DocumentRequestParameters {
        /**
         * The required document view.
         */
        public View view;

        /**
         * Group ID. If not supplied, returns user documents.
         */
        public String groupId;

        /**
         * Profile ID. If not supplied, returns user documents.
         */
        public String profileId;

        /**
         * Returns only documents modified since this timestamp. Should be supplied in ISO 8601 format.
         */
        public Date modifiedSince;

        /**
         * Returns only documents deleted since this timestamp. Should be supplied in ISO 8601 format.
         */
        public Date deletedSince;

        /**
         * The maximum number of items on the page. If not supplied, the default is 20. The largest allowable value is 500.
         */
        public Integer limit;

        /**
         * Whether or not the document was authored by the profile id user
         */
        public Boolean authored;

        /**
         * The sort order.
         */
        public Order order;

        /**
         * The field to sort on.
         */
        public Sort sort;


        public Uri appendToUi(Uri uri) {
            final Uri.Builder bld = uri.buildUpon();

            if (view != null) {
                bld.appendQueryParameter("view", view.getValue());
            }
            if (groupId != null) {
                bld.appendQueryParameter("group_id", groupId);
            }
            if (profileId != null) {
                bld.appendQueryParameter("profile_id", profileId);
            }
            if (modifiedSince != null) {
                bld.appendQueryParameter("modified_since", DateUtils.formatMendeleyApiTimestamp(modifiedSince));
            }
            if (limit != null) {
                bld.appendQueryParameter("limit", String.valueOf(limit));
            }
            if (authored != null) {
                bld.appendQueryParameter("authored", String.valueOf(authored));
            }
            if (order != null) {
                bld.appendQueryParameter("order", order.getValue());
            }
            if (sort != null) {
                bld.appendQueryParameter("sort", sort.getValue());
            }
            if (deletedSince != null) {
                bld.appendQueryParameter("deleted_since", DateUtils.formatMendeleyApiTimestamp(deletedSince));
            }


            return bld.build();
        }

        /**
         * Available fields to sort lists by.
         */
        public enum Sort {
            /**
             * Sort by last modified date.
             */
            MODIFIED("last_modified"),
            /**
             * Sort by date added.
             */
            ADDED("created"),
            /**
             * Sort by title alphabetically.
             */
            TITLE("title");

            private final String value;
            Sort(String value) {
                this.value = value;
            }
            public String getValue() {
                return value;
            }
            @Override
            public String toString() {
                return value;
            }
        }

        /**
         * Available sort orders.
         */
        public enum Order {
            /**
             * Ascending order.
             */
            ASC("asc"),
            /**
             * Descending order.
             */
            DESC("desc");

            private final String value;
            Order(String value) {
                this.value = value;
            }
            public String getValue() {
                return value;
            }
            @Override
            public String toString() {
                return value;
            }
        }

        /**
         * Extended document views. The view specifies which additional fields are returned for document objects.
         * All views return core fields.
         */
        public enum View {
            /**
             * Core + bibliographic fields.
             */
            BIB("bib"),
            /**
             * Core + client fields.
             */
            CLIENT("client"),
            /**
             * Core + bibliographic + client fields.
             */
            ALL("all");

            private final String value;
            View(String value) {
                this.value = value;
            }
            public String getValue() {
                return value;
            }
            @Override
            public String toString() {
                return value;
            }
        }
    }
}
