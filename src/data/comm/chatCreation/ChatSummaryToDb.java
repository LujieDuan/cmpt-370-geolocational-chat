package data.comm.chatCreation;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.entity.StringEntity;
import org.joda.time.DateTime;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import comm.ChatSummarySerializer;
import comm.HttpPostEntity;
import data.ChatSummary;

/**
 * Chat Summary, including all data that needs to be sent to the database.
 * This is what a user creates when they create a new chat.
 * @author wsv759
 *
 */
public class ChatSummaryToDb extends ChatSummary implements HttpPostEntity
{
	public String userId;
	public String firstMessage;
	public DateTime maxEndTime;
	
	/**
	 * Create a new 'new chat summary', 
	 * containing all the info necessary for an inbox item in the inbox UI.
	 * @param title the title of the chat
	 * @param creatorUserName the alias of the one who created the chat
	 * @param location the location of the chat
	 * @param tags all tags associated with the chat (to help filtering)
	 * @param firstMessage string representing the first message of the chat.
	 * @param range int representing the range in meters of visibility for the new chat.
	 * @param maxEndTime DateTime representing the maximum time that will pass before this chat is deleted.
	 */
	public ChatSummaryToDb(String title, LatLng location, ArrayList<String> tags, 
			String userId, String firstMessage, DateTime maxEndTime)
	{
		super(title, location, tags);
		
		this.userId = userId;
		this.firstMessage = firstMessage;
		this.maxEndTime = maxEndTime;
	}
	
	public StringEntity asJsonStringEntity() {
		GsonBuilder gsonBuilder = new GsonBuilder(); 
		gsonBuilder.registerTypeAdapter(ChatSummaryToDb.class, new ChatSummarySerializer());
	    Gson gson = gsonBuilder.create();

		String jsonString = gson.toJson(this);
		
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
