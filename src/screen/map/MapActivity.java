package screen.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import screen.chat.ChatActivity;
import screen.chatCreation.ChatCreationActivity;
import screen.inbox.InboxActivity;
import screen.settings.SettingsActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import coderunners.geolocationalchat.R;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import comm.ChatSummariesForScreenDeserializer;
import comm.HttpRequest;
import comm.TaskParams_GetInbox;

import data.UserIdNamePair;
import data.chat.ChatId;
import data.inbox.ChatSummariesForScreen;
import data.inbox.ChatSummaryForScreen;

public class MapActivity extends ActionBarActivity {

	static HashMap<String, ChatSummaryForScreen> chatSummaryMap = new HashMap<String, ChatSummaryForScreen>();
	
	public static final String SETTINGS_FILE_NAME = "GeolocationalChatStoredSettings";

	public static String DEVICE_ID;
	public static String USER_NAME = "John";
	public static UserIdNamePair USER_ID_AND_NAME;
	
	private static final String GET_INBOX_URI = "http://cmpt370duan.byethost10.com/getchs.php"; 
	public static final String TAG_SUCCESS = "success";
	private static final String TAG_CHATSUMMARY_ARRAY = "chats"; 
	
	private static final int GET_INBOX_DELAY_SECONDS = 30;
	
	Marker selectedMarker = null;
	boolean selectionAvailable = true;
	
	static GoogleMap map;
	static ArrayList<Marker> markerList = new ArrayList<Marker>();
	static final float triangleScreenSizeX = 0.05f;
	static final float triangleScreenSizeY = (float) (triangleScreenSizeX * Math.sqrt(0.75));

	static final float bubbleUnselectedScreenSizeMin = 0.10f;
	static final float bubbleUnselectedScreenSizeMax = 0.20f;

	static final float bubbleSelectedScreenSizeX = 0.67f;
	static final float bubbleSelectedScreenSizeY = 0.33f;

	final int MARKER_UPDATE_INTERVAL = 2000; /* milliseconds */
	Handler handler = new Handler();

	Runnable updateMarker = new Runnable() {
		@Override
		public void run() {
			selectionAvailable = false;

			long startTime = System.currentTimeMillis();
			long endTime = startTime + 1000;
			long currTime;

			do {
				try {
					Thread.sleep(33);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				currTime = Math.min(System.currentTimeMillis(), endTime);
				float percentElapsed = (currTime - startTime) / (endTime - startTime);

				selectedMarker.setPosition(new LatLng(52.1310799, -106.6341388 + (-106.6341388 - -106.6241388) * percentElapsed));
			} while (currTime < endTime);

			selectionAvailable = true;
		}
	};

	// TODO:
	ArrayList<ChatSummaryForScreen> generateChatSummaries() {
		ArrayList<ChatSummaryForScreen> chatSummaries = new ArrayList<ChatSummaryForScreen>();

		LatLng location = new LatLng(52.1310799,-106.6341388);

		chatSummaries.add(new ChatSummaryForScreen("Anyone up for ultimate frisbee?", location, new String[]{"sports", "fun"},
				new ChatId(InboxActivity.DEVICE_ID, new DateTime()),"Josh Heinrichs", 40, 40, new DateTime()));

		location = new LatLng(52.1310799,-106.6241388);

		chatSummaries.add(new ChatSummaryForScreen("Anyone up for MORE ultimate frisbee?", location, new String[]{"sports", "fun"},
				new ChatId(InboxActivity.DEVICE_ID, new DateTime()),"Josh Heinrichs", 80, 40, new DateTime()));

		return chatSummaries;
	}

	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu; this adds items to the action bar if it is present.
	    getMenuInflater().inflate(R.menu.main, menu);
	    return true;
	  }

	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId())
	    {
	      case R.id.action_chat_creation:
	        startActivity(new Intent(getApplicationContext(), ChatCreationActivity.class));
	        break;
	      case R.id.action_settings:
	        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
	        break;
	    }
	    return super.onOptionsItemSelected(item);
	  }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_activity);
		
		SharedPreferences settings = getSharedPreferences(SETTINGS_FILE_NAME, 0);
		boolean silent = settings.getBoolean("silentMode", false);
		
		String device_id = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
    	USER_ID_AND_NAME = new UserIdNamePair(device_id, device_id);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.1310799, -106.6341388), 14));

		// draw user view distance
		map.addCircle(new CircleOptions().center(new LatLng(52.1310799, -106.6341388)).radius(1000)
				.strokeColor(Color.argb(60, 255, 40, 50)).strokeWidth(5)
				.fillColor(Color.argb(30, 255, 40, 50)));
		
		// create markers
