package screen.settings;


import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import coderunners.geolocationalchat.R;
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
			new SendNewUserNameTask(this).execute(new UserIdNamePair(GlobalSettings.userIdAndName.userId, name));
			
			//Wait until the sendNewUserNameTask finishes. If it was unsuccessful, just keep the same name as currently.
			try {
				this.wait();
			} catch (InterruptedException e) {
				finish();
			}
			
			editName.setHint(GlobalSettings.userIdAndName.userName);
			editName.setText("");
			finish();
		}

	}
}
