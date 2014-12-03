package data.base;

import data.app.chat.ChatMessageForScreen;
import data.comm.chat.ChatMessageToDb;

/**
 * Basic chat message. This is extended by both {@link ChatMessageForScreen} and
 * {@link ChatMessageToDb} which add additional information to the object
 * depending upon what's neede.d
 * 
 * @author wsv759
 */
public class ChatMessage {

	protected String message;
	protected String userId;

	/**
	 * Constructs a new {@link ChatMessage}
	 * 
	 * @param message
	 *            The message
	 * @param userId
	 *            The phone ID of the user
	 */
	public ChatMessage(String message, String userId) {
		this.message = message;
		this.userId = userId;
	}
	
	public String getUserId()
	{
	  return userId;
	}
	
	public String getMessage()
	{
	  return message;
	}
}
