package com.xuxianming.simulator.stock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * 股票行情服务器类，用于生成单只股票行情
 * @author xuxianming01
 *
 */
public class StockMDServer {
	
	public static final String EXCHANGE_BID="bid"; 
	public static final String EXCHANGE_ASK="ask";
	public static final double MAX_PRICE=Double.MAX_VALUE;
	public static final double MIN_PRICE=-1;
	
	/**
	 * 股票代码
	 */
	private String symbol; 
	/**
	 * 中文简称，此字段实际中盘中不会每次发送，而是在盘前与客户端进行同步
	 */
	private String name;
	
	/**
	 * 流通股本
	 */
	private int shareCapitalStock;
	
	/**
	 * 当前已经挂出的卖委托单中，股票数量总数
	 * 实际交易中，该数字必须不大于shareCapitalStock
	 */
	private int shareCapitalStock_forSale;
	/**
	 * 时间戳
	 */
	private long messagetime;
	
	/**
	 * 行情更新频率
	 */
	private int updateFrequency;
	/**
	 * 涨停价格
	 */
	private double upLimitPrice;
	/**
	 * 跌停价格
	 */
	private double downLimitPrice;
	/**
	 * 对应的交易所类
	 */
	//public Exchange ex;

	
	/**
	 * 上一交易日收盘价
	 */
	private double lastClosePrice;
	/**
	 * 今日开盘
	 */
	private double open;
	/**
	 * 今日最高
	 */
	private double high;
	/**
	 * 今日最低
	 */
	private double low;
	/**
	 * 今日最新
	 */
	private double latest;

	/**
	 * 成交笔数
	 */
	private int tradenumber;
	/**
	 * 成交数量
	 */
	private int totalvolume;
	/**
	 * 成交金额
	 */
	private double totalvalue;
	/**
	 * 买一价格
	 * 买一数量
	 * 按价格从大到小排列
	 */
	private ArrayList<PriceVolumeObject> bidPricesVolumes;

	/**
	 * 卖一价格
	 * 卖一数量
	 * 按价格从小到大排列
	 */
	private ArrayList<PriceVolumeObject> askPricesVolumes;
	
	

	/**
	 * 用于共享Exchange实例对象的通道及检验交易时段
	 * @param _ex
	 */
	public void Init(Exchange _ex) 
	{
		//this.ex=_ex;
		//股票代码是一个guid
		symbol=Setting.getSymbol(); 
		name=symbol;
		//初始化股票价格
		lastClosePrice=Setting.getInitPrice();
		//初始化流通股本
		shareCapitalStock=Setting.getShareCapitalStock();
		//本股票行情更新频率
		updateFrequency=Setting.getUpdateFrequency();
		//初始化买价格和数量数组
		bidPricesVolumes=new ArrayList<PriceVolumeObject>(Setting.BID_ASK_NUM);
		
		//初始化卖价格和数量数组
		askPricesVolumes=new ArrayList<PriceVolumeObject>(Setting.BID_ASK_NUM);
		
		initData();
	} 
	
	public void initData()
	{
		for(int index=0;index<Setting.BID_ASK_NUM;index++)
		{
			PriceVolumeObject bid=new PriceVolumeObject(-1,0);
			bidPricesVolumes.add(bid);
			PriceVolumeObject ask=new PriceVolumeObject(MAX_PRICE,0);
			askPricesVolumes.add(ask);
		}
		//涨停价格
		upLimitPrice=Setting.getUpLimitPrice(lastClosePrice);
		//跌停价格
		downLimitPrice=Setting.getDownLimitPrice(lastClosePrice);
		//开盘价、最高价、最低价、今日最新初始值俊伟上一个交易日的收盘价
		//open=lastClosePrice;
		open=generateOpenByLast(lastClosePrice);
		high=lastClosePrice;
		low=lastClosePrice;
		latest=lastClosePrice;
		//成交笔数、成交数量、成交金额均为0
		tradenumber=0;
		totalvolume=0;
		totalvalue=0;
		
		//当前还没有卖出委托单
		shareCapitalStock_forSale=0;
		//
		messagetime= Calendar.getInstance().getTimeInMillis();
	}
	/**
	 * 连续交易函数，自动生成数据，打上时间戳，通过交易所类的通道发送
	 */
	public void ConsecutiveTrading ()
	{
		//确认是否配置了要判断交易时段
		/*if(!this.ex.IsTradingTime(Calendar.getInstance().getTimeInMillis()))
		{
			return;
		}*/
		int bidAskUpdateFreq=Setting.getBidAskUpdateFreq();
		for(int index=0;index<bidAskUpdateFreq;index++)
		{
			//如果产生的是偶数，则生成买信息
			if(Tool.getEven())
			{
				 generateBid();
			}
			else//如果产生的是奇数，则生成卖信息
			{
				generateAsk();
			}
		}
	}
	
