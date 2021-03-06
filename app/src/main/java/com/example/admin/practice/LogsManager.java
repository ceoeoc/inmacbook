package com.example.admin.practice;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import com.example.admin.practice.DB.CIDBHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LogsManager {

    public static final int INCOMING = CallLog.Calls.INCOMING_TYPE;
    public static final int OUTGOING = CallLog.Calls.OUTGOING_TYPE;
    public static final int MISSED = CallLog.Calls.MISSED_TYPE;
    public static final int TOTAL = 579;

    public static final int INCOMING_CALLS = 672;
    public static final int OUTGOING_CALLS = 609;
    public static final int MISSED_CALLS = 874;
    public static final int ALL_CALLS = 814;
    private static final int READ_CALL_LOG = 47;
    private Context context;
    private CIDBHandler dh;

    public LogsManager(Context context) {
        this.context = context;
    }

    @RequiresPermission(Manifest.permission.READ_CALL_LOG)
    public int getOutgoingDuration() {
        int sum = 0;

        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                CallLog.Calls.TYPE + " = " + CallLog.Calls.OUTGOING_TYPE, null, null);

        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);

        while (cursor.moveToNext()) {
            String callDuration = cursor.getString(duration);
            sum += Integer.parseInt(callDuration);
        }

        cursor.close();

        return sum;
    }

    @RequiresPermission(Manifest.permission.READ_CALL_LOG)
    public int getIncomingDuration() {
        int sum = 0;

        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                CallLog.Calls.TYPE + " = " + CallLog.Calls.INCOMING_TYPE, null, null);

        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);

        while (cursor.moveToNext()) {
            String callDuration = cursor.getString(duration);
            sum += Integer.parseInt(callDuration);
        }

        cursor.close();

        return sum;
    }

    @RequiresPermission(Manifest.permission.READ_CALL_LOG)
    public int getTotalDuration() {
        int sum = 0;

        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);

        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);

        while (cursor.moveToNext()) {
            String callDuration = cursor.getString(duration);
            sum += Integer.parseInt(callDuration);
        }

        cursor.close();

        return sum;
    }

    @RequiresPermission(Manifest.permission.READ_CALL_LOG)
    public String getCoolDuration(int type) {
        float sum;

        switch (type) {
            case INCOMING:
                sum = getIncomingDuration();
                break;
            case OUTGOING:
                sum = getOutgoingDuration();
                break;
            case TOTAL:
                sum = getTotalDuration();
                break;
            default:
                sum = 0;
        }

        String duration = "";
        String result;

        if (sum >= 0 && sum < 3600) {

            result = String.valueOf(sum / 60);
            String decimal = result.substring(0, result.lastIndexOf("."));
            String point = "0" + result.substring(result.lastIndexOf("."));

            int minutes = Integer.parseInt(decimal);
            float seconds = Float.parseFloat(point) * 60;

            DecimalFormat formatter = new DecimalFormat("#");
            duration = minutes + " min " + formatter.format(seconds) + " secs";

        } else if (sum >= 3600) {

            result = String.valueOf(sum / 3600);
            String decimal = result.substring(0, result.lastIndexOf("."));
            String point = "0" + result.substring(result.lastIndexOf("."));

            int hours = Integer.parseInt(decimal);
            float minutes = Float.parseFloat(point) * 60;

            DecimalFormat formatter = new DecimalFormat("#");
            duration = hours + " hrs " + formatter.format(minutes) + " min";

        }

        return duration;
    }

    @RequiresPermission(Manifest.permission.READ_CALL_LOG)
    public List<LogObject> getLogs(int callType,String phone, long timelimit) {
        List<LogObject> logs = new ArrayList<>();

        String selection;

        switch (callType) {
            case INCOMING_CALLS:
                selection = CallLog.Calls.TYPE + " = " + CallLog.Calls.INCOMING_TYPE;
                break;
            case OUTGOING_CALLS:
                selection = CallLog.Calls.TYPE + " = " + CallLog.Calls.OUTGOING_TYPE;
                break;
            case MISSED_CALLS:
                selection = CallLog.Calls.TYPE + " = " + CallLog.Calls.MISSED_TYPE;
                break;
            case ALL_CALLS:
                selection = CallLog.Calls.NUMBER;
            default:
                selection = CallLog.Calls.NUMBER;
        }

        if(!phone.isEmpty()){
            selection = selection +  " and " + CallLog.Calls.NUMBER + " = " + "'" +phone + "'";
        }
        if(timelimit != 0){
            selection = selection + " and " + CallLog.Calls.DATE + " >= " + timelimit;
        }

        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, selection, null, null);
        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);

        while (cursor.moveToNext()) {
            LogObject log = new LogObject(context);

            log.setNumber(cursor.getString(number));
            log.setType(cursor.getInt(type));
            log.setDuration(cursor.getInt(duration));
            log.setDate(cursor.getLong(date));
            Log.v("add",phone);
            logs.add(log);
        }

        cursor.close();
        return logs;
    }

    @RequiresPermission(Manifest.permission.READ_CALL_LOG)
    public int getLogCount(int callType,String phone, long timelimit) {
        String selection;
        int ret = 0;
        switch (callType) {
            case INCOMING_CALLS:
                selection = CallLog.Calls.TYPE + " = " + CallLog.Calls.INCOMING_TYPE;
                break;
            case OUTGOING_CALLS:
                selection = CallLog.Calls.TYPE + " = " + CallLog.Calls.OUTGOING_TYPE;
                break;
            case MISSED_CALLS:
                selection = CallLog.Calls.TYPE + " = " + CallLog.Calls.MISSED_TYPE;
                break;
            case ALL_CALLS:
                selection = CallLog.Calls.NUMBER;
            default:
                selection = CallLog.Calls.NUMBER;
        }
        if(!phone.isEmpty()){
            selection = selection +  " and " + CallLog.Calls.NUMBER + " = " + "'" +phone + "'";
        }
        if(timelimit != 0){
            selection = selection + " and " + CallLog.Calls.DATE + " >= " + timelimit;
        }
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, selection, null, null);
        ret = cursor.getCount();
        cursor.close();
        return ret;
    }
    //그룹별 통화기록 횟수를 측정함
    @RequiresPermission(Manifest.permission.READ_CALL_LOG)
    public HashMap<String,Integer> getLogs(HashMap<String,Integer> groups,int callType, long timelimit,int type) {
        if(dh == null) {
            dh = new CIDBHandler(context);
            dh.open();
        }
        List<ContactsItem> lists;
        lists = dh.getData(0);
        String temp;
        switch (callType) {
            case INCOMING_CALLS:
                temp = CallLog.Calls.TYPE + " = " + CallLog.Calls.INCOMING_TYPE;
                break;
            case OUTGOING_CALLS:
                temp = CallLog.Calls.TYPE + " = " + CallLog.Calls.OUTGOING_TYPE;
                break;
            case MISSED_CALLS:
                temp = CallLog.Calls.TYPE + " = " + CallLog.Calls.MISSED_TYPE;
                break;
            case ALL_CALLS:
                temp = CallLog.Calls.NUMBER;
            default:
                temp = CallLog.Calls.NUMBER;
        }
        if (timelimit != 0) {
            temp = temp + " and " + CallLog.Calls.DATE + " >= " + timelimit;
        }
        for(int i = 0 ; i < lists.size(); i++) {
            String selection;
            ContactsItem tmpCi = lists.get(i);
            selection = temp + " and " + CallLog.Calls.NUMBER + " = " + "'" + tmpCi.getPhone() + "'";
            Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, selection, null, null);
            switch (type) {
                case 0://통화 건수 측정
                    if (cursor.getCount() == 0) continue;
                    groups.put(tmpCi.getGroup(), groups.get(tmpCi.getGroup()) + cursor.getCount());
                break;
                case 1://통화 시간 측정
                    int sum = 0;
                    int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
                    while (cursor.moveToNext()) {
                        String callDuration = cursor.getString(duration);
                        sum += Integer.parseInt(callDuration);
                    }
                    if(sum == 0) continue;
                    groups.put(tmpCi.getGroup(), groups.get(tmpCi.getGroup()) + sum);
                break;
            }
            cursor.close();
        }

        return groups;
    }

}
