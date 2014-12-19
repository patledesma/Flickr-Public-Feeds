package flickrpublicfeeds.android.example.com.flickrpublicfeeds;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Patima on 12/10/2014.
 */
public class FlickrFeedFragment extends Fragment {

    ListView listView;
    ListViewAdapter listViewAdapter;

    public FlickrFeedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        listView = (ListView)rootView.findViewById(R.id.list_flickr_feeds);

        FlickrFeedsTask flickrFeedsTask = new FlickrFeedsTask(listViewAdapter, listView, getActivity());
        flickrFeedsTask.execute();

/*        ArrayList<String> arrayList = new ArrayList<String>();
        for(int i = 0; i < 100; i++) {
            arrayList.add("Item " + i);
        }

        String[] strs = (String[]) arrayList.toArray();*/

//        listViewAdapter = new ListViewAdapter(getActivity(), arrayList);
//        listView.setAdapter(listViewAdapter);

        return rootView;

    }

 /*   public void onUpdate() {

        FlickrFeedsTask flickrFeedsTask = new FlickrFeedsTask();
        flickrFeedsTask.execute();

    }

    @Override
    public void onStart() {
        super.onStart();
        onUpdate();
    }*/
}
