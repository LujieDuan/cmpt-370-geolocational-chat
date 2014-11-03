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
}
