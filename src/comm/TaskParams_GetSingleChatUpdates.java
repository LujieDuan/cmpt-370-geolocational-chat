package comm;

public class TaskParams_GetSingleChatUpdates 
{
	public String chatId;
	public int latestMessageId;
	
	public TaskParams_GetSingleChatUpdates(String chatId, int latestMessageId) 
	{
		this.chatId = chatId;
		this.latestMessageId = latestMessageId;
	}
}
