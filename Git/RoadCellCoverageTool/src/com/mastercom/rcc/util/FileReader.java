package com.mastercom.rcc.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

//import mastercom.cn.bigdata.util.LOGHelper;
//import mastercom.cn.bigdata.util.IWriteLogCallBack.LogType;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

/**
 * 文件讀取
 * @author Kwong
 */
public class FileReader {

	private static FileStatus[] listFileStatus(FileSystem fs, String dirpath) throws Exception{
		
		Path directory = new Path(dirpath);
		if(!fs.exists(directory)){
			throw new FileNotFoundException(dirpath);
		}
		FileStatus[] fileStatusArr = fs.listStatus(directory, new PathFilter()
		{
			@Override
			public boolean accept(Path path)
			{
				if(path.getName().endsWith(".crc"))
					return false;
				else return true;
			}
		});
		return fileStatusArr;
	}
	
	/**
	 * 讀取目錄下所有除了.crc外文件
	 * @param conf hadoopConf
	 * @param dirpath 目錄
	 * @param linehandler 處理每一行
	 * @return
	 * @throws Exception
	 */
	public static boolean readFiles(Configuration conf, String dirpath, LineHandler linehandler) throws Exception
	{
		FileSystem fs = FileSystem.get(conf);

		FileStatus[] fileStatusArr = listFileStatus(fs, dirpath);
			
		String strData = null;
		for (FileStatus fileStatus : fileStatusArr)
		{
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(fs.open(fileStatus.getPath()), "UTF-8")))
			{
				while ((strData = reader.readLine()) != null)
				{
					if (strData.trim().length() == 0)
					{
						continue;
					}

					linehandler.handle(strData);
		
				}
			}
			catch (Exception e)
			{
				throw e;
			}
		}
		
		return true;
	}
	
	public static boolean readFiles(String dirpath, LineHandler linehandler) throws Exception{
		return readFiles(new Configuration(),dirpath, linehandler);
	}
	
	public static boolean readFile(Configuration conf, String filePath, LineHandler linehandler) throws Exception
	{
		FileSystem fs = FileSystem.get(conf);

		Path file = new Path(filePath);
		if(!fs.exists(file)){
			throw new FileNotFoundException(filePath);
		}
		String strData = null;
		
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(fs.open(file), "UTF-8")))
		{
			while ((strData = reader.readLine()) != null)
			{
				if (strData.trim().length() == 0)
				{
					continue;
				}

				linehandler.handle(strData);
	
			}
		}
		catch (Exception e)
		{
			throw e;
		}
		return true;
	}
	
	public static boolean readFile(String filePath, LineHandler linehandler) throws Exception
	{
		return readFile(new Configuration(), filePath, linehandler);
	}
	
	public interface LineHandler{
		
		void handle(String line);
	}
}
