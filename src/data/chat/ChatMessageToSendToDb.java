package data.chat;

public class ChatMessageToSendToDb extends ChatMessage 
{
	public ChatId chatId;
	
	public ChatMessageToSendToDb(String message, String userId, ChatId chatId) 
	{
		super(message, userId);
		
		this.chatId = chatId;
	}
}
