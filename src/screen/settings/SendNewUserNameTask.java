package screen.settings;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import screen.map.MapActivity;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import coderunners.geolocationalchat.R;

import comm.HttpRequest;

import data.app.global.GlobalSettings;
import data.base.UserIdNamePair;

/**
 * Sends a new userId-userName pairing to the database, in the background. If
 * successful, sets the global userId-userName pairing for this phone to be the
 * new userId-userName pairing.
 * 
 * @author wsv759
 * 
 * @param UserIdNamePair
 *            a UserIdNamePair object.
 */
public class SendNewUserNameTask extends AsyncTask<UserIdNamePair, Void, Void> {
	/** the activity to which to make toast. */
	protected Activity activity;

	/**
	 * constructor keeps track of the activity to which to make toast.
	 * 
	 * @param activity
	 *            the activity to which to make toast.
	 */
	public SendNewUserNameTask(Activity activity) {
		this.activity = activity;
	}

	@Override
	protected Void doInBackground(UserIdNamePair... params) {
		UserIdNamePair newUserIdAndName = params[0];

		try {
			String responseString = HttpRequest.post(newUserIdAndName,
					SettingsActivity.SEND_NEW_USER_NAME_URI);
			JSONObject responseJson = new JSONObject(responseString);

			if (responseJson.getInt(MapActivity.TAG_SUCCESS) == 1) {
				GlobalSettings.userIdAndName = newUserIdAndName;
			} else {
				HttpRequest.handleHttpRequestFailure(
						activity,
						activity.getResources().getString(
								R.string.http_data_descriptor_response), false,
						HttpRequest.ReasonForFailure.REQUEST_REJECTED);
				Log.e("dbConnect",
						activity.getResources().getString(
								R.string.http_request_failure_rejected) + ": " + responseJson.getString(MapActivity.TAG_MESSAGE));
			}

			Log.i("dbConnect", "Sent new user name to db.");
		} catch (IOException e) {
			HttpRequest.handleHttpRequestFailure(
					activity,
					activity.getResources().getString(
							R.string.http_data_descriptor_response), false,
					HttpRequest.ReasonForFailure.REQUEST_TIMEOUT);
			Log.e("dbConnect", e.toString());
		} catch (JSONException e) {
			HttpRequest.handleHttpRequestFailure(
					activity,
					activity.getResources().getString(
							R.string.http_data_descriptor_response), false,
					HttpRequest.ReasonForFailure.NO_SERVER_RESPONSE);
			Log.e("dbConnect", e.toString());
		}
		
		synchronized(activity)
		{
			activity.notify();
		}
		
		return null;
	}
}