package com.briup.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;

import com.briup.woss.ConfigurationAWare;

public class BackUpImpl implements BackUP,ConfigurationAWare{
	private String path;
	
	private Logger log;
	@Override
	public void init(Properties paramProperties) {
		path=paramProperties.getProperty("back-temp");
	}
	//第一个参数文件的名字
		//备份数据，第二个是备份的数据
		//第三个参数是追加还是覆盖数据
	@Override
	public void store(String key, Object obj, boolean flag) throws Exception {
		ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(new File(path,key)));
		oos.writeObject(obj);
		log.info("备份完成！");
		oos.flush();
		if(oos!=null)oos.close();
	}
	//读取文件的数据，第一个参数是文件名
		//第二个参数为true，删除该文件
		//返回值就是读到的对象
	@Override
	public Object load(String key, boolean flag) throws Exception {
		File file=new File(path, key);
		if(!file.exists()){
			return null;
		}
		ObjectInputStream ois=new ObjectInputStream(new FileInputStream(file));
		Object obj=ois.readObject();
		ois.close();
		if(flag){
			file.deleteOnExit();
		}
		return obj;
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
