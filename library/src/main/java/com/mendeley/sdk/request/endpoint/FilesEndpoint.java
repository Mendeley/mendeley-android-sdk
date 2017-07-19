package com.mendeley.sdk.request.endpoint;

import android.net.Uri;
import android.util.JsonReader;

import com.mendeley.sdk.AuthManager;
import com.mendeley.sdk.ClientCredentials;
import com.mendeley.sdk.Request;
import com.mendeley.sdk.exceptions.FileDownloadException;
import com.mendeley.sdk.model.File;
import com.mendeley.sdk.request.CancellableInputStream;
import com.mendeley.sdk.request.DeleteAuthorizedRequest;
import com.mendeley.sdk.request.GetAuthorizedRequest;
import com.mendeley.sdk.request.JsonParser;
import com.mendeley.sdk.request.PostAuthorizedRequest;
import com.mendeley.sdk.util.DateUtils;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.mendeley.sdk.Request.MENDELEY_API_BASE_URL;

/**
 * Class with the implementation of typical {@link Request}s against the /files endpoint.
 * {@see http://dev.mendeley.com/methods/#files}
 */
public class FilesEndpoint {

    public final static String FILES_BASE_URL = MENDELEY_API_BASE_URL + "files";
    public static final String FILES_CONTENT_TYPE = "application/vnd.mendeley-file.1+json";

    public static class GetFilesRequest extends GetAuthorizedRequest<List<File>> {
        private static Uri getGetFilesUrl(FileRequestParameters params) {
            final Uri.Builder bld = Uri.parse(FILES_BASE_URL).buildUpon();
            if (params == null) {
                return bld.build();
            }
            return params.appendToUi(bld.build());
        }

        public GetFilesRequest(Uri url, AuthManager authManager, ClientCredentials clientCredentials) {
            super(url, authManager, clientCredentials);
        }

        public GetFilesRequest(FileRequestParameters parameters, AuthManager authManager, ClientCredentials clientCredentials) {
            this(getGetFilesUrl(parameters), authManager, clientCredentials);
        }

        @Override
        protected List<File> manageResponse(InputStream is) throws JSONException, IOException {
            final JsonReader reader = new JsonReader(new InputStreamReader(new BufferedInputStream(is)));
            return JsonParser.filesFromJson(reader);
        }

        @Override
        protected void appendHeaders(Map<String, String> headers) {
            headers.put("Content-type", FILES_CONTENT_TYPE);
            headers.put("Accept", FILES_CONTENT_TYPE);
        }
    }

    /**
     * {@link Request} to download the binary of a file (usually the pdf file)
     */
    public static class GetFileBinaryRequest extends GetAuthorizedRequest<Long> {

        private static String filesUrl = MENDELEY_API_BASE_URL + "files";

        private static final String PARTIALLY_DOWNLOADED_EXTENSION = ".part";
        private final String fileId;
        private final java.io.File targetFile;

        public GetFileBinaryRequest(String fileId, java.io.File targetFile, AuthManager authManager, ClientCredentials clientCredentials) {
            super(Uri.parse(filesUrl + "/" + fileId), authManager, clientCredentials);
            this.fileId = fileId;
            this.targetFile = targetFile;
        }

        @Override
        protected Long manageResponse(InputStream is) throws IOException, FileDownloadException {
            final java.io.File tempFile = new java.io.File(targetFile.getParent(), targetFile.getName() + PARTIALLY_DOWNLOADED_EXTENSION);

            try {
                long total = 0;
                FileOutputStream fileOutputStream = new FileOutputStream(tempFile);

                byte data[] = new byte[1024 * 16];
                int count;

                while ((count = is.read(data)) != -1) {
                    total += count;
                    fileOutputStream.write(data, 0, count);
                }

                if (!tempFile.renameTo(targetFile)) {
                    throw new FileDownloadException("Cannot rename downloaded file", fileId);
                } else {
                    return total;
                }
            } catch (Exception e) {
                if (tempFile.exists()) {
                    tempFile.delete();
                }
                throw e;
            }
        }

        public String getFileId() {
            return fileId;
        }
    }

    public static class PostFileWithBinaryRequest extends PostAuthorizedRequest<File> {
        private final String contentType;
        private final String documentId;
        private final String fileName;
        private final InputStream inputStream;

        private static String filesUrl = MENDELEY_API_BASE_URL + "files";

        public PostFileWithBinaryRequest(String contentType, String documentId, String fileName, InputStream inputStream, AuthManager authManager, ClientCredentials clientCredentials) {
            super(Uri.parse(filesUrl), authManager, clientCredentials);
            this.contentType = contentType;
            this.documentId = documentId;
            this.fileName = fileName;
            this.inputStream = inputStream;
        }

        @Override
        protected void appendHeaders(Map<String, String> headers) {
            super.appendHeaders(headers);
            headers.put("Content-Disposition", "attachment; filename*=UTF-8\'\'" + fileName);
            headers.put("Link", "<" + MENDELEY_API_BASE_URL + "documents/" + documentId + ">; rel=\"document\"");
        }

        @Override
        protected RequestBody getBody() throws JSONException {
            final InputStream cancelableInputStream = new CancellableInputStream(inputStream) {
                @Override
                protected boolean isCancelled() {
                    return PostFileWithBinaryRequest.this.isCancelled();
                }
            };
            return InputStreamRequestBody.create(MediaType.parse(contentType), cancelableInputStream);
        }

        @Override
        protected File manageResponse(InputStream is) throws Exception {
            final JsonReader reader = new JsonReader(new InputStreamReader(is));
            return JsonParser.fileFromJson(reader);
        }

    }

    public static class DeleteFileRequest extends DeleteAuthorizedRequest<Void> {
        public DeleteFileRequest(String fileId, AuthManager authManager, ClientCredentials clientCredentials) {
            super(Uri.parse(FILES_BASE_URL + "/" + fileId), authManager, clientCredentials);
        }
    }


    /**
     * This class represents parameters for file SDK requests.
     * <p>
     * Uninitialised properties will be ignored.
     */
    public static class FileRequestParameters {
        /**
         * The document ID.
         */
        public String documentId;

        /**
         * The group ID.
         */
        public String groupId;

        /**
         * Returns only files added since this timestamp. Should be supplied in ISO 8601 format.
         */
        public Date addedSince;

        /**
         * Returns files deleted since this timestamp. Should be supplied in ISO 8601 format.
         */
        public Date deletedSince;

        /**
         * The maximum number of items on the page. If not supplied, the default is 20. The largest allowable value is 500.
         */
        public Integer limit;

        /**
         * The catalog ID.
         */
        public String catalogId;


        Uri appendToUi(Uri uri) {
            final Uri.Builder bld = uri.buildUpon();

            if (documentId != null) {
                bld.appendQueryParameter("document_id", documentId);
            }
            if (groupId != null) {
                bld.appendQueryParameter("group_id", groupId);
            }
            if (addedSince != null) {
                bld.appendQueryParameter("added_since", DateUtils.formatMendeleyApiTimestamp(addedSince));
            }
            if (deletedSince != null) {
                bld.appendQueryParameter("deleted_since", DateUtils.formatMendeleyApiTimestamp(deletedSince));
            }
            if (limit != null) {
                bld.appendQueryParameter("limit", String.valueOf(limit));
            }
            if (catalogId != null) {
                bld.appendQueryParameter("catalog_id", catalogId);
            }
            return bld.build();
        }
    }



}
