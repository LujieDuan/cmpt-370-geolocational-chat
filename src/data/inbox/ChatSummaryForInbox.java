package data.inbox;

import org.joda.time.DateTime;


import android.location.Location;

public class ChatSummaryForInbox extends ChatSummaryFromDb
{
	public int numMessages;
	public int numMessagesRead;
	public DateTime latestMessageTime;
	
	/**
	 * Create a new chat summary, containing all the info necessary for an inbox item in the inbox UI.
	 * @param title the title of the chat
	 * @param location the location of the chat
	 * @param tags all tags associated with the chat (to help filtering)
	 * @param chatId String chatId, as assigned by the database.
	 * @param creatorUserName the alias of the one who created the chat
	 * @param numMessages the total number of messages in the chat
	 * @param numMessagesRead the number of messages of the chat that the phone has already displayed
	 * @param latestMessageTime DateTime representing the time of the most recent message posted in the chat.
	 */
	public ChatSummaryForInbox(String title, Location location, String[] tags, String userId, DateTime timeId, 
			String creatorUserName, int numMessages, int numMessagesRead, DateTime latestMessageTime)
	{
		super(title, location, tags, userId, timeId, creatorUserName);
		
		this.numMessages = numMessages;
		this.numMessagesRead = numMessagesRead;
		this.latestMessageTime = latestMessageTime;
	}
}
