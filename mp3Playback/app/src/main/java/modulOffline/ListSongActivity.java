package modulOffline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kiendn.kiendnoffline.R;
import com.kiendn.kiendnoffline.ListSongAdapter;
import com.kiendn.kiendnoffline.SongModel;

public class ListSongActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private Toolbar toolbar;
    private ListView lstSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_song);
        addControls();
    }

    private void addControls() {
        lstSongs = (ListView) findViewById(R.id.lst_songs);
        ListSongAdapter adapter = new ListSongAdapter(this, PlayerActivity.arrSongs);
        lstSongs.setAdapter(adapter);
        lstSongs.setOnItemClickListener(this);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SongModel songModel = PlayerActivity.arrSongs.get(position);
        Intent intent = new Intent();
        intent.putExtra("id",position);
        setResult(RESULT_OK, intent);
        finish();
    }
}
