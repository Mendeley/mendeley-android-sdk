package com.mendeley.sdk;

import android.net.Uri;

import com.mendeley.sdk.model.Annotation;
import com.mendeley.sdk.model.Document;
import com.mendeley.sdk.model.Employment;
import com.mendeley.sdk.model.File;
import com.mendeley.sdk.model.Folder;
import com.mendeley.sdk.model.Group;
import com.mendeley.sdk.model.Profile;
import com.mendeley.sdk.model.ReadPosition;
import com.mendeley.sdk.model.UserRole;
import com.mendeley.sdk.request.endpoint.AnnotationsEndpoint;
import com.mendeley.sdk.request.endpoint.DocumentEndpoint;
import com.mendeley.sdk.request.endpoint.FilesEndpoint;
import com.mendeley.sdk.request.endpoint.FoldersEndpoint;
import com.mendeley.sdk.request.endpoint.GroupsEndpoint;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Factory for creating typical {@link Request}s to interact against the Mendeley API.
 *
 * <p/>
 *
 * While apps using the Mendeley SDK may obtain their {@link Request}s using this factory, it is
 * still possible to instantiate them directly using their public constructor.
 */
public interface RequestsFactory {

    /**
     * Obtains a {@link Request} to retrieve the {@link Profile} of the signed in user.
     *
     * @return the request
     */
    Request<Profile> newGetMyProfileRequest();

    /**
     * Obtains a {@link Request} to patch the {@link Profile} of the signed in user.
     *
     * @param profile the profile to patch
     * @return the request
     */
    Request<Profile> newPatchMeProfileRequest(Profile profile);

    /**
     * Obtains a {@link Request} to retrieve the {@link Profile} of a user given the id.
     *
     * @param profileId id of the profile to get
     * @return the request
     */
    Request<Profile> newGetProfileRequest(String profileId);

    /**
     * Obtains a {@link Request} to create a new {@link Profile}.
     *
     * @param profile the profile to create
     * @param password password for the new user
     * @return the request
     */
    Request<Profile> newPostProfileRequest(Profile profile, String password);


    /**
     * Obtains a {@link Request} to delete an existing profile
     *
     * @param profileId the id of the profile to delete
     * @return the request
     */
    Request<Void> newDeleteProfileRequest(String profileId);

    /**
     * Obtains a {@link Request} to retrieve the list of valid document types. This is, the
     * possible values for {@link Document#type}
     *
     * @return the request
     */
    Request<Map<String, String>> newGetDocumentTypesRequest();

    /**
     * Obtains a {@link Request} to retrieve the list of valid document identifies. This is, the
     * possible values for {@link Document#identifiers}
     *
     * @return the request
     */
    Request<Map<String, String>> newGetDocumentIdentifierTypesRequest();

    /**
     * Obtains a {@link Request} to retrieve the list of {@link Document}s.
     *
     * @param parameters used  to configure the query. Can be null.
     * @return the requests
     */
    Request<List<Document>> newGetDocumentsRequest(DocumentEndpoint.DocumentRequestParameters parameters);

    /**
     * Obtains a {@link Request} to retrieve the list of {@link Document}s.
     *
     * @param url the URL of the request.
     *            May be the {@link Request.Response#next} field of a previous request.
     * @return the request
     */
    Request<List<Document>> newGetDocumentsRequest(Uri url);

    /**
     * Obtains a {@link Request} to retrieve one single {@link Document} by its id.
     *
     * @param documentId the id of the document to get
     * @param view used to configure which fields the server will return
     * @return the request
     */
    Request<Document> newGetDocumentRequest(String documentId, DocumentEndpoint.DocumentRequestParameters.View view);

    /**
     * Obtains a {@link Request} to create a new {@link Document} in the user's library.
     *
     * @param document the document to create
     * @return the request
     */
    Request<Document> newPostDocumentRequest(Document document);

    /**
     * Obtains a {@link Request} to update an existing document in the user's library.
     *
     * @param documentId the id of the document to be updated.
     * @param date sets an optional "if unmodified since" condition on the request. Ignored if null.
     * @param document a document object containing the fields to be updated.
     *                 Missing fields are left unchanged (not cleared).
     * @return the request
     */
    Request<Document> newPatchDocumentRequest(String documentId, Date date, Document document);

    /**
     * Obtains a {@link Request} to move an existing document to the trash of the user's library.
     *
     * @param documentId the id of the document to trash
     * @return the request
     */
    Request<Void> newTrashDocumentRequest(String documentId);

    /**
     * Obtains a {@link Request} to permanently delete an existing document which is NOT in the trash.
     *
     * @param documentId the id of the document to delete
     * @return the request
     */
    Request<Void> newDeleteDocumentRequest(String documentId);

    /**
     * Obtains a {@link Request} to permanently delete an existing document which is in the trash.
     *
     * @param documentId the id of the document to delete
     * @return the request
     */
    Request<Void> newDeleteTrashedDocumentRequest(String documentId);

