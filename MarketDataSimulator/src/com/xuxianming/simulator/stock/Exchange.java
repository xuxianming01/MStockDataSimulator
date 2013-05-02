package com.xuxianming.simulator.stock;

import java.util.Calendar;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;


/**
 * �������࣬���ƽ���ʱ�䡢����ͨ��
 * @author xuxianming01
 *
 */
public class Exchange implements SendStockDataTool.Iface {
	/**
	 * ��Ʊ�������ݷ���������
	 */
	private StockMDServer[] Stocks;
	//private TradeThread[] exThreads;
	private Thread[] exThreads;
	
	/**
	 * ��ʼ����������ʼ����Ʊ������ÿֻ��Ʊ�ǵ����������̼ۡ�����ͨ��
	 */
	public void Init()
	{
		Stocks=new StockMDServer[Setting.STOCK_NUM];
		exThreads=new Thread[Setting.STOCK_NUM];
		for(int index=0;index<Setting.STOCK_NUM;index++)
		{
			Stocks[index]=new StockMDServer();
			Stocks[index].Init(this);
			TradeThread ttd=new TradeThread(String.valueOf(index));
			ttd.setStockMDServer(Stocks[index]);
			exThreads[index]=new Thread(ttd);
		}
	}
	
	public void ConsecutiveTrading()
	{
		ConsecutiveTradingThread ctt=new ConsecutiveTradingThread();
		//Thread demo = new Thread(ctt);
		//demo.start();
		ctt.run();
	}
	
	/**
	 * ���ʱ���Ƿ��ڽ���ʱ����
	 * �����ռ򵥶�λ������
	 * @return
	 */
	public boolean IsTradingTime(final long Messagetime)
	{
		if(!Setting.TRADING_TIME_CHECK_SWITH)
		{
			return true;
		}
		//�ж��Ƿ�����
		if(!CalendarTimeSubsectionTool.isExchangeDay(Messagetime))
		{
			return false;
		}
		//�ж��Ƿ��ڽ���ʱ����
		for(String[] timeSlot:Setting.TIME_SUBSECTION)
		{
			if(CalendarTimeSubsectionTool.isShift(Messagetime, timeSlot))
			{
				return true;
			}
		}
		return false;
	}
	
	class ConsecutiveTradingThread implements Runnable {
		private int sign=0; 
		public ConsecutiveTradingThread(){}
		public void run() {
			try
			{
				//Exchange.this.ConsecutiveTrading();
				while(true)
				{
					//sign==1��ʾ�����߳��Ѿ�������
					if(sign==0&&Exchange.this.IsTradingTime(Calendar.getInstance().getTimeInMillis()))
					{
						for(int index=0;index<Setting.STOCK_NUM;index++)
						{
							//exThreads[index].run();
							//Thread demo = new Thread(exThreads[index]);
							//demo.start();
							if(!exThreads[index].isAlive())
								exThreads[index].start();
						}
						sign=1;
					}
					else
					{
						//���ǽ���ʱ�Σ��򿴽����߳��Ƿ�������������ȫ���ر�
						if(sign==1)
						{
							for(int index=0;index<Setting.STOCK_NUM;index++)
							{
							}
							sign=0;
						}
					}
					try {
		                Thread.sleep(5000);
		            } catch (Exception e) {
		                e.printStackTrace();
		            }
				}
			}catch (Exception e) {
                e.printStackTrace();
            }
		}
	}
	class TradeThread implements Runnable {
		private StockMDServer stockServer;
		private String id;
	    public TradeThread() {}
	    
	    public TradeThread(String id)
	    {
	    	this.id=id;
	    }
	 
	    public TradeThread(StockMDServer stockServer,String id) {
	        this.stockServer = stockServer;
	        this.id=id;
	    }
	    public void setStockMDServer(StockMDServer stockServer)
	    {
	    	this.stockServer = stockServer;
	    }
	 
	    public void run() {
	        while(true)
	        {
	        	if(Exchange.this.IsTradingTime(Calendar.getInstance().getTimeInMillis()))
	        	{
	        		//System.out.println("start"+i+++":"+stockServer.getUpdateFrequency());
		        	//System.out.println(id+":"+stockServer.toJsonString());
		        	stockServer.ConsecutiveTrading();
		            try {
						Exchange.this.sendStockData(stockServer.toJsonString());
					} catch (TException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
		        	System.out.println(id+":"+stockServer.toJsonString());
	        	}
	        	try {
	                Thread.sleep(stockServer.getUpdateFrequency()*1000);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Exchange ex=new Exchange();
		ex.Init();
		ex.ConsecutiveTrading();
	}

	@Override
	public void sendStockData(String length) throws TException {
		// TODO Auto-generated method stub
		TTransport transport = new TFramedTransport(new TSocket("localhost", 9799));
        try {
            transport.open();
        } catch (TTransportException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        TProtocol protocol = new TBinaryProtocol(transport);
              
        SendStockDataTool.Client client = new SendStockDataTool.Client(protocol);
        try {
            //client.store(new UserProfile(1, "", ""));
            //System.out.println(client.retrieve(1));
        	client.send_sendStockData(length);
        	System.out.println(length);
        } catch (TException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
}
