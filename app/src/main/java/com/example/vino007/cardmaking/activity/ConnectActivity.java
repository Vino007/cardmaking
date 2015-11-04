package com.example.vino007.cardmaking.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.vino007.cardmaking.constant.Constants;
import com.example.vino007.cardmaking.utils.MyApplication;
import com.example.vino007.cardmaking.utils.MyUtils;
import com.example.vino007.cardmaking.R;
import com.example.vino007.cardmaking.utils.SocketClient;

/**
 * 0x01：发送命令成功
 * 0x02: 建立连接成功
 * 0x03: 建立连接失败
 * 0x04: 连接已断开，即socketclient为null或close
 *
 * if (MyUtils.isWifiConnect(SettingActivity.this) && connectStatus)
 *
 *  if (client != null && !client.isClose())
 *  两重判断，第一个判断wifi是否连接，第二个判断socket是否建立
 */
public class ConnectActivity extends Activity {
    private Button connect_btn;
    private Handler handler;
    private boolean connectStatus = false;
    private MyApplication application;
    private SocketClient client = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        connect_btn = (Button) findViewById(R.id.connect_btn);
        handler = new MyHandler();
        application = (MyApplication) ConnectActivity.this.getApplication();
        connect_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (MyUtils.isWifiConnect(ConnectActivity.this)) {
                    if (!connectStatus) {
                        ConnectThread connectThread = new ConnectThread();
                        Thread thread = new Thread(connectThread);
                        thread.start();
                    } else {
                        client.close();
                        connectStatus = false;
                        connect_btn.setText("connect");
                    }
                } else
                    Toast.makeText(ConnectActivity.this, "请先连接对应的wifi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * *******************************handler********************************************
     */
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 0x02) {//连接成功
                connect_btn.setText("disconnect");
                connectStatus = true;
                application.setConnectStatus(true);
                Toast.makeText(ConnectActivity.this, "连接成功", Toast.LENGTH_LONG).show();
                Intent intent=new Intent(ConnectActivity.this,MainActivity.class);
                startActivity(intent);

            } else if (msg.what == 0x03) {//连接失败
                connect_btn.setText("connect");
                connectStatus = false;
                application.setConnectStatus(false);
                Toast.makeText(ConnectActivity.this, "创建连接失败，请确认是否连接对正确的wifi", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * ******************************线程********************************************
     */

    class ConnectThread implements Runnable {
        @Override
        public void run() {
            try {
                client = new SocketClient(Constants.DEFAULT_DEVICE_IP, Constants.DEFAULT_PORT);
                application.setClient(client);
                application.setConnectStatus(true);
                Message msg = handler.obtainMessage();
                msg.what = 0x02;//成功
                handler.sendMessage(msg);

            } catch (RuntimeException e) {
                e.printStackTrace();
                application.setConnectStatus(false);
                Log.e("error", "SocketClient构造器出错");
                Message msg = handler.obtainMessage();
                msg.what = 0x03;//创建连接失败
                handler.sendMessage(msg);
            }
        }

    }
}
