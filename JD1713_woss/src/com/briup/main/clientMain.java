package com.briup.main;

import java.util.Collection;

import com.briup.util.BIDR;
import com.briup.util.Configuration;
import com.briup.util.ConfigurationImpl;
import com.briup.util.LogerImpl;
import com.briup.util.Logger;
import com.briup.woss.client.Client;
import com.briup.woss.client.Gather;

public class clientMain {
	private static Logger log=new LogerImpl();
	public static void main(String[] args) {
		Configuration con=null;
		try {
			con = new ConfigurationImpl();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("客户端配置模块异常");
		}
		Client client=null;
		try {
			client = con.getClient();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("客户端对象调取异常");
		}
		Gather g = null;
		try {
			g = con.getGather();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("客户端采集对象调取异常");
		}
		Collection<BIDR> coll = null;
		try {
			coll = g.gather();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("客户端采集方法调取异常");
		}
		try {
			client.send(coll);
//			System.out.println(coll.size());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("客户端发送方法调取异常");
		}
	}
}