	/**
	 * 产生买信息
	 */
	public void generateBid()
	{
		//产生交易价格
		//产生交易数量
		PriceVolumeObject bid=new PriceVolumeObject();
		//交易撮合
		//如果买价格高于现有最大的买价格，则尝试撮合交易
		//if((bidPricesVolumes.get(0).getPrice()>0&&bid.getPrice()>bidPricesVolumes.get(0).getPrice())||bidPricesVolumes.get(0).getPrice()<0)
		if(bid.getPrice()>=askPricesVolumes.get(0).getPrice())
		{
			bid=dealExchange(EXCHANGE_BID,bid);
		}
		if(bid.getVolume()==0)
		{
			return;
		}
		
		int index=0;
		int convertSing=-1;
		while(index<Setting.BID_ASK_NUM)
		{
			PriceVolumeObject bidTemp=(PriceVolumeObject)bidPricesVolumes.get(index);
			if(bidTemp.getPrice()>0&&bidTemp.getPrice()<bid.getPrice())
			{
				break;
			}
			else if(bidTemp.getPrice()==bid.getPrice())
			{
				bidTemp.setVolume(bidTemp.getVolume()+bid.getVolume());
				convertSing=0;
				break;
			}
			index++;
		}
		//如果新产生的买入价低于现有的买入价，则忽略该买请求
		//若已经合并数据，则返回
		//if((index==Setting.BID_ASK_NUM&&bidPricesVolumes.get(Setting.BID_ASK_NUM-1).getPrice()>0)||convertSing==0)
		if(convertSing==0)
		{
			return;
		}
		//若新产生的买入价格高于现有的某一个价格，则将现有的最低价格剔除
		//PriceVolumeObject bidTemp=(PriceVolumeObject)bidPricesVolumes.get(Setting.BID_ASK_NUM-1);
		//bidTemp.copyInfo(bid);
		bidPricesVolumes.add(bid);
		//从新按价格从低到高排序
		Collections.sort(bidPricesVolumes,new BidComparator());
	}
	
	/**
	 * 产生卖信息
	 */
	public void generateAsk()
	{
		//产生交易价格
		//产生交易数量
		PriceVolumeObject ask=new PriceVolumeObject();
		shareCapitalStock_forSale=shareCapitalStock_forSale+ask.getVolume();
		//交易撮合
		//如果买价格高于现有最大的买价格，则尝试撮合交易
		//if((askPricesVolumes.get(0).getPrice()<MAX_PRICE&&ask.getPrice()<askPricesVolumes.get(0).getPrice())||askPricesVolumes.get(0).getPrice()==MAX_PRICE)
		if(ask.getPrice()<=bidPricesVolumes.get(0).getPrice())
		{
			ask=dealExchange(EXCHANGE_ASK,ask);
		}
		if(ask.getVolume()==0)
		{
			return;
		}
		
		int index=0;
		int convertSing=-1;
		while(index<Setting.BID_ASK_NUM)
		{
			PriceVolumeObject askTemp=(PriceVolumeObject)askPricesVolumes.get(index);
			if(askTemp.getPrice()<MAX_PRICE&&askTemp.getPrice()>ask.getPrice())
			{
				break;
			}else if(askTemp.getPrice()==ask.getPrice())
			{
				askTemp.setVolume(askTemp.getVolume()+ask.getVolume());
				convertSing=0;
				break;
			}
			index++;
		}
		//如果新产生的买入价低于现有的买入价，则忽略该买请求
		//若已经合并数据，则返回
		//if((index==Setting.BID_ASK_NUM&&askPricesVolumes.get(Setting.BID_ASK_NUM-1).getPrice()<MAX_PRICE)||convertSing==0)
		if(convertSing==0)
		{
			return;
		}
		//若新产生的买入价格高于现有的某一个价格，则将现有的最低价格剔除
		//PriceVolumeObject askTemp=(PriceVolumeObject)askPricesVolumes.get(Setting.BID_ASK_NUM-1);
		//askTemp.copyInfo(ask);
		askPricesVolumes.add(ask);
		//从新按价格从低到高排序
		Collections.sort(askPricesVolumes,new AskComparator());
	}
	
