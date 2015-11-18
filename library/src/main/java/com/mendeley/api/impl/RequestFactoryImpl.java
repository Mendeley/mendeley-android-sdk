package com.mendeley.api.impl;

import com.mendeley.api.AuthTokenManager;
import com.mendeley.api.ClientCredentials;
import com.mendeley.api.model.Annotation;
import com.mendeley.api.model.Document;
import com.mendeley.api.model.File;
import com.mendeley.api.model.Folder;
import com.mendeley.api.model.Group;
import com.mendeley.api.model.Profile;
import com.mendeley.api.model.ReadPosition;
import com.mendeley.api.model.UserRole;
import com.mendeley.api.request.GetFileNetworkRequest;
import com.mendeley.api.request.PostFileAuthorizedRequest;
import com.mendeley.api.request.Request;
import com.mendeley.api.request.endpoint.AnnotationsEndpoint;
import com.mendeley.api.request.endpoint.ApplicationFeaturesEndpoint;
import com.mendeley.api.request.endpoint.CatalogEndpoint;
import com.mendeley.api.request.endpoint.DocumentEndpoint;
import com.mendeley.api.request.endpoint.DocumentIdentifiersEndpoint;
import com.mendeley.api.request.endpoint.DocumentTypesEndpoint;
import com.mendeley.api.request.endpoint.FilesEndpoint;
import com.mendeley.api.request.endpoint.FolderEndpoint;
import com.mendeley.api.request.endpoint.GroupsEndpoint;
import com.mendeley.api.request.endpoint.ProfilesEndpoint;
import com.mendeley.api.request.endpoint.RecentlyReadEndpoint;
import com.mendeley.api.request.endpoint.TrashEndpoint;
import com.mendeley.api.request.params.AnnotationRequestParameters;
import com.mendeley.api.request.params.CatalogDocumentRequestParameters;
import com.mendeley.api.request.params.DocumentRequestParameters;
import com.mendeley.api.request.params.FileRequestParameters;
import com.mendeley.api.request.params.FolderRequestParameters;
import com.mendeley.api.request.params.GroupRequestParameters;
import com.mendeley.api.request.params.Page;
import com.mendeley.api.request.params.View;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the blocking API calls.
 */
public class RequestFactoryImpl implements RequestsFactory {

    public static final String TAG = RequestFactoryImpl.class.getSimpleName();

    private final ClientCredentials clientCredentials;
    private final AuthTokenManager authTokenManager;

    public RequestFactoryImpl(AuthTokenManager authTokenManager, ClientCredentials clientCredentials) {
        this.authTokenManager = authTokenManager;
        this.clientCredentials = clientCredentials;
    }

    /* DOCUMENTS */

    @Override
    public Request<List<Document>> getDocuments() {
        return getDocuments((DocumentRequestParameters) null);
    }

    @Override
    public Request<List<Document>> getDocuments(DocumentRequestParameters parameters) {
        return new DocumentEndpoint.GetDocumentsRequest(parameters, false, authTokenManager, clientCredentials);
    }

    @Override
    public Request<List<Document>> getDocuments(Page next) {
        return new DocumentEndpoint.GetDocumentsRequest(next.link, authTokenManager, clientCredentials);
    }

    @Override
    public Request<Document> getDocument(String documentId, View view) {
        return new DocumentEndpoint.GetDocumentRequest(documentId, view, authTokenManager, clientCredentials);
    }

    @Override
    public Request<List<String>> getDeletedDocuments(String deletedSince, DocumentRequestParameters parameters)  {
        return new DocumentEndpoint.GetDeletedDocumentsRequest(parameters, deletedSince, authTokenManager, clientCredentials);
    }

    @Override
    public Request<List<String>> getDeletedDocuments(Page next) {
        return new DocumentEndpoint.GetDeletedDocumentsRequest(next.link, authTokenManager, clientCredentials);
    }

    @Override
    public Request<Document> postDocument(Document document) {
        return new DocumentEndpoint.PostDocumentRequest(document, authTokenManager, clientCredentials);
    }


    @Override
    public Request<Document> patchDocument(String documentId, Date date, Document document) {
        return new DocumentEndpoint.PatchDocumentAuthorizedRequest(documentId, document, date, authTokenManager, clientCredentials);
    }

    @Override
    public Request<Void> trashDocument(String documentId) {
        return new DocumentEndpoint.TrashDocumentRequest(documentId, authTokenManager, clientCredentials);
    }

