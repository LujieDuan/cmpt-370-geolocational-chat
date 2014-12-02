package data.app.map;

import java.util.ArrayList;

import org.joda.time.DateTime;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import data.base.ChatId;
import data.base.ChatSummary;

/**
 * Individual chat, as displayed by the map screen. Only includes information
 * relevant to the map screen. Does NOT include the actual chat contents, as the
 * map screen doesn't need those.
 * 
 * @author wsv759
 *
 */
public class ChatSummaryForScreen extends ChatSummary implements Parcelable {
	
  public ChatId chatId;
	public String creatorUserName;
	public int numMessages;
	public DateTime lastMessageTime;
	public int numMessagesRead;

	/**
	 * Create a new chat summary, containing all the info necessary for an inbox
	 * item in the inbox UI.
	 * 
	 * @param title
	 *            the title of the chat
	 * @param location
	 *            the location of the chat
	 * @param tags
	 *            all tags associated with the chat (to help filtering)
	 * @param chatId
	 *            as assigned by the database.
	 * @param creatorUserName
	 *            the alias of the one who created the chat
	 */
	public ChatSummaryForScreen(String title, LatLng location,
			ArrayList<String> tags, ChatId chatId, String creatorUserName,
			int numMessages, int numMessagesRead, DateTime lastMessageTime) {
		super(title, location, tags);

		this.chatId = chatId;
		this.creatorUserName = creatorUserName;
		this.numMessages = numMessages;
		this.numMessagesRead = numMessagesRead;
		this.lastMessageTime = lastMessageTime;
	}

	/**
	 * Returns the number of messages
	 */
	public int getNumMessages() {
		return numMessages;
	}

	/**
	 * Returns the number of messages which have been read
	 */
	public int getNumMessagesRead() {
		return numMessagesRead;
	}

	/**
	 * Returns the number of messages which have not been read
	 */
	public int getNumMessagesUnread() {
		return numMessages - numMessagesRead;
	}

	/**
	 * Marks all messages as read
	 */
	public void readMessages() {
		numMessagesRead = numMessages;
	}

	public String getUserName() {
		return creatorUserName;
	}

	public String getTitle() {
		return title;
	}

	/**
	 * Returns a string representation which describes the number of messages
	 * which are within the chat, and the number which have been read
	 */
	public String getNumMessagesString() {
		if (numMessages == 1) {
			return numMessages + " reply, " + getNumMessagesUnread()
					+ " unread";
		} else {
			return numMessages + " replies, " + getNumMessagesUnread()
					+ " unread";
		}
	}

	public ChatSummaryForScreen(Parcel in) {
		this.title = in.readString();
		this.location = new LatLng(in.readDouble(), in.readDouble());
		tags = new ArrayList<String>();
		in.readList(tags, String.class.getClassLoader());
		this.chatId = new ChatId(in.readString(), new DateTime(in.readLong()));
		this.creatorUserName = in.readString();
		this.numMessages = in.readInt();
		this.numMessagesRead = in.readInt();
		this.lastMessageTime = new DateTime(in.readLong());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeDouble(location.latitude);
		dest.writeDouble(location.longitude);
		dest.writeList(tags);
		dest.writeString(chatId.creatorId);
		dest.writeLong(chatId.timeId.getMillis());
		dest.writeString(creatorUserName);
		dest.writeInt(numMessages);
		dest.writeInt(numMessagesRead);
		dest.writeLong(lastMessageTime.getMillis());
	}

	public static final Parcelable.Creator<ChatSummaryForScreen> CREATOR = new Parcelable.Creator<ChatSummaryForScreen>() {
		public ChatSummaryForScreen createFromParcel(Parcel in) {
			return new ChatSummaryForScreen(in);
		}

		public ChatSummaryForScreen[] newArray(int size) {
			return new ChatSummaryForScreen[size];
		}
	};

	/**
	 * Returns the time at which the most recent message was sent within the
	 * chat
	 */
    public DateTime getTime() {
      return lastMessageTime;
    }
}
