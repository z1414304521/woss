package com.briup.woss.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import com.briup.util.BIDR;
import com.briup.util.Configuration;
import com.briup.util.Logger;
import com.briup.woss.ConfigurationAWare;

public class ServerImpl implements Server,ConfigurationAWare{
	
	private Logger log;
	private DBStore db;
	private int port;
	@Override
	public void init(Properties paramProperties) {
		port= Integer.parseInt(paramProperties.getProperty("server-port"));
		
	}

	@Override
	public Collection<BIDR> revicer() {
		Collection<BIDR> coll=new ArrayList<>();
		//搭建服务器
		log.info("服务端已启动--------");
//		System.out.println("服务端已启动--------");
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("服务器连接异常");
		}
		
		//接收客户端连接
		Socket s = null;
		try {
			s = ss.accept();
		} catch (IOException e) {
			e.printStackTrace();
			log.error("客户端数据接收异常");
		}
		
		log.info("客户端已连接---------");
		ObjectInputStream ois = null;
		//创建IO流读取数据
		try{
			ois=new ObjectInputStream(s.getInputStream());
			coll = (Collection<BIDR>) ois.readObject();
		}catch(Exception e){
			e.printStackTrace();
			log.error("数据流读取异常");
		}
		try{
			if(ois!=null)ois.close();
			s.close();
			ss.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		try {
			db.saveToDB(coll);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("数据库获取连接失败。");
		}
		return coll;
	}

	@Override
	public void shutdown() {
		
	}

	@Override
	public void setConfiguration(Configuration paramConfiguration) {
		try {
			log=paramConfiguration.getLogger();
			db=paramConfiguration.getDBStore();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
