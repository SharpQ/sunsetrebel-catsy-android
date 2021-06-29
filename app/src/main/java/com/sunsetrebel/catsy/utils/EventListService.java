package com.sunsetrebel.catsy.utils;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import java.util.List;
import java.util.Map;

public class EventListService {
    private static List<Map<String, Object>> currentEventList = null;
    private static boolean needToUpdateList = true;
    private static final int MSG_CODE = 1;
    private static final long updateDelay = 300000;

    // this handler will receive a delayed message
    @SuppressLint("HandlerLeak")
    private static final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_CODE) {
                setListToUpdate();
            }
        }
    };

    public static boolean getListUpdateStatus() {
        return needToUpdateList;
    }

    public static void setListToNotUpdate() {
        needToUpdateList = false;
        mHandler.sendEmptyMessageDelayed(MSG_CODE, updateDelay);
    }

    public static void setListToUpdate() {
        needToUpdateList = true;
    }

    public static List<Map<String, Object>> getCurrentEventList() {
        return currentEventList;
    }

    public static void setCurrentEventList(List<Map<String, Object>> list) {
        currentEventList = list;
    }
}