package data.base;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

/**
 * Basic chat summary: Does NOT include the actual chat contents. This object is
 * extended by both {@link ChatSummaryForScreen} and {@link ChatSummaryToDb}
 * which add additional information about depending on what's needed.
 * 
 * @author wsv759
 */
public class ChatSummary {
  	
    protected String title;
  	protected LatLng location;
  	protected ArrayList<String> tags;

	/**
	 * Create a new chat summary, containing all the info necessary for an inbox
	 * item in the inbox UI.
	 * 
	 * @param title
	 *            the title of the chat
	 * @param location
	 *            the location of the chat
	 * @param tags
	 *            all tags associated with the chat (to help filtering)
	 */
	public ChatSummary(String title, LatLng location, ArrayList<String> tags) {
		this.title = title;
		this.location = location;
		this.tags = tags;
	}

	protected ChatSummary() {
	}
	
    /**
     * Returns a list of tags
     */
    public ArrayList<String> getTags() {
        return tags;
    }
  
    /**
     * Returns the location of the chat to which this chat summary corresponds
     */
    public LatLng getLocation() {
        return location;
    }
    
    /**
     * Returns the title of the chat to which this chat summary corresponds
     */
    public String getTitle() {
        return title;
    }
}