	/**
	 * 撮合交易
	 * @param exType 交易类型
	 * @param pv
	 */
	public PriceVolumeObject dealExchange(final String exType,PriceVolumeObject pv)
	{
		if(exType==EXCHANGE_BID)
		{
			//如果最便宜的卖价格高于买价格，则返回
			PriceVolumeObject ask=askPricesVolumes.get(0);
			if(ask.getPrice()>pv.getPrice())
			{
				return pv;
			}
			int index=0;
			for(index=0;index<Setting.BID_ASK_NUM;index++)
			{
				ask=askPricesVolumes.get(index);
				//如果卖价格小于等于买价格，则撮合交易
				if(ask.getPrice()<=pv.getPrice())
				{
					int change=pv.getVolume()-ask.getVolume();
					if(change>0)
					{
						//成交笔数、成交数量、成交金额均为
						dealTradeTotal(ask.getVolume(),ask.getPrice());
						if(Setting.DEBUG)
							printDeal(exType,ask.getPrice(),ask.getVolume());
						
						pv.setVolume(change);
						ask.copyInfo(MAX_PRICE, 0);
						continue;
					}
					else if(change==0)
					{
						
						//成交笔数、成交数量、成交金额均为
						dealTradeTotal(ask.getVolume(),ask.getPrice());
						if(Setting.DEBUG)
							printDeal(exType,ask.getPrice(),ask.getVolume());
						
						pv.copyInfo(MIN_PRICE, 0);
						ask.copyInfo(MAX_PRICE, 0);
						break;
					}
					else
					{
						
						//成交笔数、成交数量、成交金额均为
						dealTradeTotal(pv.getVolume(),ask.getPrice());
						if(Setting.DEBUG)
							printDeal(exType,ask.getPrice(),pv.getVolume());
						pv.copyInfo(MIN_PRICE, 0);
						ask.setVolume(0-change);
						break;
					}
				}
				
			}
			//卖信息数组重新排序
			resortPriceVolume(EXCHANGE_ASK);
		}
		else if(exType==EXCHANGE_ASK)
		{
			//如果最高的买价格低于卖价格，则返回
			PriceVolumeObject bid=bidPricesVolumes.get(0);
			if(bid.getPrice()<pv.getPrice())
			{
				return pv;
			}
			int index=0;
			for(index=0;index<Setting.BID_ASK_NUM;index++)
			{
				bid=bidPricesVolumes.get(index);
				//如果买价格大于等于卖价格，则撮合交易
				if(bid.getPrice()>=pv.getPrice())
				{
					int change=pv.getVolume()-bid.getVolume();
					if(change>0)
					{
						
						//成交笔数、成交数量、成交金额均为
						dealTradeTotal(bid.getVolume(),bid.getPrice());
						if(Setting.DEBUG)
							printDeal(exType,bid.getPrice(),bid.getVolume());
						
						pv.setVolume(change);
						bid.copyInfo(MIN_PRICE, 0);
						continue;
					}
					else if(change==0)
					{
						
						//成交笔数、成交数量、成交金额均为
						dealTradeTotal(bid.getVolume(),bid.getPrice());
						if(Setting.DEBUG)
							printDeal(exType,bid.getPrice(),bid.getVolume());
						
						pv.copyInfo(MAX_PRICE, 0);
						bid.copyInfo(MIN_PRICE, 0);
						break;
					}
					else
					{
						
						//成交笔数、成交数量、成交金额均为
						dealTradeTotal(pv.getVolume(),bid.getPrice());
						if(Setting.DEBUG)
							printDeal(exType,bid.getPrice(),pv.getVolume());
						
						pv.copyInfo(MAX_PRICE, 0);
						bid.setVolume(0-change);
						break;
					}
				}
			}
			//买信息数组重新排序
			resortPriceVolume(EXCHANGE_BID);
		}
		return pv;	
	}
	
