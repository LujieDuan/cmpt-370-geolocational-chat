package comm;

import data.newChatCreation.ChatSummaryToSendToDb;

public class TaskParams_SendNewChat
{
	public ChatSummaryToSendToDb newChatSummary;
	
	public TaskParams_SendNewChat(ChatSummaryToSendToDb newChatSummary) 
	{
		this.newChatSummary = newChatSummary;
	}
}
