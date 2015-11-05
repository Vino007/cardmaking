package com.example.vino007.cardmaking.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
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

public class SettingActivity extends Activity {
    private Button manageCardMaking_btn;
    private Button keyCardMaking_btn;
    private Button alterPassword_btn;

    private SocketClient client = null;
    private MyApplication application;
    private Handler handler;
    private List<Integer> message = new ArrayList<>();//报文存储
    private TextView oldPassword_tv;
    private TextView nowPassword_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        manageCardMaking_btn= (Button) findViewById(R.id.manageCardMaking_btn);
        keyCardMaking_btn= (Button) findViewById(R.id.keyCardMaking_btn);
        alterPassword_btn= (Button) findViewById(R.id.alterPassword_btn);
        oldPassword_tv= (TextView) findViewById(R.id.old_password_tv);
        nowPassword_tv= (TextView) findViewById(R.id.now_password_tv);
        oldPassword_tv.setText(getOldPassword());
        nowPassword_tv.setText(getNowPassword());

        //初始化下行报文
        message= MessageHandler.initMessage();
        application = (MyApplication) SettingActivity.this.getApplication();
        handler=new MyHandler();

        manageCardMaking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message=MessageHandler.getManageCardMakingMessage(message,getNowPassword());
                sendOperation();
            }
        });
        keyCardMaking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message=MessageHandler.getKeyCardMakingMessage(message,getNowPassword(),getOldPassword());
                sendOperation();
            }
        });
        alterPassword_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sf=getSharedPreferences("passwordData", MODE_PRIVATE);
                final String oldPassword=sf.getString("oldPassword", Constants.DEFAULT_OLD_PASSWORD);
                final String nowPassword=sf.getString("nowPassword", Constants.DEFAULT_NOW_PASSWORD);
                Log.i("oldPassword",oldPassword);
                Log.i("nowPassword",nowPassword);

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle(SettingActivity.this.getResources().getText(R.string.alterPasswordTitle));
                //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                View view = LayoutInflater.from(SettingActivity.this).inflate(R.layout.alter_password_dialog, null);
                //    设置我们自己定义的布局文件作为弹出框的Content
                builder.setView(view);

                final EditText alterPassword_edit = (EditText) view.findViewById(R.id.alterPassword_edit);

                builder.setPositiveButton(SettingActivity.this.getResources().getText(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newPassword = alterPassword_edit.getText().toString().trim();
                        Log.i("newPassword",newPassword);
                        //使用shareence保存起来
                        SharedPreferences.Editor editor = getSharedPreferences("passwordData", MODE_PRIVATE).edit();
                        editor.putString("oldPassword", nowPassword);
                        editor.putString("nowPassword", newPassword);
                        editor.commit();
                        nowPassword_tv.setText(newPassword);
                        oldPassword_tv.setText(nowPassword);


                    }
                });

                builder.setNegativeButton(SettingActivity.this.getResources().getText(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
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
                client.sendMessage(message);//无返回值，不读取模块返回的信息
             //   List<Integer> responseMessage=client.sendMessageWithResponse(message);
              //  Log.i("responseMessage", Arrays.toString(responseMessage.toArray()));
                Message msg = handler.obtainMessage();
                msg.what = 0X01;//发送报文成功
              //  msg.obj=responseMessage;
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
            if(msg.obj!=null&&msg.what==0x01) {
                List<Integer> responseMessage = (List<Integer>) msg.obj;
                String alterMessage = MessageHandler.handleMessage(responseMessage);
                if(alterMessage!=null)
                    Toast.makeText(SettingActivity.this,alterMessage, Toast.LENGTH_SHORT).show();

            }

             if (msg.what == 0x02) {//连接成功
                Toast.makeText(SettingActivity.this, "连接成功", Toast.LENGTH_LONG).show();
            } else if (msg.what == 0x03) {//连接失败
                Toast.makeText(SettingActivity.this, "创建连接失败，请确认是否连接对正确的wifi", Toast.LENGTH_SHORT).show();
            }else if(msg.what==0x04)
                Toast.makeText(SettingActivity.this,"连接已断开，请返回上层重新连接",Toast.LENGTH_SHORT).show();
        }
    }
    public void  sendOperation(){
        if (MyUtils.isWifiConnect(SettingActivity.this)) {
            Thread thread=new Thread(new SendMessageThread());
            thread.start();
        } else
            Toast.makeText(SettingActivity.this, "请先连接wifi", Toast.LENGTH_SHORT).show();
    }
    private String getOldPassword(){
        SharedPreferences sf=getSharedPreferences("passwordData", MODE_PRIVATE);
        String oldPassword=sf.getString("oldPassword", Constants.DEFAULT_OLD_PASSWORD);

        return oldPassword;
    }
    private String getNowPassword(){
        SharedPreferences sf=getSharedPreferences("passwordData", MODE_PRIVATE);
        String nowPassword=sf.getString("nowPassword", Constants.DEFAULT_NOW_PASSWORD);
        return nowPassword;
    }

}
