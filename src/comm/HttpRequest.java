package comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

public class HttpRequest 
{
	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final int TIMEOUT_MILLISEC = 10000;

	public static String post(HttpPostEntity entity, String uri) throws ClientProtocolException, IOException
	{
		StringEntity se = entity.asJsonStringEntity();
		se.setContentEncoding("UTF-8");
		se.setContentType("application/json");
		
		HttpPost request = new HttpPost(uri);
		request.setEntity(se);
		
		Log.d("dbConnect", "http post entity: " + request.getEntity().toString());
		return executeRequest(request);
	}
	
	public static String put(HttpPutEntity entity, String uri) throws ClientProtocolException, IOException
	{
		StringEntity se = entity.asJsonStringEntity();
		se.setContentEncoding("UTF-8");
		se.setContentType("application/json");

		HttpPut request = new HttpPut(uri);
		request.setEntity(se);
		
		Log.d("dbConnect", "http put entity: " + request.getEntity().toString());
		return executeRequest(request);
	}
	
	public static String get(HttpGetParams params, String uri) throws ClientProtocolException, IOException 
	{
		//Some requests don't need or use params.
		if (params != null)
			uri += "?" + params.getHttpStringForm();
		
		Log.d("dbConnect", "full http get uri string: " + uri);
		HttpGet request = new HttpGet(uri);         
	    return executeRequest(request);
	}
	
	private static String executeRequest(HttpRequestBase request) throws ClientProtocolException, IOException
	{
		
		HttpParams params = request.getParams();
	    HttpConnectionParams.setConnectionTimeout(params, TIMEOUT_MILLISEC);
	    HttpConnectionParams.setSoTimeout(params, TIMEOUT_MILLISEC);
	    HttpClient client = new DefaultHttpClient(params);
	    
	    Log.i("dbConnect","sending http request: " + request.toString());
	    HttpResponse response = client.execute(request); 
	   
	    HttpEntity entity = response.getEntity();
	    InputStream is = entity.getContent();
	    String responseString = convertStreamToString(is);
	    Log.i("dbConnect", "response string: " + responseString);
	    
	    // Check if server response is valid code          
	    Log.i("dbConnect", "reply code: " + Integer.toString(response.getStatusLine().getStatusCode()));
	    
	    return responseString;
	}
	
	private static String convertStreamToString(InputStream is) 
	{
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8192);
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append((line + "\n"));
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}
}
