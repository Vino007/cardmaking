package com.example.vino007.cardmaking.utils;

import android.util.Log;
import android.widget.Toast;

import com.example.vino007.cardmaking.constant.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 报文处理类
 *
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
        for (int i = 0; i < Constants.MESSAGE_LENGTH; i++)
            message.add(0xff);
        message.set(0,0X0D);
        message.set(1,0X0D);
        message.set(2,0X14);
        message.set(3,0XCD);
        message.set(19,0XFE);
        message.set(19,0XFE);

        return message;

    }

    /**
     * 20 00 00 00 00 00 00 00 00 00 00 00 00 00 00
     * @param message
     * @return
     */
    public static List<Integer> getRecycleMessage(List<Integer> message){
        message.set(0,0X0D);
        message.set(1,0X0D);
        message.set(2,0X14);
        message.set(3,0XCD);
        message.set(4,0XF0);
        message.set(5,0XFF);
        message.set(6,0XFF);
        message.set(7,0XFF);
        message.set(8,0XFF);
        message.set(9,0XFF);
        message.set(10,0XFF);
        message.set(11,0XFF);
        message.set(12,0XFF);
        message.set(13,0XFF);
        message.set(14,0XFF);
        message.set(15,0XFF);
        message.set(16,0XFF);
        message.set(17,0XFF);
        message.set(18,0XFF);
        message.set(19,0XFE);
        message.set(20,0XFE);

        return message;
    }

    /**
     * 21 X1 X2 P1 P2 P3 P4 P5 P6 00 00 00 00 00 00
     * @param message 报文
     * @param rechargeValue 充值金额
     * @param nowPassword 当前密码
     * @return
     */
    public static List<Integer> getRechargeMessage(List<Integer> message,int rechargeValue,String nowPassword){
        message.set(0,0X0D);
        message.set(1,0X0D);
        message.set(2,0X14);
        message.set(3,0XCD);
        message.set(4,0XF1);
        //设置金额
        Integer rechargeValueHigh=rechargeValue/256;
        Integer rechargeValueLow=rechargeValue%256;
        message.set(5,rechargeValueHigh);
        message.set(6,rechargeValueLow);
        //设置密码
        char[] singleNowPassword=nowPassword.toCharArray();
        Log.i("singleNowPassword", Arrays.toString(singleNowPassword));
        message.set(7,Integer.parseInt(singleNowPassword[0]+""));
        message.set(8,Integer.parseInt(singleNowPassword[1]+""));
        message.set(9,Integer.parseInt(singleNowPassword[2]+""));
        message.set(10,Integer.parseInt(singleNowPassword[3]+""));
        message.set(11,Integer.parseInt(singleNowPassword[4]+""));
        message.set(12,Integer.parseInt(singleNowPassword[5]+""));

        message.set(13,0XFF);
        message.set(14,0XFF);
        message.set(15,0XFF);
        message.set(16,0XFF);
        message.set(17,0XFF);
        message.set(18,0XFF);
        message.set(19,0XFE);
        message.set(20,0XFE);
        return message;
    }

    /**
     * 22 00 00 P1 P2 P3 P4 P5 P6 S1 S2 S3 S4 S5 S6
     * @param message
     * @return
     */
    public static List<Integer> getKeyCardMakingMessage(List<Integer> message,String nowPassword,String oldPassword){
        message.set(0,0X0D);
        message.set(1,0X0D);
        message.set(2,0X14);
        message.set(3,0XCD);
        message.set(4,0XF2);
        message.set(5,0XFF);
        message.set(6,0XFF);
        //设置新密码
        char[] singleNowPassword=nowPassword.toCharArray();
        message.set(7,Integer.parseInt(singleNowPassword[0]+""));
        message.set(8,Integer.parseInt(singleNowPassword[1]+""));
        message.set(9,Integer.parseInt(singleNowPassword[2]+""));
        message.set(10,Integer.parseInt(singleNowPassword[3]+""));
        message.set(11,Integer.parseInt(singleNowPassword[4]+""));
        message.set(12,Integer.parseInt(singleNowPassword[5]+""));

        //设置原密码
        if(oldPassword.equals(Constants.DEFAULT_OLD_PASSWORD)){
            message.set(13,0xFF);
            message.set(14,0xFF);
            message.set(15,0xFF);
            message.set(16,0xFF);
            message.set(17,0xFF);
            message.set(18,0xFF);
        }else{
            char[] singleOldPassword=oldPassword.toCharArray();
            message.set(13,Integer.parseInt(singleOldPassword[0]+""));
            message.set(14,Integer.parseInt(singleOldPassword[1]+""));
            message.set(15,Integer.parseInt(singleOldPassword[2]+""));
            message.set(16,Integer.parseInt(singleOldPassword[3]+""));
            message.set(17,Integer.parseInt(singleOldPassword[4]+""));
            message.set(18,Integer.parseInt(singleOldPassword[5]+""));
        }
        message.set(19,0XFE);
        message.set(20,0XFE);
        return message;
    }

    /**
     * 23 00 00 P1 P2 P3 P4 P5 P6 00 00 00 00 00 00
     * @param message
     * @param nowPassword 当前密码
     * @return
     */
    public static List<Integer> getManageCardMakingMessage(List<Integer> message,String nowPassword){
        message.set(0,0X0D);
        message.set(1,0X0D);
        message.set(2,0X14);
        message.set(3,0XCD);
        message.set(4,0XF3);
        message.set(5,0XFF);
        message.set(6,0XFF);
        //设置密码
        char[] singleNowPassword=nowPassword.toCharArray();
        message.set(7,Integer.parseInt(singleNowPassword[0]+""));
        message.set(8,Integer.parseInt(singleNowPassword[1]+""));
        message.set(9,Integer.parseInt(singleNowPassword[2]+""));
        message.set(10,Integer.parseInt(singleNowPassword[3]+""));
        message.set(11,Integer.parseInt(singleNowPassword[4]+""));
        message.set(12,Integer.parseInt(singleNowPassword[5]+""));

        message.set(13,0XFF);
        message.set(14,0XFF);
        message.set(15,0XFF);
        message.set(16,0XFF);
        message.set(17,0XFF);
        message.set(18,0XFF);
        message.set(19,0XFE);
        message.set(20,0XFE);
        return message;
    }



    /**
     * 处理接收到的报文
     * 05 00 X1 X2 00 00 旧卡
     * 05 00 00 00 01 01 无效卡
     * 05 00 00 00 02 02 IC卡块数据读写错误
     * 05 00 00 00 03 03 新卡
     * 05 00 00 00 04 04 制作秘钥卡
     * 05 00 00 00 06 06 制作管理卡片
     * @param msg
     * @return
     */
    public static String handleMessage(List<Integer> msg){
        //校验报文头

        if(msg==null || msg.size()!=6)
            return "失败，请重新尝试";
        Integer typeHigh=msg.get(4);
        Integer typeLow=msg.get(5);
        if(!typeHigh.equals(typeLow))
            return "失败，请重新尝试";
        String alertMessage;
        switch (typeHigh){
            case 0x03:
                alertMessage=null;break;
            case 0x00: {
               /* int remainValueHigh=msg.get(2);
                int remainValueLow=msg.get(3);
                int remainValue=remainValueHigh*256+remainValueLow;*/
                        alertMessage = null;
            }break;
            case 0x04:
                alertMessage="制秘钥卡成功";break;
            case 0x06:
                alertMessage="制管理卡成功";break;
            case 0x02:
                alertMessage=null;break;
            case 0x01:
                alertMessage=null;break;


            default:
                alertMessage="失败，请重新尝试";break;
        }
        return alertMessage;
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
