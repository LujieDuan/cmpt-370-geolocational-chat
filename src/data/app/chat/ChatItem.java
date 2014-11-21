package data.app.chat;

import java.util.ArrayList;
import org.joda.time.DateTime;

/**
 * This object represents a set of {@link ChatMessageForScreen ChatMessages} 
 * that have been sent by a single from a similar time and location. This allows 
 * messages to be grouped together when displayed.
 */
public class ChatItem {

    public ArrayList<ChatMessageForScreen> messages = new ArrayList<ChatMessageForScreen>();
  
    public ChatItem(ChatMessageForScreen... chatMessages)
    {
      for(ChatMessageForScreen message: chatMessages)
      {
        addMessage(message);
      }
    }
    
    public int numMessages()
    {
      return messages.size();
    }
    
    public ChatMessageForScreen getChatMessageForScreen(int i)
    {
      return messages.get(i);
    }
    
	public String getName()
	{
	  return messages.get(0).userName;
	}
	
	public String getUserId()
	{
		return messages.get(0).userId;
	}
	
	public String getMessage(int i)
	{
	  return messages.get(i).message;
	}
	
	public DateTime getTime()
    {
      return messages.get(messages.size() - 1).time;
    }
	
	public String getTimeString(DateTime currTime)
	{
	  return messages.get(messages.size() - 1).getTimeString(currTime);
	}
	
	public boolean isAddable(ChatMessageForScreen chatMessage)
	{
	  return messages.isEmpty() || messages.get(0).userId.equals(chatMessage.userId);
	}
	
	public void addMessage(ChatMessageForScreen chatMessage)
	{
	  if(!isAddable(chatMessage))
	  {
	    throw new IllegalArgumentException();
	  }
	  else
	  {
	    messages.add(chatMessage);
	  }
	}
}
