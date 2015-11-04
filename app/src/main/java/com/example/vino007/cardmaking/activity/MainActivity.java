package com.example.vino007.cardmaking.activity;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vino007.cardmaking.R;
import com.example.vino007.cardmaking.constant.Constants;
import com.example.vino007.cardmaking.utils.MessageHandler;
import com.example.vino007.cardmaking.utils.MyApplication;
import com.example.vino007.cardmaking.utils.MyUtils;
import com.example.vino007.cardmaking.utils.SocketClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 0x01：发送命令成功
 * 0x02: 建立连接成功
 * 0x03: 建立连接失败
 * 0x04: 连接已断开，即socketclient为null或close
 * if (MyUtils.isWifiConnect(SettingActivity.this) && connectStatus)
 * if (client != null && !client.isClose())
 * 两重判断，第一个判断wifi是否连接，第二个判断socket是否建立
 *
 * 发送一个操作后，要开启监听单片机发送回来的数据
 */
public class MainActivity extends Activity {

    private Button readParameter_btn;
    private Button recharge_btn;
    private Button recycle_btn;
    private Button setting_btn;
    private Handler handler;
    private List<Integer> message ;//报文存储
    private SocketClient client = null;
    private MyApplication application;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setDisplayHomeAsUpEnabled(true);//添加返回button

        readParameter_btn = (Button) findViewById(R.id.readParameter_btn);
        recharge_btn= (Button) findViewById(R.id.recharge_btn);
        recycle_btn= (Button) findViewById(R.id.recycle_btn);
        setting_btn= (Button) findViewById(R.id.setting_btn);
        Log.d("start", "正常启动");
        handler = new MyHandler();
        //初始化下行报文
        message= MessageHandler.initMessage();
        application = (MyApplication) MainActivity.this.getApplication();

        /******************************监听器************************************/
  /*      readParameter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyUtils.isWifiConnect(MainActivity.this)) {
                   // ReadParameterSetting readParameterSetting = new ReadParameterSetting(message);
                   // message = readParameterSetting.readAll();

                    ReadMessageThread readMessageThread = new ReadMessageThread();
                    Thread mythread = new Thread(readMessageThread);
                    mythread.start();
                } else
                    Toast.makeText(MainActivity.this, "请先连接wifi", Toast.LENGTH_SHORT).show();
            }
        });*/

        recharge_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(MainActivity.this.getResources().getText(R.string.rechargeTitle));
                //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.recharge_dialog, null);
                //    设置我们自己定义的布局文件作为弹出框的Content
                builder.setView(view);

                final EditText recharge_edit = (EditText) view.findViewById(R.id.recharge_edit);

                builder.setPositiveButton(MainActivity.this.getResources().getText(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String rechargeValueStr = recharge_edit.getText().toString().trim();
                        Integer rechargeValue = Integer.parseInt(rechargeValueStr);
                        Log.i("充值金额rechargeValue", rechargeValue + "");
                        /**
                         * 对充值的数额进行操作
                         */
                        message=MessageHandler.getRechargeMessage(message,rechargeValue,getNowPassword());
                        sendOperation();


                    }
                });
                builder.setNegativeButton(MainActivity.this.getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });

        recycle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message=MessageHandler.getRecycleMessage(message);
                sendOperation();
            }
        });
        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SettingActivity.class);
                startActivity(intent);
            }
        });
    }


    /**
     * ******************************线程********************************************
     */

/*    class ReadMessageThread implements Runnable {
        @Override
        public void run() {
            Message msg = handler.obtainMessage();
            client=application.getClient();
            if (client != null && !client.isClose()) { //判断socket连接是否还存在
                client = application.getClient();
                List<Integer> result = client.sendMessageWithResponse(message);
                msg.what = 0X01;//发送报文成功
                msg.obj = result;
                handler.sendMessage(msg);
            } else {
                msg.what = 0x04;
                handler.sendMessage(msg);
            }
        }
    }*/
    /**
     * 发送报文，并调用handler处理结果
     */
    class SendMessageThread implements Runnable {
        @Override
        public void run() {
            client=application.getClient();
            if (client != null && !client.isClose()) { //判断socket连接是否还存在
                // client.sendMessage(message);//无返回值，不读取模块返回的信息
                List<Integer> responseMessage=client.sendMessageWithResponse(message);

                Message msg = handler.obtainMessage();
                msg.what = 0X01;//发送报文成功
                msg.obj=responseMessage;
                handler.sendMessage(msg);
            }else
            {
                Message msg = handler.obtainMessage();
                msg.what = 0X04;//发送报文成功
                handler.sendMessage(msg);
            }

        }

    }
    /**
     * *******************************handler********************************************
     */
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0X01) {
                Toast.makeText(MainActivity.this, "发送命令成功", Toast.LENGTH_SHORT).show();
                if (msg.obj != null) {
                    List<Integer> responseMessage = (List<Integer>) msg.obj;
                    String alterMessage=MessageHandler.handleMessage(responseMessage);
                    Toast.makeText(MainActivity.this, alterMessage, Toast.LENGTH_SHORT).show();

                    Log.i("接收到的报文", Arrays.toString(responseMessage.toArray()));

                }

            } else if (msg.what == 0x04) {
                Toast.makeText(MainActivity.this, "连接已断开，请返回上层重新连接", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void  sendOperation(){
        if (MyUtils.isWifiConnect(MainActivity.this)) {
            Thread thread=new Thread(new SendMessageThread());
            thread.start();
        } else
            Toast.makeText(MainActivity.this, "请先连接wifi", Toast.LENGTH_SHORT).show();
    }
    private String getNowPassword(){
        SharedPreferences sf=getSharedPreferences("passwordData", MODE_PRIVATE);
        String nowPassword=sf.getString("nowPassword", Constants.DEFAULT_NOW_PASSWORD);
        return nowPassword;
    }
}
