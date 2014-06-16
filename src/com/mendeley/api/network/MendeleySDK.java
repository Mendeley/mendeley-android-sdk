package com.mendeley.api.network;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.util.Log;

import com.mendeley.api.exceptions.InterfaceNotImplementedException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Document;
import com.mendeley.api.model.Folder;
import com.mendeley.api.network.components.DocumentRequestParameters;
import com.mendeley.api.network.components.FileRequestParameters;
import com.mendeley.api.network.components.FolderRequestParameters;
import com.mendeley.api.network.interfaces.AuthenticationInterface;
import com.mendeley.api.network.interfaces.MendeleyDocumentInterface;
import com.mendeley.api.network.interfaces.MendeleyFileInterface;
import com.mendeley.api.network.interfaces.MendeleyFolderInterface;
import com.mendeley.api.network.interfaces.MendeleyInterface;
import com.mendeley.api.network.interfaces.MendeleyProfileInterface;
import com.mendeley.api.util.Utils;

/**
 * This class should be instantiated with the calling activity context.
 * The class provides public methods for network calls which are forwarded to the relevant network providers.
 * It also calls the AuthenticationMAnager for retrieving a valid access token and store the credentials. 
 * The context is used for displaying the authentication WebView, storing credentials in SharedPreferences 
 * and is also checked to see which interfaces the activity implements for sending callbacks once a network task has finished.
 */
public class MendeleySDK implements AuthenticationInterface {
	
	protected Context appContext;
	protected AuthenticationManager authentictionManager;
	protected MethodtoInvoke methodToInvoke;
	
	protected DocumentNetworkProvider documentdNetworkProvider;
	protected FileNetworkProvider fileNetworkProvider;
	protected ProfileNetworkProvider profileNetworkProvider;
	protected FolderNetworkProvider folderNetworkProvider;
	
	protected MendeleyDocumentInterface documentInterface;
	protected MendeleyFolderInterface folderInterface;
	protected MendeleyFileInterface fileInterface;
	protected MendeleyProfileInterface profileInterface;

	public MendeleySDK(Context appContext) {
		this.appContext = appContext;
		
		authentictionManager = new AuthenticationManager(appContext, this);
		initialiseInterfaces(appContext);
		
		if (!hasCredentials()) {
			authentictionManager.authenticate();
		}
	}
	
	/**
	 * Checking the given context to see which interfaces are implemented by the calling activity
	 * and initialising the relevant objects for sending callbacks.
	 * 
	 * @param context the calling activity context.
	 */
	private void initialiseInterfaces(Context context) {
		
		if (context instanceof MendeleyDocumentInterface) {
			documentInterface = (MendeleyDocumentInterface) context;
		}
		
		if (context instanceof MendeleyFolderInterface) {
			folderInterface = (MendeleyFolderInterface) context;
		}
		
		if (context instanceof MendeleyFileInterface) {
			fileInterface = (MendeleyFileInterface) context;
		}
		
		if (context instanceof MendeleyProfileInterface) {
			profileInterface = (MendeleyProfileInterface) context;
		}
		
		documentdNetworkProvider = new DocumentNetworkProvider(documentInterface);
		fileNetworkProvider = new FileNetworkProvider(fileInterface);
		profileNetworkProvider = new ProfileNetworkProvider(profileInterface);
		folderNetworkProvider = new FolderNetworkProvider(folderInterface);
	} 
	

	/**
	 * public method to call hasCredentials method on the protected AuthenticationManager
	 * 
	 * @return true if credentials are stored already in SharedPreferences.
	 */
	public boolean hasCredentials() {
		return authentictionManager.hasCredentials();
	}
	
	/**
	 * public method to call clearCredentials method on the protected AuthenticationManager
	 */
	public void clearCredentials() {
		authentictionManager.clearCredentials();
	}
	
	/**
	 * Checking if the current access token has expired
	 * 
	 * @return true if authenticated.
	 */
	private boolean isAuthenticated() {

		boolean isAuthenticated = false;
		
		if (NetworkProvider.accessToken != null && NetworkProvider.expiresAt != null) {
			Date now = new Date();
			Date expires = null;
			try {
				expires = Utils.dateFormat.parse(NetworkProvider.expiresAt);
			} catch (ParseException e) {
				Log.e("", "", e);
				return false;
			}

			long diffInMs = expires.getTime() - now.getTime();
			long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
			
			isAuthenticated = diffInSec > 0;
		}

		return isAuthenticated;
	}
	
