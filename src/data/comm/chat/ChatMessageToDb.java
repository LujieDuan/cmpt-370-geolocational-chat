package data.comm.chat;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import comm.ChatMessageSerializer;
import comm.HttpPostEntity;
import data.base.ChatId;
import data.base.ChatMessage;

/**
 * Chat message, including all data that needs to be sent to the database.
 * @author wsv759
 *
 */
public class ChatMessageToDb extends ChatMessage implements HttpPostEntity
{
	public ChatId chatId;
	
	public ChatMessageToDb(String message, String userId, ChatId chatId) 
	{
		super(message, userId);
		
		this.chatId = chatId;
	}
	
	public StringEntity asJsonStringEntity() 
	{
		GsonBuilder gsonBuilder = new GsonBuilder(); 
		gsonBuilder.registerTypeAdapter(ChatMessageToDb.class, new ChatMessageSerializer());
	    Gson gson = gsonBuilder.create();
		
		String jsonString = gson.toJson(this);
		
		Log.d("dbConnect", "json string for new message to send: " + jsonString);
		StringEntity se = null;
		try {
			se = new StringEntity(jsonString);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return se;
	}
}
