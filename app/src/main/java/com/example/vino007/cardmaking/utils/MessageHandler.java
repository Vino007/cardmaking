package com.example.vino007.cardmaking.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joker on 2015/5/14.
 */
public class MessageHandler {
    final static String[] modelDetails={"模式1:只刷卡,密码无效","模式2:只刷卡,密码无效,具有计次功能","模式3:刷卡,密码有效","模式4:刷卡,密码有效,卡片带时间限制"};
    /**
     * 初始化报文
     * @return
     */
    public static List<Integer> initMessage(){
        List<Integer> message=new ArrayList<>();
        for (int i = 0; i < 9; i++)
            message.add(0xff);
        return message;

    }
    /**
     * 处理接收到的报文信息，将处理后的信息回传给listview显示
     * @param msg 接收到的报文
     * @return parameterContents 返回listview显示用的数组
     */
    public static String[] messageHandle(List<Integer> msg){

        /**
         * 进行和校验
         */

        String[] parameterContents = {"", "", "",""};


        /**
         * 处理报文信息，转换成文本信息
         */
        String year= Integer.toHexString(msg.get(4));
        String month= Integer.toHexString(msg.get(5));
        String day= Integer.toHexString(msg.get(6));
        String hour,minute,startHour,startMinute,endHour,endMinute;
        if(msg.get(7)<10)
            hour="0"+ Integer.toHexString(msg.get(7));
        else
            hour= Integer.toHexString(msg.get(7));
        if(msg.get(8)<10)
            minute="0"+ Integer.toHexString(msg.get(8));
        else
            minute= Integer.toHexString(msg.get(8));
        if(msg.get(9)<10)
            startHour="0"+ Integer.toHexString(msg.get(9));
        else
            startHour= Integer.toHexString(msg.get(9));
        if(msg.get(10)<10)
            startMinute="0"+ Integer.toHexString(msg.get(10));
        else
            startMinute= Integer.toHexString(msg.get(10));
        if(msg.get(11)<10)
            endHour="0"+ Integer.toHexString(msg.get(11));
        else
            endHour= Integer.toHexString(msg.get(11));
        if(msg.get(12)<10)
            endMinute="0"+ Integer.toHexString(msg.get(12));
        else
            endMinute= Integer.toHexString(msg.get(12));

        String model= Integer.toHexString(msg.get(13));

        /**
         * model显示具体信息还未测试
         */
        String modelDetail;
        switch (model){
            case "1":modelDetail= modelDetails[0];break;
            case "2":modelDetail= modelDetails[1];break;
            case "3":modelDetail= modelDetails[2];break;
            case "4":modelDetail= modelDetails[3];break;
            default:modelDetail="none";break;
        }
        parameterContents[0]="20"+year+"年"+month+"月"+day+"日  "+hour+":"+minute;
        parameterContents[1]=startHour+":"+startMinute+"-"+endHour+":"+endMinute;
        parameterContents[2]=modelDetail;
        if(msg.get(9)==0x00&&msg.get(10)==0x00&&msg.get(11)==0x00&&msg.get(12)==0x00)
            parameterContents[3]="开启";
        else
            parameterContents[3]="关闭";

    return parameterContents;
    }

}