	/**
	 * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
	 * 
	 * @param parameters holds optional query parameters, will be ignored if null
	 * @throws InterfaceNotImplementedException
	 */
	public void getFolders(FolderRequestParameters parameters) throws InterfaceNotImplementedException {
		if (checkNetworkCall(folderInterface,
			 				 new Class[]{FolderRequestParameters.class}, 
			 				 new Object[]{parameters})) {
			folderNetworkProvider.doGetFolders(parameters);
		}
	}
	
	/**
	 * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
	 * 
	 * @param folderId id of the folder to get
	 * @throws InterfaceNotImplementedException
	 */
	public void getFolder(String folderId) throws InterfaceNotImplementedException {
		if (checkNetworkCall(folderInterface,
			 				 new Class[]{String.class}, 
			 				 new Object[]{folderId})) {
			folderNetworkProvider.doGetFolder(folderId);
		}
	}
	
	/**
	 * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
	 * 
	 * @param folderId id of the folder for which to get the document ids
	 * @throws InterfaceNotImplementedException
	 */
	public void getFolderDocumentIds(String folderId) throws InterfaceNotImplementedException {
		if (checkNetworkCall(folderInterface,
			 				 new Class[]{String.class}, 
			 				 new Object[]{folderId})) {
			folderNetworkProvider.doGetFolderDocumentIds(folderId);
		}
	}
	
	/**
	 * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
	 * 
	 * @param folder the folder object to post
	 * @throws InterfaceNotImplementedException
	 */
	public void postFolder(Folder folder) throws InterfaceNotImplementedException {
		if (checkNetworkCall(folderInterface,
			 				 new Class[]{Folder.class}, 
			 				 new Object[]{folder})) {
			folderNetworkProvider.doPostFolder(folder);
		}
	}
	
	/**
	 * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
	 * 
	 * @param folderId the id of the folder
	 * @param documentId the id of the document to add to the folder
	 * @throws InterfaceNotImplementedException
	 */
	public void postDocumentToFolder(String folderId, String documentId) throws InterfaceNotImplementedException {
		if (checkNetworkCall(folderInterface,
			 				 new Class[]{String.class,String.class}, 
			 				 new Object[]{folderId, documentId})) {
			folderNetworkProvider.doPostDocumentToFolder(folderId, documentId);
		}
	}
	
	/**
	 * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
	 * 
	 * @param folderId the id of the folder to delete
	 * @throws InterfaceNotImplementedException
	 */
	public void deleteFolder(String folderId) throws InterfaceNotImplementedException {
		if (checkNetworkCall(folderInterface,
			 				 new Class[]{String.class}, 
			 				 new Object[]{folderId})) {
			folderNetworkProvider.doDeleteFolder(folderId);
		}
	}
	
	/**
	 * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
	 * 
	 * @param folderId the id of the folder
	 * @param documentId the id of the document to delete
	 * @throws InterfaceNotImplementedException
	 */
	public void deleteDocumentFromFolder(String folderId, String documentId) throws InterfaceNotImplementedException {
		if (checkNetworkCall(folderInterface,
			 				 new Class[]{String.class, String.class}, 
			 				 new Object[]{folderId, documentId})) {
			folderNetworkProvider.doDeleteDocumentFromFolder(folderId, documentId);
		}
	}
	
	/**
	 * Checking if call can be executed and forwarding it to the FolderNetworkProvider.
	 * 
	 * @param folderId the id of the folder to patch
	 * @param folder the folder object that holds the new name and parent data 
	 * @throws InterfaceNotImplementedException
	 */
	public void patchFolder(String folderId, Folder folder) throws InterfaceNotImplementedException {
		if (checkNetworkCall(folderInterface,
			 				 new Class[]{String.class, Folder.class}, 
			 				 new Object[]{folderId, folder})) {
			folderNetworkProvider.doPatchFolder(folderId, folder);
		}
	}
	
	/**
	 * Checking if call can be executed and forwarding it to the ProfileNetworkProvider.
	 * 
	 * @throws InterfaceNotImplementedException
	 */
	public void getMyProfile() throws InterfaceNotImplementedException {
		if (checkNetworkCall(profileInterface,
			 				 null, null)) {
			profileNetworkProvider.doGetMyProfile();
		}
	}

	
	/**
	 * Checking if call can be executed and forwarding it to the ProfileNetworkProvider.
	 * 
	 * @throws InterfaceNotImplementedException
	 */
	public void getProfile(String profileId) throws InterfaceNotImplementedException {
		if (checkNetworkCall(profileInterface,
			 				 new Class[]{String.class}, 
			 				 new Object[]{profileId})) {
			profileNetworkProvider.doGetProfile(profileId);
		}
	}
	
