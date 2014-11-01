package comm;

import org.joda.time.DateTime;


public class TaskParams_GetNewMessages 
{
	public String chatCreatorId;
	public DateTime chatTimeId;
	public int latestMessageId;
	
	public TaskParams_GetNewMessages(String chatCreatorId, DateTime chatTimeId, int latestMessageId) 
	{
		this.chatCreatorId = chatCreatorId;
		this.chatTimeId = chatTimeId;
		this.latestMessageId = latestMessageId;
	}
}
