package comm;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import data.base.ChatId;

/**
 * Encapsulates the parameters needed for the http request to get new messages
 * from the Db.
 * 
 * @author wsv759
 *
 */
public class TaskParams_GetNewMessages extends HttpGetParams {
	public ChatId chatId;
	public int lastMessageId;

	public TaskParams_GetNewMessages(ChatId chatId, int latestMessageId) {
		this.chatId = chatId;
		this.lastMessageId = latestMessageId;
	}

	/**
	 * @return the parameters in string form.
	 */
	public String getHttpStringForm() {
		List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();

		params.add(new BasicNameValuePair("creatorId", chatId.creatorId));
		params.add(new BasicNameValuePair("timeId", chatId.getTimeIdString()));
		params.add(new BasicNameValuePair("lastMessageId", Integer
				.toString(lastMessageId)));

		return URLEncodedUtils.format(params, "utf-8");
	}
}
