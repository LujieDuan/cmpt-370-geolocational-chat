package data.inbox;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;

public class ChatSummary
{
	public String title;
	public LatLng location;
	public String[] tags = {""};
	
	/**
	 * Create a new chat summary, containing all the info necessary for an inbox item in the inbox UI.
	 * @param title the title of the chat
	 * @param location the location of the chat
	 * @param tags all tags associated with the chat (to help filtering)
	 */
	public ChatSummary(String title, LatLng location, String[] tags)
	{
		this.title = title;
		this.location = location;
		this.tags = tags;
	}

	public String getDistanceString(LatLng currLocation)
	{    
		return "50m away";
	}
}