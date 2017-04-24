package hteam.mp3playback;
        import java.io.IOException;
        import java.lang.ref.WeakReference;

        import android.app.NotificationManager;
        import android.app.Service;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.media.AudioManager;
        import android.media.MediaPlayer;
        import android.media.MediaPlayer.OnBufferingUpdateListener;
        import android.media.MediaPlayer.OnCompletionListener;
        import android.media.MediaPlayer.OnErrorListener;
        import android.media.MediaPlayer.OnInfoListener;
        import android.media.MediaPlayer.OnPreparedListener;
        import android.media.MediaPlayer.OnSeekCompleteListener;
        import android.os.Handler;
        import android.os.IBinder;
        import android.telephony.PhoneStateListener;
        import android.telephony.TelephonyManager;
        import android.util.Log;
        import android.view.View;
        import android.view.View.OnClickListener;

        import android.widget.ImageView;
        import android.widget.SeekBar;
        import android.widget.SeekBar.OnSeekBarChangeListener;
        import android.widget.TextView;

        import com.squareup.picasso.Picasso;
        import com.squareup.picasso.Target;

public class PlayerService extends Service implements OnCompletionListener,
        OnSeekBarChangeListener, OnClickListener, OnErrorListener,
        OnPreparedListener, OnBufferingUpdateListener, OnSeekCompleteListener,
        OnInfoListener {
    private WeakReference<ImageView> btnPlay, btnForward, btnBackward;
    private WeakReference<SeekBar> songProgressBar;
    private WeakReference<TextView> songCurrentDurationLabel;
    private WeakReference<TextView> songTotalDurationLabel;
    private WeakReference<ImageView> btnNext,btnPrevious;
    private WeakReference<TextView> nameSong;
    private WeakReference<TextView> linkFileEdt;
    private WeakReference<ImageView> img;
    public static MediaPlayer mp;
    //Xử lý việc cập nhật giao diện (thời gian và progress bar ...)
    private Handler mHandler = new Handler();
    private Utilities utils;
    private int seekForwardTime = 5000; // 5 giây
    private int seekBackwardTime = 5000; // 5 giây
    public static int currentSongIndex = -1;
    public static int songindexForPause = 0;
    //Thành lập broadcast identifier and intent
    public static final String BROADCAST_BUFFER = "H team";
    public static final String BROADCAST_NOTI="Noti";
    Intent bufferIntent;

    Intent bufferIntent1;
    private boolean isPausedInCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    private static final String TAG = "TELEPHONESERVICE";
    //BroadCast Receiver sử dụng để lắng nge và phát sóng dữ liệu khi tai nge được cắm
//Và nếu có cắm tai nge, thì dừng nhạc và dừng service
    private int headsetSwitch = 1;
    private BroadcastReceiver headsetReceiver = new BroadcastReceiver() {
        private boolean headsetConnected = false;
        @Override
        public void onReceive(Context context, Intent intent) {
// Log.v(TAG, "ACTION_HEADSET_PLUG Intent received");
            if (intent.hasExtra("state")) {
                if (headsetConnected && intent.getIntExtra("state", 0) == 0) {
                    headsetConnected = false;
                    headsetSwitch = 0;
                } else if (!headsetConnected && intent.getIntExtra("state", 0) == 1) {
                    headsetConnected = true;
                    headsetSwitch = 1;
                }
            }
            switch (headsetSwitch) {
                case (0):
                    headsetDisconnected();
                    break;
                case (1):
                    break;
            }
        }
    };
    @Override
    public void onCreate() {
        mp = new MediaPlayer();
        mp.setOnCompletionListener(this);
        mp.setOnErrorListener(this);
        mp.setOnPreparedListener(this);
        mp.setOnBufferingUpdateListener(this);
        mp.setOnSeekCompleteListener(this);
        mp.setOnInfoListener(this);
        mp.reset();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);//
        utils = new Utilities();
        bufferIntent = new Intent(BROADCAST_BUFFER);
        bufferIntent1=new Intent(BROADCAST_NOTI);
//Đăng ký nhận tai nge
        registerReceiver(headsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));

        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initUI();
//        String songDestination_URL = intent.getExtras().getString("songDestination URL");
//        Log.d("SongDestination URL", "SongDestination URL = " + songDestination_URL);
//        if (songDestination_URL.compareTo("") != 0)
        String songDestination_URL=MainActivity.nhac_online.getData().get(MainActivity.pos).getSource_list().get(0);
        playSong(songDestination_URL);
//Nếu có cuộc gọi đến, tạm dừng máy nge nhạc, và resume khi ngắt kết nối cuộc gọi.
// Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
// String stateString = "N/A";
                Log.v(TAG, "Starting CallStateChange");
                switch (state) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING://có cuộc gọi đến
                        if (mp != null) {
                            pauseMedia();
                            isPausedInCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE://kết thúc cuộc gọi
//Bắt đầu play nhac.
                        if (mp != null) {
                            if (isPausedInCall) {
                                isPausedInCall = false;
                                playMedia();
                            }
                        }
                        break;
                }
            }
        };
        bufferIntent1.putExtra("OpenNoti","1");
        sendBroadcast(bufferIntent1);
        //GuiThongBaoHienNoti();
