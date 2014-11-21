package comm;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import data.app.chat.ChatId;
import data.app.inbox.ChatSummaryForScreen;
import data.comm.inbox.ChatSummariesFromDb;

/**
 * Helps gson to deserialize the incoming json ChatSummary data from the database.
 * @author wsv759
 *
 */
public class ChatSummariesForScreenDeserializer implements
		JsonDeserializer<ChatSummariesFromDb> {

	@Override
	public ChatSummariesFromDb deserialize(JsonElement arg0, Type arg1,
			JsonDeserializationContext arg2) throws JsonParseException {
	
		JsonObject obj = arg0.getAsJsonObject();
		JsonArray arr = obj.getAsJsonArray("chats");
		
		ChatSummaryForScreen[] chats = new ChatSummaryForScreen[arr.size()];
		
		
		Iterator<JsonElement> iter = arr.iterator();
		
		for (int i = 0; iter.hasNext(); i++)
		{
			JsonObject innerObj = iter.next().getAsJsonObject();
			
			String title = innerObj.get("title").getAsString();
			
			JsonElement lonElement = innerObj.get("longitude");
			JsonElement latElement = innerObj.get("latitude");
			LatLng location = new LatLng(latElement.getAsDouble(), lonElement.getAsDouble());
			
			ArrayList<String> tags = new ArrayList<String>();
			
			String creatorId = innerObj.get("creatorId").getAsString();
			DateTimeFormatter formatter = DateTimeFormat.forPattern(HttpRequest.DATETIME_FORMAT);
			DateTime timeId = formatter.parseDateTime(innerObj.get("timeId").getAsString());
			
			ChatId chatId = new ChatId(creatorId, timeId);
			
			//TODO: Get name from database
			String creatorUserName = "Joseph";
			
			int numMessages = innerObj.get("numMessages").getAsInt();
			
			int numMessagesRead = 0;
			
			//TODO: Get time from database
			DateTime lastMessageTime = formatter.parseDateTime("2000-01-01 20:20:20");
			
			chats[i] = new ChatSummaryForScreen(title, location, tags, chatId, 
					creatorUserName, numMessages, numMessagesRead, lastMessageTime); 
		}
		
		return new ChatSummariesFromDb(chats);
	}

}
