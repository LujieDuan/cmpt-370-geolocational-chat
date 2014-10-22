package data.chat;

public class ChatSummary {
  
  public ChatMessage chatMessage;
  public int numMessages;
  public int numMessagesRead;
  
  public ChatSummary(ChatMessage chatMessage, int numMessages, int numMessagesRead)
  {
    this.chatMessage = chatMessage;
    this.numMessages = numMessages;
    this.numMessagesRead = numMessagesRead;
  }
  
}
