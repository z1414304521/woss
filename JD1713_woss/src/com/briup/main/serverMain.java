package com.briup.main;

import com.briup.util.Configuration;
import com.briup.util.ConfigurationImpl;
import com.briup.woss.server.Server;

public class serverMain {
	public static void main(String[] args) {
		//接收客户端的数据
		Configuration con = null;
		try {
			con = new ConfigurationImpl();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("配置模块调用异常");
		}
		Server server = null;
		try {
			server = con.getServer();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("服务器对象获取异常");
		}
		try {
			server.revicer();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("服务器方法调用异常");
		}
	}
}
