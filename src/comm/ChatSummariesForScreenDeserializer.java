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

import data.app.map.ChatSummaryForScreen;
import data.base.ChatId;
import data.comm.map.ChatSummariesFromDb;

/**
 * Helps gson to deserialize the incoming json ChatSummary data from the
 * database.
 * 
 * @author wsv759
 *
 */
public class ChatSummariesForScreenDeserializer implements
		JsonDeserializer<ChatSummariesFromDb> {

	@Override
	public ChatSummariesFromDb deserialize(JsonElement json, Type arg1,
			JsonDeserializationContext arg2) throws JsonParseException {

		JsonObject obj = json.getAsJsonObject();
		JsonArray arr = obj.getAsJsonArray("chats");

		ChatSummaryForScreen[] chats = new ChatSummaryForScreen[arr.size()];

		Iterator<JsonElement> iter = arr.iterator();

		for (int i = 0; iter.hasNext(); i++) {
			JsonObject innerObj = iter.next().getAsJsonObject();

			String title = innerObj.get("title").getAsString();

			JsonElement lonElement = innerObj.get("longitude");
			JsonElement latElement = innerObj.get("latitude");
			LatLng location = new LatLng(latElement.getAsDouble(),
					lonElement.getAsDouble());

			JsonArray tagsJson = innerObj.get("tags").getAsJsonArray();
			ArrayList<String> tags = new ArrayList<String>(tagsJson.size());

			for (JsonElement tag : tagsJson)
				tags.add(tag.getAsString());

			String creatorId = innerObj.get("creatorId").getAsString();
			DateTimeFormatter formatter = DateTimeFormat
					.forPattern(HttpRequest.DATETIME_FORMAT);
			DateTime timeId = formatter.parseDateTime(innerObj.get("timeId")
					.getAsString());

			ChatId chatId = new ChatId(creatorId, timeId);

			// TODO: Get name from database
			String creatorUserName = innerObj.get("creatorUserName")
					.getAsString();

			int numMessages = innerObj.get("numMessages").getAsInt();

			int numMessagesRead = 0;

			DateTime lastMessageTime = formatter.parseDateTime(innerObj.get(
					"lastMessageTime").getAsString());

			chats[i] = new ChatSummaryForScreen(title, location, tags, chatId,
					creatorUserName, numMessages, numMessagesRead,
					lastMessageTime);
		}

		return new ChatSummariesFromDb(chats);
	}

}
