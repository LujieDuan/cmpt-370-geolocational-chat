package data.inbox;


import java.util.ArrayList;

import org.joda.time.DateTime;

import com.google.android.gms.maps.model.LatLng;

import data.chat.ChatId;

public class ChatSummaryForScreen extends ChatSummary 
{
	public ChatId chatId;
	public String creatorUserName = "Todd";
	public int numMessages;
	public DateTime lastMessageTime;
	public int numMessagesRead = 0;
	
	/**
	 * Create a new chat summary, containing all the info necessary for an inbox item in the inbox UI.
	 * @param title the title of the chat
	 * @param location the location of the chat
	 * @param tags all tags associated with the chat (to help filtering)
	 * @param chatId as assigned by the database.
	 * @param creatorUserName the alias of the one who created the chat
	 */
	public ChatSummaryForScreen(String title, LatLng location, ArrayList<String> tags, 
			ChatId chatId, String creatorUserName, int numMessages, int numMessagesRead, DateTime lastMessageTime) 
	{
		super(title, location, tags);
		
		this.chatId = chatId;
		this.creatorUserName = creatorUserName;
		this.numMessages = numMessages;
		this.numMessagesRead = numMessagesRead;
		this.lastMessageTime = lastMessageTime;
		
	}

}
