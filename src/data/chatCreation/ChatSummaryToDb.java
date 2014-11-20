package data.chatCreation;

import org.joda.time.DateTime;

import com.google.android.gms.maps.model.LatLng;

import data.inbox.ChatSummary;

public class ChatSummaryToDb extends ChatSummary 
{
	public String userId;
	public String firstMessage;
	public DateTime maxEndTime;
	
	/**
	 * Create a new 'new chat summary', 
	 * containing all the info necessary for an inbox item in the inbox UI.
	 * @param title the title of the chat
	 * @param creatorUserName the alias of the one who created the chat
	 * @param location the location of the chat
	 * @param tags all tags associated with the chat (to help filtering)
	 * @param firstMessage string representing the first message of the chat.
	 * @param range int representing the range in meters of visibility for the new chat.
	 * @param maxEndTime DateTime representing the maximum time that will pass before this chat is deleted.
	 */
	public ChatSummaryToDb(String title, LatLng location, String[] tags, 
			String creatorUserId, String firstMessage, DateTime maxEndTime)
	{
		super(title, location, tags);
		
		this.userId = creatorUserId;
		this.firstMessage = firstMessage;
		this.maxEndTime = maxEndTime;
	}
}
