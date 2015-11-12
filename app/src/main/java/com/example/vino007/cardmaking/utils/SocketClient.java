package com.example.vino007.cardmaking.utils;

import android.util.Log;

import com.example.vino007.cardmaking.constant.Constants;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Joker on 2015/3/31.
 */
public class SocketClient {
    private Socket client = null;
    OutputStream os = null;
    PrintWriter out = null;
    InputStream is = null;
    BufferedReader in = null;

    public SocketClient(String host, int port) throws RuntimeException {
        try {
            client = new Socket(host, port);//这种构造器会一直阻塞到直到连上服务器

           // client.setSoTimeout(6000);//设置超时时间
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("newSocketError", "newSocketError");
            throw new RuntimeException("socketclient构造器出错");
        }
    }

    /**
     * 发送信息给指定服务器
     * 返回服务器发回的信息，服务器的信息需已回车结尾才能立即收到，要不然只能等到连接断开后才能收到
     * AT+CIPSEND=0，6 回车也算一个字符，因此是五个数据+回车
     *
     * @param msg
     * @return
     */
    public String sendMessage(String msg) {

        try {
            os = client.getOutputStream();
            out = new PrintWriter(os, true);//自动flush
            is = client.getInputStream();
            in = new BufferedReader(new InputStreamReader(is));
            out.println(msg);

            return in.readLine();//阻塞直到读取到换行，读取响应的字符串
           /* if(in.hasNext()) {
                String str=in.next();
                Log.e("receive","receive");
                return str;
            }*/

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("sendMessageError", "sendMessageError");
        } finally {
          /*
          *调用close来进行关闭，使得达到长连接的效果
          * */
           /* if(out!=null)
                out.close();
            if(os!=null)
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("closeError","closeError");
                }
            if(in!=null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if(is!=null)
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            return "";
        }
        // return in.hasNext()?in.nextLine():"";

    }

    public void close() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public  String getClientStatus(){
        if(client==null){
            return "client null";
        }else if(client.isClosed()==true){
            return "client closed";
        }else if(client.isConnected()==false){
            return "client disConnected";
        }else
            return "client run correctly";
    }


    public boolean isClose() {
        return client.isClosed();
    }

    /**
     * 发送整型数组
     * 每次发送完毕后初始化报文即全部设置为0xff
     *
     * @param msg
     * @return
     */

    public void sendMessage(List<Integer> msg) {
        Log.i("sendMessage",Arrays.toString(msg.toArray()));
      //  msg = sumCheck(msg);//添加和校验
        try {
            os = client.getOutputStream();
            BufferedOutputStream out = new BufferedOutputStream(os);//不能使用dataoutputStream，由于data传送的是byte类型，byte的范围-127-127,不符合
            is = client.getInputStream();
            in = new BufferedReader(new InputStreamReader(is));

            if (msg != null) {
                for (int i = 0; i < msg.size(); i++) {

                    out.write(msg.get(i));

                }
                out.flush();
                MyUtils.clearList(msg);//发送成功后清空报文数据

            }
            //return in.readLine();//阻塞直到读取到换行，读取响应的字符串

        } catch (IOException e) {
            e.printStackTrace();
        } finally {


        }
    }

    public List<Integer> readMessage(){

        List<Integer> resultMsg=new ArrayList<>();
        try {
            is = client.getInputStream();

        in = new BufferedReader(new InputStreamReader(is));
        int data;
        while ((data=in.read())!='\r'){ //判断接收到换行后停止

            if (resultMsg.size()==0&&data==0x05)  //过滤报文
                 resultMsg.add(data);
            else if (resultMsg.size()==1&&data==0x00)
                resultMsg.add(data);
            else if (resultMsg.size()>1)
                resultMsg.add(data);
        }
        }catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("receiveMessage",Arrays.toString(resultMsg.toArray()));

        return resultMsg;

    }


    /**
     * 发送整型数组
     * 每次发送完毕后初始化报文即全部设置为0xff
     *
     * @param msg List<Integer> msg
     * @return null or List<Integer>
     */
    public List<Integer> sendMessageWithResponse(List<Integer> msg) {
      //  msg = crc(msg);
        Log.i("发送的报文",Arrays.toString(msg.toArray()));
        List<Integer> resultMsg=new ArrayList<>();
        try {
            os = client.getOutputStream();
            BufferedOutputStream out = new BufferedOutputStream(os);//不能使用dataoutputStream，由于data传送的是byte类型，byte的范围-127-127,不符合
            is = client.getInputStream();
            in = new BufferedReader(new InputStreamReader(is));

            if (msg != null) {
                for (int i = 0; i < msg.size(); i++) {

                    out.write(msg.get(i));

                }
                out.flush();
                MyUtils.clearList(msg);//发送成功后清空报文数据

            }
            int data;
            while ((data=in.read())!='\r'){ //判断接收到换行后停止
                resultMsg.add(data);
            }
            Log.i("接收到的参数报文", Arrays.toString(resultMsg.toArray()));
            return resultMsg;//阻塞直到读取到换行，读取响应的字符串
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {

        }

    }
}
