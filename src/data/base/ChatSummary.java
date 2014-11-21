package data.base;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

/**
 * Basic chat summary: Does NOT include the actual chat contents.
 * @author wsv759
 *
 */
public class ChatSummary
{
	public String title;
	public LatLng location;
	public ArrayList<String> tags;
	
	/**
	 * Create a new chat summary, containing all the info necessary for an inbox item in the inbox UI.
	 * @param title the title of the chat
	 * @param location the location of the chat
	 * @param tags all tags associated with the chat (to help filtering)
	 */
	public ChatSummary(String title, LatLng location, ArrayList<String> tags)
	{
		this.title = title;
		this.location = location;
		this.tags = tags;
	}
}