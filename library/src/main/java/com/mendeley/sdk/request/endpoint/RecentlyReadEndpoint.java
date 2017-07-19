package com.mendeley.sdk.request.endpoint;

import android.net.Uri;
import android.util.JsonReader;

import com.mendeley.sdk.AuthManager;
import com.mendeley.sdk.ClientCredentials;
import com.mendeley.sdk.Request;
import com.mendeley.sdk.model.ReadPosition;
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
 * Class with the implementation of typical {@link Request}s against the /recently_read endpoint.
 * {@see http://dev.mendeley.com/methods/}
 */
public class RecentlyReadEndpoint {
	public static final String RECENTLY_READ_BASE_URL = MENDELEY_API_BASE_URL + "recently_read";
    public static final String RECENTLY_READ_CONTENT_TYPE = "application/vnd.mendeley-recently-read.1+json";


    public static class GetRecentlyReadRequest extends GetAuthorizedRequest<List<ReadPosition>> {
        private static Uri getGetRecentlyReadUrl(String groupId, String fileId, int limit) {
            final Uri.Builder bld = Uri.parse(RECENTLY_READ_BASE_URL).buildUpon();

            // groupId
            if (groupId != null) {
                bld.appendQueryParameter("group_id", groupId);
            }
            // fileId
            if (fileId != null) {
                bld.appendQueryParameter("file_id", fileId);
            }
            // limit
            bld.appendQueryParameter("limit", String.valueOf(limit));

            return bld.build();
        }

        public GetRecentlyReadRequest(String groupId, String fileId, int limit, AuthManager authManager, ClientCredentials clientCredentials) {
            super(getGetRecentlyReadUrl(groupId, fileId, limit), authManager, clientCredentials);
        }

        @Override
        protected List<ReadPosition> manageResponse(InputStream is) throws JSONException, ParseException, IOException {
            final JsonReader reader = new JsonReader(new InputStreamReader(new BufferedInputStream(is)));
            return JsonParser.readPositionsFromJson(reader);
        }

        @Override
        protected void appendHeaders(Map<String, String> headers) {
            headers.put("Content-type", RECENTLY_READ_CONTENT_TYPE);
            headers.put("Accept", RECENTLY_READ_CONTENT_TYPE);
        }
    }


    public static class PostRecentlyReadRequest extends PostAuthorizedRequest<ReadPosition> {
        // we need to crate a new procedure as this endpoint returns 200 or 201
        // depending on whether the read position existed or not

        private final ReadPosition readPosition;

        public PostRecentlyReadRequest(ReadPosition readPosition, AuthManager authManager, ClientCredentials clientCredentials) {
            super(Uri.parse(RECENTLY_READ_BASE_URL), authManager, clientCredentials);
            this.readPosition = readPosition;
        }

        @Override
        protected RequestBody getBody() throws JSONException {
            return RequestBody.create(MediaType.parse(RECENTLY_READ_CONTENT_TYPE), JsonParser.readPositionToJson(readPosition).toString());
        }

        @Override
        protected ReadPosition manageResponse(InputStream is) throws Exception {
            final JsonReader reader = new JsonReader(new InputStreamReader(is));
            return JsonParser.readPositionFromJson(reader);
        }

        @Override
        protected void appendHeaders(Map<String, String> headers) {
            headers.put("Accept", RECENTLY_READ_CONTENT_TYPE);
        }

    }

}
