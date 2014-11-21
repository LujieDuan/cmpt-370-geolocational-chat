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

import coderunners.geolocationalchat.R;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

/**
 * Uninstantiated class; handles all http methods to access the database, as well as toast to make upon failure.
 * @author wsv759
 *
 */
public class HttpRequest 
{
	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final int TIMEOUT_MILLISEC = 10000;
	public static final int HTTP_RESPONSE_SUCCESS = 1;
	
//	public static final String REQUEST_REJECTED = "rejected by server";
//	public static final String SERVER_TIMEOUT = "server timed out";
//	public static final String NO_SERVER_RESPONSE = "no response received from server";
	
	public static enum ReasonForFailure {
		REQUEST_REJECTED, REQUEST_TIMEOUT, NO_SERVER_RESPONSE
	}
	
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
		Log.d("dbConnect", "response string: " + responseString);

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
	
	
//	/**
//	 * Display a message to the current activity indicating that the server rejected the request, and the current
//	 * task could not complete properly. It is the Activity's responsibility to call this function, if
//	 * it so desires.
//	 * @param activity the calling activity
//	 * @param dataDescriptor an optional string describing the data that needed to be retrieved. If null,
//	 * this defaults to "data".
//	 * @param autoRetry true if the task will automatically be retried (e.g. on a schedule), false otherwise.
//	 */
//	public static void makeToastOnRequestRejection(final Activity activity, final String dataDescriptor, final boolean autoRetry)
//	{
//		makeToast(activity, dataDescriptor, autoRetry, "rejected by server"); 
//	}
//
//	/**
//	 * Display a message to the current activity indicating that the server timed out, and the current
//	 * task could not complete properly. It is the Activity's responsibility to call this function, if
//	 * it so desires.
//	 * @param activity the calling activity
//	 * @param dataDescriptor an optional string describing the data that needed to be retrieved. If null,
//	 * this defaults to "data".
//	 * @param autoRetry true if the task will automatically be retried (e.g. on a schedule), false otherwise.
//	 */
//	public static void makeToastOnServerTimeout(final Activity activity, final String dataDescriptor,final boolean autoRetry)
//	{
//		makeToast(activity, dataDescriptor, autoRetry, "server timed out"); 
//	}
//
//	/**
//	 * Display a message to the current activity indicating that no response was received from the server,
//	 * and the current task could not complete properly. 
//	 * It is the Activity's responsibility to call this function, if it so desires.
//	 * @param activity the calling activity
//	 * @param dataDescriptor an optional string describing the data that needed to be retrieved. If null,
//	 * this defaults to "data".
//	 * @param autoRetry true if the task will automatically be retried (e.g. on a schedule), false otherwise.
//	 */
//	public static void makeToastOnNoServerResponse(final Activity activity, final String dataDescriptor,final boolean autoRetry)
//	{
//		makeToast(activity, dataDescriptor, autoRetry, "no response received from server");
//	}

	/**
	 * Display the given toastText to the given activity.
	 * @param activity the activity on which to display the toast.
	 * @param toastText the text of the toast.
	 */
	public static void handleHttpRequestFailure(final Activity activity, String dataDescriptor, boolean autoRetry, final ReasonForFailure reason)
	{
		final String reasonForError;
		if (reason == ReasonForFailure.REQUEST_REJECTED)
			reasonForError = activity.getResources().getString(R.string.http_request_failure_rejected);
		else if (reason == ReasonForFailure.REQUEST_TIMEOUT)
			reasonForError = activity.getResources().getString(R.string.http_request_failure_timeout);
		else if (reason == ReasonForFailure.NO_SERVER_RESPONSE)
			reasonForError = activity.getResources().getString(R.string.http_request_failure_no_response);
		else
			reasonForError = activity.getResources().getString(R.string.http_request_failure_unknown);
		
		final String sensibleDataDescriptor;
		if (dataDescriptor == null || dataDescriptor.isEmpty())
			sensibleDataDescriptor = "data";
		else
			sensibleDataDescriptor = dataDescriptor;

		final String retryString;
		if (autoRetry)
			retryString = "Retrying...";
		else
			retryString = "Please try again later.";
		
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {  	
				Toast.makeText(activity, 
						"Unable to retrieve " + sensibleDataDescriptor + "; " + reasonForError + ". " + retryString, 
						Toast.LENGTH_LONG).show();
			}
		});
	}
}
