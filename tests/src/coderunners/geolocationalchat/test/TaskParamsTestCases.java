package coderunners.geolocationalchat.test;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import comm.HttpRequest;
import comm.httpParams.TaskParams_GetInbox;
import comm.httpParams.TaskParams_GetNewMessages;
import data.base.ChatId;

public class TaskParamsTestCases extends TestCase {
	ArrayList<String> testTags = new ArrayList<String>();
	LatLng testLatLng = new LatLng(50, 45);
	TaskParams_GetInbox testGetInbox;
	DateTime testDt;
	ChatId testChatId;
	TaskParams_GetNewMessages testGetMsgs;
	
	@Override
	public void setUp()
	{
		DateTimeFormatter formatter = DateTimeFormat
				.forPattern(HttpRequest.DATETIME_FORMAT);
		testDt = formatter.parseDateTime("2012-11-09 01:59:45");
		testChatId = new ChatId("testChat", testDt);
		testTags.add("a");
		testTags.add("b");
		testGetInbox = new TaskParams_GetInbox(testLatLng, testTags);
		testGetMsgs = new TaskParams_GetNewMessages(testChatId, 5);
	}
	
	public void testGetInbox()
	{
		Log.d("unitTests", testGetInbox.getHttpStringForm());
		assertEquals(testGetInbox.getHttpStringForm(),"latitude=50.0&longitude=45.0&tags%5B%5D=");
		
	}
	
	public void testGetMsgs()
	{
		Log.d("unitTests", testGetMsgs.getHttpStringForm());
		assertEquals(testGetMsgs.getHttpStringForm(),"creatorId=testChat&timeId=2012-11-09+01%3A59%3A45&lastMessageId=5");
	}
}
