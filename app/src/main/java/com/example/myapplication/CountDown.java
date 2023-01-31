package com.example.myapplication;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.myapplication.account_management.HomePageFragment;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CountDown extends LinearLayout {

    TextView tv_hour, tv_min, tv_sec;
    Runnable runnable;
    Handler mHandler = new Handler();

    int hou, min, sec;

    public CountDown(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.count_down, this, true);
        tv_hour = findViewById(R.id.countdown_hour);
        tv_min = findViewById(R.id.countdown_minute);
        tv_sec = findViewById(R.id.countdown_second);


        runnable = new Runnable() {
            @Override
            public void run() {
                boolean needProcess = processCountMsg();
                if(!needProcess) return;


                mHandler.postDelayed(this, 1000);
            }
        };

    }

    private boolean processCountMsg() {
        if (hou == 0 && min == 0 && sec == 0) {
            Toast.makeText(getContext(), "Vote time is over", Toast.LENGTH_SHORT).show();

            HomePageFragment.ifHaveCurrentVote = false;
            EventBus.getDefault().post(new TimeOutEvent());

            return false;
        }
        if (sec > 0) {
            sec--;
        } else {
            sec = 59;
            if (min == 0) {
                min = 59;
                hou--;
            } else {
                min--;
            }
        }
        String hour, minute, second;
        hour = (hou < 10) ? "0" + hou : "" + hou;
        minute = (min < 10) ? "0" + min : "" + min;
        second = (sec < 10) ? "0" + sec : "" + sec;

        tv_hour.setText(hour);
        tv_min.setText(minute);
        tv_sec.setText(second);
        return true;
    }


    public void setData(String endTime, String curTime) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date endDate = null, curDate = null;

        try {
            endDate = format.parse(endTime);
            System.out.println("format.parse(startTime)format.parse(startTime)format.parse(startTime)format.parse(startTime)format.parse(startTime)format.parse(startTime)format.parse(startTime)format.parse(startTime)format.parse(startTime)format.parse(startTime)format.parse(startTime)" + format.parse(endTime));
            curDate = format.parse(curTime);

            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++Date Parse Successfully +++++++++++++++++++++++++++++++{++++++");
            System.out.println(endTime + " " + curTime);

            //TODO
            String time;
            //计算时间戳与系统时间的时间差，单位为秒
            int timeDifference = (int) (endDate.getTime() - curDate.getTime()) / 1000;
            //将总秒数转化为时分秒
            if (timeDifference < 60) {
                time = String.format("000:00:%02d", timeDifference % 60);
            } else if (timeDifference < 3600) {
                time = String.format("000:%02d:%02d", timeDifference / 60, timeDifference % 60);
            } else {
                time = String.format("%02d:%02d:%02d", timeDifference / 3600, timeDifference % 3600 / 60, timeDifference % 60);
            }
            //通过“：”分离时、分、秒
            String[] sArray = time.split(":");
            hou = Integer.parseInt(sArray[0]);
            min = Integer.parseInt(sArray[1]);
            sec = Integer.parseInt(sArray[2]);
            //通过Handler中的post()方法传递message
            mHandler.post(runnable);

        } catch (Exception e) {


            e.printStackTrace();
        }


    }

}
