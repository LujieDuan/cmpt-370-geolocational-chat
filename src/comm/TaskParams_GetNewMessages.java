package comm;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

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
	
	public String getHttpStringForm()
	{
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();

	    params.add(new BasicNameValuePair("userId", chatId.userId));
	    params.add(new BasicNameValuePair("timeId", chatId.getTimeIdString()));
		params.add(new BasicNameValuePair("lastMessageId", Integer.toString(lastMessageId)));
	
		return URLEncodedUtils.format(params, "utf-8");
	}
}
