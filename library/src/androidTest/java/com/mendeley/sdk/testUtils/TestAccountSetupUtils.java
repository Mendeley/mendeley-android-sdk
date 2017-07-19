package com.mendeley.sdk.testUtils;

import android.content.res.AssetManager;
import android.net.Uri;
import android.util.JsonReader;

import com.mendeley.sdk.AuthManager;
import com.mendeley.sdk.BuildConfig;
import com.mendeley.sdk.ClientCredentials;
import com.mendeley.sdk.Request;
import com.mendeley.sdk.RequestsFactory;
import com.mendeley.sdk.exceptions.MendeleyException;
import com.mendeley.sdk.model.Annotation;
import com.mendeley.sdk.model.Document;
import com.mendeley.sdk.model.Education;
import com.mendeley.sdk.model.Employment;
import com.mendeley.sdk.model.File;
import com.mendeley.sdk.model.Folder;
import com.mendeley.sdk.model.Group;
import com.mendeley.sdk.model.Profile;
import com.mendeley.sdk.model.ReadPosition;
import com.mendeley.sdk.request.DeleteAuthorizedRequest;
import com.mendeley.sdk.request.JsonParser;
import com.mendeley.sdk.request.PostAuthorizedRequest;
import com.mendeley.sdk.request.endpoint.DocumentEndpoint;
import com.mendeley.sdk.request.endpoint.FoldersEndpoint;
import com.mendeley.sdk.request.endpoint.GroupsEndpoint;
import com.mendeley.sdk.util.DateUtils;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Class for setting the test account for running the tests
 */
public class TestAccountSetupUtils {

    private final RequestsFactory requestFactory;
    private final AuthManager authManager;
    private final ClientCredentials clientCredentials;

    public TestAccountSetupUtils(AuthManager authManager, ClientCredentials clientCredentials, RequestsFactory requestFactory) {
        this.clientCredentials = clientCredentials;
        this.requestFactory = requestFactory;
        this.authManager = authManager;
    }

    /**
     * After calling this method the account should be clean (no docs, no groups, nothing...)
     */
    public void cleanAll() {
        try {
            cleanDocs();
            cleanFolders();
        } catch (Exception e) {
            throw new TestAccountSetupException(e);
        }
    }

    public void setupMeRemoteProfile(Profile meProfile) throws com.mendeley.sdk.exceptions.MendeleyException {
        // delete in the server existing employments and educations
        final Profile currentMeProfile = requestFactory.newGetMyProfileRequest().run().resource;

        for (Employment employment : currentMeProfile.employment) {
            final Uri deleteEmploymentUrl = Uri.parse(Request.MENDELEY_API_BASE_URL).buildUpon().appendPath("employment").appendPath(employment.id).build();
            new DeleteAuthorizedRequest<Void>(deleteEmploymentUrl, authManager, clientCredentials).run();
        }
        for (Education education : currentMeProfile.education) {
            final Uri deleteEducationUrl = Uri.parse(Request.MENDELEY_API_BASE_URL).buildUpon().appendPath("education").appendPath(education.id).build();
            new DeleteAuthorizedRequest<Void>(deleteEducationUrl, authManager, clientCredentials).run();
        }

        // patch the details of the profile
        requestFactory.newPatchMeProfileRequest(meProfile).run();

        // create employments for the profile in server
        for (Employment employment : meProfile.employment) {
            setupEmploymentForMeProfile(employment);
        }

        // create educations for the profile in server
        for (Education education : meProfile.education) {
            setupEducationForMeProfile(education);
        }
    }

    public void setupEmploymentForMeProfile(final Employment employment) throws MendeleyException {
        final Uri url = Uri.parse(Request.MENDELEY_API_BASE_URL).buildUpon()
                .path("employment")
                .build();

        new PostAuthorizedRequest<Void>(url, authManager, clientCredentials) {

            @Override
            protected RequestBody getBody() throws JSONException {
                return RequestBody.create(MediaType.parse("application/vnd.mendeley-new-employment.1+json"), createJsonBody(employment).toString());
            }

            private JSONObject createJsonBody(Employment employment) throws JSONException {
                final JSONObject json = new JSONObject();
                json.put("position", employment.position);
                json.put("start_date", DateUtils.formatYearMonthDayDate(employment.startDate));
                json.put("end_date", DateUtils.formatYearMonthDayDate(employment.endDate));
                json.put("institution_id", employment.institution.id);
                return json;
            }

            @Override
            protected Void manageResponse(InputStream is) throws Exception {
                return null;
            }

        }.run();
    }

