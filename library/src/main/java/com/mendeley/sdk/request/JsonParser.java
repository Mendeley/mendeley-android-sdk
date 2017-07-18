package com.mendeley.sdk.request;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.JsonReader;

import com.mendeley.sdk.model.AlternativeName;
import com.mendeley.sdk.model.Annotation;
import com.mendeley.sdk.model.Discipline;
import com.mendeley.sdk.model.Document;
import com.mendeley.sdk.model.Editorship;
import com.mendeley.sdk.model.Education;
import com.mendeley.sdk.model.Employment;
import com.mendeley.sdk.model.File;
import com.mendeley.sdk.model.Folder;
import com.mendeley.sdk.model.Group;
import com.mendeley.sdk.model.Institution;
import com.mendeley.sdk.model.Person;
import com.mendeley.sdk.model.Point;
import com.mendeley.sdk.model.Profile;
import com.mendeley.sdk.model.ReadPosition;
import com.mendeley.sdk.model.UserRole;
import com.mendeley.sdk.util.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.mendeley.sdk.model.Annotation.PrivacyLevel;

/**
 * Class with parsing and formatting methods for the models in the SDK to JSON.
 */
public class JsonParser {

    private static final String AUTO_LINK_UNVERIFIED = "UNVERIFIED";

    public static Profile profileFromJson(JsonReader reader) throws JSONException, IOException, ParseException {
        final Profile.Builder builder = new Profile.Builder();

        reader.beginObject();

        while (reader.hasNext()) {

            final String key = reader.nextName();
            if (key.equals("id")) {
                builder.setId(reader.nextString());
            } else if (key.equals("profile_uuid")) {
                if (builder.getId() == null) {
                    builder.setId(reader.nextString());
                } else {
                    reader.skipValue();
                }

            } else if (key.equals("display_name")) {
                builder.setDisplayName(reader.nextString());

            } else if (key.equals("user_type")) {
                builder.setUserType(reader.nextString());

            } else if (key.equals("url")) {
                builder.setUrl(reader.nextString());

            } else if (key.equals("email")) {
                builder.setEmail(reader.nextString());

            } else if (key.equals("link")) {
                builder.setLink(reader.nextString());

            } else if (key.equals("first_name")) {
                builder.setFirstName(reader.nextString());

            } else if (key.equals("last_name")) {
                builder.setLastName(reader.nextString());

            } else if (key.equals("title")) {
                builder.setTitle(reader.nextString());

            } else if (key.equals("biography")) {
                builder.setBiography(reader.nextString());

            } else if (key.equals("research_interests")) {
                builder.setResearchInterests(reader.nextString());

            } else if (key.equals("academic_status")) {
                builder.setAcademicStatus(reader.nextString());

            } else if (key.equals("verified")) {
                builder.setVerified(reader.nextBoolean());
            } else if (key.equals("autolink_verification_status")) {
                if (builder.getVerified() == null) {
                    builder.setVerified(autolinkVerificationFromJson(reader.nextString()));
                } else {
                    reader.skipValue();
                }

            } else if (key.equals("marketing")) {
                builder.setMarketing(reader.nextBoolean());

            } else if (key.equals("created_at")) {
                builder.setCreatedAt(DateUtils.parseMendeleyApiTimestamp(reader.nextString()));

            } else if (key.equals("discipline")) {
                builder.setDiscipline(disciplineFromJson(reader));

            } else if (key.equals("photos")) {
                builder.setPhotos(profilePhotosFromJson(reader));

            } else if (key.equals("education")) {
                builder.setEducation(educationsFromJson(reader));

            } else if (key.equals("employment")) {
                builder.setEmployment(employmentsFromJson(reader));

            } else if (key.equals("institution_details")) {
                builder.setInstitutionDetails(institutionFromJson(reader));

            } else if (key.equals("editorships")) {
                builder.setEditorships(editorshipsFromJsonFromJson(reader));

            } else if (key.equals("research_interests_list")) {
                builder.setResearchInterestsList(stringListFromJson(reader));

            } else {
                reader.skipValue();
            }
        }

        reader.endObject();

        return builder.build();
    }

    private static Boolean autolinkVerificationFromJson(String autolinkVerificationStatus) {
        return !autolinkVerificationStatus.equals(AUTO_LINK_UNVERIFIED);
    }

    public static List<Profile.Photo> profilePhotosFromJson(JsonReader reader) throws IOException, JSONException, ParseException {
        List<Profile.Photo> photos = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            photos.add(profilePhotoFromJson(reader));
        }
        reader.endArray();

