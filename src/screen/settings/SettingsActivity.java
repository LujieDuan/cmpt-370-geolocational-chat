package screen.settings;


import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import screen.inbox.InboxActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import coderunners.geolocationalchat.R;

import comm.HttpRequest;
import comm.TaskParams_SendNewUserName;

import data.global.GlobalSettings;
import data.global.UserIdNamePair;

/**
 * The settings activity can be used by the user to update various information.
 * Currently, it only allows a user to change their name, but in the future it
 * may allow them to do other things such as adjust their notification settings
 * once notifications are implemented.
 */
public class SettingsActivity extends ActionBarActivity {

	//TODO: Limit name sizes?
	/**
	 * Minimum name length which may be entered by a user
	 */
	public static final int MIN_NAME_LENGTH = 1;

	/** 
	 * Maximum name length which may be entered by a user
	 */
	public static final int MAX_NAME_LENGTH = Integer.MAX_VALUE;

	public static final String SEND_NEW_USER_NAME_URI = "http://cmpt370duan.byethost10.com/updateuser.php";

	/**
	 * Sets up a settings screen to be displayed to the user
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		EditText editName = (EditText) findViewById(R.id.edit_name);
		editName.setHint(GlobalSettings.userIdAndName.userName);
	}

	/**
	 * Returns to the previous screen without submitting any data entered by the
	 * user.
	 * @param v
	 */
	public void onCancel(View v)
	{
		finish();
	}

	/**
	 * Submits the user's settings, sending the user's new name to the database.
	 * If the name provided is shorter than {@link MIN_NAME_LENGTH} or longer
	 * than {@link MAX_NAME_LENGTH}, the user will be notified via a 
	 * {@link Toast}.
	 * @param v
	 */
	public void onSubmit(View v)
	{
		EditText editName = (EditText) findViewById(R.id.edit_name);
		String name = editName.getText().toString().trim();

		if(name.length() < MIN_NAME_LENGTH)
		{
			Toast.makeText(getApplicationContext(), "Please enter a name longer than " + MIN_NAME_LENGTH + " characters", Toast.LENGTH_LONG).show();
		}
		else if(name.length() > MAX_NAME_LENGTH) 
		{
			Toast.makeText(getApplicationContext(), "Please enter a name no longer than " + MAX_NAME_LENGTH + " characters", Toast.LENGTH_LONG).show();
		}
		else if (!name.equals(GlobalSettings.userIdAndName.userName))
		{
			//TODO: finish this once I have got usernames down.
			GlobalSettings.userIdAndName.userName = name;

			new SendNewUserNameTask().execute(GlobalSettings.userIdAndName);
			editName.setHint(name);
			editName.setText("");
			finish();
		}

	}

	public static class SendNewUserNameTask extends AsyncTask<UserIdNamePair, Void, Void>
	{	
		@Override
		protected Void doInBackground(UserIdNamePair... params) 
		{
			TaskParams_SendNewUserName sendEntity = new TaskParams_SendNewUserName(params[0]);

			try {
				//TODO: Change this back to put, if the opportunity arises.
				String responseString = HttpRequest.post(sendEntity, SEND_NEW_USER_NAME_URI);
				JSONObject responseJson = new JSONObject(responseString);

				if (responseJson.getInt(InboxActivity.TAG_SUCCESS) == 1)
				{
					//TODO set user name here
				}
				else
				{
					//TODO make failure toast. 
				}
				
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
}
