package coderunners.geolocationalchat.test;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.http.entity.StringEntity;

import android.util.Log;
import data.base.UserIdNamePair;

public class userIdNamePairTestCase extends TestCase {
	UserIdNamePair test = new UserIdNamePair("testable", "testable2");
	
	public void testAsJsonStringEntity()
	{
		StringEntity se = test.asJsonStringEntity();
		
		Log.d("unitTests", se.toString());
		
		try {
			se.getContent();
		} catch (IOException e) {
			fail();
		}
	}
}
