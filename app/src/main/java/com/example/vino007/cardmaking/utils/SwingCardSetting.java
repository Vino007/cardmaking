package com.example.vino007.cardmaking.utils;

import java.util.List;

/**
 * Created by Joker on 2015/4/16.
 */
public class SwingCardSetting {
    private List<Integer> msg = null;

    public SwingCardSetting(List<Integer> msg) {
        this.msg = msg;
        msg.set(0, 0xF1);
        msg.set(1, 0x03);
    }

    public List<Integer> setMode(int workMode) {

        msg.set(1, 0x03);
        msg.set(2, workMode);
        return msg;
    }

    /**
     * 关闭或开启刷卡时段功能
     * 报文时间段默认22:00-8:00
     * 调用enable后再调用timedialog
     * @param isEnable
     * @return
     */
    public List<Integer> setEnableSwingCard(boolean isEnable){
        if(isEnable){
            msg.set(1,0x02);
            msg.set(2,0x22);
            msg.set(3,0x00);
            msg.set(4,0x08);
            msg.set(5,0x00);
            msg.set(6,0xee);
        }else {
            msg.set(1,0x02);
            msg.set(2,0x22);
            msg.set(3,0x00);
            msg.set(4,0x08);
            msg.set(5,0x00);
            msg.set(6,0x00);
        }
        return msg;
    }
}
