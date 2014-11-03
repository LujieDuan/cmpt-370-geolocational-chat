package comm;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;

import com.google.gson.Gson;

import data.newChatCreation.ChatSummaryToDb;

public class TaskParams_SendNewChat extends HttpPutEntity
{
	public ChatSummaryToDb newChatSummary;
	
	public TaskParams_SendNewChat(ChatSummaryToDb newChatSummary) 
	{
		this.newChatSummary = newChatSummary;
	}

	@Override
	public StringEntity asStringEntity() {
		Gson gson = new Gson();

		String jsonString = gson.toJson(newChatSummary);

		StringEntity se = null;
		try {
			se = new StringEntity(jsonString);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return se;
	}
}
