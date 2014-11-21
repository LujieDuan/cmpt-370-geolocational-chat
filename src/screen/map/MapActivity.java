package screen.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import screen.chat.ChatActivity;
import screen.chatCreation.ChatCreationActivity;
import screen.settings.SendNewUserNameTask;
import screen.settings.SettingsActivity;
import android.app.Activity;
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
import com.google.android.gms.maps.model.Circle;
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
import data.app.global.GlobalSettings;
import data.app.inbox.ChatSummaryForScreen;
import data.base.ChatId;
import data.base.UserIdNamePair;
import data.comm.ChatSummariesFromDb;

public class MapActivity extends Activity {

	

	public static final String SETTINGS_FILE_NAME = "GeolocationalChatStoredSettings";
	public static final String SETTINGS_KEY_USER_NAME = "userName";

	private static final String GET_INBOX_URI = "http://cmpt370duan.byethost10.com/getchs.php";
	private static final String GET_TAGS_URI = "http://cmpt370duan.byethost10.com/getalltags.php";
	
	public static final String TAG_SUCCESS = "success";

	private static final int GET_INBOX_DELAY_SECONDS = 30;
	
	static GoogleMap map;
	
	Circle userCircle;
    
	Marker selectedMarker = null;
	boolean selectionAvailable = true;

	static ArrayList<Marker> markerList = new ArrayList<Marker>();
	static HashMap<String, ChatSummaryForScreen> chatSummaryMap = new HashMap<String, ChatSummaryForScreen>();
	
	int minMessages;
    int maxMessages;
	
	static final float triangleScreenSizeX = 0.05f;
	static final float triangleScreenSizeY = (float) (triangleScreenSizeX * Math.sqrt(0.75));
	
	static final float MIN_TEXT_SIZE = 30;
	static final float MAX_TEXT_SIZE = 60;

	static final float bubbleUnselectedScreenSizeMin = 0.10f;
	static final float bubbleUnselectedScreenSizeMax = 0.20f;

	static final float bubbleSelectedScreenSizeX = 0.67f;
	static final float bubbleSelectedScreenSizeY = 0.33f;

	final int MARKER_UPDATE_INTERVAL = 1000; 
	Handler handler = new Handler();
	
