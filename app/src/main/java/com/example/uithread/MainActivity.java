package com.example.uithread;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;


/**
 *
* */
public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    static final int PROGRESS_VALUE1 = 1;
    static final int PROGRESS_VALUE2 = 2;
    Button mButton;
    ProgressBar mProgressBar;
    ProgressBar mProgressBar2;
    int mClickCount = 0;
    MyHandler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        mProgressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        mButton = (Button) findViewById(R.id.button1);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickCount++;
                new Thread() {
                    @Override
                    public void run() {
                        int what = 0;
                        if (mClickCount == 1)
                            what = PROGRESS_VALUE1;
                        else if (mClickCount == 2)
                            what = PROGRESS_VALUE2;

                        for (int i = 0; i < 100; i++) {
                            try {
                                sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            Message msg = handler.obtainMessage(what, i + 1, 0);
                            handler.sendMessage(msg);
                        }
                        super.run();
                    }
                }.start();
            }
        });
        handler = new MyHandler();
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //區分消息的種類
            if (msg.what == PROGRESS_VALUE1) {
                mProgressBar.setProgress(msg.arg1);
                Log.i(TAG, "handleMessage: " + msg.arg1);
                if (msg.arg1 == 100)
                    mProgressBar.setProgress(0);
                super.handleMessage(msg);
            } else if (msg.what == PROGRESS_VALUE2) {
                mProgressBar2.setProgress(msg.arg1);
                super.handleMessage(msg);
            }
        }
    }
}
