package data.chat;

public class ChatMessageToSendToDb extends ChatMessage 
{
	public String timeId;
	
	public ChatMessageToSendToDb(String message, String userId, String timeId) 
	{
		super(message, userId);
		
		this.timeId = timeId;
	}
}
