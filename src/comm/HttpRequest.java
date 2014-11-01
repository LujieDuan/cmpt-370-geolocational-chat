package comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
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
	
	public static String getResult(String jsonString, URI uri) throws JSONException, ClientProtocolException, IOException 
	{
	    HttpParams httpParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
	    HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
	    HttpClient client = new DefaultHttpClient(httpParams);
	    HttpPost request = new HttpPost(uri); 

	    request.setHeader( "Content-Type", "application/json" );         

	    Log.i("sent json", jsonString);

	    StringEntity se = new StringEntity(jsonString);

	    se.setContentEncoding("UTF-8");
	    se.setContentType("application/json");

	    request.setEntity(se);      

	    HttpResponse response = client.execute(request); 

	    HttpEntity entity = response.getEntity();
	    InputStream is = entity.getContent();
	    String _response = convertStreamToString(is);
	    Log.i("received json", jsonString);
	    
	    // Check if server response is valid code          
	    Log.i("reply code", Integer.toString(response.getStatusLine().getStatusCode()));
	    
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
