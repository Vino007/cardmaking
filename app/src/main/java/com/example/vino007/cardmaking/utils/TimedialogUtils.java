/**
 * Created by Joker on 2015/4/16.
 */
package com.example.vino007.cardmaking.utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.List;

public class TimedialogUtils {
    private List<Integer> msg = null;
    private String type = null;
    private Context context;

    public TimedialogUtils(Context context, List<Integer> msg) {
        this.msg = msg;
        this.context = context;
        msg.set(0, 0xF1);//设置报文头
        msg.set(1, 0x01);//默认是01

    }

    public List showDatepickerDialog() {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        final int day = c.get(Calendar.DAY_OF_MONTH);
        final int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                msg.set(2, MyUtils.decToBcd(year-2000));
                msg.set(3, MyUtils.decToBcd(monthOfYear + 1));//月份是从0开始的
                msg.set(4, MyUtils.decToBcd(dayOfMonth));

            }
        }, year, month, day);
        dialog.show();
        return msg;
    }


    /**
     * @param timeType deviceTime表示设备的时钟，beginTime表示刷卡开始时间,endTime表示刷卡结束时间
     * @return msg 返回报文
     */
    public List showTimepickerDialog(String timeType) {

        type = timeType;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog dialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.i("hour", hourOfDay + "");
                if (type.equals("beginTime")) {
                    msg.set(1, 0x02);
                    msg.set(2, MyUtils.decToBcd(hourOfDay));
                    msg.set(3, MyUtils.decToBcd(minute));

                    Log.i("beginTime", msg.get(2) + ":" + msg.get(3));

                } else if (type.equals("endTime")) {
                    msg.set(1, 0x02);
                    msg.set(4, MyUtils.decToBcd(hourOfDay));
                    msg.set(5, MyUtils.decToBcd(minute));
                    Log.i("endTime", msg.get(4) + ":" + msg.get(5));
                } else if (type.equals("deviceTime")) {
                    msg.set(5, MyUtils.decToBcd(hourOfDay));
                    msg.set(6, MyUtils.decToBcd(minute));
                    Log.i("deviceTime", msg.get(5) + ":" + msg.get(6));
                }
            }

        }, hour, minute, true);
        dialog.show();
        return msg;
    }
}
