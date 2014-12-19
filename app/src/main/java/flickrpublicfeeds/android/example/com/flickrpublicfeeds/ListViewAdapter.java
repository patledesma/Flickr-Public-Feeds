package flickrpublicfeeds.android.example.com.flickrpublicfeeds;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Patima on 12/10/2014.
 */
public class ListViewAdapter extends BaseAdapter {

    Context context;
    String[] list;
    String title = "";
    String link = "";
    String src = "";

    public ListViewAdapter(Context context, String[] list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View contextView, ViewGroup viewGroup) {

        final String LOG_TAG = ListViewAdapter.class.getSimpleName();

        final ViewHolder viewHolder;

        if(contextView == null) {

            viewHolder = new ViewHolder();

            contextView = View.inflate(context, R.layout.list_flickr_feeds, null);

            viewHolder.imageView = (ImageView)contextView.findViewById(R.id.list_flickr_feeds_imageview);
            viewHolder.textView1 = (TextView)contextView.findViewById(R.id.list_flickr_feeds_textview1);
            viewHolder.textView2 = (TextView)contextView.findViewById(R.id.list_flickr_feeds_textview2);

            contextView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder)contextView.getTag();
        }

        try {

            JSONArray flickrFeedsArray = new JSONArray();
            JSONObject jsonObject = flickrFeedsArray.getJSONObject(i);

            for (int j = 0; j < flickrFeedsArray.length(); j++) {

                JSONObject titleObject = flickrFeedsArray.getJSONObject(j);
                title = titleObject.getString("title");
                link = titleObject.getString("link");

                JSONObject mediaObject = jsonObject.getJSONObject("media");
                src = mediaObject.getString("m");

            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        new AsyncTask<String, Void, Bitmap>(){

            @Override
            protected Bitmap doInBackground(String... params) {
                // TODO Auto-generated method stub

                Bitmap bmp = null;
                URL url;

                try {
                    url = new URL(params[0]);
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (MalformedURLException e) 	{
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return bmp;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                // TODO Auto-generated method stub
                super.onPostExecute(result);
                viewHolder.imageView.setImageBitmap(result);
                viewHolder.imageView.invalidate();
            }
        }.execute(src);

        viewHolder.textView2.setText(link);
        viewHolder.textView1.setText(title);

        return contextView;
    }

    static class ViewHolder {
        TextView textView1;
        ImageView imageView;
        TextView textView2;
    }

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

            String[] resultsStr = new String[numOfFeeds];

            JSONObject jsonObject = new JSONObject(flickrFeedsStr);
            JSONArray flickrFeedsArray = jsonObject.getJSONArray("items");

            for(int i = 0; i < flickrFeedsArray.length(); i++) {

                JSONObject titleObject = flickrFeedsArray.getJSONObject(i);
                title = titleObject.getString("title");
                link = titleObject.getString("link");

                JSONObject mediaObject = jsonObject.getJSONObject("media");
                src = mediaObject.getString("m");

            }

            return resultsStr;
        }

        @Override
        protected void onPostExecute(String[] strings) {

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

}