    @Override
    public Request<Void> deleteDocument(String documentId) {
        return new DocumentEndpoint.DeleteDocumentRequest(documentId, authTokenManager, clientCredentials);
    }

    @Override
    public Request<Void> deleteTrashedDocument(String documentId) {
        return new TrashEndpoint.DeleteTrashedDocumentRequest(documentId, authTokenManager, clientCredentials);
    }

    @Override
    public Request<Map<String, String>> getDocumentTypes()  {
        return new DocumentTypesEndpoint.GetDocumentTypesRequest(authTokenManager, clientCredentials);
    }

    @Override
    public Request<Map<String, String>> getDocumentIdentifierTypes() {
        return new DocumentIdentifiersEndpoint.GetDocumentIdentifiersRequest(authTokenManager, clientCredentials);
    }

    /* ANNOTATIONS */

    @Override
    public Request<List<Annotation>> getAnnotations() {
        return getAnnotations((AnnotationRequestParameters) null);
    }

    @Override
    public Request<List<Annotation>> getAnnotations(AnnotationRequestParameters parameters) {
        return new AnnotationsEndpoint.GetAnnotationsRequest(parameters, authTokenManager, clientCredentials);
    }

    @Override
    public Request<List<Annotation>> getAnnotations(Page next) {
        return new AnnotationsEndpoint.GetAnnotationsRequest(next.link, authTokenManager, clientCredentials);
    }

    @Override
    public Request<Annotation> getAnnotation(String annotationId) {
        return new AnnotationsEndpoint.GetAnnotationRequest(annotationId, authTokenManager, clientCredentials);
    }

    @Override
    public Request<Annotation>  postAnnotation(Annotation annotation) {
        return new AnnotationsEndpoint.PostAnnotationRequest(annotation, authTokenManager, clientCredentials);
    }

    @Override
    public Request<Annotation>  patchAnnotation(String annotationId, Annotation annotation) {
       return new AnnotationsEndpoint.PatchAnnotationRequest(annotationId, annotation, authTokenManager, clientCredentials);
    }

    @Override
    public Request<Void> deleteAnnotation(String annotationId) {
        return new AnnotationsEndpoint.DeleteAnnotationRequest(annotationId, authTokenManager, clientCredentials);
    }

    /* FILES BLOCKING */

    @Override
    public Request<List<File>> getFiles() {
        return getFiles((FileRequestParameters) null);
    }

    @Override
    public Request<List<File>> getFiles(FileRequestParameters parameters) {
        return new FilesEndpoint.GetFilesRequest(parameters, authTokenManager, clientCredentials);
    }

    @Override
    public Request<List<File>> getFiles(Page next) {
        return new FilesEndpoint.GetFilesRequest(next.link, authTokenManager, clientCredentials);
    }

    @Override
    public GetFileNetworkRequest getFileBinary(String fileId, java.io.File targetFile) {
        return new GetFileNetworkRequest(fileId, targetFile, authTokenManager, clientCredentials);
    }

    @Override
    public Request<File> postFileBinary(String contentType, String documentId, InputStream inputStream, String fileName) {
        return new PostFileAuthorizedRequest(contentType, documentId, fileName, inputStream, authTokenManager, clientCredentials);
    }

    @Override
    public Request<Void> deleteFile(String fileId) {
        return new FilesEndpoint.DeleteFileRequest(fileId, authTokenManager, clientCredentials);
    }

    /* FOLDERS BLOCKING */

    @Override
    public Request<List<Folder>> getFolders() {
        return getFolders((FolderRequestParameters) null);
    }

    @Override
    public Request<List<Folder>> getFolders(FolderRequestParameters parameters) {
        return new FolderEndpoint.GetFoldersRequest(parameters, authTokenManager, clientCredentials);
    }

    @Override
    public Request<List<Folder>> getFolders(Page next) {
        return new FolderEndpoint.GetFoldersRequest(next.link, authTokenManager, clientCredentials);
    }

    @Override
    public Request<Folder> getFolder(String folderId) {
        return new FolderEndpoint.GetFolderRequest(folderId, authTokenManager, clientCredentials);
    }

    @Override
    public Request<Folder> postFolder(Folder folder) {
        return new FolderEndpoint.PostFolderRequest(folder, authTokenManager, clientCredentials);
    }

    @Override
    public Request<Folder> patchFolder(String folderId, Folder folder) {
        return new FolderEndpoint.PatchFolderAuthorizedRequest(folderId, folder, authTokenManager, clientCredentials);
    }

