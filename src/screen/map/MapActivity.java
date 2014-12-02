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
import screen.settings.SettingsActivity;
import android.content.Context;
import android.content.Intent;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
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
import comm.HttpRequest;
import comm.gsonHelper.ChatSummariesForScreenDeserializer;
import comm.httpParams.TaskParams_GetInbox;

import data.app.global.GlobalSettings;
import data.app.map.ChatSummaryForScreen;
import data.comm.map.ChatSummariesFromDb;

/**
 * The map activity is the starting activity in the program. It displays summary
 * information about various chats within the user's radius to the user. From
 * here a user can select a chat, their settings page, or create a new chat at
 * their current location.
 */
public class MapActivity extends ActionBarActivity {

	private static final String GET_INBOX_URI = "http://cmpt370duan.byethost10.com/getchs.php";
	private static final String GET_TAGS_URI = "http://cmpt370duan.byethost10.com/getalltags.php";

	public static final String TAG_SUCCESS = "success";
	public static final String TAG_MESSAGE = "message";
	public static final String TAG_CHATS_ARRAY = "chats";

	public static enum ActivityRequestCode {
		CHAT_SCREEN, SETTINGS_SCREEN, CHAT_CREATION_SCREEN
	}

	public static final String CHAT_SUMMARY_STRING = "chatSummary";
	private static final int GET_INBOX_DELAY_SECONDS = 30;

	static GoogleMap map;

	Circle userCircle;

	Marker selectedMarker = null;
	boolean selectionAvailable = true;

	ArrayList<Marker> markerList = new ArrayList<Marker>();
	HashMap<String, ChatSummaryForScreen> chatSummaryMap = new HashMap<String, ChatSummaryForScreen>();

	int minMessages;
	int maxMessages;

	static final float MIN_TEXT_SIZE = 30;
	static final float MAX_TEXT_SIZE = 60;

	Handler handler = new Handler();

	private ScheduledThreadPoolExecutor getTagsTaskScheduler;
	private ScheduledThreadPoolExecutor inboxUpdateScheduler;

	Location location;
	Criteria criteria;
	LocationManager locationManager;

	/**
	 * Sets up the map screen, and grabs various user settings needed for the
	 * application such as the user's location, and display name. After this, a
	 * thread is started which polls the database for chat summaries.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_activity);
		
		//check for Internet connection, and do nothing if there is none.
		if (!isOnline())
		{
			Toast.makeText(this, "This device is not connected to the internet!", Toast.LENGTH_LONG).show();
			return;
		}

		GlobalSettings.initialize(this);
		getTagsTaskScheduler = new ScheduledThreadPoolExecutor(1);
		getTagsTaskScheduler.scheduleWithFixedDelay(new GetTagsTask(), 0, 5, TimeUnit.SECONDS);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		// TODO: Might have issues with emulator
		// map.setMyLocationEnabled(true);

		updateLocation();

		LatLng curPhoneLocation = GlobalSettings.curPhoneLocation;

		map.moveCamera(CameraUpdateFactory.newLatLngZoom(curPhoneLocation, 14));

		userCircle = map.addCircle(new CircleOptions().radius(1000)
				.strokeColor(Color.argb(60, 255, 40, 50)).strokeWidth(5)
				.fillColor(Color.argb(30, 255, 40, 50))
				.center(GlobalSettings.curPhoneLocation));

		inboxUpdateScheduler = new ScheduledThreadPoolExecutor(1);

		inboxUpdateScheduler.scheduleWithFixedDelay(new GetInboxTask(), 0,
				GET_INBOX_DELAY_SECONDS, TimeUnit.SECONDS);

		map.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				if (selectedMarker != null
						&& selectedMarker.getId().equals(marker.getId())) {
					Intent chatScreenIntent = new Intent(MapActivity.this,
							ChatActivity.class);
					ChatSummaryForScreen curChatSummary = chatSummaryMap
							.get(marker.getId());

					chatScreenIntent.putExtra(ChatActivity.CHAT_SUMMARY_STRING,
							curChatSummary);
					startActivityForResult(chatScreenIntent,
							ActivityRequestCode.CHAT_SCREEN.ordinal());
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
		switch (item.getItemId()) {
		case R.id.action_chat_creation:
			startActivity(new Intent(getApplicationContext(),
					ChatCreationActivity.class));
			break;
		case R.id.action_settings:
			startActivityForResult(new Intent(getApplicationContext(),
					SettingsActivity.class),
					ActivityRequestCode.SETTINGS_SCREEN.ordinal());
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Selects the given marker, updating its icon and state.
	 * 
	 * @param marker
	 *            Marker to be selected
	 */
	void selectMarker(Marker marker) {
		deselectMarker();
		selectedMarker = marker;
		selectedMarker.setIcon(BitmapDescriptorFactory
				.fromBitmap(createSelectedMarkerIcon(chatSummaryMap
						.get(selectedMarker.getId()))));
	}

