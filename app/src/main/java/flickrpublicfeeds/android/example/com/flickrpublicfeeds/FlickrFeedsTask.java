package flickrpublicfeeds.android.example.com.flickrpublicfeeds;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.ListPreference;
import android.text.Html;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Patima on 12/10/2014.
 */
public class FlickrFeedsTask extends AsyncTask<String, Void, String[]> {

    ListView listView;
    ListViewAdapter listViewAdapter;
    Context context;

    public FlickrFeedsTask(ListViewAdapter listViewAdapter, ListView listView, Context context) {

        this.listView = listView;
        this.listViewAdapter = listViewAdapter;
        this.context = context;
    }

    private String[] getFlickrFeedsFromJson(String flickrFeedsStr, int numOfFeeds) throws JSONException {

        final String LOG_TAG = FlickrFeedsTask.class.getSimpleName();

        String title;
        String media;
        String link;

        String[] resultsStr = new String[numOfFeeds];

        JSONObject jsonObject = new JSONObject(flickrFeedsStr);
        JSONArray flickrFeedsArray = jsonObject.getJSONArray("items");

        for(int i = 0; i < flickrFeedsArray.length(); i++) {

            JSONObject titleObject = flickrFeedsArray.getJSONObject(i);
            title = titleObject.getString("title");
           // JSONObject mediaObject = flickrFeedsArray.getJSONObject(i);
            media = titleObject.getString("media");
           // JSONObject linkObject = flickrFeedsArray.getJSONObject(i);
            link = titleObject.getString("link");


//            String desc = TextView.setText(Html.fromHtml(description));
            resultsStr[i] = title + "\n" + media + "\n" + link;

        }

        return resultsStr;
    }

    @Override
    protected void onPostExecute(String[] strings) {
        /*if (strings != null) {
            listViewAdapter.clear();
            for (String feeds : strings) {
                listViewAdapter.add(feeds);
            }

        }*/

        listViewAdapter = new ListViewAdapter(context, strings);
        listView.setAdapter(listViewAdapter);
        listView.invalidate();
    }

    @Override
    protected String[] doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String flickrFeedStr = null;

        try {
            final String FLICKR_FEED_BASE_URL = "https://api.flickr.com/services/feeds/photos_public.gne?format=json";

            Uri builtUri = Uri.parse(FLICKR_FEED_BASE_URL).buildUpon().build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if(inputStream == null){
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = reader.readLine()) != null){
                buffer.append(line + "\n");
            }

            if(buffer.length() == 0){
                return null;
            }

            flickrFeedStr = buffer.toString();

        } catch (IOException e) {
            flickrFeedStr = null;
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if(reader != null){
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("FLICKR", "Error");
                }
            }
        }

        String flickrFeedStrSub;
        flickrFeedStrSub = flickrFeedStr.substring(15, flickrFeedStr.length());

        try {
            return getFlickrFeedsFromJson(flickrFeedStrSub, 20);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
