package hteam.offline;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import hteam.mp3playback.MainActivity;
import hteam.mp3playback.PlayerService;
import hteam.mp3playback.R;

public class MainListActivity extends AppCompatActivity {

    public  static ArrayList<SongModel> arrSongs;
    SongModel songModel;
    SongsManager songsManager;
    ListSongAdapter listSongAdapter;
    ListView list_song;
    private boolean isOpen;
    Intent intent;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        addControls();
        addEvents();
    }


    private void addEvents() {
        list_song.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(MainListActivity.this,PlayOfflineActivity.class);
                bundle = new Bundle();
                bundle.putSerializable("SongArray",arrSongs);
                bundle.putInt("id",position);
               intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void addControls() {
        arrSongs = new ArrayList<SongModel>();
        songsManager = new SongsManager();
        list_song = (ListView) findViewById(R.id.list_song);
        arrSongs =songsManager.getPlayList();
        listSongAdapter = new ListSongAdapter(MainListActivity.this,arrSongs);
        list_song.setAdapter(listSongAdapter);
    }
}
