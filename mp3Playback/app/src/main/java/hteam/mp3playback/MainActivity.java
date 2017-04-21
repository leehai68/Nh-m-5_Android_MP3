package hteam.mp3playback;

        import android.app.Notification;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.os.Bundle;
        import android.app.Activity;
        import android.app.ProgressDialog;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;

        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.SeekBar;
        import android.widget.TextView;

        import com.squareup.picasso.Picasso;

        import java.util.concurrent.ExecutionException;

        import hteam.model.ZingMp3;


public class MainActivity extends Activity implements OnClickListener {
    public static final String ACTION_PLAY="MY_ACTION_PLAY";
    public static final String ACTION_PAUSE="MY_ACTION_PAUSE";
    public String MUSIC_LINK;
    public String artist;
    public String name;
    public String urlCover;
    public static ImageView btnPlay, btnForward, btnBackward,btnNext,btnPrevious;
    public static SeekBar songProgressBar;
    public static TextView songCurrentDurationLabel, songTotalDurationLabel;
    public static TextView nameSong;
    public static EditText linkFileEdt;
    private Intent playerService;
    public static ImageView img;
    private final String URL="http://mp3.zing.vn/album/Nhac-Viet-Moi-Thang-03-2017/ZOZ0WFDO.html";
    public static int SumOfPlaylist;
    public static int pos;
    public static ZingMp3 nhac_online;
    public static Notification.Builder mBuilder;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle b=getIntent().getExtras();
        nhac_online= (ZingMp3) b.getSerializable("PICKMUSIC");
        pos=b.getInt("POS");
        SumOfPlaylist=nhac_online.getData().size();
        MUSIC_LINK=nhac_online.getData().get(pos).getSource_list().get(0);
        artist=nhac_online.getData().get(pos).getArtist();
        name=nhac_online.getData().get(pos).getName();
        urlCover=nhac_online.getData().get(pos).getCover();
//        parseJSON a=new parseJSON();
//        a.execute(URL);
//        try {
//            ZingMp3 nhac_online=a.get();
//            MUSIC_LINK=nhac_online.getData().get(0).getSource_list().get(0).toString();
//            artist=nhac_online.getData().get(0).getArtist().toString();
//            name = nhac_online.getData().get(0).getName().toString();
//            urlCover=nhac_online.getData().get(0).getCover().toString();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
        initViews();
        Picasso.with(getApplicationContext()).load(urlCover).into(img);
        playerService = new Intent(MainActivity.this, PlayerService.class);
        //playerService.putExtra("songDestination URL", MUSIC_LINK);
        linkFileEdt.setText(artist);
        nameSong.setText(name);
        startService(playerService);
    }
    private void initViews() {
        btnPlay = (ImageView) findViewById(R.id.btn_play);
        btnForward = (ImageView) findViewById(R.id.btn_forward);
        btnBackward = (ImageView) findViewById(R.id.btn_backward);
        btnNext= (ImageView) findViewById(R.id.btn_next);
        btnPrevious= (ImageView) findViewById(R.id.btn_previous);
        songProgressBar = (SeekBar) findViewById(R.id.song_playing_progressbar);
        songCurrentDurationLabel = (TextView) findViewById(R.id.current_time_txt);
        songTotalDurationLabel = (TextView) findViewById(R.id.total_time_txt);
        btnPlay.setOnClickListener(this);
        btnForward.setOnClickListener(this);
        btnBackward.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        songProgressBar.setOnClickListener(this);
        songCurrentDurationLabel.setOnClickListener(this);
        songTotalDurationLabel.setOnClickListener(this);
        linkFileEdt = (EditText) findViewById(R.id.linkfile_edt);
        nameSong=(TextView) findViewById(R.id.name);
        img=(ImageView) findViewById(R.id.imgCover);
    }
    //Dialog xử lý tiến trình tải giữa PlayerService và MainActivity
    private ProgressDialog pdBuff = null;
    boolean mBufferBroadcastIsRegistered;
    // Thiết lập BroadcastReceiver
    private BroadcastReceiver broadcastBufferReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            showPD(bufferIntent);
            //showNOTI(bufferIntent);
        }
    };
    private BroadcastReceiver notireceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showNOTI(intent);
        }
    };
    private void showNOTI(Intent bufferIntent) {
        String bufferValue=bufferIntent.getStringExtra("OpenNoti");
        int bufferIntValue=Integer.parseInt(bufferValue);
        if(bufferIntValue==1)
            MoNoti();
    }

    private void MoNoti() {
        Intent intentPlay=new Intent(ACTION_PLAY);
        //intentPlay.putExtra("NOTI1","1");
        //intentPlay.setAction(ACTION_PLAY);
        PendingIntent intentPplay= PendingIntent.getBroadcast(this,0,intentPlay,0);

        Intent intentPause=new Intent(ACTION_PAUSE);
        //intentPause.putExtra("NOTI1","2");
        //intentPlay.setAction(ACTION_PAUSE);
        PendingIntent intentPpause= PendingIntent.getBroadcast(this,0,intentPause,0);

        Notification.MediaStyle style=new Notification.MediaStyle();
        mBuilder=
                new Notification.Builder(this)
                .setStyle(style)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(name)
                .setContentText(artist)
                .addAction(android.R.drawable.ic_media_play,"PLAY",intentPplay)
                        .addAction(android.R.drawable.ic_media_pause,"PAUSE",intentPpause);


        Intent result=new Intent(this,MainActivity.class);
        result.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
        PendingIntent resultPendingIntent=PendingIntent.getActivity(this,0,result,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        style.setShowActionsInCompactView(0,1);
        mBuilder.setOngoing(true);
        NotificationManager mNotificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(1,mBuilder.build());

    }

    private void showPD(Intent bufferIntent) {
        String bufferValue = bufferIntent.getStringExtra("buffering");
        int bufferIntValue = Integer.parseInt(bufferValue);
//Nếu giá trị bufferIntValue bằng 1 thì cho dialog chạy
//Nếu giá trị bufferIntValue bằng 0 thì cho dismiss dialog
        switch (bufferIntValue) {
            case 0:
                if (pdBuff != null) {
                    pdBuff.dismiss();
                }
                break;
            case 1:
                BufferDialogue();
                break;
        }
    }
    private void BufferDialogue() {
        pdBuff = ProgressDialog.show(this, "Vui lòng chờ...", "Đang tải dữ liệu...", true);
        pdBuff.setCancelable(true);
    }
    @Override
    protected void onResume() {
//Đăng ký broadcast receiver
        if (!mBufferBroadcastIsRegistered) {
            registerReceiver(broadcastBufferReceiver, new IntentFilter(PlayerService.BROADCAST_BUFFER));

            mBufferBroadcastIsRegistered = true;
        }
        registerReceiver(notireceiver,new IntentFilter(PlayerService.BROADCAST_NOTI));
        super.onResume();
    }
    @Override
    protected void onPause() {
//Hủy đăng ký broadcast receiver
        if (mBufferBroadcastIsRegistered) {
            unregisterReceiver(broadcastBufferReceiver);
            mBufferBroadcastIsRegistered = false;
            unregisterReceiver(notireceiver);
        }
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!PlayerService.mp.isPlaying()) {
            stopService(playerService);
        }
    }
    public void onClick(View v) {
    }

}