package hteam.mp3playback;

import android.content.Intent;
import android.icu.util.RangeValueIterator;
import android.os.AsyncTask;
import android.provider.DocumentsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import hteam.model.PlaylistMp3;

public class abc extends AppCompatActivity {
    public static String s;
    public static Vector<PlaylistMp3> Playlist123=new Vector<PlaylistMp3>();
    public static Fragment plFragment;
    public static  FragmentManager fmgr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Intent intent=new Intent();
        setContentView(R.layout.activity_abc);
        Bundle extra = new Bundle(getIntent().getExtras());
        s = extra.getString("KEY");
        Log.e("abc",s);
        //getPlaylist(s);
        loadPlaylist a=new loadPlaylist();
        a.execute(s);
        try {
            Playlist123=a.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        plFragment=new PlaylistFragment();
        addFragment(plFragment);
    }
    protected void addFragment(Fragment fragment)
    {
       fmgr = getSupportFragmentManager();
        FragmentTransaction ft = fmgr.beginTransaction();
        ft.add(R.id.layout_contain, fragment);
        //ft.hide(fragment);
        ft.addToBackStack(fragment.getClass().getSimpleName());
        ft.commit();
    }

    protected class loadPlaylist extends AsyncTask<String,Void,Vector<PlaylistMp3>>
    {

        @Override
        protected Vector<PlaylistMp3> doInBackground(String... params) {
            Vector playlist=new Vector<PlaylistMp3>();
            try {
                Document document = Jsoup.connect(params[0]).get();
                Elements metaElems = document.select("a");
                Elements imgElems=metaElems.select("img");
                Vector img=new Vector();
                for (Element imgElem : imgElems) {
                    if(imgElem.attr("class").equals("img-responsive")==true)
                    {
                        String name=imgElem.attr("src");
                        img.add(name);
                        Log.e("ABC", "data [" + name + "]\r\n");
                    }
                }
                //Elements titleElems=metaElems.attr("title");
                Vector titlename=new Vector();
                Vector playplist_url=new Vector();
                for(Element title : metaElems)
                {
                    if(title.attr("tracking").equals("_frombox=topic_playlist_")==true)
                    {
                        String name=title.attr("title");
                        String name1="http://mp3.zing.vn"+title.attr("href");
                        playplist_url.add(name1);
                        titlename.add(name);
                        Log.e("ABC","title ["+name1+"]\r\n");
                    }

                }
                for(int i=0;i<titlename.size();i++)
                {
                    PlaylistMp3 temp=new PlaylistMp3(img.get(i).toString(),playplist_url.get(i).toString(),titlename.get(i).toString());
                    playlist.add(temp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return playlist;
        }
    }
}
