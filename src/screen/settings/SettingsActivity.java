package screen.settings;


import java.io.IOException;

import screen.map.MapActivity;
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
import data.user.UserIdNamePair;

public class SettingsActivity extends ActionBarActivity {

	//TODO: Limited name size?
	public static final int MAX_NAME_LENGTH = Integer.MAX_VALUE;

	public static final String SEND_NEW_USER_NAME_URI = "http://cmpt370duan.byethost10.com/updateuser.php";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings_activity);

		EditText editName = (EditText) findViewById(R.id.edit_name);
		//TODO: Get user's name;
		String name = "Anonymous";
		editName.setHint(name);
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

	public void onSubmit(View v)
	{
		EditText editName = (EditText) findViewById(R.id.edit_name);
		String name = editName.getText().toString().trim();

		if(name.isEmpty())
		{
			Toast.makeText(getApplicationContext(), "Please enter a name", Toast.LENGTH_LONG).show();
		}
		else if(name.length() > MAX_NAME_LENGTH) 
		{
			Toast.makeText(getApplicationContext(), "Your name must be no longer than " + MAX_NAME_LENGTH + " characters", Toast.LENGTH_LONG).show();
		}
		else if (!name.equals(MapActivity.USER_ID_AND_NAME.userName))
		{
			//TODO: finish this once I have got usernames down.
			MapActivity.USER_ID_AND_NAME.userName = name;

			new SendNewUserNameTask().execute(MapActivity.USER_ID_AND_NAME);
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
				//TODO: Change this back to put, when the opportunity arises.
				HttpRequest.post(sendEntity, SEND_NEW_USER_NAME_URI);
				
				Log.i("dbConnect", "Sent new user name to db.");
			} catch (IOException e) {
				//TODO: Implement retries properly, presumably by setting the DefaultHttpRequestRetryHandler.
				e.printStackTrace();
				Log.e("dbConnect", e.toString());
			}
			
			return null;
		}
	}
}
