package data.app.map;


import java.util.ArrayList;

import org.joda.time.DateTime;

import com.google.android.gms.maps.model.LatLng;

import data.base.ChatId;
import data.base.ChatSummary;

/**
 * Individual chat, as displayed by the map screen. Only includes information relevant to the map screen.
 * Does NOT include the actual chat contents, as the map screen doesn't need those.
 * @author wsv759
 *
 */
public class ChatSummaryForScreen extends ChatSummary 
{
	public ChatId chatId;
	public String creatorUserName;
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
	
	/**
	 * @param currLocation the current location of the user's phone.
	 * @return string representing distance between this chat and the user's phone.
	 */
	public String getDistanceString(LatLng currLocation)
	{    
		return "50m away"; //TODO un-fake this.
	}
}
