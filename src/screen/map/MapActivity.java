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
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import data.app.map.ChatSummaryForScreen;
import data.base.ChatId;
import data.base.UserIdNamePair;
import data.comm.map.ChatSummariesFromDb;

public class MapActivity extends ActionBarActivity {

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

	ArrayList<Marker> markerList = new ArrayList<Marker>();
	HashMap<String, ChatSummaryForScreen> chatSummaryMap = new HashMap<String, ChatSummaryForScreen>();
	
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

	Handler handler = new Handler();
	private ScheduledThreadPoolExecutor inboxUpdateScheduler;
	
	Location location;
	Criteria criteria;
	LocationManager locationManager;
	
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
		
		// TODO: Might have issues with emulator
		//map.setMyLocationEnabled(true);
		
		updateLocation();
		
		LatLng curPhoneLocation = GlobalSettings.curPhoneLocation;
        
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(curPhoneLocation, 14));

	    userCircle = 
	        map.addCircle(new CircleOptions().radius(1000)
	            .strokeColor(Color.argb(60, 255, 40, 50))
	            .strokeWidth(5)
	            .fillColor(Color.argb(30, 255, 40, 50)).center(GlobalSettings.curPhoneLocation));
		
		inboxUpdateScheduler = new ScheduledThreadPoolExecutor(1);

		inboxUpdateScheduler.scheduleWithFixedDelay(new GetInboxTask(), 0, GET_INBOX_DELAY_SECONDS, TimeUnit.SECONDS);

		map.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				if (selectedMarker != null && selectedMarker.getId().equals(marker.getId())) {
					Intent chatScreenIntent = new Intent(MapActivity.this, ChatActivity.class);
					ChatId curChatId = chatSummaryMap.get(marker.getId()).chatId;

					chatScreenIntent.putExtra(ChatActivity.CHATID_STRING,curChatId);
					startActivity(chatScreenIntent);
				} else if (selectionAvailable) {
					selectMarker(marker);
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
	    MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
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

    void selectMarker(Marker marker) {
      deselectMarker();
      selectedMarker = marker;
      selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(createSelectedMarkerIcon(chatSummaryMap.get(selectedMarker.getId()))));
    }
    
	void deselectMarker() {
	  if(selectedMarker != null)
      {
        selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(createMarkerIcon(chatSummaryMap.get(selectedMarker.getId()),  minMessages, maxMessages)));
		selectedMarker = null;
      }
	}

	/**
	 * Creates and returns a marker bitmap for the given chat summary
	 * @param chatSummary chat summary for which an icon is created
	 * @param minMessages The minimum number of messages for known chats, used
	 * to determine the scale of the marker
	 * @param maxMessages The maximum number of messages for known chats, used
	 * to determine the scale of the marker
	 */
	Bitmap createMarkerIcon(ChatSummaryForScreen chatSummary, int minMessages, int maxMessages) {
	  
	    float scale = (float) (chatSummary.getNumMessages() - minMessages) / (float) (maxMessages - minMessages);

		float triangleWidth = 30;
		float triangleHeight = (float) (triangleWidth * Math.sqrt(0.75));

		String strText = Integer.toString(chatSummary.getNumMessagesUnread());

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

	// TODO: This method contains outdated math; ideally all animation would be handled by a different class
	/**
	 * Creates a selected marker bitmap for the given chat summary.
	 * @param chatSummary chat summary for which an icon is created
	 */
	Bitmap createSelectedMarkerIcon(ChatSummaryForScreen chatSummary)
    {
	  
      
      String nameText = chatSummary.getUserName();
      String titleText = chatSummary.getTitle();
      String infoText = chatSummary.getTimeString() + ", " + chatSummary.getNumMessagesString();
      
      Log.d("dbConnect", nameText);
      Log.d("dbConnect", titleText);
      Log.d("dbConnect", infoText);
      
      //TODO: limit name size
      Paint paintNameText = new Paint();
      paintNameText.setColor(getResources().getColor(R.color.white));
      paintNameText.setTextSize(30);
      paintNameText.setAntiAlias(true);
      
      //TODO: limit title size
      Paint paintTitleText = new Paint();
      paintTitleText.setColor(getResources().getColor(R.color.white));
      paintTitleText.setTextSize(40);
      paintTitleText.setAntiAlias(true);
      
      Paint paintInfoText = new Paint();
      paintInfoText.setColor(getResources().getColor(R.color.white));
      paintInfoText.setTextSize(20);
      paintInfoText.setAntiAlias(true);
      
      Paint paintShape = new Paint();
      paintShape.setColor(getResources().getColor(R.color.chat_me_background));
      paintShape.setAntiAlias(true);
      
      Rect boundsNameText = new Rect();
      paintNameText.getTextBounds(nameText, 0, nameText.length(), boundsNameText);
      
      Rect boundsTitleText = new Rect();
      paintTitleText.getTextBounds(titleText, 0, titleText.length(), boundsTitleText);
      
      Rect boundsInfoText = new Rect();
      paintInfoText.getTextBounds(infoText, 0, infoText.length(), boundsInfoText);
      
      Log.d("dbConnect", "height: " + boundsTitleText.bottom + ", " + boundsInfoText.bottom);
      Log.d("dbConnect", "size: " + Math.max(Math.max(boundsNameText.right, boundsTitleText.right), boundsInfoText.right));
      
      Rect boundsText = new Rect();
      boundsText.set(0, 0, 
          Math.max(Math.max(boundsNameText.right, boundsTitleText.right), boundsInfoText.right) + 50, 
          boundsNameText.height() + boundsTitleText.height() + boundsInfoText.height() + 70);
      
      float triangleWidth = 30;
      float triangleHeight = (float) (triangleWidth * Math.sqrt(0.75));
      
      Log.d("dbConnect", "size 2: " + boundsText.width());
      
      Bitmap image =
          Bitmap.createBitmap(boundsText.width(), 
              boundsText.height() + (int) triangleHeight / 2, 
              Bitmap.Config.ARGB_8888);
      
      Canvas canvas = new Canvas(image);
      
      Point trianglePosition = new Point();
      trianglePosition.x = (int) (image.getWidth() / 2 - triangleWidth / 2);
      trianglePosition.y = (int) (image.getHeight() - triangleHeight);

      Path triangle = new Path();
      triangle.moveTo(trianglePosition.x, trianglePosition.y);
      triangle.lineTo(trianglePosition.x + triangleWidth * 1.0f, trianglePosition.y);
      triangle.lineTo(trianglePosition.x + triangleWidth * 0.5f, trianglePosition.y + triangleHeight);
      
      canvas.drawPath(triangle, paintShape);
      canvas.drawRect(boundsText, paintShape);
      canvas.drawText(titleText, 25, 50, paintTitleText);
      canvas.drawText(nameText, 25, 90, paintNameText);
      canvas.drawText(infoText, 25, 125, paintInfoText);

      return image;
    }
	
//	/**
//	 * Returns the current location of the user
//	 */
//	public LatLng getCurrentLocation()
//	{
//	  locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//	  criteria = new Criteria();
//	  criteria.setAccuracy(Criteria.ACCURACY_FINE);
//	  String provider = locationManager.getBestProvider(criteria, true);
//	  
//	  location = locationManager.getLastKnownLocation(provider);
//	  LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
//	  return currentLocation;
//	}
	
     /**
      * Returns the current location of the user
      */
     public void updateLocation()
     {
       locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
       criteria = new Criteria();
       criteria.setAccuracy(Criteria.ACCURACY_FINE);
       String provider = locationManager.getBestProvider(criteria, true);
       location = locationManager.getLastKnownLocation(provider);
       if(location != null)
       {
         GlobalSettings.curPhoneLocation = new LatLng(location.getLatitude(), location.getLongitude());
       }
       else
       {
         //defaults to usask when no GPS is available
         GlobalSettings.curPhoneLocation = new LatLng(52.1334, -106.631358);
       }
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
	
	/**
	 * Stop updating the inbox when the app finishes.
	 */
	@Override
	protected void onDestroy(){
		super.onDestroy();
		
		inboxUpdateScheduler.shutdownNow();
		inboxUpdateScheduler = null;
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
		    updateLocation();
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
					
					Log.i("dbConnect", "create: " + summaryCreateList.size());
					Log.i("dbConnect", "update: " + markerUpdateList.size());
					Log.i("dbConnect", "remove: " + markerRemoveList.size());
					
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

							userCircle.setCenter(GlobalSettings.curPhoneLocation);
							
							Log.i("dbConnect", "Cleared and replaced chat summaries, on the map screen.");
						}
					});
				}
				else
				{
					HttpRequest.handleHttpRequestFailure(
							MapActivity.this, 
							getResources().getString(R.string.http_data_descriptor_new_inbox), 
							true, 
							HttpRequest.ReasonForFailure.REQUEST_REJECTED);
					Log.e("dbConnect", getResources().getString(R.string.http_request_failure_rejected));
				}
			} catch (IOException e) {
				HttpRequest.handleHttpRequestFailure(
						MapActivity.this, 
						getResources().getString(R.string.http_data_descriptor_new_inbox), 
						true, 
						HttpRequest.ReasonForFailure.REQUEST_TIMEOUT);
				Log.e("dbConnect", e.toString());
			} catch (JSONException | JsonSyntaxException e) {
				HttpRequest.handleHttpRequestFailure(
						MapActivity.this, 
						getResources().getString(R.string.http_data_descriptor_new_inbox), 
						true, 
						HttpRequest.ReasonForFailure.NO_SERVER_RESPONSE);
				Log.e("dbConnect", e.toString());
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
				
				Log.i("dbConnect", "received tags. First tag: " + newTags[0]);
			} catch (IOException e) {
				HttpRequest.handleHttpRequestFailure(
						MapActivity.this, 
						getResources().getString(R.string.http_data_descriptor_tags), 
						false, 
						HttpRequest.ReasonForFailure.REQUEST_TIMEOUT);
				Log.e("dbConnect", e.toString());
			}

			return null;
		}
	}
}
