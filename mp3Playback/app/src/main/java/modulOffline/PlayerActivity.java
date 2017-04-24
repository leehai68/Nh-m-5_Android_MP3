package modulOffline;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.kiendn.kiendnoffline.R;
import com.kiendn.kiendnoffline.SongManeger;
import com.kiendn.kiendnoffline.SongModel;
import com.kiendn.kiendnoffline.Util;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {
    //Activity for Result
    public static final int SELECT_SONG_REQUEST = 0;
    private Toolbar toolbar;

    public  static ArrayList<SongModel> arrSongs;
    //tua trước, sau(milliseconds)
    private int seekForwardTime = 5000;
    private int seekBackwardTime = 5000;

    private  int currentSongIndex = 0;
    //Media player
    private MediaPlayer mediaPlayer;

    private android.os.Handler mhandler ;

    //property
    private ImageView btnPlay;
    private ImageView btnForward;
    private ImageView btnBackward;
    private ImageView btnNext;
    private ImageView btnPrevious;
    private SeekBar songProgressBar;
    private TextView lblCurrentDuration;
    private  TextView lblTotalDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        addControls();
        addEvents();
    }

    private void addEvents() {
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        btnForward.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnBackward.setOnClickListener(this);

        //tua nhanh bài hát = cách kéo SeekBar
        songProgressBar.setOnSeekBarChangeListener(this);
        //chuyển nhạc auto
        mediaPlayer.setOnCompletionListener(this);

        playSong(currentSongIndex);
    }

    private void playSong(int songIndex) {
        try{
            mediaPlayer.reset();
            mediaPlayer.setDataSource(arrSongs.get(songIndex).path);
            mediaPlayer.prepare();
            mediaPlayer.start();

            //thay đổi thanh toolbar
            toolbar.setTitle(arrSongs.get(songIndex).title);

            //chuyển nút play sang pause
            btnPlay.setImageResource(R.drawable.pause_icon);

            //thay đổi progressbar
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);
            updateProgressBar();

            //thông báo
            buildNotification();

        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void buildNotification() {
        //do somethings
    }

    /**
     * update thời gian trên seekBar
     */
    private void updateProgressBar() {

        mhandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            try{
                long totalDuration = mediaPlayer.getDuration();
                long currentDuration = mediaPlayer.getCurrentPosition();
                //hiển thị tổng thời gian
                lblTotalDuration.setText(""+ Util.milliSecondsToTimer(totalDuration));
                //hiển thị thời gian đã chơi của bài nhạc
                lblCurrentDuration.setText(""+Util.milliSecondsToTimer(currentDuration));
                //updating progressBar
                int progress = (int)(Util.getProgressPercentage(currentDuration,totalDuration));
                songProgressBar.setProgress(progress);

                mhandler.postDelayed(this,100);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    };
    private void addControls() {
        btnPlay = (ImageView) findViewById(R.id.btn_play);
        btnBackward = (ImageView) findViewById(R.id.btn_backward);
        btnForward = (ImageView) findViewById(R.id.btn_forward);
        btnNext = (ImageView) findViewById(R.id.btn_next);
        btnPrevious = (ImageView) findViewById(R.id.btn_previous);
        lblCurrentDuration = (TextView) findViewById(R.id.lbl_current_time);
        lblTotalDuration = (TextView) findViewById(R.id.lbl_total_time);
        songProgressBar = (SeekBar) findViewById(R.id.prg_play);
        mediaPlayer = new MediaPlayer();
        mhandler = new android.os.Handler();

        //vẽ Toolbar
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Chương trình nghe nhạc MP3 offline");
        toolbar.setTitleTextColor(Color.GREEN);
        toolbar.setNavigationIcon(R.drawable.mp3_icon);
        setSupportActionBar(toolbar);

        //lấy bài hát từ SDcard cho vào Array
        SongManeger songManeger = new SongManeger();
        arrSongs = songManeger.getPlayList();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        //play or pause
        if (id == R.id.btn_play){
            // kiểm tra xem bài nhạc có chạy không nếu chạy thì set Image = stop ngược lại = play
            if (mediaPlayer.isPlaying()){
                if (mediaPlayer != null){
                    mediaPlayer.pause();
                    btnPlay.setImageResource(R.drawable.play_icon);
                }
            }else {
                if (mediaPlayer != null){
                    mediaPlayer.start();
                    btnPlay.setImageResource(R.drawable.stop_icon);
                }
            }
        }

        //tua nhanh bài hát
        if (id == R.id.btn_forward){
            int currentPosition = mediaPlayer.getCurrentPosition();
            //kiểm tra nếu chưa tua quá tổng thời gian bài nhạc
            if (currentPosition + seekForwardTime <= mediaPlayer.getDuration()){
                //chạy bài nhạc từ vị trí mới
                mediaPlayer.seekTo(currentPosition + seekForwardTime);
            }else {
                mediaPlayer.seekTo(mediaPlayer.getDuration());
            }
        }
        if (id == R.id.btn_backward){
            int currentPosition = mediaPlayer.getCurrentPosition();
            if (currentPosition - seekBackwardTime >= 0){
                mediaPlayer.seekTo(currentPosition - seekBackwardTime);
            }else {
                mediaPlayer.seekTo(0);
            }
        }

        //chuyển bài hát
        if (id == R.id.btn_next){
            //kiểm tra xem nếu chưa phải là bài hát cuối cùng
            if (currentSongIndex <(arrSongs.size() -1 )){
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            }
        }else{
            playSong(0);
            currentSongIndex = 0;
        }
        buildNotification();
        if (id == R.id.btn_next){
            //kiểm tra xem nếu chưa phải là bài hát đầu tiên
            if (currentSongIndex > 0){
                playSong(currentSongIndex - 1);
                currentSongIndex = currentSongIndex - 1;
            }
        }else{
            playSong(arrSongs.size() - 1);
            currentSongIndex = arrSongs.size() - 1;
        }
        buildNotification();

    }

    /**
     * xử lý auto chuyển bài nhạc (tự động lặp lại bài hát)
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (currentSongIndex < arrSongs.size() - 1){
            playSong(currentSongIndex + 1);
            currentSongIndex = currentSongIndex + 1;
        }else {
            playSong(0);
            currentSongIndex = 0;
        }
        buildNotification();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    //click vào menu trên toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.List_songs:
                Intent intent = new Intent(this,ListSongActivity.class);
                startActivityForResult(intent,SELECT_SONG_REQUEST);
                break;
        }
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_SONG_REQUEST && resultCode == RESULT_OK){
            currentSongIndex = data.getExtras().getInt("id");
            playSong(currentSongIndex);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
