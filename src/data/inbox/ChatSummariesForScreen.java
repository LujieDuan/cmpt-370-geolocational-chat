package data.inbox;

import com.google.gson.annotations.Expose;

public class ChatSummariesForScreen 
{
	public ChatSummaryForScreen[] chats;
	
	public ChatSummariesForScreen(ChatSummaryForScreen[] chats)
	{
		this.chats = chats;
	}
}