	/**
	 * 更新成交笔数、成交数量、成交金额
	 * @param volume 交易数量
	 * @param price 交易价格
	 */
	public void dealTradeTotal(int volume,double price)
	{	
		Calendar tempCalendar = Calendar.getInstance();
		
		Calendar oldCalendar = Calendar.getInstance();
		oldCalendar.setTimeInMillis(messagetime);  
		
		//判断是否是新的一天
		if(oldCalendar.get(Calendar.DAY_OF_YEAR)!=tempCalendar.get(Calendar.DAY_OF_YEAR))
		{
			//昨日收盘价等于昨日最新价格
			lastClosePrice=latest;
			initData();
		}
		else
		{
			//对于已经完成交易部分，需要从中扣除
			shareCapitalStock_forSale=shareCapitalStock_forSale-volume;
			
			messagetime=tempCalendar.getTimeInMillis();
			tradenumber+=1;
			totalvolume+=volume;
			totalvalue+=price*volume;
			
			latest=price;
			if(high<price)
			{
				high=price;
			}
			if(low>price)
			{
				low=price;
			}
		}
	}
	
	/**
	 * 对买卖信息数组进行排序
	 * @param exType
	 */
	public void resortPriceVolume(final String exType)
	{
		if(exType==EXCHANGE_BID)
		{
			Collections.sort(bidPricesVolumes,new BidComparator());
		}
		else if(exType==EXCHANGE_ASK)
		{
			Collections.sort(askPricesVolumes,new AskComparator());
		}
	}
	
	/**
	 * 随机产生股票价格
	 * @return
	 */
	public double generatePrice()
	{
		double temp=Tool.getDoubleRandom(lastClosePrice-lastClosePrice*0.2, lastClosePrice+lastClosePrice*0.2);  
		BigDecimal   b   =   new   BigDecimal(temp);  
		double   f1   =   b.setScale(Setting.EXCHANGE_SCALE_PRICE,BigDecimal.ROUND_HALF_UP).doubleValue(); 
		double result=f1;
		if(f1<this.downLimitPrice)
		{
			result=this.downLimitPrice;
		}
		else if(f1>this.upLimitPrice)
		{
			result=this.upLimitPrice;
		}
		if(result<=0)
		{
			result=Setting.PRICE_MIN;
		}
		return result;
	}
	
	/**
	 * 随机产生股票交易数量
	 * @return
	 */
	public int generateVolume()
	{
		int result=Tool.getIntRandom(1, this.shareCapitalStock/100);
		result=(result/100)*100;
		if(result<Setting.EXCHENGE_SCALE_MINIMUM)
		{
			result=Setting.EXCHENGE_SCALE_MINIMUM;
		}
		if(shareCapitalStock_forSale+result>shareCapitalStock)
		{
			result=shareCapitalStock-shareCapitalStock_forSale;
			result=(result/100)*100;
			if(result<Setting.EXCHENGE_SCALE_MINIMUM)
			{
				result=0;
			}
		}
		
		return result;
	}
	
	/**
	 * 生成开盘价
	 * 开盘价为昨日收盘价加一定扰动生成
	 * @param lastPrice
	 * @return
	 */
	public double generateOpenByLast(double lastPrice)
	{
		double temp=Tool.getDoubleRandom(lastClosePrice-lastClosePrice*0.05, lastClosePrice+lastClosePrice*0.05);
		BigDecimal   b   =   new   BigDecimal(temp);  
		double   f1   =   b.setScale(Setting.EXCHANGE_SCALE_PRICE,BigDecimal.ROUND_HALF_UP).doubleValue(); 
		double result=f1;
		return result;
	}
	
