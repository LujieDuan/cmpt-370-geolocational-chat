package data.chat;

import java.util.ArrayList;


public class Chat 
{	
	public ArrayList<ChatItem> chatItems = new ArrayList<ChatItem>();

	public void addMessages(ChatMessageForScreen... chatMessages)
	{
		for(ChatMessageForScreen message : chatMessages)
		{
			if(!chatItems.isEmpty() && chatItems.get(chatItems.size() - 1).isAddable(message))
			{
				chatItems.get(chatItems.size() - 1).addMessage(message);
			}
			else
			{
				chatItems.add(new ChatItem(message));
			}
		}
	}
	
	public ArrayList<ChatItem> getChatItems()
	{
	  return chatItems;
	}
	
	/**
	 * Returns the number of messages within the chat
	 */
	public int numMessages()
	{
	  int numMessages = 0;
	  for(int i=0; i<chatItems.size(); i++)
	  {
	    numMessages += chatItems.get(i).numMessages();
	  }
	  return numMessages;
	}
	
	/**
	 * Returns the ith {@link ChatMessageForScreen} within the chat
	 * @param i Index number of the message to be returned
	 */
	public ChatMessageForScreen getChatMessageForScreen(int i)
	{
	  int j = 0;
	  int k = 0;
	  
	  while(j + chatItems.get(k).numMessages() < i)
	  {
	    j += chatItems.get(k).numMessages();
	    k += 1;
	  }
	  
	  return chatItems.get(k).getChatMessageForScreen(i - j);
	}
}
