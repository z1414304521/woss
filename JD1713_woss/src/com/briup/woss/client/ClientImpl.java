package com.briup.woss.client;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

import com.briup.util.BIDR;
import com.briup.util.Configuration;
import com.briup.util.Logger;
import com.briup.woss.ConfigurationAWare;

public class ClientImpl implements Client,ConfigurationAWare{
	
	private Logger log;
	private String ip;
	private int prot;

	@Override
	public void init(Properties paramProperties) {
		ip=paramProperties.getProperty("ip");
		prot=Integer.parseInt(paramProperties.getProperty("client-port"));
	}

	@Override
	public void send(Collection<BIDR> bidr) throws Exception {
		//搭建客户端
		log.info("客户端已连接-----------");
		Socket socket=new Socket(ip, prot);
		log.info(prot+":客户端连接成功---------");
		
		//准备IO流，将集合发送到服务器
		OutputStream os = socket.getOutputStream();
		log.info("客户端准备发送数据---------");
		ObjectOutputStream oos=new ObjectOutputStream(os);
		
		//发送数据
		oos.writeObject(bidr);
		
		oos.close();
		socket.close();
		
	}

	@Override
	public void setConfiguration(Configuration paramConfiguration) {
		try {
			log=paramConfiguration.getLogger();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
