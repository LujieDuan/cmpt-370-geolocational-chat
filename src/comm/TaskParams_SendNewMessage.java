package comm;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.joda.time.DateTime;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import data.chat.ChatMessageToDb;

public class TaskParams_SendNewMessage extends HttpPostEntity {

	public ChatMessageToDb newChatMessage;
	
	public TaskParams_SendNewMessage(ChatMessageToDb newChatMessage) {
		this.newChatMessage = newChatMessage;
	}

	@Override
	public StringEntity asJsonStringEntity() 
	{
		GsonBuilder gsonBuilder = new GsonBuilder(); 
		gsonBuilder.registerTypeAdapter(ChatMessageToDb.class, new ChatMessageSerializer());
	    Gson gson = gsonBuilder.create();
		
		String jsonString = gson.toJson(newChatMessage);
		
		Log.d("dbConnect", "send string: " + jsonString);
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
