package com.example.vino007.cardmaking.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;

/**
 * Created by Joker on 2015/4/16.
 */
public class MyUtils {
    //将list清空成全零数组
    public static void clearList(List list) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                list.set(i, 0xff);
            }
        }

    }

    /**
     * 判断是否连接上wifi
     * isConnected 为true表示当前手机有连接上网络
     * iswifi 为true 表示当前手机连接的网络是wifi
     *
     * @return
     */
    public static boolean isWifiConnect(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();//若当前没有任何网络连接则为null，因此使用之前要先判断！null
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        boolean isWifi = false;
        if (isConnected) {
            isWifi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

        }
        return isWifi;
    }
    public static int decToBcd(int dec){
        int high=dec/10;
        int low=dec%10;

        return high*16+low;

    }
}
