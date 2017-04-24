package modulOffline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kiendn.kiendnoffline.R;
import com.kiendn.kiendnoffline.SongModel;
import com.kiendn.kiendnoffline.ListSongActivity;

import java.util.ArrayList;

/**
 * Created by Kiendn on 19/03/2017.
 */

public class ListSongAdapter extends BaseAdapter {

    private ArrayList<SongModel> arr = new ArrayList<>();
    private Context context;

    public ListSongAdapter(ListSongActivity context, ArrayList<SongModel> arr) {
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
        View row = layoutInflater.inflate(R.layout.song_item,parent,false);
        TextView lblName = (TextView) row.findViewById(R.id.lbl_song_name);
        lblName.setText(arr.get(position).title);
        return row;
    }
}
