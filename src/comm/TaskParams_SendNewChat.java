package comm;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import data.chatCreation.ChatSummaryToDb;

public class TaskParams_SendNewChat extends HttpPostEntity
{
	public ChatSummaryToDb newChatSummary;
	
	public TaskParams_SendNewChat(ChatSummaryToDb newChatSummary) 
	{
		this.newChatSummary = newChatSummary;
	}

	@Override
	public StringEntity asJsonStringEntity() {
		GsonBuilder gsonBuilder = new GsonBuilder(); 
		gsonBuilder.registerTypeAdapter(ChatSummaryToDb.class, new ChatSummarySerializer());
	    Gson gson = gsonBuilder.create();

		String jsonString = gson.toJson(newChatSummary);
		
		Log.d("dbConnect","json string for new chat to send: " + jsonString);
		StringEntity se = null;
		try {
			se = new StringEntity(jsonString);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return se;
	}
}