//Đăng ký lắng nge từ việc quản lý điện thoại
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        //showNotification();
        super.onStart(intent, startId);
        registerReceiver(noti,new IntentFilter(MainActivity.ACTION_PAUSE));

        registerReceiver(noti,new IntentFilter(MainActivity.ACTION_PLAY));
        return START_STICKY;
    }

    private BroadcastReceiver noti=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String bufferValue = intent.getAction();

            //int bufferIntValue=Integer.parseInt(bufferValue);
            Log.e("STRING",bufferValue);
            if(bufferValue.equals(MainActivity.ACTION_PAUSE))
            {
                Log.e("Service","Pause clicked");
                pauseMedia();
                btnPlay.get().setImageResource(R.drawable.ic_media_play);
                MainActivity.mBuilder.setOngoing(false);
                NotificationManager mNotificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotificationManager.notify(1,MainActivity.mBuilder.build());
            }
            if(bufferValue.equals(MainActivity.ACTION_PLAY))
            {
                Log.e("Service","Play clicked");
                playMedia();
                btnPlay.get().setImageResource(R.drawable.ic_media_pause);
                MainActivity.mBuilder.setOngoing(true);
                NotificationManager mNotificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotificationManager.notify(1,MainActivity.mBuilder.build());
//                if(!mp.isPlaying())
//                    mp.start();
            }
        }
    };
    private void initUI() {
        songCurrentDurationLabel = new WeakReference<TextView>(MainActivity.songCurrentDurationLabel);
        songTotalDurationLabel = new WeakReference<TextView>(MainActivity.songTotalDurationLabel);
        btnPlay = new WeakReference<ImageView>(MainActivity.btnPlay);
        btnForward = new WeakReference<ImageView>(MainActivity.btnForward);
        btnBackward = new WeakReference<ImageView>(MainActivity.btnBackward);
        btnNext=new WeakReference<ImageView>(MainActivity.btnNext);
        btnPrevious=new WeakReference<ImageView>(MainActivity.btnPrevious);
        btnPlay.get().setOnClickListener(this);
        btnForward.get().setOnClickListener(this);
        btnBackward.get().setOnClickListener(this);
        btnNext.get().setOnClickListener(this);
        btnPrevious.get().setOnClickListener(this);
        songProgressBar = new WeakReference<SeekBar>(MainActivity.songProgressBar);
        songProgressBar.get().setOnSeekBarChangeListener(this);
        nameSong=new WeakReference<TextView>(MainActivity.nameSong);
        linkFileEdt=new WeakReference<TextView>(MainActivity.linkFileEdt);
        img=new WeakReference<ImageView>(MainActivity.img);
    }
    //Gửi tin nhắn tới MainActivity rằng bài hát đã được lưu vào vùng nhớ đệm
    private void sendBufferingBroadcast() {
// Log.v(TAG, "BufferStartedSent");
        bufferIntent.putExtra("buffering", "1");
        sendBroadcast(bufferIntent);
    }
    //Gửi tin nhắn tới MainActivity rằng bài hát đã sẵn sàn để play
    private void sendBufferCompleteBroadcast() {
        bufferIntent.putExtra("buffering", "0");
        sendBroadcast(bufferIntent);
    }
    // -------------------------------------------------------------------------//
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                if (mp.isPlaying()) {
                    if (mp != null) {
                        mp.pause();
//Thay đổi image button
                        btnPlay.get().setImageResource(R.drawable.ic_media_play);
                        Log.d("Player Service", "Pause");
                        MainActivity.mBuilder.setOngoing(false);
                        NotificationManager mNotificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        mNotificationManager.notify(1,MainActivity.mBuilder.build());
                    }
                } else {
                    if (mp != null) {
                        mp.start();
//hay đổi image button
                        btnPlay.get().setImageResource(R.drawable.ic_media_pause);
                        Log.d("Player Service", "Play");
                        MainActivity.mBuilder.setOngoing(true);
                        NotificationManager mNotificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        mNotificationManager.notify(1,MainActivity.mBuilder.build());
                    }
                }
                break;
            case R.id.btn_forward:
//vị trí được phát hiện tại
                int currentPosition = mp.getCurrentPosition();
//kiểm tra thời gian của seekForward so với bài hát
                if (currentPosition + seekForwardTime <= mp.getDuration()) {
//Chỉnh về phía sau 5s
                    mp.seekTo(currentPosition + seekForwardTime);
                } else {
                    mp.seekTo(mp.getDuration());
                }
                break;
            case R.id.btn_backward:
//vị trí được phát hiện tại
                int currentPosition2 = mp.getCurrentPosition();
