package data.chat;

import org.joda.time.DateTime;

import android.os.Parcel;
import android.os.Parcelable;
import comm.HttpRequest;

public class ChatId implements Parcelable {
	
	public String creatorId;
	public DateTime timeId;
	
	public ChatId(String creatorId, DateTime timeId) 
	{
		this.creatorId = creatorId;
		this.timeId = timeId;
	}
	
	public ChatId(Parcel in)
	{
		creatorId = in.readString();
		timeId = new DateTime(in.readLong());
	}
    
	public String getTimeIdString()
	{
		return timeId.toString(HttpRequest.DATETIME_FORMAT);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) 
	{
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
    
    public String toString()
    {
    	return "creatorId: " + creatorId + ", timeId: " + getTimeIdString();
    }
}