	private ScheduledThreadPoolExecutor chatUpdateScheduler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_activity);
		
		String deviceId = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);

		SharedPreferences settings = getSharedPreferences(SETTINGS_FILE_NAME, MODE_PRIVATE);
		String userName = settings.getString(SETTINGS_KEY_USER_NAME, "");
		
		if (userName.isEmpty())
		{
			//The SendNewUserNameTask changes the global userIdAndName for us.
			new SendNewUserNameTask(this).execute(new UserIdNamePair(deviceId,getResources().getString(R.string.unknown_user_name)));
		}
		else
		{
			GlobalSettings.userIdAndName = new UserIdNamePair(deviceId, userName);
		}
		
		new GetTagsTask().execute();
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		LatLng curPhoneLocation = GlobalSettings.curPhoneLocation;
        
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(curPhoneLocation, 14));

	    userCircle = 
	        map.addCircle(new CircleOptions().radius(1000)
	            .strokeColor(Color.argb(60, 255, 40, 50))
	            .strokeWidth(5)
	            .fillColor(Color.argb(30, 255, 40, 50)).center(new LatLng(52.1310799, -106.6341388)));
		
		chatUpdateScheduler = new ScheduledThreadPoolExecutor(1);

		chatUpdateScheduler.scheduleWithFixedDelay(new GetInboxTask(), 0, GET_INBOX_DELAY_SECONDS, TimeUnit.SECONDS);

		map.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				if (selectedMarker != null && selectedMarker.getId().equals(marker.getId())) {
					Intent chatScreenIntent = new Intent(MapActivity.this, ChatActivity.class);
					ChatId curChatId = chatSummaryMap.get(marker.getId()).chatId;

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

	void deselectMarker() {
		if (selectedMarker != null) {
			AnimateMarkerDeselect animateMarkerDeselected = new AnimateMarkerDeselect();
			animateMarkerDeselected.doInBackground(new String(selectedMarker.getId()));
			selectedMarker = null;
		}
	}

	Bitmap createMarkerIcon(ChatSummaryForScreen chatSummary, int minMessages, int maxMessages) {
		// todo: create function for bubble size

	  
	    float scale = (float) (chatSummary.numMessages - minMessages) / (float) (maxMessages - minMessages);
	  
		int numRepliesUnread = chatSummary.numMessages - chatSummary.numMessagesRead;

		float triangleWidth = 30;
		float triangleHeight = (float) (triangleWidth * Math.sqrt(0.75));

		String strText = Integer.toString(numRepliesUnread);

		Paint paintText = new Paint();
		paintText.setColor(getResources().getColor(R.color.chat_me_foreground));
		paintText.setTextSize(scale * (MAX_TEXT_SIZE - MIN_TEXT_SIZE) + MIN_TEXT_SIZE);
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

	private class AnimateMarkerDeselect extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... markerIds) {
			if(selectedMarker != null)
			{
  			  selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(createMarkerIcon(chatSummaryMap.get(selectedMarker.getId()),  minMessages, maxMessages)));
			}
			return null;
		}
	}

	// TODO: This method contains outdated math; ideally all animation would be handled by a different class
	/**
	 * Animates a marker selection, repeatedly changing the marker icon.
	 * @param marker Marker to animate
	 * @param display Display on which it will be animated
	 */
	static void animateMarkerSelection(final Marker marker, final Display display)
	{
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

				Point bubbleSize =
						new Point(100 + (int) ((screenSize.x * 0.67 - 100) * t),
								100 + (int) ((screenSize.x * 0.33 - 100) * t));

				Bitmap image =
						Bitmap.createBitmap(bubbleSize.x, bubbleSize.y + triangleSize.y / 2,
								Bitmap.Config.ARGB_8888);

				float radius = 50 - 50 * t;
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
			
				marker.setIcon(BitmapDescriptorFactory.fromBitmap(image));

				if(t < 1)
				{
					handler.postDelayed(this, 16);
				}
			}

		});
	}
	
	/**
	 * Save the userName, if it exists, to internal storage.
	 */
	@Override
	protected void onStop(){
		super.onStop();

		SharedPreferences settings = getSharedPreferences(SETTINGS_FILE_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		
		UserIdNamePair userIdAndName = GlobalSettings.userIdAndName;
		if (userIdAndName != null && !userIdAndName.userName.isEmpty())
			editor.putString(SETTINGS_KEY_USER_NAME, GlobalSettings.userIdAndName.userName);
			
		editor.commit();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		
		chatUpdateScheduler.shutdownNow();
		//Can't use it anymore anyway, so this will help emphasize that...
		chatUpdateScheduler = null;
	}
	/**
	 * Gets the full list of nearby chats from the database, in the background. Then, in the foreground, adds
	 * their new markers to the map. Makes toast if unsuccessful.
	 * @author wsv759
	 *
	 */
	private class GetInboxTask implements Runnable 
	{
		@Override
		public void run() 
		{
			LatLng l = GlobalSettings.curPhoneLocation;
			ArrayList<String> tags = GlobalSettings.tagsToFilterFor;
			TaskParams_GetInbox sendParams = new TaskParams_GetInbox(l, tags);
			
			try {
				String responseString = HttpRequest.get(sendParams, GET_INBOX_URI);
				JSONObject responseJson = new JSONObject(responseString);

				if (responseJson.getInt(TAG_SUCCESS) == HttpRequest.HTTP_RESPONSE_SUCCESS)
				{
					GsonBuilder gsonBuilder = new GsonBuilder();
					gsonBuilder.registerTypeAdapter(ChatSummariesFromDb.class, new ChatSummariesForScreenDeserializer());
					Gson gson = gsonBuilder.create();

					final ChatSummaryForScreen[] newChatSummaries = gson.fromJson(responseString, ChatSummariesFromDb.class).chats;
					
					// This area could definitely be optimized, but will require significant restructuring
					
					final ArrayList<ChatSummaryForScreen> summaryCreateList = new ArrayList<ChatSummaryForScreen>();
					final ArrayList<ChatSummaryForScreen> summaryUpdateList = new ArrayList<ChatSummaryForScreen>();
					final ArrayList<Marker> markerUpdateList = new ArrayList<Marker>();
					final ArrayList<Marker> markerRemoveList = new ArrayList<Marker>();
					
					for(int i=0; i<newChatSummaries.length; i++)
					{
					  boolean match = false;
					  for(int j=0; j<markerList.size(); j++)
					  {
					    ChatSummaryForScreen oldChatSummary = chatSummaryMap.get(markerList.get(j).getId());
					    if (newChatSummaries[i].chatId.creatorId.equals(oldChatSummary.chatId.creatorId)
					        && newChatSummaries[i].chatId.timeId.equals(oldChatSummary.chatId.timeId))
					    {
					      match = true;
					      oldChatSummary.lastMessageTime = newChatSummaries[i].lastMessageTime;
					      oldChatSummary.numMessages = newChatSummaries[i].numMessages;
					      summaryUpdateList.add(oldChatSummary);
					      markerUpdateList.add(markerList.get(j));
					    }
					  }
					  if(!match)
					  {
					    summaryCreateList.add(newChatSummaries[i]);
					  }
					}
					
					for(int i=0; i<markerList.size(); i++)
					{
					  boolean match = false;
					  for(int j=0; j<markerUpdateList.size(); j++)
					  {
					    if(markerList.get(i) == markerUpdateList.get(j))
					    {
					      match = true;
					    }
					  }
					  if(!match)
					  {
					    markerRemoveList.add(markerList.get(i));
					  }
					}
					
					minMessages = Integer.MAX_VALUE;
                    maxMessages = 0;
                    
                    for(int i=0; i<summaryCreateList.size(); i++)
                    {
                      minMessages = Math.min(minMessages, summaryCreateList.get(i).numMessages);
                      maxMessages = Math.max(maxMessages, summaryCreateList.get(i).numMessages);
                    }
                      
                    for(int i=0; i<summaryUpdateList.size(); i++)
                    {
                      minMessages = Math.min(minMessages, summaryUpdateList.get(i).numMessages);
                      maxMessages = Math.max(maxMessages, summaryUpdateList.get(i).numMessages);
                    }
					
					chatSummaryMap.clear();
					markerList.clear();
					
					Log.d("dbConnect", "create: " + summaryCreateList.size());
					Log.d("dbConnect", "update: " + markerUpdateList.size());
					Log.d("dbConnect", "remove: " + markerRemoveList.size());
					
					runOnUiThread(new Runnable() {
					  
						@Override
						public void run() {
						  
						    for(int i=0; i<summaryCreateList.size(); i++)
						    {
						      //create marker
						      Marker marker =
                                  map.addMarker(new MarkerOptions()
                                  .icon(BitmapDescriptorFactory.fromBitmap(createMarkerIcon(summaryCreateList.get(i), minMessages, maxMessages)))
                                  .anchor(0.5f, 1.0f) // Anchors the marker on the bottom left
                                  .position(summaryCreateList.get(i).location));
						      
						      //add to marker list
						      markerList.add(marker);
						      
						      //add summary to hash map
						      chatSummaryMap.put(marker.getId(), summaryCreateList.get(i));
						    }
						  
						    for(int i=0; i<markerUpdateList.size(); i++)
						    {
						      
						      //add to marker list
                              markerList.add(markerUpdateList.get(i));
                              
                              //add summary to hash map
                              chatSummaryMap.put(markerUpdateList.get(i).getId(), summaryUpdateList.get(i));
						      
						      //handle selected
						      if(selectedMarker != null && !markerUpdateList.get(i).getId().equals(selectedMarker.getId()))
						      {
    						      //draw marker icon
    						      markerUpdateList.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(createMarkerIcon(summaryUpdateList.get(i), minMessages, maxMessages)));
						      }
						    }
						    
						    for(int i=0; i<markerRemoveList.size(); i++)
						    {
						      Log.d("dbConnect", "Removing something, shouldn't be... Remove size: " + markerRemoveList.size());
						      //handle selected
						      if(selectedMarker != null && markerRemoveList.get(i).getId().equals(selectedMarker.getId()))
						      {
						        selectedMarker = null;
						      }
						      //remove marker
						      markerRemoveList.get(i).remove();
						    }

							//TODO: Get location
							userCircle.setCenter(new LatLng(52.1310799, -106.6341388));
							
							Log.d("dbConnect", "Cleared and replaced chat summaries, on the map screen.");
						}
					});
				}
				else
				{
					HttpRequest.makeToastOnRequestRejection(MapActivity.this, "new inbox data", true);
				}
			} catch (IOException e) {
				HttpRequest.makeToastOnServerTimeout(MapActivity.this, "new inbox data", true);
				Log.e("dbConnect", e.toString());
			} catch (JSONException | JsonSyntaxException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Gets all the tags for the app from the database, in the background. 
	 * Sets the global tags object if successful, and does nothing otherwise, causing the app to fail. TODO change that
	 * @author wsv759
	 *
	 */
	private class GetTagsTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params) {
			try {
				String responseString = HttpRequest.get(null, GET_TAGS_URI);
				
				Gson gson = new Gson();
				String[] newTags = gson.fromJson(responseString, String[].class);
				GlobalSettings.allTags = new ArrayList<String>(Arrays.asList(newTags));
				
				Log.d("dbConnect", "received tags. First tag: " + newTags[0]);
			} catch (IOException e) {
				HttpRequest.makeToastOnServerTimeout(MapActivity.this, "tags", false);
				Log.e("dbConnect", e.toString());
			}

			return null;
		}
	}
}
