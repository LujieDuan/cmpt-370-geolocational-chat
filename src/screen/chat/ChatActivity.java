package screen.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import screen.OutputStrings;
import screen.map.MapActivity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import coderunners.geolocationalchat.R;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import comm.HttpRequest;
import comm.gsonHelper.DateTimeDeserializer;
import comm.httpParams.TaskParams_GetNewMessages;
import data.app.chat.Chat;
import data.app.chat.ChatItem;
import data.app.chat.ChatMessageForScreen;
import data.app.global.GlobalSettings;
import data.app.map.ChatSummaryForScreen;
import data.comm.chat.ChatMessageToDb;
import data.comm.chat.ChatMessagesFromDb;

/**
 * The chat activity displays all messages sent for a given chat id. From here a
 * user can both send and recieve messages specific to that chat.
 */
public class ChatActivity extends ActionBarActivity {
	private static final String GET_NEW_MESSAGES_URI = "http://cmpt370duan.byethost10.com/getmess.php";
	private static final String SEND_NEW_MESSAGE_URI = "http://cmpt370duan.byethost10.com/create_message.php";
	private static final String TAG_MESSAGE_ARRAY = "messages";
	public static final String CHAT_SUMMARY_STRING = "chatId";

	private Chat chat = new Chat();

	private ChatSummaryForScreen chatSummary;

	private ChatItemArrayAdapter adapter;

	private static final int GET_MESSAGES_DELAY_SEC = 5;

	private ScheduledThreadPoolExecutor chatUpdateScheduler;

	/**
	 * Creates a new chat activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.chat_screen);

		chatSummary = getIntent().getExtras()
				.getParcelable(CHAT_SUMMARY_STRING);

		final ListView listView = (ListView) findViewById(R.id.listview);

		adapter = new ChatItemArrayAdapter(this, chat.getChatItems());
		listView.setAdapter(adapter);
	}

	/**
	 * Start updating the chat, when the chat starts. (i.e. returns to the phone
	 * screen.)
	 */
	@Override
	public void onStart() {
		super.onStart();

		chatUpdateScheduler = new ScheduledThreadPoolExecutor(1);
		chatUpdateScheduler.scheduleWithFixedDelay(new GetNewMessagesTask(), 0,
				GET_MESSAGES_DELAY_SEC, TimeUnit.SECONDS);
	}

	/**
	 * Stop updating the chat, when the chat stops. (i.e. vanishes from the
	 * phone screen.)
	 */
	@Override
	public void onStop() {
		super.onStop();

		chatUpdateScheduler.shutdownNow();
		// Can't use it anymore anyway, so this will help emphasize that...
		chatUpdateScheduler = null;
	}

	/**
	 * When the chat is *destroyed*, send its current ChatSummary back to the
	 * map screen.
	 */
	@Override
	public void onBackPressed() {
	  
	  int numMessages = chat.numMessages();  
	  
	  ChatSummaryForScreen newChatSummary = new ChatSummaryForScreen(
	        chatSummary.getTitle(), 
	        chatSummary.getLocation(), 
	        chatSummary.getTags(), 
	        chatSummary.getChatId(), 
	        chatSummary.getCreatorName(),
	        numMessages, 
	        numMessages, 
	        chat.getChatMessageForScreen(numMessages - 1).getTime());
	 

		Intent returnIntent = new Intent();

		returnIntent.putExtra(MapActivity.CHAT_SUMMARY_STRING, newChatSummary);
		setResult(RESULT_OK, returnIntent);

		finish();
	}

