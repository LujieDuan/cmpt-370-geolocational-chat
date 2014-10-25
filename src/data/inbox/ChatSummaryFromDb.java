package data.inbox;

import android.location.Location;

public class ChatSummaryFromDb extends ChatSummary 
{
	public String chatId;
	public String creatorUserName;
	
	/**
	 * Create a new chat summary, containing all the info necessary for an inbox item in the inbox UI.
	 * @param title the title of the chat
	 * @param location the location of the chat
	 * @param tags all tags associated with the chat (to help filtering)
	 * @param chatId String chatId, as assigned by the database.
	 * @param creatorUserName the alias of the one who created the chat
	 */
	public ChatSummaryFromDb(String title, Location location, String[] tags, 
			String chatId, String creatorUserName) 
	{
		super(title, location, tags);
		
		this.chatId = chatId;
		this.creatorUserName = creatorUserName;
	}

}
