package com.xuxianming.simulator.stock;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 参数设置
 * @author xuxianming01
 *
 */
public class Setting {
	
	public final static boolean DEBUG=false;
	/**
	 * 模拟的股票数量
	 */
	public final static int STOCK_NUM=5;
	/**
	 * 股票更新频率
	 * Level行情更新频率为大概3秒一次（参照深交所、上交所相关说明），但有时会高于或低于此间隔,我们假设在2~6之间。另，不同股票间的更新可能不一致。
	 */
	public final static int UPDATES_FREQUENCY_LOW=2;
	/**
	 * 股票更新频率
	 * Level行情更新频率为大概3秒一次（参照深交所、上交所相关说明），但有时会高于或低于此间隔,我们假设在2~6之间。另，不同股票间的更新可能不一致。
	 */
	public final static int UPDATES_FREQUENCY_HIGH=6;
	/**
	 * 股票价格最小变动单位为0.01元，因此所有价格精确度为0.01元
	 * 这里为double类型数据保留2位小数点
	 */
	public final static int EXCHANGE_SCALE_PRICE=2;
	
	public final static double PRICE_MIN=0.01;
	/**
	 * 最小买卖数量
	 * 买入卖出申报时最小申报单位为100股（即1手），但卖出申报时若股票数量有零头不足100股，允许一起申报卖出。此处我们为简化起见，假设买入卖出最小单位均为100股；
	 */
	public final static int EXCHENGE_SCALE_MINIMUM=100;
	/**
	 * 涨跌幅限制
	 * 此处涨跌幅以10%为例
	 */
	public final static double LIMIT_UP_DOWN_EXTENT=10;
	
	/**
	 *是否检验交易时段开关（便于展示）
	 *默认为true，表示需哟啊检验交易时段
	 */
	public final static boolean TRADING_TIME_CHECK_SWITH=false;
	
	/**
	 * 行情时间段
	 * 行情时间戳应在9:30~11:30 、13:00~15:00两个时间段内
	 */
	public final static String[][] TIME_SUBSECTION = {{"9:00", "11:30"}, {"13:00", "15:00"}};  
	

	/**
	 * 行情数据中保留的买卖最优价数量
	 */
	public final static int BID_ASK_NUM=10;
	
	/**
	 * 股票初始时的最高价格
	 */
	public final static int STOCK_PRICE_HIGH=100;
	/**
	 * 股票初始时的最低价格
	 */
	public final static int STOCK_PRICE_LOW=5;
	/**
	 * 流通股本
	 */
	public final static int SHARE_CAPITAL_STOCK_NUM_HIGH=10000;
	/**
	 * 流通股本
	 */
	public final static int SHARE_CAPITAL_STOCK_NUM_LOW=100;
	
	/**
	 * 每个更新间隔间产生的交易（买卖）信息为0-BID_ASK_UPDATE_FREQ
	 */
	public final static int BID_ASK_UPDATE_FREQ=6;
	
	/**
	 * 计算当日涨停价
	 * @param LastClosePrice 上一个交易日收盘价
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
	 * 计算当日跌停价
	 * @param LastClosePrice 上一个交易日收盘价
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
	 * 股票更新频率
	 * 在UPDATES_FREQUENCY_LOW与UPDATES_FREQUENCY_HIGH之间
	 * @return
	 */
	public static int getUpdateFrequency()
	{
		//return (int) Math.round(Math.random()*(Setting.UPDATES_FREQUENCY_HIGH-Setting.UPDATES_FREQUENCY_LOW)+Setting.UPDATES_FREQUENCY_LOW);
		return Tool.getIntRandom(Setting.UPDATES_FREQUENCY_LOW, Setting.UPDATES_FREQUENCY_HIGH);
	}
	
	/**
	 *  获得股票代码
	 * 在此以guid替代
	 * @return
	 */
	public static String getSymbol()
	{
		return UUID.randomUUID().toString();
	}
	
	
	/**
	 * 获得股票的初始价格
	 * @return
	 */
	public static double getInitPrice()
	{
		//return (int) Math.round(Math.random()*(Setting.STOCK_PRICE_HIGH-Setting.STOCK_PRICE_LOW)+Setting.STOCK_PRICE_LOW);
		double result=Tool.getDoubleRandom(Setting.STOCK_PRICE_LOW, Setting.STOCK_PRICE_HIGH);
		return result;
	}
	/**
	 * 初始化发行股本
	 * @return
	 */
	public static int getShareCapitalStock()
	{
		//return (int) Math.round(Math.random()*(Setting.SHARE_CAPITAL_STOCK_NUM_HIGH-Setting.SHARE_CAPITAL_STOCK_NUM_LOW)+Setting.SHARE_CAPITAL_STOCK_NUM_LOW);
		return Tool.getIntRandom(Setting.SHARE_CAPITAL_STOCK_NUM_LOW, Setting.SHARE_CAPITAL_STOCK_NUM_HIGH);
	}
	
	/**
	 * 获得行情更新间隔期间产生的交易信息数量
	 * 为0到Setting.BID_ASK_UPDATE_FREQ之间的随机数
	 * @return
	 */
	public static int getBidAskUpdateFreq()
	{
		//return (int) Math.round(Math.random()*Setting.SHARE_CAPITAL_STOCK_NUM_HIGH);
		return Tool.getIntRandom(0, Setting.BID_ASK_UPDATE_FREQ);
	}
}