    public void setupEducationForMeProfile(final Education education) throws MendeleyException {
        final Uri url = Uri.parse(Request.MENDELEY_API_BASE_URL).buildUpon()
                .path("education")
                .build();

        new PostAuthorizedRequest<Void>(url, authManager, clientCredentials) {

            @Override
            protected RequestBody getBody() throws JSONException {
                return RequestBody.create(MediaType.parse("application/vnd.mendeley-new-education.1+json"), createJsonBody(education).toString());
            }

            private JSONObject createJsonBody(Education education) throws JSONException {
                final JSONObject json = new JSONObject();
                json.put("degree", education.degree);
                json.put("start_date", DateUtils.formatYearMonthDayDate(education.startDate));
                json.put("end_date", DateUtils.formatYearMonthDayDate(education.endDate));
                json.put("institution_id", education.institution.id);
                return json;
            }

            @Override
            protected Void manageResponse(InputStream is) throws Exception {
                return null;
            }

        }.run();
    }


    private void cleanDocs() throws MendeleyException {
        // delete non-trashed docs
        // FIXME: do not delegate into the requestFactory to this, because we are testing the requestFactory...
        for (Document doc : requestFactory.newGetDocumentsRequest((DocumentEndpoint.DocumentRequestParameters) null).run().resource) {
            // FIXME: do not delegate into the requestFactory to this, because we are testing the requestFactory...
            requestFactory.newDeleteDocumentRequest(doc.id).run();
        }

        // delete trashed docs
        // FIXME: do not delegate into the requestFactory to this, because we are testing the requestFactory...
        for (Document doc : requestFactory.newGetTrashedDocumentsRequest((DocumentEndpoint.DocumentRequestParameters) null).run().resource) {
            // FIXME: do not delegate into the requestFactory to this, because we are testing the requestFactory...
            requestFactory.newDeleteTrashedDocumentRequest(doc.id).run();
        }

        // ensure no documents at all (trashed or deleted)
        // FIXME: do not delegate into the requestFactory to this, because we are testing the requestFactory...
        Assert.assertEquals("Expected empty list of non trashed docs in server", 0, requestFactory.newGetDocumentsRequest((DocumentEndpoint.DocumentRequestParameters) null).run().resource.size());
        Assert.assertEquals("Expected empty list of trashed docs in server", 0, requestFactory.newGetTrashedDocumentsRequest((DocumentEndpoint.DocumentRequestParameters) null).run().resource.size());

    }

    private void cleanFolders() throws MendeleyException {

        // delete parent folders as sub folders will be deleted as well
        // FIXME: do not delegate into the requestFactory to this, because we are testing the requestFactory...
        for (Folder folder : requestFactory.newGetFoldersRequest((FoldersEndpoint.FolderRequestParameters) null).run().resource) {
            // FIXME: do not delegate into the requestFactory to this, because we are testing the requestFactory...
            if (folder.parentId == null) {
                requestFactory.newDeleteFolderRequest(folder.id).run();
            }
        }

        Assert.assertEquals("Expected empty list of folders in server", 0, requestFactory.newGetFoldersRequest((FoldersEndpoint.FolderRequestParameters) null).run().resource.size());

    }

    public Document setupDocument(Document doc) throws MendeleyException {
        // FIXME: do not delegate into the requestFactory to this, because we are testing the requestFactory this should receive a JSON and post it using HTTP
        return requestFactory.newPostDocumentRequest(doc).run().resource;
    }

    public void trashDocument(String docId) throws MendeleyException {
        // FIXME: do not delegate into the requestFactory to this, because we are testing the requestFactory this should receive a JSON and post it using HTTP
        requestFactory.newTrashDocumentRequest(docId).run();
    }

    public Annotation setupAnnotation(Annotation annotation) throws MendeleyException {
        // FIXME: do not delegate into the requestFactory to this, because we are testing the requestFactory this should receive a JSON and post it using HTTP
        return requestFactory.newPostAnnotationRequest(annotation).run().resource;
    }

