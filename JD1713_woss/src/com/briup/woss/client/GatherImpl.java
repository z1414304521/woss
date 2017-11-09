package com.briup.woss.client;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.briup.util.BIDR;
import com.briup.util.BackUP;
import com.briup.util.BackUpImpl;
import com.briup.util.Configuration;
import com.briup.util.Logger;
import com.briup.woss.ConfigurationAWare;

/**
 * 采集模块的具体实现
 *   gather方法从本地文件中提取用户登录信息
 *   封装成BIDR对象放入集合，返回
 */

import com.briup.woss.client.Gather;

public class GatherImpl implements Gather,ConfigurationAWare{
	private String file;
	private BufferedReader br;
	private BackUP bui;
 
	private Logger log;
	@Override
	public void init(Properties paramProperties) {
		file=(String) paramProperties.get("src-file");
	}

	@Override
	public Collection<BIDR> gather(){
		Map<String, BIDR> map1=new HashMap<String, BIDR>();
		
		// 先导入不完整数据到map1里面
//		Object obj = bui.load("Loging_name.bak", true);
//		Map<String ,BIDR> m=(Map<String,BIDR>) obj;
//		map1.putAll(m);
		Collection<BIDR> list=new ArrayList<>();
		try {
		//准备一个IO流
		//存放不完整的登录对象信息
			br=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		//读取一行数据
		log.info("数据开始采集----------");
		String str="";
//		System.out.println(str);
		//拆分字符块
			while((str=br.readLine())!=null){
				String[] art = str.split("[|]");	
//				System.out.print(art[i]+" ");
				//将核实信息填入BIDR对象
				if(art[2].equals("7")){
					BIDR bidr=new BIDR();
					bidr.setAAA_login_name(art[0]);
					bidr.setLogin_ip(art[4]);
					Long date=Long.parseLong(art[3])*1000;
					bidr.setLogin_date(new Timestamp(date));
					map1.put(art[4].trim(), bidr);
					
				}else if(art[2].equals("8")){
					BIDR bidr=map1.remove(art[4]);
					//获取下线信息，清除map1的对象
					Long date=Long.parseLong(art[3])*1000;
					bidr.setLogout_date(new Timestamp(date));
					//计算在线时间
					bidr.setTime_deration((int) ((bidr.getLogin_date().getTime()-bidr.getLogout_date().getTime())/6000));
					//一次登陆操作执行完毕，将对象存储到list集合
					list.add(bidr);
				}
				//保留只有上线的用户
			log.info("采集完成"+list.size());
			}
			log.info("采集完成，准备发送。");
			bui.store("Loging_name.bak", map1, BackUpImpl.STORE_OVERRIDE);
			log.warn("未采集数据已备份。");
		} catch (Exception e) {
			e.getMessage();
		} finally{	
			try {
				if(br!=null)br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
//		System.out.println("采集完成3"+list.size());
		return list;
	}

	@Override
	public void setConfiguration(Configuration paramConfiguration) {
		try {
			bui=paramConfiguration.getBackup();
			log=paramConfiguration.getLogger();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
