package hteam.offline;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import hteam.mp3playback.MainActivity;
import hteam.mp3playback.R;

public class PlayOfflineActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {

    public static final String ACTION_PLAY="MY_ACTION_PLAY";
    public static final String ACTION_PAUSE="MY_ACTION_PAUSE";
    //property
    private ImageView btnPlay;
    private ImageView btnForward;
    private ImageView btnBackward;
    private ImageView btnNext;
    private ImageView btnPrevious;
    private SeekBar songProgressBar;
    private TextView lblCurrentDuration;
    private  TextView lblTotalDuration;
    private Toolbar toolbar;
    private ImageButton list_songs;

    //tua trước, sau(milliseconds)
    private int seekForwardTime = 5000;
    private int seekBackwardTime = 5000;
    int currentSongIndex;
    //Media player
    private MediaPlayer mp;
    public static ArrayList<SongModel> arrSongs;

    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_offline);

        //find views
        btnPlay = (ImageView) findViewById(R.id.btn_play_song);
        btnBackward = (ImageView) findViewById(R.id.btn_back_song);
        btnForward = (ImageView) findViewById(R.id.btn_forward);
        btnNext = (ImageView) findViewById(R.id.btn_next_song);
        btnPrevious = (ImageView) findViewById(R.id.btn_rewind);
        lblCurrentDuration = (TextView) findViewById(R.id.lbl_current_time);
        lblTotalDuration = (TextView) findViewById(R.id.lbl_total_time);
        songProgressBar = (SeekBar) findViewById(R.id.prg_play);
        list_songs = (ImageButton) findViewById(R.id.list_songs);
        //Mediaplayer
        mp = new MediaPlayer();

        //vẽ Toolbar
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        toolbar.setTitle("Chương trình nghe nhạc MP3 offline");
//        toolbar.setTitleTextColor(Color.GREEN);
        toolbar.setNavigationIcon(R.drawable.box_icon);
//        setSupportActionBar(toolbar);


        //lay data
        Bundle bundle = getIntent().getExtras();
        currentSongIndex = bundle.getInt("id");
        arrSongs = (ArrayList<SongModel>) bundle.getSerializable("SongArray");

        btnPlay.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        btnForward.setOnClickListener(this);
        btnBackward.setOnClickListener(this);

        //tua nhanh bài hát = cách kéo SeekBar
        songProgressBar.setOnSeekBarChangeListener(this);

        //chuyển nhạc auto
        mp.setOnCompletionListener(this);

        playSong(currentSongIndex);
    }


    private void playSong(int songIndex) {
        try{
            mp.reset();
            mp.setDataSource(arrSongs.get(songIndex).Path);
            mp.prepare();
            mp.start();
            Log.e("BAI HAT HIEN TAI",arrSongs.get(songIndex).Name);
            //thay đổi thanh toolbar
            toolbar.setTitle(arrSongs.get(songIndex).Name);

            //chuyển nút play sang pause
            btnPlay.setImageResource(R.drawable.pause_icon);

            //thay đổi progressbar
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);

            //updating progress bar
            updateProgressBar();
//
//            //thông báo
            buildNotification();

        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        //play or pause
        if (id == R.id.btn_play_song){
            // kiểm tra xem bài nhạc có chạy không nếu chạy thì set Image = stop ngược lại = play
            Log.e("DDDDDDDDDDDDDDDDDD",String.valueOf(mp.isPlaying()));
            if (mp.isPlaying()){

                if (mp != null){
                    mp.pause();
                    btnPlay.setImageResource(R.drawable.play_icon);
                    Log.e("KIEM TRA SU KIEN","");
                }
            }else {
                if (mp != null){
                    mp.start();
                    btnPlay.setImageResource(R.drawable.pause_icon);
                }
            }
        }

        //tua nhanh bài hát
        if (id == R.id.btn_forward){
            int currentPosition = mp.getCurrentPosition();
            //kiểm tra nếu chưa tua quá tổng thời gian bài nhạc
            if (currentPosition + seekForwardTime <= mp.getDuration()){
                //chạy bài nhạc từ vị trí mới
                mp.seekTo(currentPosition + seekForwardTime);
            }else {
                mp.seekTo(mp.getDuration());
            }
        }
        if (id == R.id.btn_rewind){
            int currentPosition = mp.getCurrentPosition();
            if (currentPosition - seekBackwardTime >= 0){
                mp.seekTo(currentPosition - seekBackwardTime);
            }else {
                mp.seekTo(0);
            }
        }

        //chuyển bài hát
        if (id == R.id.btn_next_song){
            Log.e("TEN BAI HAT",arrSongs.get(currentSongIndex).Name);
            //kiểm tra xem nếu chưa phải là bài hát cuối cùng
            if (currentSongIndex <(arrSongs.size()-1)){
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            }
            else{
                playSong(0);
                currentSongIndex = 0;
                // Toast.makeText(this,"day la bai hat cuoi cung",Toast.LENGTH_LONG).show();
            }
        }
        buildNotification();
        if (id == R.id.btn_back_song){
            //kiểm tra xem nếu chưa phải là bài hát đầu tiên
            if (currentSongIndex > 0){
                playSong(currentSongIndex - 1);
                currentSongIndex = currentSongIndex - 1;
            }
            else{
                playSong(arrSongs.size() - 1);
                currentSongIndex = arrSongs.size() - 1;
            }
        }

    }

    private void buildNotification() {
        Intent intentPlay=new Intent(ACTION_PLAY);
        PendingIntent intentPplay= PendingIntent.getBroadcast(this,0,intentPlay,0);

        Intent intentPause=new Intent(ACTION_PAUSE);
        PendingIntent intentPpause= PendingIntent.getBroadcast(this,0,intentPause,0);

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(),1, intent, 0);
        Notification.Builder builder = new Notification.Builder(this).setSmallIcon(R.drawable.ic_launcher).setContentTitle("Media Play").setContentText(arrSongs.get(currentSongIndex).Name).setDeleteIntent(pendingIntent).addAction(android.R.drawable.ic_media_play,"PLAY",intentPplay).addAction(android.R.drawable.ic_media_pause,"PAUSE",intentPpause);;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,builder.build());
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            try {
                long totalDuration = mp.getDuration();
                long currentDuration = mp.getCurrentPosition();

                //tong thoi gian
                lblTotalDuration.setText(""+Util.milliSecondsToTimer(totalDuration));
                //thoi gian dang chay
                lblCurrentDuration.setText(""+Util.milliSecondsToTimer(currentDuration));

                //updating progress bar
                int prgress = (int) (Util.getProgressPercentage(currentDuration,totalDuration));
                songProgressBar.setProgress(prgress);

                //chay luong sau 100 millis
                mHandler.postDelayed(this,100);
            }catch (Exception ex){

            }
        }
    };

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }
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
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = Util.progressToTimer(seekBar.getProgress(), totalDuration);
//Thực hiện về trước sau theo số giây
        mp.seekTo(currentPosition);
//Cập nhật lại thời gian ProgressBar
        updateProgressBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.release();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }

    public void OpenListSongs(View view) {
        Intent intent = new Intent(this,MainListActivity.class);
        if (mp.isPlaying()){
            mp.stop();
        }
        startActivity(intent);
    }
}
