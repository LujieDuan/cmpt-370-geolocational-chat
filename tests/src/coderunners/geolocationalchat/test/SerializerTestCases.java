package coderunners.geolocationalchat.test;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import comm.HttpRequest;
import comm.gsonHelper.ChatMessageSerializer;
import comm.gsonHelper.ChatSummarySerializer;
import comm.gsonHelper.DateTimeDeserializer;

import data.app.chat.ChatMessageForScreen;
import data.base.ChatId;
import data.comm.chat.ChatMessageToDb;
import data.comm.chat.ChatMessagesFromDb;
import data.comm.chatCreation.ChatSummaryToDb;

public class SerializerTestCases extends TestCase {
	DateTimeFormatter formatter;
	DateTime testDt;
	LatLng testLatLng = new LatLng(50, 45);
	ArrayList<String> tags = new ArrayList<String>();
	ChatMessageToDb cmTest;
	ChatSummaryToDb csTest;
	
	@Override
	public void setUp()
	{
		formatter = DateTimeFormat
				.forPattern(HttpRequest.DATETIME_FORMAT);
		testDt = formatter.parseDateTime("2012-11-09 01:59:45");

		cmTest = new ChatMessageToDb("hi", "me", new ChatId("me", testDt));
		
		tags.add("sports");
		tags.add("event");
		csTest = new ChatSummaryToDb("title", testLatLng, tags, "fakeId", "hello", testDt);
	}
	
	public void testCMSerialize()
	{
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(ChatMessageToDb.class, new ChatMessageSerializer());
		builder.registerTypeAdapter(DateTime.class, new DateTimeDeserializer());
		Gson gson = builder.create();

		String cmJson = gson.toJson(cmTest, ChatMessageToDb.class);

		assertEquals(cmJson, "{\"creatorId\":\"me\",\"timeId\":\"2012-11-09 01:59:45\",\"userId\":\"me\",\"message\":\"hi\"}");
	}
	
	public void testCMDeserialize()
	{
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(DateTime.class, new DateTimeDeserializer());
		Gson gson = builder.create();
		
		ChatMessagesFromDb cmReference = new ChatMessagesFromDb();
		cmReference.messages = new ChatMessageForScreen[1];
		
		cmReference.messages[0] = new ChatMessageForScreen("woo go team", "f9465d7d38ea45f6", "Anonymous",6, testDt);
		
		String cmJson = "{\"messages\":[{\"messageId\":\"6\",\"time\":\"2012-11-09 01:59:45\",\"message\":\"woo go team\",\"userName\":\"Anonymous\",\"userId\":\"f9465d7d38ea45f6\"}],\"success\":1}";
		
		
		ChatMessagesFromDb cmTest = gson.fromJson(cmJson, ChatMessagesFromDb.class);
		ChatMessageForScreen testMsg = cmTest.messages[0];
		assertEquals(testMsg.getMessage(), "woo go team");
		assertEquals(testMsg.getMessageId(), 6);
		assertEquals(testMsg.getName(), "Anonymous");
		assertEquals(testMsg.getUserId(),"f9465d7d38ea45f6");
		assertEquals(testMsg.getTime().toString(formatter), testDt.toString(formatter));
	}
	
	public void testCSSerialize()
	{
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(ChatSummaryToDb.class, new ChatSummarySerializer());
		Gson gson = builder.create();
		
		String csJson = gson.toJson(csTest, ChatSummaryToDb.class);

		assertEquals(csJson, "{\"title\":\"title\",\"longitude\":45.0,\"latitude\":50.0,\"tags\":[\"sports\",\"event\"],\"userId\":\"fakeId\",\"firstMessage\":\"hello\",\"maxEndTime\":\"2012-11-09 01:59:45\"}");
	}
}
