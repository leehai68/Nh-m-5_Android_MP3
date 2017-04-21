package hteam.mp3playback;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Vector;

import hteam.model.SubjectMp3;

/**
 * Created by Admin on 3/7/2017.
 */

class loadSubject extends AsyncTask<String, Intent, Vector<SubjectMp3>> {
    private static final String TAG = "MainActivity";

    protected Vector<SubjectMp3> doInBackground(String... params) {
        Vector ChuDe = new Vector<SubjectMp3>();
        String name = "";
        try {
            Document document = Jsoup.connect(params[0]).get();
            String title = document.title();
            Log.e(TAG, "Title:" + title + "\r\n");
            Elements metaElems = document.select("a");
            Elements imgElems = metaElems.select("img");
            Vector imgCD = new Vector();
            for (Element imgElem : imgElems) {
                if (imgElem.attr("class").equals("img-responsive") == true) {
                    name = imgElem.attr("src");
                    imgCD.add(name);
                    Log.e(TAG, "img [" + name + "]\r\n");
                }
            }
            Vector urlCD = new Vector();
            Vector ten = new Vector();
            for (Element aElem : metaElems)
                if (aElem.attr("class").equals("_trackLink") == true) {
                    name = "http://mp3.zing.vn" + aElem.attr("href");
                    urlCD.add(name);
                    Log.e(TAG,"URL_CD ["+name+"]\r\n");
                    name = aElem.attr("title");
                    ten.add(name);
                    Log.e(TAG, "a [" + name + "]\r\n");
                }
            for (int i = 0; i < urlCD.size(); i++) {
                SubjectMp3 temp = new SubjectMp3(imgCD.get(i).toString(), urlCD.get(i).toString(), ten.get(i).toString());
                ChuDe.add(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ChuDe;
    }
}