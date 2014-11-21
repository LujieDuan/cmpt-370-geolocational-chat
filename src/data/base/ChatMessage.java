package data.base;

/**
 * Basic chat message.
 * @author wsv759
 *
 */
public class ChatMessage {

  public String message;
  public String userId;
  
  public ChatMessage(String message, String userId)
  {
    this.message = message;
    this.userId = userId;
  }
}