    /**
     * Obtains a {@link Request} to retrieve the list of {@link Document}s in the trash of the
     * user's library.
     *
     * @param parameters used to configure the query. Can be null.
     * @return the requests
     */
    Request<List<Document>> newGetTrashedDocumentsRequest(DocumentEndpoint.DocumentRequestParameters parameters);

    /**
     * Obtains a {@link Request} to retrieve the list of {@link Document}s in the trash.
     *
     * @param uri the URL of the request.
     *            May be the {@link Request.Response#next} field of a previous request.
     * @return the request
     */
    Request<List<Document>> newGetTrashedDocumentsRequest(Uri uri);

    /**
     * Obtains a {@link Request} to restore one specific {@link Document} from the trash.
     *
     * @param documentId id of the document to restore.
     */
    Request<Void> newRestoreTrashedDocumentRequest(String documentId);

    /**
     * Obtains a {@link Request} to get a list of the @{link File}s in the user's library.
     *
     * @param parameters used  to configure the query. Can be null.
     * @return the request
     */
    Request<List<File>> newGetFilesRequest(FilesEndpoint.FileRequestParameters parameters);

    /**
     *
     * Obtains a {@link Request} to get a list of the @{link File}s in the user's library.
     *
     * @param uri the URL of the request.
     *            May be the {@link Request.Response#next} field of a previous request.
     * @return the request
     */
    Request<List<File>> newGetFilesRequest(Uri uri);

    /**
     * Obtains a {@link Request} to download the data related to a {@link File}. Normally,
     * this is the pdf file that belongs to the {@link File}.
     *
     * @param fileId the id of the file
     * @param targetFile the {@link File} in the file system where the data will be saved to
     * @return the request
     */
    FilesEndpoint.GetFileBinaryRequest newGetFileBinaryRequest(String fileId, java.io.File targetFile);

    /**
     *
     * Obtains a {@link Request} to create a {@link File} in the server linked to the data posted
     * by the request.
     *
     * @param contentType the content type of the data to be posted
     * @param documentId the id of the {@link Document} the created file will belong to
     * @param inputStream used to read the data posted to the server.
     * @param fileName the name of the file.
     * @return the request
     */
    Request<File> newPostFileWithBinaryRequest(String contentType, String documentId, InputStream inputStream, String fileName);

    /**
     * Obtains a {@link Request} to delete the {@link File} with the passed id.
     *
     * @param fileId the id of the file to delete
     * @return the request
     */
    Request<Void> newDeleteFileRequest(String fileId);

    /**
     * Obtains a {@link Request} to get the list of {@link Folder}s in the user's library.
     *
     * @param parameters used to configure the query. Can be null.
     * @return the request
     */
    Request<List<Folder>> newGetFoldersRequest(FoldersEndpoint.FolderRequestParameters parameters);

    /**
     * Obtains a {@link Request} to get the list of {@link Folder}s in the user's library.
     *
     * @param uri the URL of the request.
     *            May be the {@link Request.Response#next} field of a previous request.
     * @return the request
     */
    Request<List<Folder>> newGetFoldersRequest(Uri uri);

    /**
     * Obtains a {@link Request} to get one existing {@link Folder} with the passed id.
     *
     * @param folderId the id of the folder
     * @return the request
     */
    Request<Folder> newGetFolderRequest(String folderId);

    /**
     * Obtains a {@link Request} to create a new {@link Folder} in the user's library.
     *
     * @param folder the folder to create
     * @return the request
     */
    Request<Folder> newPostFolderRequest(Folder folder);

    /**
     * Obtains a {@link Request} to update an existing folder in the user's library.
     *
     * @param folderId the id of the folder to be updated.
     * @param folder a folder object containing the fields to be updated.
     *                 Missing fields are left unchanged (not cleared).
     * @return the request
     */
    Request<Folder> newPatchFolderRequest(String folderId, Folder folder);

    /**
     * Obtains a {@link Request} to permanently delete an existing {@link Folder}
     *
     * @param folderId the id of the document to delete
     * @return the request
     */
    Request<Void> newDeleteFolderRequest(String folderId);

    /**
     * Obtains a {@link Request} to retrieve the content of one specific {@link Folder} in the form
     * of the ids of the {@link Document}s in it
     *
     * @param parameters used to configure the query. Can be null.
     * @param folderId the id of the folder to query
     * @return the request
     */
    Request<List<String>> newGetFolderDocumentsRequest(FoldersEndpoint.FolderRequestParameters parameters, String folderId);

    /**
     * Obtains a {@link Request} to retrieve the content of one specific {@link Folder} in the form
     * of the ids of the {@link Document}s in it
     *
     * @param uri the URL of the request.
     *            May be the {@link Request.Response#next} field of a previous request.
     * @return the request
     */
    Request<List<String>> newGetFolderDocumentsRequest(Uri uri);

    /**
     * Obtains a {@link Request} to insert one document into one folder.
     *
     * @param folderId the id the folder.
     * @param documentId the id of the document to be added to the folder.
     * @return the request
     */
    Request<Void> newPostDocumentToFolderRequest(String folderId, String documentId);

