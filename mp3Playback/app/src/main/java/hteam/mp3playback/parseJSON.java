package hteam.mp3playback;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import hteam.model.Data;
import hteam.model.ZingMp3;

class parseJSON extends AsyncTask<String, Intent, ZingMp3>
{
    private static final String TAG = "MainActivity";
    protected ZingMp3 doInBackground(String... params)
    {
        String name="";
        try {
            Document document = Jsoup.connect(params[0]).get();
            // Get document (HTML page) title
            String title = document.title();
            Log.e(TAG,"Title:" + title + "\r\n");

            Elements metaElems = document.select("div");
            for (Element metaElem : metaElems) {
                if(metaElem.attr("data-xml").equals("")==false)
                {
                    name=metaElem.attr("data-xml");
                    Log.e(TAG,"data-xml ["+name+"]\r\n");
                }
            }
            //name="http://mp3.zing.vn"+name;
            if(name.contains("mp3.zing.vn")==false)
            {
                name="http://mp3.zing.vn"+name;
            }
            Log.d("link :","["+name+"]");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ZingMp3 zingMp3=null;
        try {
            URL url = new URL(name);
            InputStreamReader reader=new InputStreamReader(url.openStream(),"UTF-8");
            //Log.e("Stream","Van ok");
            zingMp3=new Gson().fromJson(reader,ZingMp3.class);
            Log.e("TEST_GSON", zingMp3.getData().get(0).getSource_list().get(0));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zingMp3;
    }
}