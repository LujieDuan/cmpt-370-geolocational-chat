package comm;

import org.apache.http.params.BasicHttpParams;

import data.chat.ChatId;



public class TaskParams_GetNewMessages extends HttpGetParams
{
	public ChatId chatId;
	public int lastMessageId;
	
	public TaskParams_GetNewMessages(ChatId chatId, int latestMessageId) 
	{
		this.chatId = chatId;
		this.lastMessageId = latestMessageId;
	}
	
	public BasicHttpParams getHttpParamsForm()
	{
		BasicHttpParams params = new BasicHttpParams();
		
		params.setParameter("userId", chatId.userId);
		params.setParameter("timeId", chatId.getTimeIdString());
		params.setIntParameter("lastMessageId", lastMessageId);
		
		return params;
	}
}
