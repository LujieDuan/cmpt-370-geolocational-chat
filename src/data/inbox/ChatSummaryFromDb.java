package data.inbox;

import org.joda.time.DateTime;

import android.location.Location;

public class ChatSummaryFromDb extends ChatSummary 
{
	public String userId;
	public DateTime timeId;
	public String creatorUserName;
	
	/**
	 * Create a new chat summary, containing all the info necessary for an inbox item in the inbox UI.
	 * @param title the title of the chat
	 * @param location the location of the chat
	 * @param tags all tags associated with the chat (to help filtering)
	 * @param chatId as assigned by the database.
	 * @param creatorUserName the alias of the one who created the chat
	 */
	public ChatSummaryFromDb(String title, Location location, String[] tags, 
			String userId, DateTime timeId, String creatorUserName) 
	{
		super(title, location, tags);
		
		this.userId = userId;
		this.timeId = timeId;
		this.creatorUserName = creatorUserName;
	}

}
