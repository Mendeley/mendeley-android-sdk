package com.mendeley.api.network;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;

import junit.framework.TestCase;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.mendeley.api.auth.AccessTokenProvider;
import com.mendeley.api.model.Document;

import static com.mendeley.api.network.NetworkUtils.*;

public class NetworkConnectionTest extends AndroidTestCase {

	static String testDocumentId = "test_id";
	
	HttpsURLConnection getConnection;
	HttpsURLConnection postConnection;
	HttpsURLConnection deleteConnection;
	NetworkUtils.HttpPatch httpPatch;
    AccessTokenProvider accessTokenProvider;
	
	@Override
	protected void setUp()
			throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		createConnections() ;
    }
	

	public static Document getTestDocument() {
		return new Document.Builder("test_document_title", "book").setId("test_document_id").build();
	}

	private void createConnections()
			throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		
		String testUrl = "https://httpbin.org/";
		String getTestUrl = testUrl+"get";
		String postTestUrl =  testUrl+"post";
		String deleteTestUrl = testUrl+"delete";
		String patchTestUrl = testUrl+"patch";
		String date = "";

        accessTokenProvider = new AccessTokenProvider() {
            @Override
            public String getAccessToken() {
                return null;
            }
        };

        getConnection = getHttpsURLConnection(getTestUrl, "GET");
		postConnection = getHttpsURLConnection(postTestUrl, "POST");
		deleteConnection = getHttpsURLConnection(deleteTestUrl, "DELETE");		
		httpPatch = getHttpPatch(patchTestUrl, date);

	}
	
	private HttpsURLConnection getHttpsURLConnection(String testUrl, String endPoint)
			throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		HttpsURLConnection con = getConnection(testUrl, endPoint, accessTokenProvider);
		con.setDoOutput(true);
		con.addRequestProperty("Content-type", "application/vnd.mendeley-document.1+json");
		
		return con;
	}
	
	private NetworkUtils.HttpPatch getHttpPatch(String url, String date) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return NetworkUtils.getHttpPatch(url, date, null, accessTokenProvider);
	}
	
	public void testPreconditions() {
		assertNotNull("HttpsURLConnection object getConnection  is null", getConnection);
		assertNotNull("HttpsURLConnection object postConnection  is null", postConnection);
		assertNotNull("HttpsURLConnection object deleteConnection  is null", deleteConnection);
		assertNotNull("HttpPatch object httpPacth is null", httpPatch);
    }	
	
	
	@SmallTest
	public void test_getResponse() throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {
        String testProprty = "Test-Proprty";
        String testValue = "testValue";

        getConnection.addRequestProperty(testProprty, testValue);
        getConnection.connect();

        InputStream is = getConnection.getInputStream();

        String responseString = getJsonString(is);

        JSONObject reponseJson = new JSONObject(responseString);
        JSONObject headersJson = reponseJson.getJSONObject("headers");

        String responseValue = headersJson.getString(testProprty);

        assertEquals("Request propery with wrong value", testValue, responseValue);
        assertEquals("Response code != 200", 200, getConnection.getResponseCode());
	}

	@SmallTest
	public void test_postResponse() throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {
		
		JsonParser parser = new JsonParser();
		Document testDocument = getTestDocument();
		
		String testDocumentString = parser.jsonFromDocument(testDocument);

		postConnection.connect();		
		OutputStream os = postConnection.getOutputStream();
		BufferedWriter writer = new BufferedWriter(
		        new OutputStreamWriter(os, "UTF-8"));
		writer.write(testDocumentString);
		writer.flush();
		writer.close();
		os.close();

		InputStream is = postConnection.getInputStream();
		String responseString = getJsonString(is);
		is.close();
		
		JSONObject reponseJson = new JSONObject(responseString);
		JSONObject jsonObject = reponseJson.getJSONObject("json");
		
		Document responseDocument = parser.parseDocument(jsonObject.toString());
		
		assertEquals("Response code != 200", 200, postConnection.getResponseCode());
		assertEquals("Posted and returned documents are not equal", testDocument, responseDocument);
	}
	
	@SmallTest
	public void test_deleteResponse() throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {
		
		deleteConnection.connect();
		
		assertEquals("Response code != 200", 200, deleteConnection.getResponseCode());
	}
	
	@SmallTest
	public void test_patchResponse() throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, JSONException {

    	JsonParser parser = new JsonParser();
		Document testDocument = getTestDocument();
		
		String testDocumentString = parser.jsonFromDocument(testDocument);

    	httpPatch.setEntity(new StringEntity(testDocumentString));

    	HttpClient httpclient = new DefaultHttpClient();
    	HttpResponse response = httpclient.execute(httpPatch);

    	int responseCode = response.getStatusLine().getStatusCode();	
    	
    	ArrayList<Object> values = new ArrayList<Object>();		
		values.add(deleteConnection);
    	InputStream is = response.getEntity().getContent();
		
		String responseString = getJsonString(is);

		JSONObject reponseJson = new JSONObject(responseString);
		JSONObject jsonObject = reponseJson.getJSONObject("json");
		
		Document responseDocument = parser.parseDocument(jsonObject.toString());
		
		assertEquals("Response code != 200", 200, responseCode);
		assertEquals("Posted and returned documents are not equal", testDocument, responseDocument);
	}
	
	
}
