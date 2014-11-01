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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;

import android.util.Log;

public class HttpRequest 
{
	public static final int TIMEOUT_MILLISEC = 10000;
	
//	public static String post(HttpPostEntity entity, String uri)
//	{
//		StringEntity se = new StringEntity();
//	    se.setContentEncoding("UTF-8");
//	    se.setContentType("application/json");
//	}
	public static String get(HttpGetParams params, String uri) throws JSONException, ClientProtocolException, IOException 
	{
	    HttpParams httpParams = params.getHttpParamsForm();
	   
	    HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
	    HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
	    HttpClient client = new DefaultHttpClient(httpParams);
	    HttpGet request = new HttpGet(uri); 

//	    request.setHeader("Content-Type", "application/json");         

	    request.setParams(httpParams);
	    
	    Log.d("dbConnect", "trying");
	    HttpResponse response = client.execute(request); 
	    Log.i("dbConnect","send request: " + request.toString());
	   
	    HttpEntity entity = response.getEntity();
	    InputStream is = entity.getContent();
	    String _response = convertStreamToString(is);
	    Log.i("dbConnect", "respond json: " + _response);
	    
	    // Check if server response is valid code          
	    Log.i("dbConnect", "reply code: " + Integer.toString(response.getStatusLine().getStatusCode()));
	    
	    return _response;
	}
	
	private static String convertStreamToString(InputStream is) {

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
