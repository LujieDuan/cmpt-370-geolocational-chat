package data.comm;

import data.app.inbox.ChatSummaryForScreen;

/**
 * Helps gson to deserialize incoming json array of ChatSummary's.
 * @author wsv759
 *
 */
public class ChatSummariesFromDb 
{
	public ChatSummaryForScreen[] chats;
	
	public ChatSummariesFromDb(ChatSummaryForScreen[] chats)
	{
		this.chats = chats;
	}
}