    public File setupFile(String docId, String fileName, InputStream inputStream) throws MendeleyException {
        // FIXME: do not delegate into the requestFactory to this, because we are testing the requestFactory this should receive a JSON and post it using HTTP
        return requestFactory.newPostFileWithBinaryRequest("application/pdf", docId, inputStream, fileName).run().resource;
    }

    public Folder setupFolder(Folder folder) throws MendeleyException {
        // FIXME: do not delegate into the requestFactory to this, because we are testing the requestFactory this should receive a JSON and post it using HTTP
        return requestFactory.newPostFolderRequest(folder).run().resource;
    }

    public List<Group> getGroups() throws MendeleyException {
        // FIXME: do not delegate into the requestFactory to this, because we are testing the requestFactory this should receive a JSON and post it using HTTP
        return requestFactory.newGetGroupsRequest(new GroupsEndpoint.GroupRequestParameters()).run().resource;
    }


    public ReadPosition setupReadingPosition(String fileId, int page, int verticalPosition, Date date) throws Exception {
        HttpsURLConnection con = null;
        OutputStream os = null;
        BufferedWriter writer = null;

        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("file_id", fileId);
            jsonObject.put("page", page);
            jsonObject.put("vertical_position", verticalPosition);
            jsonObject.put("date", DateUtils.formatMendeleyApiTimestamp(date));


            final URL callUrl = new URL(BuildConfig.WEB_API_BASE_URL + "/" + "recently_read");

            con = (HttpsURLConnection) callUrl.openConnection();
            con.setRequestMethod("POST");
            con.addRequestProperty("Authorization", "Bearer " + authManager.getAccessToken());
            con.addRequestProperty("Content-type", "application/vnd.mendeley-recently-read.1+json");

            con.connect();

            os = con.getOutputStream();

            writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();

            final int responseCode = con.getResponseCode();
            if (responseCode < 200 && responseCode >= 300) {
                throw new Exception("Invalid response code posting recently read position: " + responseCode + " " + con.getResponseMessage());
            }

            final JsonReader reader = new JsonReader(new InputStreamReader(con.getInputStream()));
            return JsonParser.readPositionFromJson(reader);
        } finally {
            try {
                if (con != null) {
                    con.disconnect();
                }
                if (os != null) {
                    os.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    public List<ReadPosition> getAllReadingPositions() throws Exception {
        HttpsURLConnection con = null;
        InputStream is = null;

        try {
            final URL callUrl = new URL(BuildConfig.WEB_API_BASE_URL + "/" + "recently_read");

            con = (HttpsURLConnection) callUrl.openConnection();
            con.setRequestMethod("GET");
            con.addRequestProperty("Authorization", "Bearer " + authManager.getAccessToken());
            con.addRequestProperty("Content-type", "application/vnd.mendeley-recently-read.1+json");

            con.connect();

            is = con.getInputStream();

            final int responseCode = con.getResponseCode();
            if (responseCode < 200 && responseCode >= 300) {
                throw new Exception("Invalid response code getting recently read position: " + responseCode + " " + con.getResponseMessage());
            }

            final JsonReader reader = new JsonReader(new InputStreamReader(con.getInputStream()));
            return JsonParser.readPositionsFromJson(reader);
        } finally {
            try {
                if (con != null) {
                    con.disconnect();
                }
                if (is != null) {
                    is.close();
                }
            } catch (Exception ignored) {
            }
        }
    }


    /**
     * Dumps a file from AssetManger to an array of bytes.
     * Needed to work around some issures when posting files from the the AssetManager.
     *
     * @param assetManager
     * @param fileName
     * @return
     * @throws IOException
     */
    public byte[] readFully(AssetManager assetManager, String fileName) throws IOException {
        InputStream assetsInputStream = null;
        ByteArrayOutputStream bos = null;

        try {
            assetsInputStream = assetManager.open(fileName);
            bos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024 * 10];

            int read;
            while ((read = assetsInputStream.read(buffer, 0, buffer.length)) > 0) {
                bos.write(buffer, 0, read);
            }

            return bos.toByteArray();
        } finally {
            if (assetsInputStream != null) {
                assetsInputStream.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
    }

    /**
     * Exceptions that may happen when trying to set up the testing scenario
     */
    public static class TestAccountSetupException extends RuntimeException {
        public TestAccountSetupException(Exception cause) {
            super("A problem happened when trying to setup the testing scenario", cause);
        }
    }
}