	/**
	 * 将stock行情信息转化为json字符串
	 * @return
	 */
	public String toJsonString()
	{
		//这种方式构建出来的json串太复杂
		//String result=JsonUtil.getJsonString4JavaPOJO(this, "MM-dd-yyyy");
		//return result;
		
		String result;
		JSONObject outData = new JSONObject();
		outData.put("symbol", this.getSymbol());
		outData.put("name", this.getName());
		outData.put("shareCapitalStock", this.getShareCapitalStock());
		outData.put("messagetime",Tool.longToDateString(this.getMessagetime()) );
		outData.put("updateFrequency", this.getUpdateFrequency());
		outData.put("upLimitPrice", this.getUpLimitPrice());
		outData.put("downLimitPrice", this.getDownLimitPrice());
		outData.put("lastClosePrice", this.getLastClosePrice());
		outData.put("open", this.getOpen());
		outData.put("high", this.getHigh());
		outData.put("low", this.getLow());
		outData.put("latest", this.getLatest());
		outData.put("tradenumber", this.getTradenumber());
		outData.put("totalvolume", this.getTotalvolume());
		outData.put("totalvalue", this.getTotalvalue());
		
		JSONArray gBidTable = new JSONArray();   
	    JSONArray gAskTable = new JSONArray();
	    
	    for(int index=0;index<Setting.BID_ASK_NUM;index++)
	    {
	    	if(bidPricesVolumes.get(index).getPrice()>0)
	    	{
	    		JSONObject bidNode = new JSONObject(); 
		    	bidNode.put("price",bidPricesVolumes.get(index).getPrice() );
		    	bidNode.put("volume", bidPricesVolumes.get(index).getVolume());
		    	gBidTable.add(bidNode); 
	    	}
	    	if(askPricesVolumes.get(index).getPrice()<MAX_PRICE)
	    	{
	    		JSONObject askNode = new JSONObject(); 
		    	askNode.put("price",askPricesVolumes.get(index).getPrice() );
		    	askNode.put("volume", askPricesVolumes.get(index).getVolume());
		    	gAskTable.add(askNode); 
	    	}
	    	
	    }
	    outData.put("bidPricesVolumes", gBidTable);  
	    outData.put("askPricesVolumes", gAskTable);
	    result=outData.toString();
	    if(Setting.DEBUG)
	    	printStockMDServer();
	    
	    return result; 
	}
	
	public void printDeal(String type, double price, int volume)
	{
		System.out.println("*********************************************");
		System.out.println("messagetime:"+Tool.longToDateString(this.getMessagetime()) );
		System.out.println("type:"+type);
		System.out.println("price:"+price);
		System.out.println("volume:"+volume);
		System.out.println("tradenumber:"+tradenumber);
		System.out.println("totalvolume:"+totalvolume);
		System.out.println("totalvalue:"+totalvalue);
		System.out.println("*********************************************");
	}
	
	public void printStockMDServer()
	{
		System.out.println("symbol:"+this.getSymbol());
		System.out.println("name:"+this.getName());
		System.out.println("shareCapitalStock:"+this.getShareCapitalStock());
		System.out.println("messagetime:"+Tool.longToDateString(this.getMessagetime()) );
		System.out.println("updateFrequency:"+ this.getUpdateFrequency());
		System.out.println("upLimitPrice:"+this.getUpLimitPrice());
		System.out.println("downLimitPrice:"+this.getDownLimitPrice());
		System.out.println("lastClosePrice:"+ this.getLastClosePrice());
		System.out.println("open:"+this.getOpen());
		System.out.println("high:"+ this.getHigh());
		System.out.println("low:"+this.getLow());
		System.out.println("latest:"+this.getLatest());
		System.out.println("tradenumber:"+ this.getTradenumber());
		System.out.println("totalvolume:"+ this.getTotalvolume());
		System.out.println("totalvalue:"+ this.getTotalvalue());
	    
	    System.out.println("bidPricesVolumes:");  
	    
	    
	    for(int index=0;index<Setting.BID_ASK_NUM;index++)
	    {
	    	if(bidPricesVolumes.get(index).getPrice()>0)
	    	{
	    		System.out.println("price:"+bidPricesVolumes.get(index).getPrice() );
	    		System.out.println("volume:"+ bidPricesVolumes.get(index).getVolume()); 
	    	}
	    }
	    System.out.println("askPricesVolumes:");
	    for(int index=0;index<Setting.BID_ASK_NUM;index++)
	    {
	    	if(askPricesVolumes.get(index).getPrice()<MAX_PRICE)
	    	{
	    		System.out.println("price:"+askPricesVolumes.get(index).getPrice() );
	    		System.out.println("volume:"+ askPricesVolumes.get(index).getVolume());
	    	}
	    	
	    }
	}
	
	public class PriceVolumeObject
	{
		private double price;
		private int volume;
		public PriceVolumeObject()
		{
			this.price=generatePrice();
			this.volume=generateVolume();
			if(Setting.DEBUG)
			{
				System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
				System.out.println("generate data:");
				System.out.println("price:"+this.price);
				System.out.println("volume:"+this.volume);
				System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
			}
		}
		public PriceVolumeObject(double price,int volume)
		{
			this.price=price;
			this.volume=volume;
		}
		
		public void copyInfo(PriceVolumeObject pv)
		{
			this.price=pv.getPrice();
			this.volume=pv.getVolume();
		}
		public void copyInfo(double price,int volume)
		{
			this.price=price;
			this.volume=volume;
		}
		