//		for (int i = 0; i < chatSummaries.size(); i++) {
//			ChatSummaryForScreen chatSummary = chatSummaries.get(i);
//
//			LatLng location = new LatLng(chatSummary.location.latitude, chatSummary.location.longitude); 
//
//			Marker marker =
//					map.addMarker(new MarkerOptions()
//					.icon(BitmapDescriptorFactory.fromBitmap(createMarkerIcon(chatSummary)))
//					.anchor(0.5f, 1.0f) // Anchors the marker on the bottom left
//					.position(location));
//			chatSummaryMap.put(marker.getId(), chatSummary);
//		}
	    ScheduledThreadPoolExecutor chatUpdateScheduler = new ScheduledThreadPoolExecutor(1);
	    chatUpdateScheduler.scheduleWithFixedDelay(new GetInboxTask(), 0, GET_INBOX_DELAY_SECONDS, TimeUnit.SECONDS);

		map.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				if (selectedMarker != null && selectedMarker.getId().equals(marker.getId())) {
					Intent chatScreenIntent = new Intent(MapActivity.this, ChatActivity.class);
					ChatId curChatId = chatSummaryMap.get(marker.getId()).chatId;
					Log.d("intents", "chatId1: " + curChatId.toString());
					chatScreenIntent.putExtra(ChatActivity.CHATID_STRING,curChatId);
					startActivity(chatScreenIntent);
				} else if (selectionAvailable) {
					deselectMarker();
					selectedMarker = marker;
					animateMarkerSelection(marker, getWindowManager().getDefaultDisplay());
					//          AnimateMarkerSelect animateMarkfinalerSelect = new AnimateMarkerSelect();
					//          animateMarkerSelect.doInBackground(marker.getId());
				}        
				return false;
			}

		});

		map.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng point) {
				deselectMarker();
			}

		});
	}

	void deselectMarker() {
		if (selectedMarker != null) {
			AnimateMarkerDeselect animateMarkerDeselected = new AnimateMarkerDeselect();
			animateMarkerDeselected.doInBackground(new String(selectedMarker.getId()));
			selectedMarker = null;
		}
	}

	Bitmap createMarkerIcon(ChatSummaryForScreen chatSummary) {
		// todo: create function for bubble size

		int numRepliesUnread = chatSummary.numMessages - chatSummary.numMessagesRead;

		float triangleWidth = 30;
		float triangleHeight = (float) (triangleWidth * Math.sqrt(0.75));

		String strText = Integer.toString(numRepliesUnread);

		Paint paintText = new Paint();
		paintText.setColor(getResources().getColor(R.color.chat_me_foreground));
		paintText.setTextSize(10 + chatSummary.numMessages);
		paintText.setTextAlign(Paint.Align.CENTER);
		paintText.setAntiAlias(true);

		Rect boundsText = new Rect();
		paintText.getTextBounds(strText, 0, strText.length(), boundsText);

		int maxBounds = Math.max(boundsText.width(), boundsText.height());

		Bitmap image =
				Bitmap.createBitmap((int) (maxBounds * 1.75), (int) (maxBounds * 1.75)
						+ (int) triangleHeight / 2, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(image);

		Point trianglePosition = new Point();
		trianglePosition.x = (int) (image.getWidth() / 2 - triangleWidth / 2);
		trianglePosition.y = (int) (image.getHeight() - triangleHeight);

		Path triangle = new Path();
		triangle.moveTo(trianglePosition.x, trianglePosition.y);
		triangle.lineTo(trianglePosition.x + triangleWidth * 1.0f, trianglePosition.y);
		triangle.lineTo(trianglePosition.x + triangleWidth * 0.5f, trianglePosition.y + triangleHeight);

		Paint paintShape = new Paint();
		paintShape.setColor(getResources().getColor(R.color.chat_me_background));
		paintShape.setAntiAlias(true);

		canvas.drawPath(triangle, paintShape);
		canvas.drawCircle(canvas.getWidth() / 2, canvas.getWidth() / 2, canvas.getWidth() / 2,
				paintShape);
		canvas.drawText(strText, canvas.getWidth() / 2,
				canvas.getWidth() / 2 + boundsText.height() / 2, paintText);

		return image;
	}

	private class AnimateMarkerSelect extends AsyncTask<Marker, Void, Void> {

		@Override
		protected Void doInBackground(Marker... markers) {
			selectionAvailable = false;

			Marker marker = markers[0];
			
			ChatSummaryForScreen chatSummary = chatSummaryMap.get(marker.getId());

			long startTime = System.currentTimeMillis();
			long endTime = startTime + 1000;
			long currTime;

			// TODO: Make size relative to screen size, add text, make formula for bubble size
			Display display = getWindowManager().getDefaultDisplay();
			Point screenSize = new Point();
			display.getSize(screenSize);

			// TODO: Instead of using screen.x, use smallest screen dim?
			Point triangleSize =
					new Point((int) (screenSize.x * triangleScreenSizeX),
							(int) (screenSize.x * triangleScreenSizeY));

			Point bubbleSizeInitial =
					new Point((int) (screenSize.x * 0.20 + chatSummary.numMessages),
							(int) (screenSize.x * 0.20 + chatSummary.numMessages));

			do {
				try {
					Thread.sleep(33);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				currTime = Math.min(System.currentTimeMillis(), endTime);
				float percentElapsed = (currTime - startTime) / (endTime - startTime);

				//        Point bubbleSize =
				//            new Point(100 + (int) ((screenSize.x * 0.67 - 100) * percentElapsed),
				//                100 + (int) ((screenSize.x * 0.33 - 100) * percentElapsed));
				//
				//        Bitmap image =
				//            Bitmap.createBitmap(bubbleSize.x, bubbleSize.y + triangleSize.y / 2,
				//                Bitmap.Config.ARGB_8888);
				//
				//        float radius = 50 - 30 * percentElapsed;
				//        float[] radii = {radius, radius, radius, radius, radius, radius, radius, radius};
				//        RoundfinalRectShape bubble = new RoundRectShape(radii, null, null);
				//
				//        bubble.resize(bubbleSize.x, bubbleSize.y);
				//
				//        Point trianglePosition =
				//            new Point((int) (image.getWidth() / 2 - triangleSize.x / 2),
				//                (int) (image.getHeight() - triangleSize.y));
				//
				//        Path triangle = new Path();
				//        triangle.moveTo(trianglePosition.x, trianglePosition.y);
				//        triangle.lineTo(trianglePosition.x + triangleSize.x, trianglePosition.y);
				//        triangle.lineTo(trianglePosition.x + triangleSize.x * 0.5f, trianglePosition.y
				//            + triangleSize.y);
				//
				//        Paint paint = new Paint();
				//        paint.setAntiAlias(true);
				//        paint.setColor(getResources().getColor(R.color.chat_me_background));
				//
				//        Canvas canvas = new Canvas(image);
				//        bubble.draw(canvas, paint);
				//        canvas.drawPath(triangle, paint);
				//        marker.setIcon(BitmapDescriptorFactory.fromBitmap(image));

				marker.setPosition(new LatLng(52.1310799, -106.6341388 + (-106.6341388 - -106.6241388) * percentElapsed));
			} while (currTime < endTime);

			selectionAvailable = true;
			return null;
		}
	}

	private class AnimateMarkerDeselect extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... markerIds) {
			Marker marker = selectedMarker;
			//TODO uncomment this after importing google maps.
			marker.setIcon(BitmapDescriptorFactory.fromBitmap(createMarkerIcon(chatSummaryMap.get(marker
					.getId()))));
			return null;
		}
	}

	static void animateMarkerSelection(final Marker marker, final Display display)
	{
		final LatLng startPosition = marker.getPosition();
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		final float durationInMs = 1000;
		final ChatSummaryForScreen chatSummary = chatSummaryMap.get(marker.getId());

		handler.post(new Runnable()
		{

			long elapsed;
			float t;

			@Override
			public void run() {
				elapsed = SystemClock.uptimeMillis() - start;
				t = elapsed / durationInMs;
				t = (float) Math.min(t, 1.0);

				// TODO: Make size relative to screen size, add text, make formula for bubble size

				Point screenSize = new Point();
				display.getSize(screenSize);

				// TODO: Instead of using screen.x, use smallest screen dim?
				Point triangleSize =
						new Point((int) (screenSize.x * triangleScreenSizeX),
								(int) (screenSize.x * triangleScreenSizeY));

				Point bubbleSizeInitial =
						new Point((int) (screenSize.x * 0.20 + chatSummary.numMessages),
								(int) (screenSize.x * 0.20 + chatSummary.numMessages));


				Point bubbleSize =
						new Point(100 + (int) ((screenSize.x * 0.67 - 100) * t),
								100 + (int) ((screenSize.x * 0.33 - 100) * t));

				Bitmap image =
						Bitmap.createBitmap(bubbleSize.x, bubbleSize.y + triangleSize.y / 2,
								Bitmap.Config.ARGB_8888);

				float radius = 50 - 30 * t;
				float[] radii = {radius, radius, radius, radius, radius, radius, radius, radius};
				RoundRectShape bubble = new RoundRectShape(radii, null, null);

				bubble.resize(bubbleSize.x, bubbleSize.y);

				Point trianglePosition =
						new Point((int) (image.getWidth() / 2 - triangleSize.x / 2),
								(int) (image.getHeight() - triangleSize.y));

				Path triangle = new Path();
				triangle.moveTo(trianglePosition.x, trianglePosition.y);
				triangle.lineTo(trianglePosition.x + triangleSize.x, trianglePosition.y);
				triangle.lineTo(trianglePosition.x + triangleSize.x * 0.5f, 
						trianglePosition.y + triangleSize.y);

				Paint paint = new Paint();
				paint.setAntiAlias(false);
				paint.setARGB(255, 255, 65 + (int) (30 * t), 65 + (int) (30 * t));

				Canvas canvas = new Canvas(image);
				bubble.draw(canvas, paint);
				canvas.drawPath(triangle, paint);
				//TODO uncomment this after importing google maps.
				marker.setIcon(BitmapDescriptorFactory.fromBitmap(image));

				if(t < 1)
				{
					handler.postDelayed(this, 16);
				}
			}

		});
	}

	private class GetInboxTask implements Runnable 
	{
		@Override
		public void run() 
		{
			//TODO change these to be the actual location and tags, when those elements have been implemented.
			LatLng l = new LatLng(InboxActivity.LAT,InboxActivity.LONG);
			String[] tags = {""};
			TaskParams_GetInbox sendParams = new TaskParams_GetInbox(l, tags);
			try {
				String responseString = HttpRequest.get(sendParams, GET_INBOX_URI);
				JSONObject responseJson = new JSONObject(responseString);
				if (responseJson.getInt(TAG_SUCCESS) == 1)
				{
					JSONArray summaries = responseJson.getJSONArray(TAG_CHATSUMMARY_ARRAY);

					Log.d("dbConnect", "chat summaries: " + summaries);
					Log.d("dbConnect", "trying to convert json...");
					GsonBuilder gsonBuilder = new GsonBuilder();
					gsonBuilder.registerTypeAdapter(ChatSummariesForScreen.class, new ChatSummariesForScreenDeserializer());
					Gson gson = gsonBuilder.create();
					final ChatSummaryForScreen[] newChatSummaries = gson.fromJson(responseString, ChatSummariesForScreen.class).chats;

					Log.d("dbConnect", "new chat summaries title one: " + newChatSummaries[0].title);

//					ArrayList<ChatSummaryForScreen> newChatSummariesList = new ArrayList<ChatSummaryForScreen>(Arrays.asList(newChatSummaries));
//					int numChats = newChatSummariesList.size();
					
//					map.clear();
					
					//					Log.d("dbConnect", "cleared map");
					chatSummaryMap.clear();
					markerList.clear();
					Log.d("dbConnect", "cleared summaries");
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							for(int i=0; i<markerList.size(); i++)
							{
								markerList.get(i).remove();
							}							
							for (int i = 0; i < newChatSummaries.length; i++)
							{
								Log.d("dbConnect", "reached for loop");
								ChatSummaryForScreen curChatSummary = newChatSummaries[i];

								Marker marker =
										map.addMarker(new MarkerOptions()
										.icon(BitmapDescriptorFactory.fromBitmap(createMarkerIcon(curChatSummary)))
										.anchor(0.5f, 1.0f) // Anchors the marker on the bottom left
										.position(curChatSummary.location));
								
								markerList.add(marker);
								
								chatSummaryMap.put(marker.getId(), curChatSummary);

								Log.d("dbConnect", "added chat to map");


								Log.d("dbConnect", "added new marker " + i);
							}
						}
					});
				}
			} catch (IOException | JsonSyntaxException e) {
				e.printStackTrace();
				Log.e("dbConnect", e.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
