package hteam.mp3playback;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.Vector;
import java.util.concurrent.ExecutionException;

import hteam.model.SubjectMp3;

public class SubjectScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_screen);
        loadSubject subject=new loadSubject();
        subject.execute("http://mp3.zing.vn/chu-de");
        Vector home_center=new Vector<SubjectMp3>();
        RecyclerView rvMp3=(RecyclerView) findViewById(R.id.recycler_view);
        rvMp3.setHasFixedSize(true);
        rvMp3.setLayoutManager(new LinearLayoutManager(this));

        try {
            home_center=subject.get();
            rvMp3.setAdapter(new AdapterMp3(home_center,this));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
//        Button btn1=(Button) findViewById(R.id.btnGoToScreen2);
//        btn1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(SubjectScreen.this,MainActivity.class);
//                startActivity(intent);
//            }
//        });
    }
}
