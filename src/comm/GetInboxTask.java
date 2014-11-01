package comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.google.gson.Gson;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

public class GetInboxTask extends AsyncTask<Void, Integer, Integer> 
{
	@Override
	protected Integer doInBackground(Void... params) 
	{
		int TIMEOUT_MILLISEC = 10000;
		String GET_INBOX_URL = "somekindaUrl";
		Location curPhoneLocation = null;
		String[] tags = null;
		TaskParams_GetInbox sendParams = new TaskParams_GetInbox(curPhoneLocation, tags);
		
		Gson gson = new Gson(); 
		String json = gson.toJson(sendParams);
		

		
        return 0;
	}
//    protected void onProgressUpdate(Integer... progress) {
//        setProgressPercent(progress[0]);
//    }
//
//    protected void onPostExecute(Long result) {
//        showDialog("Downloaded " + result + " bytes");
//    }

	
}