	/**
	 *  Checking if call can be executed and forwarding it to the FileNetworkProvider.
	 *  
	 * @param parameters holds optional query parameters, will be ignored if null
	 * @throws InterfaceNotImplementedException
	 */
	public void getFiles(FileRequestParameters parameters) throws InterfaceNotImplementedException {
		if (checkNetworkCall(fileInterface,
			 				 new Class[]{FileRequestParameters.class}, 
		 				 	 new Object[]{parameters})) {
			fileNetworkProvider.doGetFiles(parameters);
		}
	}
	
	/**
	 *  Checking if call can be executed and forwarding it to the FileNetworkProvider.
	 *  
	 * @param fileId the id of the file to get
	 * @param folderPath the path in which to save the file
	 * @throws InterfaceNotImplementedException
	 */
	public void getFile(String fileId, String folderPath) throws InterfaceNotImplementedException {
		if (checkNetworkCall(fileInterface,
			 				 new Class[]{String.class, String.class}, 
		 				 	 new Object[]{fileId, folderPath})) {
			fileNetworkProvider.doGetFile(fileId, folderPath);
		}
	}
	
	/**
	 *  Checking if call can be executed and forwarding it to the FileNetworkProvider.
	 *  
	 * @throws InterfaceNotImplementedException
	 */
	public void deleteFile(String fileId) throws InterfaceNotImplementedException {
		if (checkNetworkCall(fileInterface,
			 				 new Class[]{String.class}, 
		 				 	 new Object[]{fileId})) {
			fileNetworkProvider.doDeleteFile(fileId);
		}
	}
	
	/**
	 *  Checking if call can be executed and forwarding it to the FileNetworkProvider.
	 *  
	 * @param contentType the content type of the file
	 * @param documentId the id of the document the file is related to
	 * @param filePath the absolute file path
	 * @throws InterfaceNotImplementedException
	 */
	public void postFile(String contentType, String documentId, String filePath) throws InterfaceNotImplementedException {
		if (checkNetworkCall(fileInterface,
			 				 new Class[]{String.class, String.class, String.class}, 
		 				 	 new Object[]{contentType, documentId, filePath})) {
			fileNetworkProvider.doPostFile(contentType, documentId, filePath);
		}
	}
	
	/**
	 *  Checking if call can be executed and forwarding it to the DocumentNetworkProvider.
	 * 
	 * @param parameters holds optional query parameters, will be ignored if null
	 * @throws InterfaceNotImplementedException
	 */
	public void getDocuments(DocumentRequestParameters parameters) throws InterfaceNotImplementedException {
		if (checkNetworkCall(documentInterface,
									 new Class[]{DocumentRequestParameters.class}, 
									 new Object[]{parameters})) {
			documentdNetworkProvider.doGetDocuments(parameters);
		}
	}

	/**
	 * Checking if call can be executed and forwarding it to the DocumentNetworkProvider.
	 * 
	 * @param documentId the document id to get
	 * @param parameters holds optional query parameters, will be ignored if null
	 * @throws InterfaceNotImplementedException
	 */
	public void getDocument(String documentId, DocumentRequestParameters parameters) throws InterfaceNotImplementedException {
		if (checkNetworkCall(documentInterface,
									 new Class[]{String.class,DocumentRequestParameters.class}, 
									 new Object[]{documentId, parameters})) {
			documentdNetworkProvider.doGetDocument(documentId, parameters);
		}
	}
	
	/**
	 * Checking if call can be executed and forwarding it to the DocumentNetworkProvider.
	 * 
	 * @param documentId the document id to be trashed
	 * @throws InterfaceNotImplementedException
	 */
	public void trashDocument(String documentId) throws InterfaceNotImplementedException {
		if (checkNetworkCall(documentInterface,
									 new Class[]{String.class}, 
							 		 new Object[]{documentId})) {
			documentdNetworkProvider.doPostTrashDocument(documentId);
		}
	}
	
	/**
	 * Checking if call can be executed and forwarding it to the DocumentNetworkProvider.
	 * 
	 * @param documentId the document id to be deleted
	 * @throws InterfaceNotImplementedException
	 */
	public void deleteDocument(String documentId) throws InterfaceNotImplementedException {
		if (checkNetworkCall(documentInterface,
									 new Class[]{String.class},
									 new Object[]{documentId})) {
			documentdNetworkProvider.doDeleteDocument(documentId);
		}
	}
	
