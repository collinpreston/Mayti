package collin.mayti.datacapture;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Collin on 1/13/2018.
 */

public class GetJSONData extends AsyncTask <URL, Integer, String> {

    public GetJSONData(AsyncResponse taskComplete, AsyncPreExecute preExec) {
        this.taskResponse = taskComplete;
        this.taskPreExecute = preExec;
    }

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public interface AsyncPreExecute {
        void preExecute();
    }

    private final AsyncResponse taskResponse;
    private final AsyncPreExecute taskPreExecute;

    @Override
    protected String doInBackground(URL... urls) {

        StringBuilder buffer = new StringBuilder();
        try {
//            URLConnection connection = urls[0].openConnection();
//            connection.setConnectTimeout(3000);
//            connection.setReadTimeout(6000);
//            connection.connect();
//            InputStream stream = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(urls[0].openStream()));

            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line).append('\n');
            }
           // stream.close();


        } catch (IOException ignored) {
        }
        return buffer.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        // In onPostExecute we check if the listener is valid
        if(this.taskResponse != null) {
            // And if it is we call the callback function on it.
            this.taskResponse.processFinish(s);
        }
    }

    @Override
    protected void onPreExecute() {
        if (this.taskPreExecute != null) {
            this.taskPreExecute.preExecute();
        }
    }
}
