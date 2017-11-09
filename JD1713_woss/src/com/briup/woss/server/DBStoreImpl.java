package com.briup.woss.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import com.briup.util.BIDR;
import com.briup.util.BackUP;
import com.briup.util.Configuration;
import com.briup.util.Logger;
import com.briup.woss.ConfigurationAWare;

public class DBStoreImpl implements DBStore,ConfigurationAWare{
	private static String Driver;
	private static String URL;
	private static String UserName;
	private static String PassWd;
	
	private int batch_size;
	
	private Logger log;
	private BackUP bus;
	@Override
	public void init(Properties paramProperties) {
		Driver=paramProperties.getProperty("driver");
		URL=paramProperties.getProperty("url");
		UserName=paramProperties.getProperty("userName");
		PassWd=paramProperties.getProperty("passWord");
		batch_size=Integer.parseInt(paramProperties.getProperty("batch-size"));
	}

	@Override
	public void saveToDB(Collection<BIDR> coll){
		Connection conn=null;
		try {
			Class.forName(Driver);
			conn=DriverManager.getConnection(URL, UserName, PassWd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Collection<BIDR> s_list=new ArrayList<>();
		PreparedStatement ps=null;
		log.info("入库开始");
		int i=1;
		int date=0;
		if(s_list!=null){
			try{
				conn.setAutoCommit(false);
				for (BIDR bidr : coll) { 
					i++;
					int day=bidr.getLogin_date().getDate();
					if(date!=day){
						if(ps!=null){
							//处理前一个PS对象
							ps.executeBatch();
							ps.close();
						}
						//准备SQL语句
						String sql="insert into T_DETAIL_"+day+" values(?,?,?,?,?,?)";
						ps=conn.prepareStatement(sql);
					}
					ps.setString(1,bidr.getAAA_login_name());
					ps.setString(2,bidr.getLogin_ip());
					ps.setDate(3, new java.sql.Date(bidr.getLogin_date().getTime()));
					ps.setDate(4, new java.sql.Date(bidr.getLogout_date().getTime()));
					ps.setString(5, bidr.getNAS_ip());
					ps.setInt(6, (int)((bidr.getLogout_date().getTime()-bidr.getLogin_date().getTime())/6000));
					log.info("已入库"+i+"条数据！");
					//设置批处理长度
					ps.addBatch();
					if(i%batch_size==0){
						ps.executeBatch();
					}
				}
				//处理最后一批
				if(ps!=null){
					ps.executeBatch();
					conn.commit();
					log.info("入库完成！");
				}
			}catch(Exception e){
				try{
					//添加异常备份模块

					bus.store("DBStore.bak", coll, false);
					conn.rollback();
					log.debug("数据入库失败，备份成功，已回滚到更改前状态。");
					i=0;
				}catch(Exception e1){
					e.printStackTrace();
				}
			}finally {
				try{
					//关闭
					if(ps!=null)ps.close();
					if(conn!=null)conn.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void setConfiguration(Configuration paramConfiguration) {
		try {
			log=paramConfiguration.getLogger();
			bus=paramConfiguration.getBackup();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}