	/**
	 * Sends a message to the database when the send button is clicked by the
	 * user. This function is directly linked into the layout's XML.
	 * 
	 * @param v
	 */
	public void sendMessage(View v) {
		EditText editText = (EditText) findViewById(R.id.EditText);
		String message = editText.getText().toString().trim();
		editText.setText("");
		if (!message.equals("")) {
			// TODO implement a dummy message, for immediate viewing.
			// chat.addMessages(new
			// ChatMessageForScreen(message,MapActivity.USER_ID_AND_NAME.userId,MapActivity.USER_ID_AND_NAME.userName,
			// FAKE_MESSAGE_ID, new DateTime()));
			// onChatUpdated();

			new SendNewMessageTask().execute(new ChatMessageToDb(message,
					GlobalSettings.userIdAndName.userId, chatSummary.getChatId()));
		}
	}

	/**
	 * The chat item array adapter is used to place {@link ChatItem}s within a
	 * list view in the chat screen, allowing multiple messages to be displayed.
	 */
	public class ChatItemArrayAdapter extends ArrayAdapter<ChatItem> {

		private final Context context;
		private final ArrayList<ChatItem> values;

		/**
		 * Creates a new ChatItemArrayAdapter, used to list messages within the
		 * chat activity.
		 * 
		 * @param context
		 *            Activity in which it's created
		 * @param values
		 *            Messages to adapt to a list view
		 */
		public ChatItemArrayAdapter(Context context, ArrayList<ChatItem> values) {
			super(context, R.layout.chat_item_me, values);
			this.context = context;
			this.values = values;
		}

		/**
		 * Returns a list view in which the messages are contained. <br>
		 * {@inheritDoc}
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View itemView;

			if (values.get(position).getUserId()
					.equals(GlobalSettings.userIdAndName.userId)) {
				itemView = inflater.inflate(R.layout.chat_item_me, parent,
						false);
				LinearLayout bubbleList = (LinearLayout) itemView
						.findViewById(R.id.chat_bubble_list);
				for (int i = 0; i < values.get(position).numMessages(); i++) {
					View bubbleView = inflater.inflate(R.layout.chat_bubble_me,
							parent, false);
					TextView textViewMessage = (TextView) bubbleView
							.findViewById(R.id.textViewMessage);
					textViewMessage.setText(values.get(position).getMessage(i));
					bubbleList.addView(bubbleView);
				}
			} else {
				itemView = inflater.inflate(R.layout.chat_item_them, parent,
						false);
				LinearLayout bubbleList = (LinearLayout) itemView
						.findViewById(R.id.chat_bubble_list);
				for (int i = 0; i < values.get(position).numMessages(); i++) {
					View bubbleView = inflater.inflate(
							R.layout.chat_bubble_them, parent, false);
					TextView textViewMessage = (TextView) bubbleView
							.findViewById(R.id.textViewMessage);
					textViewMessage.setText(values.get(position).getMessage(i));
					bubbleList.addView(bubbleView);
				}
			}

			TextView textViewName = (TextView) itemView
					.findViewById(R.id.textViewName);
			textViewName.setText(values.get(position).getName());
			TextView textViewTimeLocation = (TextView) itemView
					.findViewById(R.id.timeAndLocation);

			Location location = new Location("");
			location.setLatitude(0);
			location.setLongitude(0);

			textViewTimeLocation.setText(OutputStrings.getRelativeTimeString(values.get(position).getTime()));

			return itemView;
		}

	}

	/**
	 * Update the UI when the underlying chat messages have changed.
	 */
	private void onChatUpdated() {
		adapter.notifyDataSetChanged();

		ListView listView = (ListView) findViewById(R.id.listview);
		listView.smoothScrollToPosition(listView.getBottom());
	}