	/**
	 * Checking if call can be executed and forwarding it to the DocumentNetworkProvider.
	 * 
	 * @param document the document object to be posted
	 * @throws InterfaceNotImplementedException
	 */
	public void postDocument(Document document) throws InterfaceNotImplementedException {		
		if (checkNetworkCall(documentInterface,
									 new Class[]{Document.class}, 
									 new Object[]{document})) {
			documentdNetworkProvider.doPostDocument(document);
		}
	}

	
	/**
	 * Checking if call can be executed and forwarding it to the DocumentNetworkProvider.
	 * 
	 * @param documentId the id of the document to be patched
	 * @param date for the api condition if unmodified since.
	 * @param document the document object
	 * @throws InterfaceNotImplementedException
	 */
	public void patchDocument(String documentId, Date date, Document document) throws InterfaceNotImplementedException {		
		if (checkNetworkCall(documentInterface,
									 new Class[]{String.class, Date.class, Document.class}, 
									 new Object[]{documentId, date, document})) {
			documentdNetworkProvider.doPatchDocument(documentId, date, document);
		}
	}
	
	
	/**
	 * First checking that the MendeleyInterface has been instantiated for sending callbacks to the application, if not throwing InterfaceNotImplementedException.
	 * Then checking if client is authenticated, if false initialising the MethodToInvoke object with the calling method name and its arguments
	 * and calling authenticate on the AuthenticationManager, else returns true.
	 * 
	 * @param mendeleyInterface the instance of MendeleyInterface that will be used for the callbacks
	 * @param classes of the arguments of the calling method
	 * @param values of the arguments of the calling method
	 * @return true if network call can be executed
	 * @throws InterfaceNotImplementedException
	 */
	private boolean checkNetworkCall(MendeleyInterface mendeleyInterface, @SuppressWarnings("rawtypes") Class[] classes, Object[] values) throws InterfaceNotImplementedException {
		if (mendeleyInterface == null) {
			throw new InterfaceNotImplementedException("The required MendeleyInterface is not implemented by the calling class");
		}
		if (!isAuthenticated()) {
			if (classes == null) {
				methodToInvoke = new MethodtoInvoke(new Exception().getStackTrace()[1].getMethodName());
				authentictionManager.authenticate();
			} else {
				methodToInvoke = new MethodtoInvoke(new Exception().getStackTrace()[1].getMethodName(), classes, values);
				authentictionManager.authenticate();
			}
			
		} else {
			return true;
		}
		return false;
	}

	/**
	 * Invoking the method stored in the MethodToInvoke object.
	 */
	private void invokeMethod() {
		
		if (methodToInvoke != null) {
			try {
				Method method = null;
				String className = this.getClass().getName();			
				Class<?> clazz = Class.forName(className);  	
				if (methodToInvoke.argumentTypes != null) {
					method = clazz.getMethod(methodToInvoke.methodName, methodToInvoke.argumentTypes);  	
					method.invoke(this, methodToInvoke.arguments);  
				} else {
					method = clazz.getMethod(methodToInvoke.methodName);
					method.invoke(this);
				}
			}
			catch (Exception e) {
				Log.e("", "", e);
			}
		}
	}

	/**
	 * A callback method that is called from AuthenticationManager when client has been authenticated. 
	 */
	@Override
	public void onAuthenticated() {
		invokeMethod();
	}
	

	/**
	 * A callback method that is called from AuthenticationManager when client has failed to be authenticated. 
	 */
	@Override
	public void onAuthenticationFail() {
		((MendeleyInterface)appContext).onAPICallFail(new MendeleyException("Authentication failed"));
	}
	
	/**
	 * Inner class that holds details of the method and arguments to be called once the client has been authenticated.
	 *
	 */
	protected class MethodtoInvoke {
		
		String methodName;
		@SuppressWarnings("rawtypes")
		Class[] argumentTypes;
		Object[] arguments;
		
		MethodtoInvoke(String methodName) {
			this.methodName = methodName;
			argumentTypes = null;
			arguments = null;
		}
		
		MethodtoInvoke(String methodName, @SuppressWarnings("rawtypes") Class[] argumentTypes, Object[] arguments) {
			this.methodName = methodName;
			this.argumentTypes = argumentTypes;
			this.arguments = arguments;
		}
	}

	
	/**
	 * public default constructor for junit testing.
	 */
	public MendeleySDK() {
	}
	
}
