package com.example.looper;

import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//1.練習 handler
//        2.練習 looper
//        3.練習

/**練習looper
想知道 Looper.prepare(); 和 Looper.loop(); 是要怎麼用
 這中間就是無限迴圈，用來做一些需要一直執行的任務(事情)
 想停止就是 quit() 才會繼續執行後面的程式碼
 */
public class MainActivity extends AppCompatActivity {
    private WorkerThread mWorkerThread;
    private TextView mStatusLine;
    private Handler mMainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String text = (String) msg.obj;
                if (TextUtils.isEmpty(text)) {
                    return;
                }
                mStatusLine.setText(text);
            }
        };
        mWorkerThread = new WorkerThread();
        final Button action = (Button) findViewById(R.id.looper_demo_action);
        action.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mWorkerThread.executeTask("please do me a favor");
            }
        });
        final Button end = (Button) findViewById(R.id.looper_demo_quit);
        end.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mWorkerThread.exit();
            }
        });
        mStatusLine = (TextView) findViewById(R.id.looper_demo_displayer);
        mStatusLine.setText("Press ‘do me a favor’ to execute a task, press ‘end of service’ to stop looper thread");

    }

    public class WorkerThread extends Thread {
        protected static final String TAG = "WorkerThread";
        private Handler mHandler;
        private Looper mLooper;

        public WorkerThread() {
            start();
        }

        public void run() {
            // Attention: if you obtain looper before Looper#prepare(), you can still use the looper
            // to process message even after you call Looper#quit(), which means the looper does not
            //really quit.
            Looper.prepare();
            // So we should call Looper#myLooper() after Looper#prepare(). Anyway, we should put all stuff between Looper#prepare()
            // and Looper#loop().
            // In this case, you will receive “Handler{4051e4a0} sending message to a Handler on a dead thread
            // 05-09 08:37:52.118: W/MessageQueue(436): java.lang.RuntimeException: Handler{4051e4a0} sending message
            // to a Handler on a dead thread”, when try to send a message to a looper which Looper#quit() had called,
            // because the thread attaching the Looper and Handler dies once Looper#quit() gets called.
            mLooper = Looper.myLooper();
            // either new Handler() and new Handler(mLooper) will work
            mHandler = new Handler(mLooper) {
                @Override
                public void handleMessage(Message msg) {
                    /*
                     * Attention: object Message is not reusable, you must obtain a new one for each time you want to use it.
                     * Otherwise you got “android.util.AndroidRuntimeException: { what=1000 when=-15ms obj=it is my please
                     * to serve you, please be patient to wait!…….. } This message is already in use.”
                     */
//      Message newMsg = Message.obtain();
                    StringBuilder sb = new StringBuilder();
                    sb.append("it is my please to serve you, please be patient to wait !\n");
                    Log.e(TAG, "workerthread, it is my please to serve you, please be patient to wait !")
                    ;
                    for (int i = 1; i < 100; i++) {
                        sb.append(".");
                        Message newMsg = Message.obtain();
                        newMsg.obj = sb.toString();
                        mMainHandler.sendMessage(newMsg);
                        Log.e(TAG, "workthread, working" + sb.toString());
                        SystemClock.sleep(100);
                    }
                    Log.e(TAG, "workerthread, your work is done.");
                    sb.append("now your work is done");
                    Message newMsg = Message.obtain();
                    newMsg.obj = sb.toString();
                    mMainHandler.sendMessage(newMsg);
                }
            };
            Looper.loop();
            Log.i(TAG, "結束了啦" );
        }

        public void exit() {
            if (mLooper != null) {
                mLooper.quit();
                mLooper = null;
            }
        }

        public void executeTask(String text) {
            if (mLooper == null || mHandler == null) {
                Message msg = Message.obtain();
                msg.obj = "Sorry man, it is out of service";
                mMainHandler.sendMessage(msg);
                return;
            }
            Message msg = Message.obtain();
            msg.obj = text;
            mHandler.sendMessage(msg);
        }
    }
}
