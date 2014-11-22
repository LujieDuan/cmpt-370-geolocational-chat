package data.app.global;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

import data.base.UserIdNamePair;

/**
 * Uninstantiated class; contains some global constants that various parts of the app will need to access.
 * @author wsv759
 *
 */
public class GlobalSettings 
{
	/** 
	 * Unique userId and userName pair for this app. 
	 */
	public static UserIdNamePair userIdAndName;
	
	/** 
	 * All tags available for the user to filter the chat selection by.
	 */
	public static ArrayList<String> allTags;
	
	/**
	 * The tags by which the user has chosen to filter chat selection. 
	 * E.g. If a user only wants to see sports-related chats, this might contain only "sports".
	 * This defaults to empty, in which case the chats are not filtered at all.
	 */
	public static ArrayList<String> tagsToFilterFor = new ArrayList<String>();
	
	/**
	 * Is the user filtering the chats they see based on tags?
	 */
	public static boolean tagFilteringIsOn = false;
	/**
	 * The current location of the phone. This is updated automatically, and should be accessed whenever 
	 * the current location of the phone is needed.
	 */
	public static LatLng curPhoneLocation = new LatLng(52.1310799, -106.6341388); //TODO un-fake the location.
}
