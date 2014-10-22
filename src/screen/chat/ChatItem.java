package screen.chat;

import java.util.ArrayList;

public class ChatItem {

	public String name;
	public ArrayList<String> messages = new ArrayList<String>();
	public String time;
	public String distance;
	
	public ChatItem(String name, String message, String time, String distance)
	{
		this.name = name;
		this.messages.add(message);
		this.time = time;
		this.distance = distance;
	}
}
