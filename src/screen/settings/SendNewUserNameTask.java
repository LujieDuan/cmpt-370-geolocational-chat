package screen.settings;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import screen.inbox.InboxActivity;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import comm.HttpRequest;
import comm.TaskParams_SendNewUserName;

import data.global.GlobalSettings;
import data.global.UserIdNamePair;

public class SendNewUserNameTask extends AsyncTask<UserIdNamePair, Void, Void>
{	
	protected Activity activity;
	public SendNewUserNameTask(Activity activity)
	{
		this.activity = activity;
	}
	@Override
	protected Void doInBackground(UserIdNamePair... params) 
	{
		UserIdNamePair newUserIdAndName = params[0];
		TaskParams_SendNewUserName sendEntity = new TaskParams_SendNewUserName(newUserIdAndName);

		try {
			String responseString = HttpRequest.post(sendEntity, SettingsActivity.SEND_NEW_USER_NAME_URI);
			JSONObject responseJson = new JSONObject(responseString);

			if (responseJson.getInt(InboxActivity.TAG_SUCCESS) == 1)
			{
				GlobalSettings.userIdAndName = newUserIdAndName;
			}
			else
			{
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(
								activity, 
								"Server rejected new user.\nPlease try again later.", 
								Toast.LENGTH_LONG).show();
					}
				});
			}
			
			activity.notify();
			Log.i("dbConnect", "Sent new user name to db.");
		} catch (IOException e) {
			//TODO: Implement retries properly, presumably by setting the DefaultHttpRequestRetryHandler.
			e.printStackTrace();
			Log.e("dbConnect", e.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}