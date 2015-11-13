package com.example.vino007.cardmaking.utils;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author vino007
 * @create 2015/11/12
 */
public class SocketServer {
    ServerSocket server;
    public SocketServer(int port){
        try {
            server=new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void beginListen(){
        InputStream is = null;
        BufferedReader in = null;
        while(true){
            try{
                final Socket socket = server.accept();
                List<Integer> resultMsg=new ArrayList<>();
                is = socket.getInputStream();
                in = new BufferedReader(new InputStreamReader(is));
                int data;
                while ((data=in.read())!='\r') { //判断接收到换行后停止

                    /*if (resultMsg.size() == 0 && data == 0x05)  //过滤报文
                        resultMsg.add(data);
                    else if (resultMsg.size() == 1 && data == 0x00)
                        resultMsg.add(data);
                    else if (resultMsg.size() > 1)*/
                        resultMsg.add(data);
                }
                socket.close();
                Log.i("receiveMessage", Arrays.toString(resultMsg.toArray()));
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

}
