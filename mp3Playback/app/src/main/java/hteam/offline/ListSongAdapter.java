package hteam.offline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import hteam.mp3playback.R;

/**
 * Created by kiend on 4/28/2017.
 */

public class ListSongAdapter extends BaseAdapter {
    private ArrayList<SongModel> arr = new ArrayList<>();
    private Context context;

    public ListSongAdapter(MainListActivity context, ArrayList<SongModel> arr) {
        this.arr = arr;
        this.context = context;
    }

    @Override
    public int getCount() {
        return arr.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View row = layoutInflater.inflate(R.layout.list_item_view,parent,false);
        TextView lblName = (TextView) row.findViewById(R.id.txt_Name);
        lblName.setText(arr.get(position).Name);
        return row;
    }
}
