package collin.mayti.datacapture;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by Collin on 1/13/2018.
 */

public class GetJSONData extends AsyncTask <URL, Integer, String> {

    public GetJSONData(AsyncResponse taskComplete, AsyncPreExecute preExec) {
        this.taskResponse = taskComplete;
        this.taskPreExecute = preExec;
    }

    public interface AsyncResponse {
        void processFinish(String output) throws InterruptedException, ExecutionException, MalformedURLException, JSONException;
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
            try {
                this.taskResponse.processFinish(s);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPreExecute() {
        if (this.taskPreExecute != null) {
            this.taskPreExecute.preExecute();
        }
    }
}
