package com.briup.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.briup.woss.ConfigurationAWare;
import com.briup.woss.WossModule;
import com.briup.woss.client.Client;
import com.briup.woss.client.Gather;
import com.briup.woss.server.DBStore;
import com.briup.woss.server.Server;
/**
 * 配置模块的实现
 *	获取到基本的配置信息
 *  通过依赖注入获取各种类的对象
 */
public class ConfigurationImpl implements Configuration{
	//解析XML文件，获取各个类的全包名
	//利用反射获取到各个类的对象
	//保存所有类的对象，放入容器
	private Map<String, WossModule> map=new HashMap<>();
	private Properties pro=new Properties();
	
	public ConfigurationImpl() throws Exception {
		this("src/conf.xml");
	}
	public ConfigurationImpl(String fileName) throws Exception {
		SAXReader reader=new SAXReader();
		//创建解析对象
		Document document = reader.read(fileName);
		//获取头结点
		Element root=document.getRootElement();
		//获取头结点下的子节点
		List<Element> list= root.elements();
		for (Element element : list) {
//			String name2 = element.getName();
//			System.out.println(name2);
			String clz=element.attributeValue("class");
			String name = element.getName();
//			System.out.println("value:"+value);
			//通过包名，利用反射获取对象
			WossModule w= (WossModule) Class.forName(clz).newInstance();
			map.put(name, w);
			
			List<Element> li=element.elements();
			for (Element ele : li) {
				String text = ele.getText();
				String str = ele.getName();
				//加入Pro
				pro.put(str, text);
			}
		}
		//开始依赖注入
		//遍历map集合
		for (String key : map.keySet()) {
			WossModule w=map.get(key);
			//配置模块辅助初始化
			if(w instanceof WossModule){
				w.init(pro);
			}
			if(w instanceof ConfigurationAWare){
				((ConfigurationAWare) w).setConfiguration(this);
			}
		}
	}
	@Override
	public Logger getLogger() throws Exception {
		return (Logger) map.get("logger");
	}


	@Override
	public BackUP getBackup() throws Exception {
		return (BackUP) map.get("backup");
	}

	@Override
	public Gather getGather() throws Exception {
		return (Gather) map.get("gather");
	}

	@Override
	public Client getClient() throws Exception {
		return (Client) map.get("client");
	}

	@Override
	public Server getServer() throws Exception {
		return (Server) map.get("server");
	}

	@Override
	public DBStore getDBStore() throws Exception {
		return (DBStore) map.get("dbstore");
	}

	public static void main(String[] args) throws Exception {
		ConfigurationImpl impl = new ConfigurationImpl();
//		((ConfigurationImpl) new ConfigurationImpl().map).getLogger();
		System.out.println(impl.getLogger());
		System.out.println(impl.getBackup());
		System.out.println(impl.getClient());
		System.out.println(impl.getDBStore());
		System.out.println(impl.getGather());
		System.out.println(impl.getServer());
	}
}
