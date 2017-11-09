package com.briup.util;

import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

public class LogerImpl implements Logger{
	private static String path;
	private org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger(LogerImpl.class);
//	static{
//		PropertyConfigurator.configure("src/log4j.properties");
//	}
	@Override
	public void init(Properties paramProperties) {
		path=paramProperties.getProperty("log-properties");
		PropertyConfigurator.configure(path);
	}
	

	@Override
	public void debug(String deb) {
		logger.debug(deb);
	}

	@Override
	public void info(String inf) {
		logger.info(inf);
	}

	@Override
	public void warn(String war) {
		logger.warn(war);
	}

	@Override
	public void error(String err) {
		logger.error(err);
	}

	@Override
	public void fatal(String fat) {
		logger.fatal(fat);
	}
//	public static void main(String[] args) {
//		LogerImpl log=new LogerImpl();
//		log.info("test");
//	}


}
