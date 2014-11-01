package data.chat;

import org.joda.time.DateTime;

public class ChatId {
	
	public String userId;
	public DateTime timeId;
	public ChatId(String userId, DateTime timeId) 
	{
		this.userId = userId;
		this.timeId = timeId;
	}
	
	public String getTimeIdString()
	{
		return timeId.toString("yyyy-MM-dd HH:mm:ss");
	}
}
