package comm;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;

import com.google.gson.Gson;

import data.chat.ChatMessageToDb;

public class TaskParams_SendNewMessage extends HttpPutEntity {

	public ChatMessageToDb newChatMessage;
	
	public TaskParams_SendNewMessage(ChatMessageToDb newChatMessage) {
		this.newChatMessage = newChatMessage;
	}

	@Override
	public StringEntity asStringEntity() 
	{
		Gson gson = new Gson();
		
		String jsonString = gson.toJson(newChatMessage);
		
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
