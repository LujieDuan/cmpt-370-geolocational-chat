package comm;

import data.chat.ChatMessageToSendToDb;

public class TaskParams_SendNewMessage {

	public ChatMessageToSendToDb newChatMessage;
	
	public TaskParams_SendNewMessage(ChatMessageToSendToDb newChatMessage) {
		this.newChatMessage = newChatMessage;
	}

}
