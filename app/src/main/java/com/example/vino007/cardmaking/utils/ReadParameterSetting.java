package com.example.vino007.cardmaking.utils;

import java.util.List;

/**
 * Created by Joker on 2015/4/22.
 */
public class ReadParameterSetting {
    private List<Integer> msg;
    public ReadParameterSetting(List<Integer> msg) {
        this.msg=msg;
        msg.set(0,0x0D);
        msg.set(1,0x0D);
        msg.set(2,0x14);
        msg.set(3,0xCD);
    }
    public List<Integer> readAll(){

        return msg;
    }
}