	/**
	 * Deselects a currently selected marker, if a currently selected marker
	 * exists, updating its icon and state.
	 */
	void deselectMarker() {
		if (selectedMarker != null) {
			selectedMarker.setIcon(BitmapDescriptorFactory
					.fromBitmap(createMarkerIcon(
							chatSummaryMap.get(selectedMarker.getId()),
							minMessages, maxMessages)));
			selectedMarker = null;
		}
	}

	/**
	 * Creates and returns a marker bitmap for the given chat summary
	 * 
	 * @param chatSummary
	 *            chat summary for which an icon is created
	 * @param minMessages
	 *            The minimum number of messages for known chats, used to
	 *            determine the scale of the marker
	 * @param maxMessages
	 *            The maximum number of messages for known chats, used to
	 *            determine the scale of the marker
	 */
	Bitmap createMarkerIcon(ChatSummaryForScreen chatSummary, int minMessages,
			int maxMessages) {

		float scale = (float) (chatSummary.getNumMessages() - minMessages)
				/ (float) (maxMessages - minMessages);

		float triangleWidth = 30;
		float triangleHeight = (float) (triangleWidth * Math.sqrt(0.75));

		String strText = Integer.toString(chatSummary.getNumMessagesUnread());

		Paint paintText = new Paint();
		paintText.setColor(getResources().getColor(R.color.chat_me_foreground));
		paintText.setTextSize(scale * (MAX_TEXT_SIZE - MIN_TEXT_SIZE)
				+ MIN_TEXT_SIZE);
		paintText.setTextAlign(Paint.Align.CENTER);
		paintText.setAntiAlias(true);

		Rect boundsText = new Rect();
		paintText.getTextBounds(strText, 0, strText.length(), boundsText);

		int maxBounds = Math.max(boundsText.width(), boundsText.height());

		Bitmap image = Bitmap.createBitmap((int) (maxBounds * 1.75),
				(int) (maxBounds * 1.75) + (int) triangleHeight / 2,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(image);

		Point trianglePosition = new Point();
		trianglePosition.x = (int) (image.getWidth() / 2 - triangleWidth / 2);
		trianglePosition.y = (int) (image.getHeight() - triangleHeight);

		Path triangle = new Path();
		triangle.moveTo(trianglePosition.x, trianglePosition.y);
		triangle.lineTo(trianglePosition.x + triangleWidth * 1.0f,
				trianglePosition.y);
		triangle.lineTo(trianglePosition.x + triangleWidth * 0.5f,
				trianglePosition.y + triangleHeight);

		Paint paintShape = new Paint();
		paintShape
		.setColor(getResources().getColor(R.color.chat_me_background));
		paintShape.setAntiAlias(true);

		canvas.drawPath(triangle, paintShape);
		canvas.drawCircle(canvas.getWidth() / 2, canvas.getWidth() / 2,
				canvas.getWidth() / 2, paintShape);
		canvas.drawText(strText, canvas.getWidth() / 2, canvas.getWidth() / 2
				+ boundsText.height() / 2, paintText);

		return image;
	}

	/**
	 * Creates a selected marker bitmap for the given chat summary.
	 * 
	 * @param chatSummary
	 *            chat summary for which an icon is created
	 */
	Bitmap createSelectedMarkerIcon(ChatSummaryForScreen chatSummary) {
		String nameText = chatSummary.getUserName();
		String titleText = chatSummary.getTitle();
		String infoText = chatSummary.getNumMessagesString() + " from "
				+ chatSummary.getTimeString();

		Log.d("dbConnect", nameText);
		Log.d("dbConnect", titleText);
		Log.d("dbConnect", infoText);

		Paint paintNameText = new Paint();
		paintNameText.setColor(getResources().getColor(R.color.white));
		paintNameText.setTextSize(30);
		paintNameText.setAntiAlias(true);

		Paint paintTitleText = new Paint();
		paintTitleText.setColor(getResources().getColor(R.color.white));
		paintTitleText.setTextSize(40);
		paintTitleText.setAntiAlias(true);

		Paint paintInfoText = new Paint();
		paintInfoText.setColor(getResources().getColor(R.color.white));
		paintInfoText.setTextSize(20);
		paintInfoText.setAntiAlias(true);

		Paint paintShape = new Paint();
		paintShape
		.setColor(getResources().getColor(R.color.chat_me_background));
		paintShape.setAntiAlias(true);

		Rect boundsNameText = new Rect();
		paintNameText.getTextBounds(nameText, 0, nameText.length(),
				boundsNameText);

		Rect boundsTitleText = new Rect();
		paintTitleText.getTextBounds(titleText, 0, titleText.length(),
				boundsTitleText);

		Rect boundsInfoText = new Rect();
		paintInfoText.getTextBounds(infoText, 0, infoText.length(),
				boundsInfoText);

		Log.d("dbConnect", "height: " + boundsTitleText.bottom + ", "
				+ boundsInfoText.bottom);
		Log.d("dbConnect",
				"size: "
						+ Math.max(Math.max(boundsNameText.right,
								boundsTitleText.right), boundsInfoText.right));

		Rect boundsText = new Rect();
		boundsText.set(0, 0, Math.max(
				Math.max(boundsNameText.right, boundsTitleText.right),
				boundsInfoText.right) + 50, boundsNameText.height()
				+ boundsTitleText.height() + boundsInfoText.height() + 70);

		float triangleWidth = 30;
		float triangleHeight = (float) (triangleWidth * Math.sqrt(0.75));

		Log.d("dbConnect", "size 2: " + boundsText.width());

		Bitmap image = Bitmap.createBitmap(boundsText.width(),
				boundsText.height() + (int) triangleHeight / 2,
				Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(image);

		Point trianglePosition = new Point();
		trianglePosition.x = (int) (image.getWidth() / 2 - triangleWidth / 2);
		trianglePosition.y = (int) (image.getHeight() - triangleHeight);

		Path triangle = new Path();
		triangle.moveTo(trianglePosition.x, trianglePosition.y);
		triangle.lineTo(trianglePosition.x + triangleWidth * 1.0f,
				trianglePosition.y);
		triangle.lineTo(trianglePosition.x + triangleWidth * 0.5f,
				trianglePosition.y + triangleHeight);

		canvas.drawPath(triangle, paintShape);
		canvas.drawRect(boundsText, paintShape);
		canvas.drawText(titleText, 25, 50, paintTitleText);
		canvas.drawText(nameText, 25, 90, paintNameText);
		canvas.drawText(infoText, 25, 125, paintInfoText);

		return image;
	}

	// /**
	// * Returns the current location of the user
	// */
	// public LatLng getCurrentLocation()
	// {
	// locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	// criteria = new Criteria();
	// criteria.setAccuracy(Criteria.ACCURACY_FINE);
	// String provider = locationManager.getBestProvider(criteria, true);
	//
	// location = locationManager.getLastKnownLocation(provider);
	// LatLng currentLocation = new LatLng(location.getLatitude(),
	// location.getLongitude());
	// return currentLocation;
	// }

	/**
	 * Updates the location of the user in
	 * {@link GlobalSettings#curPhoneLocation}. If the location cannot be
	 * obtained (i.e. when running through an emulator), it is left as whatever
	 * is specified in {@link GlobalSettings#curPhoneLocation}.
	 */
	public void updateLocation() {
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = locationManager.getBestProvider(criteria, true);
		location = locationManager.getLastKnownLocation(provider);
		if (location != null) {
			GlobalSettings.curPhoneLocation = new LatLng(
					location.getLatitude(), location.getLongitude());
		}
	}
	
	/**
	 * Update the markers on the map display to reflect the new chat summaries provided.
	 * @param newChatSummaries the new total set of chat summaries to display.
	 */
	public void replaceMapMarkers(ChatSummaryForScreen[] newChatSummaries)
	{
		// This area could definitely be optimized, but will require
		// significant restructuring
		final ArrayList<ChatSummaryForScreen> summaryCreateList = new ArrayList<ChatSummaryForScreen>();
		final ArrayList<ChatSummaryForScreen> summaryUpdateList = new ArrayList<ChatSummaryForScreen>();
		final ArrayList<Marker> markerUpdateList = new ArrayList<Marker>();
		final ArrayList<Marker> markerRemoveList = new ArrayList<Marker>();

		for (int i = 0; i < newChatSummaries.length; i++) {
			boolean match = false;
			for (int j = 0; j < markerList.size(); j++) {
				ChatSummaryForScreen oldChatSummary = chatSummaryMap
						.get(markerList.get(j).getId());
				if (newChatSummaries[i].chatId.creatorId
						.equals(oldChatSummary.chatId.creatorId)
						&& newChatSummaries[i].chatId.timeId
						.equals(oldChatSummary.chatId.timeId)) {
					match = true;
					oldChatSummary.lastMessageTime = newChatSummaries[i].lastMessageTime;
					oldChatSummary.numMessages = newChatSummaries[i].numMessages;
					summaryUpdateList.add(oldChatSummary);
					markerUpdateList.add(markerList.get(j));
				}
			}
			if (!match) {
				summaryCreateList.add(newChatSummaries[i]);
			}
		}

		for (int i = 0; i < markerList.size(); i++) {
			boolean match = false;
			for (int j = 0; j < markerUpdateList.size(); j++) {
				if (markerList.get(i) == markerUpdateList.get(j)) {
					match = true;
				}
			}
			if (!match) {
				markerRemoveList.add(markerList.get(i));
			}
		}

		minMessages = Integer.MAX_VALUE;
		maxMessages = 0;

		for (int i = 0; i < summaryCreateList.size(); i++) {
			minMessages = Math.min(minMessages,
					summaryCreateList.get(i).numMessages);
			maxMessages = Math.max(maxMessages,
					summaryCreateList.get(i).numMessages);
		}

		for (int i = 0; i < summaryUpdateList.size(); i++) {
			minMessages = Math.min(minMessages,
					summaryUpdateList.get(i).numMessages);
			maxMessages = Math.max(maxMessages,
					summaryUpdateList.get(i).numMessages);
		}

		chatSummaryMap.clear();
		markerList.clear();

		Log.i("dbConnect", "create: " + summaryCreateList.size());
		Log.i("dbConnect", "update: " + markerUpdateList.size());
		Log.i("dbConnect", "remove: " + markerRemoveList.size());

		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				for (int i = 0; i < summaryCreateList.size(); i++) {
					// create marker
					Marker marker = map
							.addMarker(new MarkerOptions()
							.icon(BitmapDescriptorFactory
									.fromBitmap(createMarkerIcon(
											summaryCreateList
											.get(i),
											minMessages,
											maxMessages)))
											.anchor(0.5f, 1.0f) // Anchors
											// the
											// marker on
											// the
											// bottom
											// left
											.position(
													summaryCreateList
													.get(i).location));

					// add to marker list
					markerList.add(marker);

					// add summary to hash map
					chatSummaryMap.put(marker.getId(),
							summaryCreateList.get(i));
				}

				for (int i = 0; i < markerUpdateList.size(); i++) {

					// add to marker list
					markerList.add(markerUpdateList.get(i));

					// add summary to hash map
					chatSummaryMap.put(markerUpdateList.get(i)
							.getId(), summaryUpdateList.get(i));

					// handle selected
					if (selectedMarker != null
							&& !markerUpdateList.get(i).getId()
							.equals(selectedMarker.getId())) {
						// draw marker icon
						markerUpdateList
						.get(i)
						.setIcon(
								BitmapDescriptorFactory
								.fromBitmap(createMarkerIcon(
										summaryUpdateList
										.get(i),
										minMessages,
										maxMessages)));
					}
				}

				for (int i = 0; i < markerRemoveList.size(); i++) {
					Log.d("dbConnect",
							"Removing something, shouldn't be... Remove size: "
									+ markerRemoveList.size());
					// handle selected
					if (selectedMarker != null
							&& markerRemoveList.get(i).getId()
							.equals(selectedMarker.getId())) {
						selectedMarker = null;
					}
					// remove marker
					markerRemoveList.get(i).remove();
				}

				userCircle
				.setCenter(GlobalSettings.curPhoneLocation);

				Log.i("dbConnect",
						"Cleared and replaced chat summaries, on the map screen.");
			}
		});
	}
	
	/**
	 * Save the userName, if it exists, to internal storage.
	 */
	@Override
	protected void onStop() {
		super.onStop();

		GlobalSettings.saveChanges(this);
	}

	/**
	 * Stop updating the inbox when the app finishes.
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (getTagsTaskScheduler != null)
		{
			getTagsTaskScheduler.shutdownNow();
			getTagsTaskScheduler = null;
		}

		if (inboxUpdateScheduler != null)
		{
			inboxUpdateScheduler.shutdownNow();
			inboxUpdateScheduler = null;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == ActivityRequestCode.CHAT_SCREEN.ordinal()) {
			if (resultCode == RESULT_OK) {
				ChatSummaryForScreen updatedChatSummary = data.getExtras()
						.getParcelable(MapActivity.CHAT_SUMMARY_STRING);

				chatSummaryMap.remove(selectedMarker.getId());
				chatSummaryMap.put(selectedMarker.getId(), updatedChatSummary);

				selectedMarker.setIcon(BitmapDescriptorFactory
						.fromBitmap(createSelectedMarkerIcon(chatSummaryMap
								.get(selectedMarker.getId()))));
			}
			if (resultCode == RESULT_CANCELED) {
				// Do nothing
			}
		} else if (requestCode == ActivityRequestCode.SETTINGS_SCREEN.ordinal()) {
			if (resultCode == RESULT_OK) {
				inboxUpdateScheduler.shutdown();

				inboxUpdateScheduler = new ScheduledThreadPoolExecutor(1);
				inboxUpdateScheduler.scheduleWithFixedDelay(new GetInboxTask(),
						0, GET_INBOX_DELAY_SECONDS, TimeUnit.SECONDS);
			}
		}
	}

	public boolean isOnline() {
		ConnectivityManager cm =
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}

	/**
	 * Gets the full list of nearby chats from the database, in the background.
	 * Then, in the foreground, adds their new markers to the map. Makes toast
	 * if unsuccessful.
	 * 
	 * @author wsv759
	 *
	 */
	private class GetInboxTask implements Runnable {
		@Override
		public void run() {
			updateLocation();
			LatLng l = GlobalSettings.curPhoneLocation;
			ArrayList<String> tags = GlobalSettings.tagsToFilterFor;
			TaskParams_GetInbox queryParams = new TaskParams_GetInbox(l, tags);

			try {
				String responseString = HttpRequest.get(queryParams,
						GET_INBOX_URI);

				JSONObject responseJson = new JSONObject(responseString);

				if (responseJson.getInt(TAG_SUCCESS) == HttpRequest.HTTP_RESPONSE_SUCCESS) {
					final ChatSummaryForScreen[] newChatSummaries;
					// Even if the server responds successfully, it may not find
					// any new chats.
					if (responseJson.optJSONArray(TAG_CHATS_ARRAY) == null) {
						newChatSummaries = new ChatSummaryForScreen[0];
					} else {
						GsonBuilder gsonBuilder = new GsonBuilder();
						gsonBuilder.registerTypeAdapter(
								ChatSummariesFromDb.class,
								new ChatSummariesForScreenDeserializer());
						Gson gson = gsonBuilder.create();

						newChatSummaries = gson.fromJson(responseString,
								ChatSummariesFromDb.class).chats;
						
						replaceMapMarkers(newChatSummaries);
					}


				} else {
					HttpRequest
					.handleHttpRequestFailure(
							MapActivity.this,
							getResources()
							.getString(
									R.string.http_data_descriptor_new_inbox),
									true,
									HttpRequest.ReasonForFailure.REQUEST_REJECTED);
					Log.e("dbConnect",
							getResources().getString(
									R.string.http_request_failure_rejected) + ": " + responseJson.getString(TAG_MESSAGE));
				}
			} catch (IOException e) {
				HttpRequest.handleHttpRequestFailure(
						MapActivity.this,
						getResources().getString(
								R.string.http_data_descriptor_new_inbox), true,
								HttpRequest.ReasonForFailure.REQUEST_TIMEOUT);
				Log.e("dbConnect", e.toString());
			} catch (JSONException e) {
				HttpRequest.handleHttpRequestFailure(
						MapActivity.this,
						getResources().getString(
								R.string.http_data_descriptor_new_inbox), true,
								HttpRequest.ReasonForFailure.NO_SERVER_RESPONSE);
				Log.e("dbConnect", e.toString());
			}
		}
	}

	/**
	 * Gets all the tags for the app from the database, in the background. Sets
	 * the global tags object if successful, and does nothing otherwise, causing
	 * the app to fail.
	 * 
	 * @author wsv759
	 *
	 */
	private class GetTagsTask implements Runnable {
		@Override
		public void run() {
			try {
				String responseString = HttpRequest.get(null, GET_TAGS_URI);

				Gson gson = new Gson();
				String[] newTags = gson
						.fromJson(responseString, String[].class);
				GlobalSettings.allTags = new ArrayList<String>(
						Arrays.asList(newTags));

				//We don't need to get tags anymore!
				getTagsTaskScheduler.shutdown();
				getTagsTaskScheduler = null;

				Log.i("dbConnect", "received tags. First tag: " + newTags[0]);
			} catch (IOException e) {
				HttpRequest.handleHttpRequestFailure(
						MapActivity.this,
						getResources().getString(
								R.string.http_data_descriptor_tags), true,
								HttpRequest.ReasonForFailure.REQUEST_TIMEOUT);
				Log.e("dbConnect", e.toString());
			} catch (JsonSyntaxException e) {
				HttpRequest.handleHttpRequestFailure(
						MapActivity.this,
						getResources().getString(
								R.string.http_data_descriptor_tags), true,
								HttpRequest.ReasonForFailure.NO_SERVER_RESPONSE);
				Log.e("dbConnect", e.toString());
			} 
		}
	}
}
