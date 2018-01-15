package collin.mayti.datacapture;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import collin.mayti.urlUtil.AsyncResponse;

/**
 * Created by Collin on 1/13/2018.
 */

public class GetJSONData extends AsyncTask <URL, Integer, InputStream> {
    public AsyncResponse response = null;

    @Override
    protected InputStream doInBackground(URL... urls) {


        try {
            URLConnection connection = urls[0].openConnection();
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(60000);
            return connection.getInputStream();
        } catch (IOException ignored) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(InputStream inputStream) {
        response.processFinish(inputStream.toString());
    }
}
