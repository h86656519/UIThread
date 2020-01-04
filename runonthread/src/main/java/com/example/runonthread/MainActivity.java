package com.example.runonthread;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //创建一个线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                //延迟两秒
                try {
                    Thread.sleep( 2000 );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "hah", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        }).start();
    }
}
