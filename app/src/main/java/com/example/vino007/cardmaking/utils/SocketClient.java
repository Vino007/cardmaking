package com.example.vino007.cardmaking.utils;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
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

    /**
     * 和校验
     * @param msg
     * @return
     */
    public List<Integer> sumCheck(List<Integer> msg){

        Integer sum=0;
        for(int i=0;i<msg.size()-2;i++){
            sum=sum+msg.get(i);
        }
        Integer addh=sum/256;
        Integer addl=sum%256;
        msg.set(msg.size() - 2, addh);
        msg.set(msg.size() - 1, addl);
        return  msg;
    }

    /**
     * 下行报文的crc校验
     *
     * @param msg
     * @return
     */
    public List<Integer> crc(List<Integer> msg) {
        StringBuilder sb = new StringBuilder();
        //校验前7位，后两位为crc校验码
        for (int i = 0; i < msg.size() - 2; i++) {
            if (msg.get(i) < 17)
                sb.append("0" + Integer.toHexString(msg.get(i)));
            else
                sb.append(Integer.toHexString(msg.get(i)));
        }
        byte[] sbuf = CRC16M.getSendBuf(sb.toString());
        String crcResultHexString = CRC16M.getBufHexStr(sbuf);
        String crcLow = crcResultHexString.charAt(crcResultHexString.length() - 2) + "" + crcResultHexString.charAt(crcResultHexString.length() - 1);
        String crcHigh = crcResultHexString.charAt(crcResultHexString.length() - 4) + "" + crcResultHexString.charAt(crcResultHexString.length() - 3);
        Log.i("crc", crcResultHexString);
        Log.i("crclow", crcLow);
        Log.i("crchigh", crcHigh);
        msg.set(msg.size() - 2, Integer.parseInt(crcHigh, 16));
        msg.set(msg.size() - 1, Integer.parseInt(crcLow, 16));
        Log.i("下行报文", Arrays.toString(msg.toArray()));
        return msg;

    }

    public boolean isClose() {
        return client.isClosed();
    }

    /**
     * 发送整型数组
     * 每次发送完毕后初始化报文即全部设置为0x00
     *
     * @param msg
     * @return
     */

    public void sendMessage(List<Integer> msg) {

        msg = sumCheck(msg);//添加和校验
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


    /**
     * 发送整型数组
     * 每次发送完毕后初始化报文即全部设置为0x00
     *
     * @param msg
     * @return
     */
    public List<Integer> readMessage(List<Integer> msg) {
        msg = crc(msg);
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
            while ((data=in.read())!='\n'){ //判断接收到换行后停止
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
