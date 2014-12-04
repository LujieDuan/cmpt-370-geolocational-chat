package coderunners.geolocationalchat.test;

import junit.framework.TestCase;

import org.joda.time.DateTime;

import android.os.Parcel;
import data.base.ChatId;

public class ChatIdTestCase extends TestCase
{
	DateTime testDt;
	ChatId chatId;

	@Override
	public void setUp()
	{	  
		testDt = new DateTime();
		chatId = new ChatId("testChat", testDt);
	}

	public void testCreateFromParcel()
	{	
	    Parcel parcel = Parcel.obtain();
	    chatId.writeToParcel(parcel, 0);

	    parcel.setDataPosition(0);

	    ChatId createdFromParcel = ChatId.CREATOR.createFromParcel(parcel);
	    assertEquals(chatId, createdFromParcel);
	    
	    parcel.recycle();
	}
}