    @Override
    public Request<List<String>> getFolderDocumentIds(FolderRequestParameters parameters, String folderId) {
        return new FolderEndpoint.GetFolderDocumentIdsRequest(parameters, folderId, authTokenManager, clientCredentials);
    }

    @Override
    public Request<List<String>> getFolderDocumentIds(Page next) {
        return new FolderEndpoint.GetFolderDocumentIdsRequest(next.link, authTokenManager, clientCredentials);
    }

    @Override
    public Request<Void> postDocumentToFolder(String folderId, String documentId) {
        return new FolderEndpoint.PostDocumentToFolderRequest(folderId, documentId, authTokenManager, clientCredentials);
    }

    @Override
    public Request<Void> deleteFolder(String folderId) {
        return new FolderEndpoint.DeleteFolderRequest(folderId, authTokenManager, clientCredentials);
    }

    @Override
    public Request<Void> deleteDocumentFromFolder(String folderId, String documentId) {
        return new FolderEndpoint.DeleteDocumentFromFolder(folderId, documentId, authTokenManager, clientCredentials);
    }


    /* PROFILES */

    @Override
    public Request<Profile> getMyProfile() {
        return new ProfilesEndpoint.GetProfileRequest("me", authTokenManager, clientCredentials);
    }

    @Override
    public Request<Profile> getProfile(final String profileId) {
        return new ProfilesEndpoint.GetProfileRequest(profileId, authTokenManager, clientCredentials);
    }

    /* GROUPS */

    @Override
    public Request<List<Group>> getGroups(GroupRequestParameters parameters) {
        return new GroupsEndpoint.GetGroupsRequest(parameters, authTokenManager, clientCredentials);
    }

    @Override
    public Request<List<Group>>  getGroups(Page next) {
        return new GroupsEndpoint.GetGroupsRequest(next.link, authTokenManager, clientCredentials);
    }

    @Override
    public Request<Group> getGroup(String groupId) {
        return new GroupsEndpoint.GetGroupRequest(groupId, authTokenManager, clientCredentials);
    }

    @Override
    public Request<List<UserRole>> getGroupMembers(GroupRequestParameters parameters, String groupId) {
        return new GroupsEndpoint.GetGroupMembersRequest(parameters, groupId, authTokenManager, clientCredentials);
    }

    @Override
    public Request<List<UserRole>> getGroupMembers(Page next){
        return new GroupsEndpoint.GetGroupMembersRequest(next.link, authTokenManager, clientCredentials);

    }

    /* TRASH */

    @Override
    public Request<List<Document>> getTrashedDocuments(){
        return getTrashedDocuments((DocumentRequestParameters) null);
    }

    @Override
    public Request<List<Document>> getTrashedDocuments(DocumentRequestParameters parameters) {
        return new DocumentEndpoint.GetDocumentsRequest(parameters, true, authTokenManager, clientCredentials);
    }

    @Override
    public Request<List<Document>> getTrashedDocuments(Page next) {
        return new DocumentEndpoint.GetDocumentsRequest(next.link, authTokenManager, clientCredentials);
    }

    @Override
    public Request<Void> restoreDocument(String documentId) {
        return new TrashEndpoint.RestoreDocumentRequest(documentId, authTokenManager, clientCredentials);
    }

    /* CATALOG  */

    @Override
    public Request<List<Document>> getCatalogDocuments(CatalogDocumentRequestParameters parameters) {
        return new CatalogEndpoint.GetCatalogDocumentsRequest(parameters, authTokenManager, clientCredentials);
    }

    @Override
    public Request<Document> getCatalogDocument(String catalogId, View view) {
        return new CatalogEndpoint.GetCatalogDocumentRequest(catalogId, view, authTokenManager, clientCredentials);
    }

    /* RECENTLY READ */

    @Override
    public Request<List<ReadPosition>> getRecentlyRead(String groupId, String fileId, int limit) {
        return new RecentlyReadEndpoint.GetRecentlyReadRequest(groupId, fileId, limit, authTokenManager, clientCredentials);
    }

    @Override
    public Request<ReadPosition> postRecentlyRead(ReadPosition readPosition) {
        return new RecentlyReadEndpoint.PostRecentlyReadRequest(readPosition, authTokenManager, clientCredentials);
    }

    @Override
    public Request<List<String>> getApplicationFeatures() {
        return new ApplicationFeaturesEndpoint.GetApplicationFeaturesProcedure(authTokenManager, clientCredentials);
    }

}
