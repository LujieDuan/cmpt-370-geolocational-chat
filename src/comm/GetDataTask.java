package comm;

import android.location.Location;
import android.os.AsyncTask;

public abstract class GetDataTask extends AsyncTask<TaskParams_GetInbox, Integer, Integer> 
{
	@Override
	protected Integer doInBackground(TaskParams_GetInbox... params) 
	{
		Location loc = params[0].curPhoneLocation;
		return null;
	}

//    protected void onProgressUpdate(Integer... progress) {
//        setProgressPercent(progress[0]);
//    }
//
//    protected void onPostExecute(Long result) {
//        showDialog("Downloaded " + result + " bytes");
//    }


}
