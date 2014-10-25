package data.inbox;

import android.location.Location;

public class ChatSummary
{
	public String title;
	public Location location;
	public String[] tags;
	
	/**
	 * Create a new chat summary, containing all the info necessary for an inbox item in the inbox UI.
	 * @param title the title of the chat
	 * @param location the location of the chat
	 * @param tags all tags associated with the chat (to help filtering)
	 */
	public ChatSummary(String title, Location location, String[] tags)
	{
		this.title = title;
		this.location = location;
		this.tags = tags;
	}

	public String getDistanceString(Location currLocation)
	{    
		float distance = currLocation.distanceTo(location);
		return distance + "m away";
	}
}