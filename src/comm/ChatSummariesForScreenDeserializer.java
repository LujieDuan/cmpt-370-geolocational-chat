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

import data.chat.ChatId;
import data.inbox.ChatSummariesForScreen;
import data.inbox.ChatSummaryForScreen;

public class ChatSummariesForScreenDeserializer implements
		JsonDeserializer<ChatSummariesForScreen> {

	@Override
	public ChatSummariesForScreen deserialize(JsonElement arg0, Type arg1,
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
			
			String creatorUserName = "Joseph";
			
			int numMessages = innerObj.get("numMessages").getAsInt();
			
			int numMessagesRead = 0;
			
			DateTime lastMessageTime = formatter.parseDateTime("2000-01-01 20:20:20");
			
			chats[i] = new ChatSummaryForScreen(title, location, tags, chatId, 
					creatorUserName, numMessages, numMessagesRead, lastMessageTime); 
		}
		
		return new ChatSummariesForScreen(chats);
	}

}