	/**
	 * Get any new messages for this chat from the database, in the background.
	 * On success, update the chat. Make toast on failure.
	 * 
	 * @author wsv759
	 *
	 */
	private class GetNewMessagesTask implements Runnable {
		@Override
		public void run() {
			int lastMessageId = -1;
			if (chat.numMessages() > 0) {
				lastMessageId = chat
						.getChatMessageForScreen(chat.numMessages() - 1).getMessageId();
			}

			TaskParams_GetNewMessages sendParams = new TaskParams_GetNewMessages(
					chatSummary.getChatId(), lastMessageId);

			try {
				String responseString = HttpRequest.get(sendParams,
						GET_NEW_MESSAGES_URI);
				JSONObject responseJson = new JSONObject(responseString);

				if (responseJson.getInt(MapActivity.TAG_SUCCESS) == HttpRequest.HTTP_RESPONSE_SUCCESS) {
					// Request Could be successful, but without finding any new
					// messages.
					if (responseJson.optJSONArray(TAG_MESSAGE_ARRAY) != null) {
						GsonBuilder gsonBuilder = new GsonBuilder();
						gsonBuilder.registerTypeAdapter(DateTime.class,
								new DateTimeDeserializer());
						Gson gson = gsonBuilder.create();
						ChatMessageForScreen[] newChatMessages = gson.fromJson(
								responseString, ChatMessagesFromDb.class).messages;
						Log.i("dbConnect", "num new chat messages: "
								+ newChatMessages.length);

						chat.addMessages(newChatMessages);

						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								onChatUpdated();
							}
						});
					}
				} else {
					HttpRequest
							.handleHttpRequestFailure(
									ChatActivity.this,
									getResources()
											.getString(
													R.string.http_data_descriptor_new_messages),
									true,
									HttpRequest.ReasonForFailure.REQUEST_REJECTED);
					Log.e("dbConnect",
							getResources().getString(
									R.string.http_request_failure_rejected) + ": " + responseJson.getString(MapActivity.TAG_MESSAGE));
				}
			} catch (IOException e) {
				HttpRequest.handleHttpRequestFailure(
						ChatActivity.this,
						getResources().getString(
								R.string.http_data_descriptor_new_messages),
						true, HttpRequest.ReasonForFailure.REQUEST_TIMEOUT);
				Log.e("dbConnect", e.toString());
			} catch (JSONException e) {
				HttpRequest.handleHttpRequestFailure(
						ChatActivity.this,
						getResources().getString(
								R.string.http_data_descriptor_new_messages),
						true, HttpRequest.ReasonForFailure.NO_SERVER_RESPONSE);
				Log.e("dbConnect", e.toString());
			}
		}
	}

	/**
	 * Send a new message to the database, in the background. On failure, make
	 * toast.
	 * 
	 * @author wsv759
	 *
	 */
	private class SendNewMessageTask extends
			AsyncTask<ChatMessageToDb, Void, Void> {
		@Override
		protected Void doInBackground(ChatMessageToDb... params) {
			try {
				String responseString = HttpRequest.post(params[0],
						SEND_NEW_MESSAGE_URI);
				JSONObject responseJson = new JSONObject(responseString);

				if (responseJson.getInt(MapActivity.TAG_SUCCESS) != HttpRequest.HTTP_RESPONSE_SUCCESS) {
					HttpRequest.handleHttpRequestFailure(
							ChatActivity.this,
							getResources().getString(
									R.string.http_data_descriptor_response),
							false,
							HttpRequest.ReasonForFailure.REQUEST_REJECTED);
					Log.e("dbConnect",
							getResources().getString(
									R.string.http_request_failure_rejected) + ": " + responseJson.getString(MapActivity.TAG_MESSAGE));
				}
			} catch (IOException e) {
				HttpRequest.handleHttpRequestFailure(
						ChatActivity.this,
						getResources().getString(
								R.string.http_data_descriptor_response), false,
						HttpRequest.ReasonForFailure.REQUEST_TIMEOUT);
				Log.e("dbConnect", e.toString());
			} catch (JSONException e) {
				HttpRequest.handleHttpRequestFailure(
						ChatActivity.this,
						getResources().getString(
								R.string.http_data_descriptor_response), false,
						HttpRequest.ReasonForFailure.NO_SERVER_RESPONSE);
				Log.e("dbConnect", e.toString());
			}

			// TODO immediately get new messages.
			return null;
		}
	}
}