package com.xuxianming.simulator.stock;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tool {

	/**
	 * 产生low到high之间的整数随机数
	 * @param low
	 * @param high
	 * @return
	 */
	public static int getIntRandom(int low,int high)
	{
		return (int) Math.round(Math.random()*(high-low)+low);
	}
	
	/**
	 * 产生low到high之间的随机数
	 * @param low
	 * @param high
	 * @return
	 */
	public static double getDoubleRandom(double low,double high)
	{
		double result=Math.random()*(high-low)+low;
		BigDecimal   b   =   new   BigDecimal(result);  
		result   =   b.setScale(Setting.EXCHANGE_SCALE_PRICE,BigDecimal.ROUND_HALF_UP).doubleValue();
		return  result;
	}
	
	/**
	 * 判断产生的随机数是否是偶数
	 * @return
	 */
	public static boolean getEven()
	{
		int result=getIntRandom(0,10);
		return (result%2==0)?true:false;	
	}
	//
	public static String longToDateString(long time,String dateformate)
	{
		SimpleDateFormat sdf= new SimpleDateFormat(dateformate);
		Date dt = new Date(time);  
		String sDateTime = sdf.format(dt); 
		return sDateTime;
	}
	public static String longToDateString(long time)
	{
		return longToDateString(time,"MM/dd/yyyy HH:mm:ss");
	}
}