//Kiểm tra thời gian hơn 0s
                if (currentPosition2 - seekBackwardTime >= 0) {
//Chỉnh về phía trước 5s
                    mp.seekTo(currentPosition2 - seekBackwardTime);
                } else {
                    mp.seekTo(0);
                }
                break;
            case R.id.btn_next:
                if(MainActivity.pos<MainActivity.SumOfPlaylist)
                {
                    MainActivity.pos=MainActivity.pos+1;
                    playSong(MainActivity.nhac_online.getData().get(MainActivity.pos).getSource_list().get(0));
                    String url_cover=MainActivity.nhac_online.getData().get(MainActivity.pos).getCover();
                    String tenCS=MainActivity.nhac_online.getData().get(MainActivity.pos).getArtist();
                    String tenBH=MainActivity.nhac_online.getData().get(MainActivity.pos).getName();
                    Picasso.with(getApplicationContext()).load(url_cover).into(img.get());
                    nameSong.get().setText(tenBH);
                    linkFileEdt.get().setText(tenCS);
                    MainActivity.mBuilder.setContentText(tenCS);
                    MainActivity.mBuilder.setContentTitle(tenBH);
                    MainActivity.mBuilder.setOngoing(true);
                    NotificationManager mNotificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    mNotificationManager.notify(1,MainActivity.mBuilder.build());
                }
                break;
            case R.id.btn_previous:
                if(MainActivity.pos>0)
                {
                    MainActivity.pos=MainActivity.pos-1;
                    playSong(MainActivity.nhac_online.getData().get(MainActivity.pos).getSource_list().get(0));
                    String url_cover=MainActivity.nhac_online.getData().get(MainActivity.pos).getCover();
                    String tenCS=MainActivity.nhac_online.getData().get(MainActivity.pos).getArtist();
                    String tenBH=MainActivity.nhac_online.getData().get(MainActivity.pos).getName();
                    Picasso.with(getApplicationContext()).load(url_cover).into(img.get());
                    nameSong.get().setText(tenBH);
                    linkFileEdt.get().setText(tenCS);
                    MainActivity.mBuilder.setContentText(tenCS);
                    MainActivity.mBuilder.setContentTitle(tenBH);
                    MainActivity.mBuilder.setOngoing(true);
                    NotificationManager mNotificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    mNotificationManager.notify(1,MainActivity.mBuilder.build());
                }
                break;
        }
    }
    // -------------------------------------------------------------//
    public void playSong(String songPath) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        mp.reset();
        if (!mp.isPlaying()) {
            try {
                mp.setDataSource(songPath);
//gửi tin nhắn đến MainActivity để hiển thị đồng bộ
                sendBufferingBroadcast();
                mp.prepareAsync();
//Thay đổi button tạm dừng
                btnPlay.get().setImageResource(R.drawable.ic_media_pause);
//Thiết lập giá trị thanh Progress bar
                songProgressBar.get().setProgress(0);
                songProgressBar.get().setMax(100);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    //Gọi sẵn sàn để phát lại bài hát.
    public void onPrepared(MediaPlayer mp) {
//Gởi tin nhắn kết thúc
        sendBufferCompleteBroadcast();
        playMedia();
    }
    //Thực hiện chơi nhạc của media
    public void playMedia() {
        if (!mp.isPlaying()) {
            mp.start();
            updateProgressBar();
        }
    }
    //Thực hiện tạm dừng media
    public void pauseMedia() {
// Log.v(TAG, "Pause Media");
        if (mp.isPlaying()) {
            mp.pause();
        }
    }
    //Thực hiện dừng media
    public void stopMedia() {
        if (mp.isPlaying()) {
            mp.stop();
        }
    }
    //Cập nhật thời gian trên seekbar
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = 0;
            try {
                totalDuration = mp.getDuration();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            long currentDuration = 0;
            try {
                currentDuration = mp.getCurrentPosition();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
//Hiển thị tổng thời gian
            songTotalDurationLabel.get().setText("" + utils.milliSecondsToTimer(totalDuration));
//Hiển thị thời gian hoàn thành
            songCurrentDurationLabel.get().setText("" + utils.milliSecondsToTimer(currentDuration));
//Thực hiện việc cập nhật progress bar
            int progress = (int) (utils.getProgressPercentage(currentDuration,
                    totalDuration));
// Log.d("Progress", ""+progress);
            songProgressBar.get().setProgress(progress);
//Chạy lại sau 0,1s
            mHandler.postDelayed(this, 100);
        }
    };
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
//xóa tin nhắn xử lý cập nhật progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
//Thực hiện về trước sau theo số giây
        mp.seekTo(currentPosition);
//Cập nhật lại thời gian ProgressBar
        updateProgressBar();
    }
    @Override
    public void onCompletion(MediaPlayer arg0) {
        mp.stop();
        mp.release();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        currentSongIndex = -1;
        mHandler.removeCallbacks(mUpdateTimeTask);
        Log.d("Player Service", "Player Service Stopped");
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.release();
            NotificationManager mNotificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotificationManager.cancelAll();
        }
        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener,
                    PhoneStateListener.LISTEN_NONE);
        }
//Hủy đăng ký headsetReceiver
        unregisterReceiver(noti);
        unregisterReceiver(headsetReceiver);
    }
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }
    @Override
    public void onSeekComplete(MediaPlayer mp) {
    }
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
    }
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }
    private void headsetDisconnected() {
        pauseMedia();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}