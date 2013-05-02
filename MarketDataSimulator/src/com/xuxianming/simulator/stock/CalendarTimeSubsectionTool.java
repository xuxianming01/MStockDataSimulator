package com.xuxianming.simulator.stock;

import java.util.Calendar;

/**
 * ʱ��δ�����
 * @author xuxianming01
 *
 */
public class CalendarTimeSubsectionTool
{
	/** 
     * ��֤ĳһʱ���Ƿ���ĳһʱ��� ����������Դ���磩
     * @param currTime ĳһʱ�� 
     * @param timeSlot ĳһʱ��� 
     * @return true/false 
     */  
    public static boolean isShift(final long currTime, String[] timeSlot) {  
        Calendar tempCalendar = Calendar.getInstance();  
        tempCalendar.setTimeInMillis(currTime);  
        String[] tmpArray = timeSlot[0].split(":");  
        long startTime, stopTime;  
        tempCalendar.clear(Calendar.HOUR_OF_DAY);  
        tempCalendar.clear(Calendar.MINUTE);  
        tempCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tmpArray[0]));  
        tempCalendar.set(Calendar.MINUTE, Integer.parseInt(tmpArray[1]));  
        startTime = tempCalendar.getTimeInMillis();  
        tmpArray = timeSlot[1].split(":");  
        int stopHour  = Integer.parseInt(tmpArray[0]), stopMinute = Integer.parseInt(tmpArray[1]);  
        if (stopHour == 0) {  
            tempCalendar.add(Calendar.DAY_OF_MONTH, 1);  
        }  
        tempCalendar.clear(Calendar.HOUR_OF_DAY);  
        tempCalendar.clear(Calendar.MINUTE);  
        tempCalendar.set(Calendar.HOUR_OF_DAY, stopHour);  
        tempCalendar.set(Calendar.MINUTE, stopMinute);  
        stopTime = tempCalendar.getTimeInMillis();  
        return ((startTime < currTime && currTime <= stopTime) ? true : false);  
    }  
    
    /**
     * �ж�ĳһʱ���Ƿ��ǹ�����
     * @param currTime
     * @return
     */
    public static boolean isExchangeDay(final long currTime)
    {
    	Calendar tempCalendar = Calendar.getInstance();  
        tempCalendar.setTimeInMillis(currTime); 
        int day=tempCalendar.get(Calendar.DAY_OF_WEEK);
        return  ((day>1&&day<7)?true : false);
    }

}