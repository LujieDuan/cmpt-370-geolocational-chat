package data.app.global;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import screen.settings.SendNewUserNameTask;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;
import coderunners.geolocationalchat.R;

import com.google.android.gms.maps.model.LatLng;

import data.base.UserIdNamePair;

/**
 * Uninstantiated class; contains some global constants that various parts of
 * the app will need to access.
 * 
 * @author wsv759
 *
 */
public class GlobalSettings {
	public static final String SETTINGS_FILE_NAME = "GeolocationalChatStoredSettings";
	private static final String SETTINGS_KEY_TAGS_TO_FILTER_FOR = "tagsToFilterFor";
	private static final String SETTINGS_KEY_TAG_FILTERING_IS_ON = "tagFilteringIsOn";
	public static final String SETTINGS_KEY_USER_NAME = "userName";
	
	/**
	 * Unique userId and userName pair for this app.
	 */
	public static UserIdNamePair userIdAndName;

	/**
	 * All tags available for the user to filter the chat selection by.
	 */
	public static ArrayList<String> allTags;

	/**
	 * The tags by which the user has chosen to filter chat selection. E.g. If a
	 * user only wants to see sports-related chats, this might contain only
	 * "sports". This defaults to empty, in which case the chats are not
	 * filtered at all.
	 */
	public static ArrayList<String> tagsToFilterFor;

	/**
	 * Is the user filtering the chats they see based on tags?
	 */
	public static boolean tagFilteringIsOn;

	/**
	 * The current location of the phone. This is updated automatically, and
	 * should be accessed whenever the current location of the phone is needed.
	 */
	public static LatLng curPhoneLocation = new LatLng(52.1310799, -106.6341388);
	
	public static void initialize(Activity activity)
	{
		String deviceId = Secure.getString(activity.getBaseContext()
				.getContentResolver(), Secure.ANDROID_ID);
		
		SharedPreferences settings = activity.getSharedPreferences(SETTINGS_FILE_NAME,
				Context.MODE_PRIVATE);
		String userName = settings.getString(SETTINGS_KEY_USER_NAME, "");
		// This is just required to pass Set<String>.toArray() the right type of
		// array.
		String[] tagsToFilterFor = new String[0];
		GlobalSettings.tagsToFilterFor = new ArrayList<String>(
				Arrays.asList(settings.getStringSet(
						SETTINGS_KEY_TAGS_TO_FILTER_FOR, new HashSet<String>())
						.toArray(tagsToFilterFor)));
		GlobalSettings.tagFilteringIsOn = settings.getBoolean(
				SETTINGS_KEY_TAG_FILTERING_IS_ON, false);

		if (userName.isEmpty()) {
			// The SendNewUserNameTask changes the global userIdAndName for us.
			new SendNewUserNameTask(activity).execute(new UserIdNamePair(deviceId,
					activity.getResources().getString(R.string.unknown_user_name)));
		} else {
			GlobalSettings.userIdAndName = new UserIdNamePair(deviceId,
					userName);
		}
	}
	
	public static void saveChanges(Activity activity)
	{
		SharedPreferences settings = activity.getSharedPreferences(SETTINGS_FILE_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();

		UserIdNamePair userIdAndName = GlobalSettings.userIdAndName;
		if (userIdAndName != null && !userIdAndName.getUserName().isEmpty())
			editor.putString(SETTINGS_KEY_USER_NAME,
					GlobalSettings.userIdAndName.getUserName());
		editor.putStringSet(SETTINGS_KEY_TAGS_TO_FILTER_FOR,
				new HashSet<String>(GlobalSettings.tagsToFilterFor));
		editor.putBoolean(SETTINGS_KEY_TAG_FILTERING_IS_ON,
				GlobalSettings.tagFilteringIsOn);

		editor.commit();
	}
}