        return photos;
    }

    public static Profile.Photo profilePhotoFromJson(JsonReader reader) throws JSONException, IOException, ParseException {
        final Profile.Photo.Builder bld = new Profile.Photo.Builder();

        reader.beginObject();
        while (reader.hasNext()) {
            final String key = reader.nextName();

            if (key.equals("width")) {
                bld.setWidth(reader.nextInt());
            } else if (key.equals("height")) {
                bld.setHeight(reader.nextInt());
            } else if (key.equals("url")) {
                bld.setUrl(reader.nextString());
            } else if (key.equals("original")) {
                bld.setOriginal(reader.nextBoolean());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return bld.build();
    }

    public static Group.Photo groupPhotoFromJson(JsonReader reader) throws IOException {
        reader.beginObject();

        String original = null;
        String standard = null;
        String square = null;

        while (reader.hasNext()) {
            final String key = reader.nextName();
            if ("original".equals(key)) {
                original = reader.nextString();
            } else if ("standard".equals(key)) {
                standard = reader.nextString();
            } else if ("square".equals(key)) {
                square = reader.nextString();
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
        return new Group.Photo(original, standard, square);
    }


    public static List<Document> documentsFromJson(JsonReader reader) throws JSONException, IOException, ParseException {
        final List<Document> documents = new ArrayList<Document>();
        reader.beginArray();

        while (reader.hasNext()) {
            documents.add(documentFromJson(reader));
        }

        reader.endArray();
        return documents;
    }

    public static Document documentFromJson(JsonReader reader) throws JSONException, IOException, ParseException {

        final Document.Builder bld = new Document.Builder();

        reader.beginObject();
        while (reader.hasNext()) {

            final String key = reader.nextName();
            if (key.equals("title")) {
                bld.setTitle(reader.nextString());

            } else if (key.equals("type")) {
                bld.setType(reader.nextString());

            } else if (key.equals("last_modified")) {
                bld.setLastModified(DateUtils.parseMendeleyApiTimestamp(reader.nextString()));

            } else if (key.equals("group_id")) {
                bld.setGroupId(reader.nextString());

            } else if (key.equals("profile_id")) {
                bld.setProfileId(reader.nextString());

            } else if (key.equals("read")) {
                bld.setRead(reader.nextBoolean());

            } else if (key.equals("starred")) {
                bld.setStarred(reader.nextBoolean());

            } else if (key.equals("authored")) {
                bld.setAuthored(reader.nextBoolean());

            } else if (key.equals("confirmed")) {
                bld.setConfirmed(reader.nextBoolean());

            } else if (key.equals("hidden")) {
                bld.setHidden(reader.nextBoolean());

            } else if (key.equals("id")) {
                bld.setId(reader.nextString());

            } else if (key.equals("month")) {
                bld.setMonth(reader.nextInt());

            } else if (key.equals("year")) {
                bld.setYear(reader.nextInt());

            } else if (key.equals("day")) {
                bld.setDay(reader.nextInt());

            } else if (key.equals("source")) {
                bld.setSource(reader.nextString());

            } else if (key.equals("revision")) {
                bld.setRevision(reader.nextString());

            } else if (key.equals("created")) {
                bld.setCreated(DateUtils.parseMendeleyApiTimestamp(reader.nextString()));

            } else if (key.equals("abstract")) {
                bld.setAbstractString(reader.nextString());

            } else if (key.equals("pages")) {
                bld.setPages(reader.nextString());

            } else if (key.equals("notes")) {
                bld.setNotes(reader.nextString());

            } else if (key.equals("volume")) {
                bld.setVolume(reader.nextString());

            } else if (key.equals("issue")) {
                bld.setIssue(reader.nextString());

            } else if (key.equals("publisher")) {
                bld.setPublisher(reader.nextString());

            } else if (key.equals("city")) {
                bld.setCity(reader.nextString());

            } else if (key.equals("edition")) {
                bld.setEdition(reader.nextString());

            } else if (key.equals("institution")) {
                bld.setInstitution(reader.nextString());

            } else if (key.equals("series")) {
                bld.setSeries(reader.nextString());

            } else if (key.equals("chapter")) {
                bld.setChapter(reader.nextString());

            } else if (key.equals("client_data")) {
                bld.setClientData(reader.nextString());

            } else if (key.equals("unique_id")) {
                bld.setUniqueId(reader.nextString());

            } else if (key.equals("authors")) {
                bld.setAuthors(personsFromJson(reader));

            } else if (key.equals("editors")) {
                bld.setEditors(personsFromJson(reader));

            } else if (key.equals("identifiers")) {
                final Map<String, String> map = new HashMap<>();

                reader.beginObject();
                while (reader.hasNext()) {
                    map.put(reader.nextName(), reader.nextString());
                }
                reader.endObject();
                bld.setIdentifiers(map);
            } else if (key.equals("tags")) {
                bld.setTags(stringListFromJson(reader));

            } else if (key.equals("file_attached")) {
                bld.setFileAttached(reader.nextBoolean());

            } else if (key.equals("keywords")) {
                bld.setKeywords(stringListFromJson(reader));

            } else if (key.equals("websites")) {
                bld.setWebsites(stringListFromJson(reader));
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();

        return bld.build();
    }

    public static JSONObject documentToJson(Document document) throws JSONException {
        final JSONObject jDocument = new JSONObject();

        if (!document.websites.isNull()) {
            JSONArray websites = new JSONArray();
            for (int i = 0; i < document.websites.size(); i++) {
                websites.put(i, document.websites.get(i));
            }
            jDocument.put("websites", websites);
        }

        if (!document.keywords.isNull()) {
            JSONArray keywords = new JSONArray();
            for (int i = 0; i < document.keywords.size(); i++) {
                keywords.put(i, document.keywords.get(i));
            }
            jDocument.put("keywords", keywords);
        }

        if (!document.tags.isNull()) {
            JSONArray tags = new JSONArray();
            for (int i = 0; i < document.tags.size(); i++) {
                tags.put(i, document.tags.get(i));
            }
            jDocument.put("tags", tags);
        }

        if (!document.authors.isNull()) {
            JSONArray authorsJson = personsToJson(document.authors);
            jDocument.put("authors", authorsJson);
        }

        if (!document.editors.isNull()) {
            JSONArray editors = new JSONArray();
            for (int i = 0; i < document.editors.size(); i++) {
                JSONObject editor = new JSONObject();
                editor.put("first_name", document.editors.get(i).firstName);
                editor.put("last_name", document.editors.get(i).lastName);
                editors.put(i, editor);
            }
            jDocument.put("editors", editors);
        }

        if (!document.identifiers.isNull()) {
            JSONObject identifiers = new JSONObject();
            for (String key : document.identifiers.keySet()) {
                identifiers.put(key, document.identifiers.get(key));
            }
            jDocument.put("identifiers", identifiers);
        }

        jDocument.put("title", document.title);
        jDocument.put("type", document.type);
        jDocument.put("id", document.id);

        if (document.lastModified != null) {
            jDocument.put("last_modified", DateUtils.formatMendeleyApiTimestamp(document.lastModified));
        }
        jDocument.put("group_id", document.groupId);
        jDocument.put("profile_id", document.profileId);
        jDocument.put("read", document.read);
        jDocument.put("starred", document.starred);
        jDocument.put("authored", document.authored);
        jDocument.put("confirmed", document.confirmed);
        jDocument.put("hidden", document.hidden);
        jDocument.put("month", document.month);
        jDocument.put("year", document.year);
        jDocument.put("day", document.day);
        jDocument.put("source", document.source);
        jDocument.put("revision", document.revision);
        jDocument.put("abstract", document.abstractString);
        if (document.created != null) {
            jDocument.put("created", DateUtils.formatMendeleyApiTimestamp(document.created));
        }
        jDocument.put("pages", document.pages);
        jDocument.put("notes", document.notes);
        jDocument.put("volume", document.volume);
        jDocument.put("issue", document.issue);
        jDocument.put("publisher", document.publisher);
        jDocument.put("city", document.city);
        jDocument.put("edition", document.edition);
        jDocument.put("institution", document.institution);
        jDocument.put("series", document.series);
        jDocument.put("chapter", document.chapter);
        jDocument.put("file_attached", document.fileAttached);
        jDocument.put("client_data", document.clientData);
        jDocument.put("unique_id", document.uniqueId);

        return jDocument;
    }

    public static JSONObject documentIdToJson(String documentId) throws JSONException {
        JSONObject jDocument = new JSONObject();
        jDocument.put("id", documentId);
        return jDocument;
    }

    public static List<String> documentsIdsFromJson(JsonReader reader) throws JSONException, IOException {
        final List<String> documentIds = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            documentIds.add(documentIdFromJson(reader));
        }
        reader.endArray();

        return documentIds;
    }

    public static String documentIdFromJson(JsonReader reader) throws JSONException, IOException {
        String id = null;

        reader.beginObject();

        while (reader.hasNext()) {

            final String key = reader.nextName();
            if (key.equals("id")) {
                id = reader.nextString();
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();

        return id;
    }

    public static List<File> filesFromJson(JsonReader reader) throws JSONException, IOException {

        final List<File> files = new ArrayList<File>();

        reader.beginArray();

        while (reader.hasNext()) {
            files.add(fileFromJson(reader));
        }

        reader.endArray();

        return files;
    }

    public static File fileFromJson(JsonReader reader) throws JSONException, IOException {
        reader.beginObject();

        final File.Builder builder = new File.Builder();

        while (reader.hasNext()) {

            String key = reader.nextName();
            if (key.equals("id")) {
                builder.setId(reader.nextString());

            } else if (key.equals("document_id")) {
                builder.setDocumentId(reader.nextString());

            } else if (key.equals("mime_type")) {
                builder.setMimeType(reader.nextString());

            } else if (key.equals("file_name")) {
                builder.setFileName(reader.nextString());

            } else if (key.equals("filehash")) {
                builder.setFileHash(reader.nextString());

            } else if (key.equals("size")) {
                builder.setFileSize(reader.nextInt());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return builder.build();
    }

    public static List<Folder> foldersFromJson(JsonReader reader) throws JSONException, IOException, ParseException {

        final List<Folder> folders = new ArrayList<Folder>();

        reader.beginArray();
        while (reader.hasNext()) {
            folders.add(folderFromJson(reader));
        }
        reader.endArray();

        return folders;
    }

    public static Folder folderFromJson(JsonReader reader) throws JSONException, IOException, ParseException {
        final Folder.Builder bld = new Folder.Builder();

        reader.beginObject();
        while (reader.hasNext()) {
            final String key = reader.nextName();

            if (key.equals("name")) {
                bld.setName(reader.nextString());
            } else if (key.equals("parent_id")) {
                bld.setParentId(reader.nextString());
            } else if (key.equals("id")) {
                bld.setId(reader.nextString());
            } else if (key.equals("group_id")) {
                bld.setGroupId(reader.nextString());
            } else if (key.equals("added")) {
                bld.setAdded(DateUtils.parseMendeleyApiTimestamp(reader.nextString()));
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return bld.build();
    }

    public static JSONObject folderToJson(Folder folder) throws JSONException {
        JSONObject jFolder = new JSONObject();

        jFolder.put("name", folder.name);
        jFolder.put("parent_id", folder.parentId);
        jFolder.put("id", folder.id);
        jFolder.put("group_id", folder.groupId);
        if (folder.added != null) {
            jFolder.put("added", DateUtils.formatMendeleyApiTimestamp(folder.added));
        }

        return jFolder;
    }

    public static JSONObject profileToJson(Profile profile, String password) throws JSONException {
        JSONObject jProfile = new JSONObject();

        jProfile.put("first_name", profile.firstName);
        jProfile.put("last_name", profile.lastName);
        jProfile.put("email", profile.email);
        if (password != null) {
            jProfile.put("password", password);
        }
        if (profile.discipline != null) {
            jProfile.put("discipline", profile.discipline.name);
        }
        jProfile.put("academic_status", profile.academicStatus);
        jProfile.put("marketing", profile.marketing);

        //TODO: format employments
        //TODO: format education

        return jProfile;
    }

    public static JSONObject profileToJsonAmendment(Profile profile) throws JSONException {
        JSONObject jProfile = new JSONObject();

        jProfile.put("first_name", profile.firstName);
        jProfile.put("last_name", profile.lastName);
        jProfile.put("title", profile.title);
        jProfile.put("academic_status", profile.academicStatus);
        if (profile.institutionDetails != null && !TextUtils.isEmpty(profile.institutionDetails.id)) {
            jProfile.put("institution_id", profile.institutionDetails.id);
        }

        return jProfile;
    }


    public static List<Group> groupsFromJson(JsonReader reader) throws JSONException, IOException, ParseException {
        final List<Group> groups = new ArrayList<Group>();
        reader.beginArray();

        while (reader.hasNext()) {
            groups.add(groupFromJson(reader));
        }

        reader.endArray();
        return groups;
    }

    public static Group groupFromJson(JsonReader reader) throws JSONException, IOException, ParseException {
        final Group.Builder builder = new Group.Builder();
        reader.beginObject();

        while (reader.hasNext()) {

            final String key = reader.nextName();

            if (key.equals("id")) {
                builder.setId(reader.nextString());

            } else if (key.equals("created")) {
                builder.setCreated(DateUtils.parseMendeleyApiTimestamp(reader.nextString()));

            } else if (key.equals("owning_profile_id")) {
                builder.setOwningProfileId(reader.nextString());

            } else if (key.equals("link")) {
                builder.setLink(reader.nextString());

            } else if (key.equals("role")) {
                builder.setRole(Group.Role.fromValue(reader.nextString()));

            } else if (key.equals("access_level")) {
                builder.setAccessLevel(Group.AccessLevel.fromValue(reader.nextString()));

            } else if (key.equals("name")) {
                builder.setName(reader.nextString());

            } else if (key.equals("description")) {
                builder.setDescription(reader.nextString());

            } else if (key.equals("tags")) {
                builder.setTags(stringListFromJson(reader));

            } else if (key.equals("webpage")) {
                builder.setWebpage(reader.nextString());

            } else if (key.equals("disciplines")) {
                builder.setDisciplines(stringListFromJson(reader));

            } else if (key.equals("photo")) {
                builder.setPhoto(groupPhotoFromJson(reader));

            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
        return builder.build();
    }

    public static List<Annotation> annotationsFromJson(JsonReader reader) throws JSONException, IOException, ParseException {
        final List<Annotation> annotations = new ArrayList<Annotation>();
        reader.beginArray();

        while (reader.hasNext()) {
            annotations.add(annotationFromJson(reader));
        }

        reader.endArray();
        return annotations;
    }

    public static Annotation annotationFromJson(JsonReader reader) throws JSONException, IOException, ParseException {
        final Annotation.Builder builder = new Annotation.Builder();

        reader.beginObject();

        while (reader.hasNext()) {
            final String key = reader.nextName();

            if (key.equals("id")) {
                builder.setId(reader.nextString());

            } else if (key.equals("type")) {
                builder.setType(Annotation.Type.fromName(reader.nextString()));

            } else if (key.equals("previous_id")) {
                builder.setPreviousId(reader.nextString());

            } else if (key.equals("color")) {
                builder.setColor(colorFromJson(reader));

            } else if (key.equals("text")) {
                builder.setText(reader.nextString());

            } else if (key.equals("profile_id")) {
                builder.setProfileId(reader.nextString());

            } else if (key.equals("positions")) {
                builder.setPositions(positionsFromJson(reader));

            } else if (key.equals("created")) {
                builder.setCreated(DateUtils.parseMendeleyApiTimestamp(reader.nextString()));

            } else if (key.equals("last_modified")) {
                builder.setLastModified(DateUtils.parseMendeleyApiTimestamp(reader.nextString()));

            } else if (key.equals("privacy_level")) {
                builder.setPrivacyLevel(PrivacyLevel.fromName(reader.nextString()));

            } else if (key.equals("filehash")) {
                builder.setFileHash(reader.nextString());

            } else if (key.equals("document_id")) {
                builder.setDocumentId(reader.nextString());
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
        return builder.build();
    }

    public static JSONObject annotationToJson(Annotation annotation) throws JSONException {
        JSONObject jAnnotation = new JSONObject();

        jAnnotation.put("id", annotation.id);
        if (annotation.type != null) {
            jAnnotation.put("type", annotation.type.name);
        }
        jAnnotation.put("previous_id", annotation.previousId);
        if (annotation.color != null) {
            jAnnotation.put("color", colorToJson(annotation.color));
        }
        jAnnotation.put("text", annotation.text);
        jAnnotation.put("profile_id", annotation.profileId);

        if (!annotation.positions.isNull()) {
            JSONArray positions = new JSONArray();
            for (int i = 0; i < annotation.positions.size(); i++) {
                Annotation.Position position = annotation.positions.get(i);
                positions.put(i, positionToJson(position));
            }
            jAnnotation.put("positions", positions);
        }

        if (annotation.created != null) {
            jAnnotation.put("created", DateUtils.formatMendeleyApiTimestamp(annotation.created));
        }
        if (annotation.lastModified != null) {
            jAnnotation.put("last_modified", DateUtils.formatMendeleyApiTimestamp(annotation.lastModified));
        }
        if (annotation.privacyLevel != null) {
            jAnnotation.put("privacy_level", annotation.privacyLevel.name);
        }
        jAnnotation.put("filehash", annotation.fileHash);
        jAnnotation.put("document_id", annotation.documentId);

        return jAnnotation;
    }

    public static List<ReadPosition> readPositionsFromJson(JsonReader reader) throws JSONException, ParseException, IOException {
        final List<ReadPosition> readPositions = new LinkedList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            readPositions.add(readPositionFromJson(reader));
        }
        reader.endArray();
        return readPositions;
    }


    public static ReadPosition readPositionFromJson(JsonReader reader) throws JSONException, ParseException, IOException {
        final ReadPosition.Builder bld = new ReadPosition.Builder();
        reader.beginObject();

        while (reader.hasNext()) {
            final String key = reader.nextName();

            if (key.equals("id")) {
                bld.setId(reader.nextString());

            } else if (key.equals("file_id")) {
                bld.setFileId(reader.nextString());

            } else if (key.equals("page")) {
                bld.setPage(reader.nextInt());

            } else if (key.equals("vertical_position")) {
                bld.setVerticalPosition((float) reader.nextDouble());

            } else if (key.equals("date")) {
                bld.setDate(DateUtils.parseMendeleyApiTimestamp(reader.nextString()));
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
        return bld.build();
    }


    public static JSONObject readPositionToJson(ReadPosition readPosition) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", readPosition.id);
        jsonObject.put("file_id", readPosition.fileId);
        jsonObject.put("page", readPosition.page);
        jsonObject.put("vertical_position", readPosition.verticalPosition);
        jsonObject.put("date", DateUtils.formatMendeleyApiTimestamp(readPosition.date));

        return jsonObject;
    }

    public static ArrayList<Person> personsFromJson(JsonReader reader) throws JSONException, IOException {
        final ArrayList<Person> authorsList = new ArrayList<Person>();

        reader.beginArray();
        while (reader.hasNext()) {
            final Person author = personFromJson(reader);
            authorsList.add(author);
        }
        reader.endArray();
        return authorsList;
    }

    private static Person personFromJson(JsonReader reader) throws IOException {
        reader.beginObject();

        String authorName = null;
        String authorLastName = null;

        while (reader.hasNext()) {
            final String key = reader.nextName();
            if ("first_name".equals(key)) {
                authorName = reader.nextString();
            } else if ("last_name".equals(key)) {
                authorLastName = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return new Person(authorName, authorLastName);
    }

    public static JSONArray personsToJson(List<Person> persons) throws JSONException {
        JSONArray authorsJson = new JSONArray();
        for (int i = 0; i < persons.size(); i++) {
            JSONObject author = new JSONObject();
            author.put("first_name", persons.get(i).firstName);
            author.put("last_name", persons.get(i).lastName);
            authorsJson.put(i, author);
        }
        return authorsJson;
    }

    private static List<Annotation.Position> positionsFromJson(JsonReader reader) throws JSONException, IOException {
        final List<Annotation.Position> positions = new ArrayList<Annotation.Position>();

        reader.beginArray();
        while (reader.hasNext()) {
            positions.add(positionFromJson(reader));
        }
        reader.endArray();

        return positions;
    }

    private static Annotation.Position positionFromJson(JsonReader reader) throws JSONException, IOException {
        Point topLeft = null;
        Point bottomRight = null;
        Integer page = null;

        reader.beginObject();

        while (reader.hasNext()) {
            final String key = reader.nextName();

            if (key.equals("page")) {
                page = reader.nextInt();
            } else if (key.equals("top_left")) {
                topLeft = pointFromJson(reader);
            } else if (key.equals("bottom_right")) {
                bottomRight = pointFromJson(reader);
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
        return new Annotation.Position(topLeft, bottomRight, page);
    }

    private static JSONObject positionToJson(Annotation.Position position) throws JSONException {
        JSONObject topLeft = null;
        JSONObject bottomRight = null;

        if (position.topLeft != null) {
            topLeft = new JSONObject();
            topLeft.put("x", position.topLeft.x);
            topLeft.put("y", position.topLeft.y);
        }
        if (position.bottomRight != null) {
            bottomRight = new JSONObject();
            bottomRight.put("x", position.bottomRight.x);
            bottomRight.put("y", position.bottomRight.y);
        }

        JSONObject bbox = new JSONObject();
        bbox.put("top_left", topLeft);
        bbox.put("bottom_right", bottomRight);
        bbox.put("page", position.page);
        return bbox;
    }


    private static Point pointFromJson(JsonReader reader) throws IOException {
        double x = 0;
        double y = 0;

        reader.beginObject();
        while (reader.hasNext()) {
            final String key = reader.nextName();

            if (key.equals("x")) {
                x = reader.nextDouble();
            } else if (key.equals("y")) {
                y = reader.nextDouble();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return new Point(x, y);
    }

    private static int colorFromJson(JsonReader reader) throws JSONException, IOException {
        reader.beginObject();

        int r = 0;
        int g = 0;
        int b = 0;

        while (reader.hasNext()) {
            final String key = reader.nextName();

            if (key.equals("r")) {
                r = reader.nextInt();
            } else if (key.equals("g")) {
                g = reader.nextInt();
            } else if (key.equals("b")) {
                b = reader.nextInt();
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
        return Color.rgb(r, g, b);
    }


    private static JSONObject colorToJson(int color) throws JSONException {
        JSONObject jColor = new JSONObject();
        jColor.put("r", Color.red(color));
        jColor.put("g", Color.green(color));
        jColor.put("b", Color.blue(color));
        return jColor;
    }


    private static void appendDocumentTypeFromJson(JsonReader reader, Map<String, String> map) throws IOException {
        reader.beginObject();

        String nameValue = null;
        String descriptionValue = null;

        while (reader.hasNext()) {
            final String key = reader.nextName();
            if (key.equals("name")) {
                nameValue = reader.nextString();
            } else if (key.equals("description")) {
                descriptionValue = reader.nextString();
            } else {
                reader.skipValue();
            }
        }

        map.put(nameValue, descriptionValue);

        reader.endObject();
    }

    private static List<AlternativeName> alternativeNamesFromJson(JsonReader reader) throws IOException, JSONException, ParseException {
        final List<AlternativeName> list = new LinkedList<>();
        reader.beginArray();

        while (reader.hasNext()) {
            list.add(alternativeNameFromJson(reader));
        }

        reader.endArray();
        return list;
    }

    private static AlternativeName alternativeNameFromJson(JsonReader reader) throws IOException {
        final AlternativeName alternativeName = new AlternativeName();
        reader.beginObject();

        while (reader.hasNext()) {
            final String key = reader.nextName();
            if ("name".equals(key)) {
                alternativeName.name = reader.nextString();
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
        return alternativeName;
    }

    private static Discipline disciplineFromJson(JsonReader reader) throws IOException {
        final Discipline discipline = new Discipline();
        reader.beginObject();

        while (reader.hasNext()) {
            final String key = reader.nextName();
            if ("name".equals(key)) {
                discipline.name = reader.nextString();
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
        return discipline;
    }

    private static List<Employment> employmentsFromJson(JsonReader reader) throws IOException, JSONException, ParseException {
        final List<Employment> list = new LinkedList<>();
        reader.beginArray();

        while (reader.hasNext()) {
            list.add(employmentFromJson(reader));
        }

        reader.endArray();
        return list;
    }

    private static List<Editorship> editorshipsFromJsonFromJson(JsonReader reader) throws IOException, JSONException, ParseException {
        final List<Editorship> list = new LinkedList<>();
        reader.beginArray();

        while (reader.hasNext()) {
            list.add(editorshipFromJson(reader));
        }

        reader.endArray();
        return list;
    }

    public static List<Institution> institutionsFromJson(JsonReader reader) throws IOException, JSONException, ParseException {
        final List<Institution> list = new LinkedList<>();
        reader.beginArray();

        while (reader.hasNext()) {
            list.add(institutionFromJson(reader));
        }

        reader.endArray();
        return list;
    }

    public static Employment employmentFromJson(JsonReader reader) throws JSONException, IOException, ParseException {
        final Employment.Builder builder = new Employment.Builder();
        reader.beginObject();

        while (reader.hasNext()) {

            final String key = reader.nextName();

            if (key.equals("id")) {
                builder.setId(reader.nextString());

            } else if (key.equals("institution_details")) {
                builder.setInstitution(institutionFromJson(reader));

            } else if (key.equals("position")) {
                builder.setPosition(reader.nextString());

            } else if (key.equals("start_date")) {
                builder.setStartDate(DateUtils.parseYearMonthDayDate(reader.nextString()));

            } else if (key.equals("end_date")) {
                builder.setEndDate(DateUtils.parseYearMonthDayDate(reader.nextString()));

            } else if (key.equals("website")) {
                builder.setWebsite(reader.nextString());

            } else if (key.equals("is_main_employment")) {
                builder.setIsMainEmployment(reader.nextBoolean());

            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
        return builder.build();
    }

    public static Editorship editorshipFromJson(JsonReader reader) throws JSONException, IOException, ParseException {
        final Editorship.Builder builder = new Editorship.Builder();
        reader.beginObject();

        while (reader.hasNext()) {

            final String key = reader.nextName();

            if (key.equals("id")) {
                builder.setId(reader.nextString());

            } else if (key.equals("created")) {
                builder.setCreated(DateUtils.parseYearMonthDayDate(reader.nextString()));

            } else if (key.equals("position")) {
                builder.setPosition(reader.nextString());

            } else if (key.equals("journal")) {
                builder.setJournal(reader.nextString());

            } else if (key.equals("start_date")) {
                builder.setStartDate(DateUtils.parseYearMonthDayDate(reader.nextString()));

            } else if (key.equals("end_date")) {
                builder.setEndDate(DateUtils.parseYearMonthDayDate(reader.nextString()));

            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
        return builder.build();
    }

    public static Institution institutionFromJson(JsonReader reader) throws JSONException, IOException, ParseException {
        final Institution.Builder builder = new Institution.Builder();
        reader.beginObject();

        while (reader.hasNext()) {

            final String key = reader.nextName();

            if (key.equals("scival_id")) {
                builder.setScivalId(reader.nextInt());

            } else if (key.equals("id")) {
                builder.setId(reader.nextString());

            } else if (key.equals("parent_id")) {
                builder.setParentId(reader.nextString());

            } else if (key.equals("name")) {
                builder.setName(reader.nextString());

            } else if (key.equals("city")) {
                builder.setCity(reader.nextString());

            } else if (key.equals("state")) {
                builder.setState(reader.nextString());

            } else if (key.equals("country")) {
                builder.setCountry(reader.nextString());

            } else if (key.equals("parent_id")) {
                builder.setParentId(reader.nextString());

            } else if (key.equals("urls")) {
                builder.setUrls(stringListFromJson(reader));

            } else if (key.equals("profile_url")) {
                builder.setProfilerUrl(reader.nextString());

            } else if (key.equals("alt_names")) {
                builder.setAltNames(alternativeNamesFromJson(reader));

            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
        return builder.build();
    }

    private static List<Education> educationsFromJson(JsonReader reader) throws IOException, JSONException, ParseException {
        final List<Education> list = new LinkedList<>();
        reader.beginArray();

        while (reader.hasNext()) {
            list.add(educationFromJson(reader));
        }

        reader.endArray();
        return list;
    }

    public static Education educationFromJson(JsonReader reader) throws JSONException, IOException, ParseException {
        final Education.Builder builder = new Education.Builder();

        reader.beginObject();

        while (reader.hasNext()) {
            final String key = reader.nextName();
            if (key.equals("id")) {
                builder.setId(reader.nextString());
            } else if (key.equals("degree")) {
                builder.setDegree(reader.nextString());
            } else if (key.equals("institution_details")) {
                builder.setInstitution(institutionFromJson(reader));
            } else if (key.equals("start_date")) {
                builder.setStartDate(DateUtils.parseYearMonthDayDate(reader.nextString()));
            } else if (key.equals("end_date")) {
                builder.setEndDate(DateUtils.parseYearMonthDayDate(reader.nextString()));
            } else if (key.equals("website")) {
                builder.setWebsite(reader.nextString());
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
        return builder.build();
    }

    public static List<UserRole> groupUserRolesFromJson(JsonReader reader) throws JSONException, IOException {
        final List<UserRole> userRoles = new ArrayList<UserRole>();
        reader.beginArray();

        while (reader.hasNext()) {
            userRoles.add(groupUserRoleFromJson(reader));
        }

        reader.endArray();
        return userRoles;
    }

    public static UserRole groupUserRoleFromJson(JsonReader reader) throws JSONException, IOException {
        final UserRole.Builder mendeleyUserRole = new UserRole.Builder();
        reader.beginObject();

        while (reader.hasNext()) {
            final String key = reader.nextName();

            if (key.equals("profile_id")) {
                mendeleyUserRole.setProfileId(reader.nextString());
            } else if (key.equals("joined")) {
                mendeleyUserRole.setJoined(reader.nextString());
            } else if (key.equals("role")) {
                mendeleyUserRole.setRole(reader.nextString());
            } else {
                reader.skipValue();
            }

        }

        reader.endObject();
        return mendeleyUserRole.build();
    }

    private static List<String> stringListFromJson(JsonReader reader) throws IOException {
        List<String> list = new LinkedList<String>();

        reader.beginArray();
        while (reader.hasNext()) {
            list.add(reader.nextString());
        }
        reader.endArray();
        return list;
    }

    public static Map<String, String> stringsMapFromJson(JsonReader reader) throws JSONException, IOException {
        final Map<String, String> typesMap = new HashMap<String, String>();

        reader.beginArray();

        while (reader.hasNext()) {
            appendDocumentTypeFromJson(reader, typesMap);
        }

        reader.endArray();
        return typesMap;
    }


    public static List<String> subjectAreasFromJson(JsonReader reader) throws JSONException, IOException {
        final List<String> subjectAreas = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            subjectAreas.add(stringValueFromJson(reader, "name"));
        }
        reader.endArray();

        return subjectAreas;
    }

    public static List<String> userRolesFromJson(JsonReader reader) throws JSONException, IOException {
        final List<String> subjectAreas = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            subjectAreas.add(stringValueFromJson(reader, "description"));
        }
        reader.endArray();

        return subjectAreas;
    }

    public static String stringValueFromJson(JsonReader reader, String keyString) throws JSONException, IOException {
        String value = null;
        reader.beginObject();
        while (reader.hasNext()) {

            final String key = reader.nextName();
            if (key.equals(keyString)) {
                value = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return value;
    }


}
