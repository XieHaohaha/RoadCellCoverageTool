package com.mastercom.rcc.util;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.Iterator;

//import mastercom.cn.bigdata.util.LOGHelper;
//import mastercom.cn.bigdata.util.IWriteLogCallBack.LogType;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * 写到文件系统
 * @author Kwong
 *
 */
public class FileWriter {
	
	private static final String LINE_SEPERATOR = System.getProperty("line.separator", "\n");

	public interface LineGetter extends Iterator<String>{
		
	}
	
	public static boolean writeToFile(String filePath, LineGetter lineGetter) throws Exception
	{
		return writeToFile(new Configuration(), filePath, true, lineGetter);
	}
	
	public static boolean writeToFile(Configuration conf, String filePath, LineGetter lineGetter) throws Exception
	{
		return writeToFile(conf, filePath, true, lineGetter);
	}
	
	public static boolean writeToFile(String filePath, boolean overwrite, LineGetter lineGetter) throws Exception
	{
		return writeToFile(new Configuration(), filePath, overwrite, lineGetter);
	}
	
	public static boolean writeToFile(Configuration conf, String filePath, boolean overwrite, LineGetter lineGetter) throws Exception
	{
		FileSystem fs = FileSystem.get(conf);

		Path file = new Path(filePath);
		
		String strData = null;
		
		try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fs.create(file, overwrite), "utf-8"))){
			
			while(lineGetter.hasNext()){
				
				strData = lineGetter.next();
				
				writer.write(strData);
				
				writer.write(LINE_SEPERATOR);
				
			}
			
		}catch (Exception e)
		{
//			LOGHelper.GetLogger().writeLog(LogType.error, "doWriteFiles error : " + strData, e);
			throw e;
		}
		return true;
		
	}
	
}
