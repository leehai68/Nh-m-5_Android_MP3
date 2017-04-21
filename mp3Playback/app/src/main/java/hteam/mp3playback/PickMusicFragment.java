package hteam.mp3playback;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import hteam.model.Data;
import hteam.model.ZingMp3;

/**
 * Created by Admin on 3/15/2017.
 */

public class PickMusicFragment extends ListFragment
{
    ZingMp3 pick_music;



    public void onCreate(Bundle savedInstanceState)
    {
        String data=getArguments().getString("LINKPLAYLIST");
        parseJSON a=new parseJSON();
        a.execute(data);
        try {
            pick_music=a.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setListAdapter(new MyAdapter1(getActivity(),pick_music));

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentTransaction ft = ((abc)getActivity()).fmgr.beginTransaction();
        ft.show(((abc)getActivity()).plFragment);
        ft.commit();
    }

//    @Override
//    public void onDetach() {
//        //listener=null;
//        super.onDetach();
//    }

    @Override
    public void onListItemClick (ListView l, View v, int position, long id)
    {
        Intent intent=new Intent(getActivity(),MainActivity.class);
        Bundle e=new Bundle();
        e.putSerializable("PICKMUSIC",pick_music);
        e.putInt("POS",position);
        intent.putExtras(e);
        startActivity(intent);

    }
    public class MyAdapter1 extends BaseAdapter
    {
        private ZingMp3 pick_adap;
        private LayoutInflater layoutInflater;
        private Context context1;
        public MyAdapter1(Context a,ZingMp3 b)
        {
            this.context1=a;
            this.pick_adap=b;
            layoutInflater=LayoutInflater.from(a);
        }

        @Override
        public int getCount() {
            return pick_adap.getData().size();
        }

        @Override
        public Object getItem(int position) {
            return pick_adap.getData().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView=layoutInflater.inflate(R.layout.custom_pick_music_layout,null);
            TextView tv= (TextView) convertView.findViewById(R.id.tvTenBaiHat_playlist);
            TextView tv1= (TextView) convertView.findViewById(R.id.tvTenCaSy_playlist);
            TextView tv2= (TextView) convertView.findViewById(R.id.tvSTT);
            Data s=this.pick_adap.getData().get(position);
            tv.setText(s.getName());
            tv1.setText(s.getArtist());
            int real=position+1;
            tv2.setText(String.valueOf(real));
            return convertView;
        }
    }
}