		public double getPrice()
		{
			return this.price;
		}
		public int getVolume()
		{
			return this.volume;
		}
		public void setPrice(double price)
		{
			this.price=price;
		}
		public void setVolume(int volume)
		{
			this.volume=volume;
		}
		
	}
	
	class BidComparator implements Comparator<PriceVolumeObject>
	{
		@Override
		public int compare(PriceVolumeObject pv0, PriceVolumeObject pv1) {
			// TODO Auto-generated method stub
			if(pv0.getPrice()<pv1.getPrice())
			{
				return 1;
			}
			else if(pv0.getPrice()>pv1.getPrice())
			{
				return -1;
			}
			else
			{
				return 0;
			}
		}
	}
	class AskComparator implements Comparator<PriceVolumeObject>
	{
		@Override
		public int compare(PriceVolumeObject pv0, PriceVolumeObject pv1) {
			// TODO Auto-generated method stub
			if(pv0.getPrice()>pv1.getPrice())
			{
				return 1;
			}
			else if(pv0.getPrice()<pv1.getPrice())
			{
				return -1;
			}
			else
			{
				return 0;
			}
			
		}
	}
	
	public void setSymbol(String symbol)
	{
		this.symbol=symbol;
	}
	public String getSymbol()
	{
		return this.symbol;
	}
	public void setName(String name )
	{
		this.name=name;
	}
	public String getName()
	{
		return this.name;
	}
	public void setShareCapitalStock(int shareCapitalStock)
	{
		this.shareCapitalStock=shareCapitalStock;
	}
	public int getShareCapitalStock()
	{
		return this.shareCapitalStock;
	}
	public void setMessagetime(long messagetime)
	{
		this.messagetime=messagetime;
	}
	public long getMessagetime()
	{
		return this.messagetime;
	}
	public void setUpdateFrequency(int updateFrequency)
	{
		this.updateFrequency=updateFrequency;
	}
	public int getUpdateFrequency()
	{
		return this.updateFrequency;
	}
	public void setUpLimitPrice(double upLimitPrice)
	{
		this.upLimitPrice=upLimitPrice;
	}
	public double getUpLimitPrice()
	{
		return this.upLimitPrice;
	}
	public void setDownLimitPrice(double downLimitPrice)
	{
		this.downLimitPrice=downLimitPrice;
	}
	public double getDownLimitPrice()
	{
		return this.downLimitPrice;
	}
	public void setLastClosePrice(double lastClosePrice)
	{
		this.lastClosePrice=lastClosePrice;
	}
	public double getLastClosePrice()
	{
		return this.lastClosePrice;
	}
	public void setOpen(double open)
	{
		this.open=open;
	}
	public double getOpen()
	{
		return this.open;
	}
	public void setHigh(double high)
	{
		this.high=high;
	}
	public double getHigh()
	{
		return this.high;
	}
	public void setLow(double low)
	{
		this.low=low;
	}
	public double getLow()
	{
		return this.low;
	}
	public void setLatest(double latest)
	{
		this.latest=latest;
	}
	public double getLatest()
	{
		return this.latest;
	}
	public void setTradenumber(int tradenumber)
	{
		this.tradenumber=tradenumber;
	}
	public int getTradenumber()
	{
		return this.tradenumber;
	}
	public void setTotalvolume(int totalvolume)
	{
		this.totalvolume=totalvolume;
	}
	public int getTotalvolume()
	{
		return this.totalvolume;
	}
	public void setTotalvalue(double totalvalue)
	{
		this.totalvalue=totalvalue;
	}
	public double getTotalvalue()
	{
		return this.totalvalue;
	}
	public void setBidPricesVolumes(ArrayList<PriceVolumeObject> bidPricesVolumes)
	{
		for(int index=0;index<Setting.BID_ASK_NUM;index++)
		{
			this.bidPricesVolumes.get(index).copyInfo(bidPricesVolumes.get(index));
		}
	}
	public ArrayList<PriceVolumeObject> getBidPricesVolumes()
	{
		return this.bidPricesVolumes;
	}
	public void setAskPricesVolumes( ArrayList<PriceVolumeObject> askPricesVolumes)
	{
		for(int index=0;index<Setting.BID_ASK_NUM;index++)
		{
			this.askPricesVolumes.get(index).copyInfo(askPricesVolumes.get(index));
		}
	}
	public ArrayList<PriceVolumeObject> getAskPricesVolumes()
	{
		return this.askPricesVolumes;
	}
}
