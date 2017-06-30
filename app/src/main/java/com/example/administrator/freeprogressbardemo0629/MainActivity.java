package com.example.administrator.freeprogressbardemo0629;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


//模仿daimajia数字进度条
public class MainActivity extends AppCompatActivity implements OnProgressBarListener {

    private NumberProgressBar bnp;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bnp = (NumberProgressBar) findViewById(R.id.number1);
        bnp.setOnProgressBarListener(this);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    bnp.incrementProgressBy(1);
                    }
                });
            }
        },1000,100);
    }

    @Override
    public void onProgressChange(int current, int max) {
        if(current == max) {
            Toast.makeText(getApplicationContext(),"进度条已完成。。。", Toast.LENGTH_SHORT).show();
         bnp.setProgress(0);
        }
    }
}
