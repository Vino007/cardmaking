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
import android.widget.TextView;
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


    private Button recharge_btn;
    private Button recycle_btn;
    private Button setting_btn;
    private TextView remainValue_tv;
    private TextView cardStatus_tv;
    private Handler handler;
    private List<Integer> message ;//报文存储
    private SocketClient client = null;
    private MyApplication application;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setDisplayHomeAsUpEnabled(true);//添加返回button

        remainValue_tv= (TextView) findViewById(R.id.remainValue_tv);
        cardStatus_tv= (TextView) findViewById(R.id.cardStatus_tv);
        recharge_btn= (Button) findViewById(R.id.recharge_btn);
        recycle_btn= (Button) findViewById(R.id.recycle_btn);
        setting_btn= (Button) findViewById(R.id.setting_btn);
        Log.d("start", "正常启动");
        handler = new MyHandler();
        //初始化下行报文
        message= MessageHandler.initMessage();
        application = (MyApplication) MainActivity.this.getApplication();
        readOperation();//死循环线程在其他activity中也不会销毁
        /******************************监听器************************************/


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
                        Log.i("rechargevaluestr",rechargeValueStr);
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

    /**
     * 发送报文，并调用handler处理结果
     */
    class SendMessageThread implements Runnable {
        @Override
        public void run() {
            client=application.getClient();
            if (client != null && !client.isClose()) { //判断socket连接是否还存在
                // client.sendMessage(message);//无返回值，不读取模块返回的信息
              //  List<Integer> responseMessage=client.sendMessageWithResponse(message);
                client.sendMessage(message);
           //     Log.i("responseMessage",Arrays.toString(responseMessage.toArray()));
                Message msg = handler.obtainMessage();
                msg.what = 0X01;//发送报文成功
           //     msg.obj=responseMessage;
                handler.sendMessage(msg);
            }else
            {
                Message msg = handler.obtainMessage();
                msg.what = 0X04;//发送报文成功
                handler.sendMessage(msg);
            }

        }

    }
    public class ReadMessageThread implements Runnable{

        @Override
        public void run() {
            client=application.getClient();
            while (true) {
                if (client != null && !client.isClose()) { //判断socket连接是否还存在
                    List<Integer> responseMessage=client.readMessage();
                    if(responseMessage!=null) {
                        Log.i("监听收到的报文为", Arrays.toString(responseMessage.toArray()));
                        Message msg = handler.obtainMessage();
                        msg.what=0x01;
                        msg.obj = responseMessage;
                        handler.sendMessage(msg);
                    }
                } else {
                    Message msg = handler.obtainMessage();
                    msg.what = 0X04;//发送报文成功
                    handler.sendMessage(msg);
                }
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
              //  Toast.makeText(MainActivity.this, "发送命令成功", Toast.LENGTH_SHORT).show();
                if (msg.obj != null) {
                    List<Integer> responseMessage = (List<Integer>) msg.obj;
                    String alterMessage=MessageHandler.handleMessage(responseMessage);
                    if(alterMessage!=null)
                        Toast.makeText(MainActivity.this, alterMessage, Toast.LENGTH_SHORT).show();
                    if(responseMessage!=null &&responseMessage.size()==Constants.RECEIVED_MESSAGE_LENGTH){
                        if(responseMessage.get(4).equals(0x00)&&responseMessage.get(5).equals(0x00)) {
                            remainValue_tv.setText(responseMessage.get(2) * 256 + responseMessage.get(3) + "");
                            cardStatus_tv.setText("旧卡");
                            recharge_btn.setClickable(true);
                            setting_btn.setClickable(false);
                            recycle_btn.setClickable(true);
                        }
                        else if(responseMessage.get(4).equals(0x03)&&responseMessage.get(5).equals(0x03)) {
                            cardStatus_tv.setText("新卡，可以制作秘钥卡和操作管理卡");
                            remainValue_tv.setText("无数据");
                            recharge_btn.setClickable(true);
                            setting_btn.setClickable(true);
                            recycle_btn.setClickable(false);
                        }
                        else if(responseMessage.get(4).equals(0x01)&&responseMessage.get(5).equals(0x01)) {
                            cardStatus_tv.setText("无效卡，不可操作");
                            remainValue_tv.setText("无数据");
                            recharge_btn.setClickable(false);
                            setting_btn.setClickable(false);
                            recycle_btn.setClickable(true);
                        } else if(responseMessage.get(4).equals(0x02)&&responseMessage.get(5).equals(0x02)) {
                            cardStatus_tv.setText("IC卡块数据读写错误");
                            remainValue_tv.setText("无数据");
                            recharge_btn.setClickable(false);
                            setting_btn.setClickable(false);
                            recycle_btn.setClickable(false);
                        }
                        Log.i("接收到的报文", Arrays.toString(responseMessage.toArray()));
                    }


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
    public void readOperation(){
        if (MyUtils.isWifiConnect(MainActivity.this)) {
            Thread thread=new Thread(new ReadMessageThread());
            thread.start();
        } else
            Toast.makeText(MainActivity.this, "请先连接wifi", Toast.LENGTH_SHORT).show();
    }
    private String getNowPassword(){
        SharedPreferences sf=getSharedPreferences("pwdData", MODE_PRIVATE);
        String nowPassword=sf.getString("nowPwd", Constants.DEFAULT_NOW_PASSWORD);
        return nowPassword;
    }

}
