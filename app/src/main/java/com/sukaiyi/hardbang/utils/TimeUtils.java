package com.sukaiyi.hardbang.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 集中了时间相关的处理函数
 * Created by sukai on 2017/04/21.
 */

public class TimeUtils {

    private static final Map<Integer, String> DAY_NAME_MAP;

    static {
        DAY_NAME_MAP = new HashMap<Integer, String>();
        DAY_NAME_MAP.put(0, "今天");
        DAY_NAME_MAP.put(1, "昨天");
        DAY_NAME_MAP.put(2, "前天");
        DAY_NAME_MAP.put(-1, "明天");
        DAY_NAME_MAP.put(-2, "后天");
    }

    /**
     * @return 当前时间在月份中的日期
     */
    public static int getDay() {
        return getDay(new Date());
    }

    /**
     * @param time 时间
     * @return 指定时间在月份中的日期
     */
    public static int getDay(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("dd");
        return Integer.parseInt(format.format(date));
    }

    /**
     * @param date 时间
     * @return 指定时间在月份中的日期
     */
    public static int getDay(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd");
        return Integer.parseInt(format.format(date));
    }

    /**
     * 判断两个时间是不是同一天
     *
     * @return
     */
    public static boolean isTheSameDay(long t1, long t2) {
        return isTheSameDay(new Date(t1), new Date(t2));
    }

    /**
     * 判断两个时间是不是同一天
     *
     * @return
     */
    public static boolean isTheSameDay(Date d1, Date d2) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr1 = format.format(d1);
        String dateStr2 = format.format(d2);
        return dateStr1.equals(dateStr2);
    }

    /**
     * @param time
     * @return 指定时间相对于现在的称呼，例如 昨天，前天，明天。。。
     * 如果时间相差过久，则返回日期（MM-dd）
     */
    public static String getDayName(long time) {
        Date now = new Date();
        Date then = new Date(time);
        //172800000ms = 48h
        if (Math.abs(now.getTime() - time) < 172800000) {
            int nowDay = getDay(now);
            int thenDay = getDay(then);
            int offset = nowDay - thenDay;
            return DAY_NAME_MAP.get(offset);
        } else {//直接返回MM-dd
            SimpleDateFormat format = new SimpleDateFormat("MM-dd");
            return format.format(then);
        }
    }

    /**
     * @param time
     * @return 指定时间相对于现在的称呼，例如 昨天，前天，明天(包含具体时间)。。。
     * 如果时间相差过久，则返回日期（MM-dd）
     * 例如 昨天 12:54
     */
    public static String getDayAndTimeName(long time){
        String dayName = getDayName(time);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return dayName+" "+format.format(new Date(time));
    }
}
