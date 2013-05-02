package com.xuxianming.simulator.stock;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * ��������
 * @author xuxianming01
 *
 */
public class Setting {
	
	public final static boolean DEBUG=false;
	/**
	 * ģ��Ĺ�Ʊ����
	 */
	public final static int STOCK_NUM=5;
	/**
	 * ��Ʊ����Ƶ��
	 * Level�������Ƶ��Ϊ���3��һ�Σ�����������Ͻ������˵����������ʱ����ڻ���ڴ˼��,���Ǽ�����2~6֮�䡣����ͬ��Ʊ��ĸ��¿��ܲ�һ�¡�
	 */
	public final static int UPDATES_FREQUENCY_LOW=2;
	/**
	 * ��Ʊ����Ƶ��
	 * Level�������Ƶ��Ϊ���3��һ�Σ�����������Ͻ������˵����������ʱ����ڻ���ڴ˼��,���Ǽ�����2~6֮�䡣����ͬ��Ʊ��ĸ��¿��ܲ�һ�¡�
	 */
	public final static int UPDATES_FREQUENCY_HIGH=6;
	/**
	 * ��Ʊ�۸���С�䶯��λΪ0.01Ԫ��������м۸�ȷ��Ϊ0.01Ԫ
	 * ����Ϊdouble�������ݱ���2λС����
	 */
	public final static int EXCHANGE_SCALE_PRICE=2;
	
	public final static double PRICE_MIN=0.01;
	/**
	 * ��С��������
	 * ���������걨ʱ��С�걨��λΪ100�ɣ���1�֣����������걨ʱ����Ʊ��������ͷ����100�ɣ�����һ���걨�������˴�����Ϊ���������������������С��λ��Ϊ100�ɣ�
	 */
	public final static int EXCHENGE_SCALE_MINIMUM=100;
	/**
	 * �ǵ�������
	 * �˴��ǵ�����10%Ϊ��
	 */
	public final static double LIMIT_UP_DOWN_EXTENT=10;
	
	/**
	 *�Ƿ���齻��ʱ�ο��أ�����չʾ��
	 *Ĭ��Ϊtrue����ʾ��Ӵ�����齻��ʱ��
	 */
	public final static boolean TRADING_TIME_CHECK_SWITH=false;
	
	/**
	 * ����ʱ���
	 * ����ʱ���Ӧ��9:30~11:30 ��13:00~15:00����ʱ�����
	 */
	public final static String[][] TIME_SUBSECTION = {{"9:00", "11:30"}, {"13:00", "15:00"}};  
	

	/**
	 * ���������б������������ż�����
	 */
	public final static int BID_ASK_NUM=10;
	
	/**
	 * ��Ʊ��ʼʱ����߼۸�
	 */
	public final static int STOCK_PRICE_HIGH=100;
	/**
	 * ��Ʊ��ʼʱ����ͼ۸�
	 */
	public final static int STOCK_PRICE_LOW=5;
	/**
	 * ��ͨ�ɱ�
	 */
	public final static int SHARE_CAPITAL_STOCK_NUM_HIGH=10000;
	/**
	 * ��ͨ�ɱ�
	 */
	public final static int SHARE_CAPITAL_STOCK_NUM_LOW=100;
	
	/**
	 * ÿ�����¼��������Ľ��ף���������ϢΪ0-BID_ASK_UPDATE_FREQ
	 */
	public final static int BID_ASK_UPDATE_FREQ=6;
	
	/**
	 * ���㵱����ͣ��
	 * @param LastClosePrice ��һ�����������̼�
	 * @return
	 */
	public static double getUpLimitPrice(double LastClosePrice)
	{
		double result=Math.floor(LastClosePrice*(100+Setting.LIMIT_UP_DOWN_EXTENT));
		result=result/100;
		if(result<=0)
		{
			result=PRICE_MIN;
		}
		BigDecimal   b   =   new   BigDecimal(result);  
		result   =   b.setScale(Setting.EXCHANGE_SCALE_PRICE,BigDecimal.ROUND_HALF_UP).doubleValue();
		return result;
	}
	
	/**
	 * ���㵱�յ�ͣ��
	 * @param LastClosePrice ��һ�����������̼�
	 * @return
	 */
	public static double getDownLimitPrice(double LastClosePrice)
	{
		double result=Math.ceil(LastClosePrice*(100-Setting.LIMIT_UP_DOWN_EXTENT));
		result=result/100;
		if(result<=0)
		{
			result=PRICE_MIN;
		}
		BigDecimal   b   =   new   BigDecimal(result);  
		result   =   b.setScale(Setting.EXCHANGE_SCALE_PRICE,BigDecimal.ROUND_HALF_UP).doubleValue();
		return result;
	}
	
	/**
	 * ��Ʊ����Ƶ��
	 * ��UPDATES_FREQUENCY_LOW��UPDATES_FREQUENCY_HIGH֮��
	 * @return
	 */
	public static int getUpdateFrequency()
	{
		//return (int) Math.round(Math.random()*(Setting.UPDATES_FREQUENCY_HIGH-Setting.UPDATES_FREQUENCY_LOW)+Setting.UPDATES_FREQUENCY_LOW);
		return Tool.getIntRandom(Setting.UPDATES_FREQUENCY_LOW, Setting.UPDATES_FREQUENCY_HIGH);
	}
	
	/**
	 *  ��ù�Ʊ����
	 * �ڴ���guid���
	 * @return
	 */
	public static String getSymbol()
	{
		return UUID.randomUUID().toString();
	}
	
	
	/**
	 * ��ù�Ʊ�ĳ�ʼ�۸�
	 * @return
	 */
	public static double getInitPrice()
	{
		//return (int) Math.round(Math.random()*(Setting.STOCK_PRICE_HIGH-Setting.STOCK_PRICE_LOW)+Setting.STOCK_PRICE_LOW);
		double result=Tool.getDoubleRandom(Setting.STOCK_PRICE_LOW, Setting.STOCK_PRICE_HIGH);
		return result;
	}
	/**
	 * ��ʼ�����йɱ�
	 * @return
	 */
	public static int getShareCapitalStock()
	{
		//return (int) Math.round(Math.random()*(Setting.SHARE_CAPITAL_STOCK_NUM_HIGH-Setting.SHARE_CAPITAL_STOCK_NUM_LOW)+Setting.SHARE_CAPITAL_STOCK_NUM_LOW);
		return Tool.getIntRandom(Setting.SHARE_CAPITAL_STOCK_NUM_LOW, Setting.SHARE_CAPITAL_STOCK_NUM_HIGH);
	}
	
	/**
	 * ���������¼���ڼ�����Ľ�����Ϣ����
	 * Ϊ0��Setting.BID_ASK_UPDATE_FREQ֮��������
	 * @return
	 */
	public static int getBidAskUpdateFreq()
	{
		//return (int) Math.round(Math.random()*Setting.SHARE_CAPITAL_STOCK_NUM_HIGH);
		return Tool.getIntRandom(0, Setting.BID_ASK_UPDATE_FREQ);
	}
}