    /**
     * Obtains a {@link Request} to delete one document from one folder.
     * This just deletes the document from the folder, but the document won't be deleted and will
     * continue to exist in other folders it might be.
     *
     * @param folderId the id of the folder
     * @param documentId the id of the document
     * @return the request
     */
    Request<Void> newDeleteDocumentFromFolderRequest(String folderId, String documentId);

    /**
     * Obtains a {@link Request} to retrieve the list of {@link Group}s in the user library.
     *
     * @param parameters used  to configure the query. Can be null.
     * @return the request
     */
    Request<List<Group>> newGetGroupsRequest(GroupsEndpoint.GroupRequestParameters parameters);

    /**
     * Obtains a {@link Request} to retrieve the list of {@link Group}s in the user library.
     *
     * @param uri the URL of the request.
     *            May be the {@link Request.Response#next} field of a previous request.
     * @return the request
     */
    Request<List<Group>> newGetGroupsRequest(Uri uri);

    /**
     * Obtains a {@link Request} to retrieve one single {@link Group} by its id.
     *
     * @param groupId the id of the group to retrieve
     */
    Request<Group> newGetGroupRequest(String groupId);

    /**
     * Obtains a {@link Request} to retrieve the list of the members in one specific {@link Group}
     *
     * @param parameters used  to configure the query. Can be null.
     * @param groupId the id of the group whose members are being retrieved
     * @return the request
     */
    Request<List<UserRole>> newGetGroupMembersRequest(GroupsEndpoint.GroupRequestParameters parameters, String groupId);

    /**
     * Obtains a {@link Request} to retrieve the list of the members in one specific {@link Group}
     *
     * @param url the URL of the request.
     *            May be the {@link Request.Response#next} field of a previous request.
     * @return the request
     */
    Request<List<UserRole>> newGetGroupMembersRequest(Uri url);

    /**
     * Obtains a {@link Request} to retrieve the list of {@link Annotation}s.
     *
     * @param parameters used to configure the query. Can be null.
     * @return the requests
     */
    Request<List<Annotation>> newGetAnnotationsRequest(AnnotationsEndpoint.AnnotationRequestParameters parameters);

    /**
     * Obtains a {@link Request} to retrieve the list of {@link Annotation}s.
     *
     * @param url the URL of the request.
     *            May be the {@link Request.Response#next} field of a previous request.
     * @return the request
     */
    Request<List<Annotation>> newGetAnnotationsRequest(Uri url);

    /**
     * Obtains a {@link Request} to retrieve one single {@link Annotation} by its id.
     *
     * @param annotationId the id of the annotation to get
     * @return the request
     */
    Request<Annotation> newGetAnnotationRequest(String annotationId);

    /**
     * Obtains a {@link Request} to create a new {@link Annotation} in the user's library.
     *
     * @param annotation the annotation to create
     * @return the request
     */
    Request<Annotation> newPostAnnotationRequest(Annotation annotation);

    /**
     * Obtains a {@link Request} to update an existing {@link Annotation} in the user's library.
     *
     * @param annotationId the id of the annotation to be updated.
     * @param annotation an annotation object containing the fields to be updated.
     *                 Missing fields are left unchanged (not cleared).
     * @return the request
     */
    Request<Annotation> newPatchAnnotationRequest(String annotationId, Annotation annotation);

    /**
     * Obtains a {@link Request} to delete the {@link Annotation} with the passed id.
     *
     * @param annotationId the id of the annotation to delete
     * @return the request
     */
    Request<Void> newDeleteAnnotationRequest(String annotationId);

    /**
     * Obtains a {@link Request} to retrieve the last {@link ReadPosition}s for some files
     *
     * @param groupId id of the group of the documents of the files of the queried read positions.
     *                If null it will return only the ones in the user's library.
     * @param fileId id of the file of which read position we want.
     *               If null it will return positions for all the files in the specified group.
     * @param limit maximum number of entries to get
     * @return the request
     */
    Request<List<ReadPosition>> newGetRecentlyReadRequest(String groupId, String fileId, int limit);

    /**
     * Obtains a {@link Request} to post the new {@link ReadPosition}.
     *
     * @param readPosition the readPosition to create
     * @return the request
     */
    Request<ReadPosition> newPostRecentlyReadRequest(ReadPosition readPosition);

    /**
     * Obtains a {@link Request} to retrieve the list of subject areas.
     *
     * @return the request
     */
    Request<List<String>> newGetSubjectAreasRequest();

    /**
     * Obtains a {@link Request} to retrieve the list of user roles.
     *
     * @return the request
     */
    Request<List<String>> newGetUserRolesRequest();

    /**
     * Obtains a {@link Document} if found in the catalog with the given catalog id
     *
     * @return the request
     */
    Request<List<Document>> newGetCatalogDocument(String identifier, String value);

    /**
     * Obtains a {@link Document} if found in the catalog with the given document id
     *
     * @return the request
     */
    Request<List<Document>> newGetCatalogDocument(String documentId);


}
