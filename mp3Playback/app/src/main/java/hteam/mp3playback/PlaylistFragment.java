package hteam.mp3playback;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Vector;

import hteam.model.PlaylistMp3;

/**
 * Created by Admin on 3/14/2017.
 */

public class PlaylistFragment extends ListFragment {
    Vector<PlaylistMp3> m;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        m=((abc)getActivity()).Playlist123;
        setListAdapter(new MyAdapter(getActivity(),m));
        //((abc) getActivity()).addFragment(new PlaylistFragment());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().finish();
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //Log.e("wtf","sao deoo hien clgt");
        Bundle link=new Bundle();
        link.putString("LINKPLAYLIST",m.get(position).getUrl_playlist().toString());
        Fragment ab=new PickMusicFragment();
        ab.setArguments(link);
        FragmentTransaction ft = ((abc)getActivity()).fmgr.beginTransaction();
        ft.hide(this);
        ft.commit();
        ((abc) getActivity()).addFragment(ab);
        Toast.makeText(getActivity(),m.get(position).getUrl_playlist().toString(),Toast.LENGTH_SHORT).show();
    }



//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
//    {
//        Log.e("wtf","sao deoo hien clgt");
//        Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();
//    }

    public static class MyAdapter extends BaseAdapter
    {
        private Vector<PlaylistMp3> playlist_adap;
        private LayoutInflater layoutInflater;
        private Context context;
        public MyAdapter(Context a,Vector<PlaylistMp3> b)
        {
            this.context=a;
            this.playlist_adap=b;
            layoutInflater=LayoutInflater.from(a);
        }

        @Override
        public int getCount() {
            return playlist_adap.size();
        }

        @Override
        public Object getItem(int position) {
            return playlist_adap.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView=layoutInflater.inflate(R.layout.custom_playlist_layout,null);
            TextView tv= (TextView) convertView.findViewById(R.id.tvTenPlaylist);
            ImageView imgP= (ImageView) convertView.findViewById(R.id.imgCoverPlaylist);
            PlaylistMp3 s=this.playlist_adap.get(position);
            tv.setText(s.getName_playlist());
            Picasso.with(context).load(s.getUrl_img_playlist()).into(imgP);
            return convertView;
        }
    }
}
