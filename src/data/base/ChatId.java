package data.base;

import org.joda.time.DateTime;

import android.os.Parcel;
import android.os.Parcelable;

import comm.HttpRequest;

/**
 * Encapsulates the unique chatId for each chat, as created by the database.
 * This chatId causes the chat screen to be able to request the right chat
 * messages from the database, for display.
 * 
 * Must be parcelable, as it has to be passed from the map screen to the chat
 * screen.
 * 
 * @author wsv759
 *
 */
public class ChatId implements Parcelable {

	protected String creatorId;
	protected DateTime timeId;

	/**
	 * Creates a new {@link ChatId}.
	 * 
	 * @param creatorId
	 *            phone ID of the user which created the chat
	 * @param timeId
	 *            The time at which the chat was created
	 */
	public ChatId(String creatorId, DateTime timeId) {
		this.creatorId = creatorId;
		this.timeId = timeId;
	}

	public ChatId(Parcel in) {
		creatorId = in.readString();
		timeId = new DateTime(in.readLong());
	}

	public String getTimeIdString() {
		return timeId.toString(HttpRequest.DATETIME_FORMAT);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(creatorId);
		dest.writeLong(timeId.getMillis());
	}

	public static final Parcelable.Creator<ChatId> CREATOR = new Parcelable.Creator<ChatId>() {
		public ChatId createFromParcel(Parcel in) {
			return new ChatId(in);
		}

		public ChatId[] newArray(int size) {
			return new ChatId[size];
		}
	};

	public String toString() {
		return "creatorId: " + creatorId + ", timeId: " + getTimeIdString();
	}
	
	public boolean equals(Object other)
	{
	  if(!(other instanceof ChatId))
	  {
	    return false;
	  }
	  else
	  {
	    return creatorId.equals(((ChatId) other).creatorId) 
	        && timeId.equals(((ChatId) other).timeId);
	  }
	}

	/**
	 * Returns the creator portion of a chat ID
	 */
    public String getCreatorId() {
      return creatorId;
    }
  
    /**
     * Returns the time portion of a chat ID
     */
    public DateTime getTimeId() {
      return timeId;
    }
}
