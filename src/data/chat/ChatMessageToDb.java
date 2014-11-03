package data.chat;

public class ChatMessageToDb extends ChatMessage 
{
	public ChatId chatId;
	
	public ChatMessageToDb(String message, String userId, ChatId chatId) 
	{
		super(message, userId);
		
		this.chatId = chatId;
	}
}
