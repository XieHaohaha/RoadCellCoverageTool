package com.mastercom.rcc.model;

import com.mastercom.rcc.util.DataGeter;

public class LocationItem {
	public long imsi;
	public int itime;
	public int itimeMS;
	public int locTime;
	public int locTimeMS;
	public int eci;
	public String userIP = "";
	public int port;
	public String serverIP = "";
	public int location;
	public String loctp = "";
	public double radius;
	public double longitude;
	public double latitude;
	public String wifiName = "";
	public String msisdn;

	public long tmTime = 0;

	public boolean fillData(String[] vals, int startPos) {
		int i = startPos;

		try {
			imsi = DataGeter.GetLong(vals[i++], 0);//i==0
			tmTime = DataGeter.GetLong(vals[i++], 0);//i==1
			if ((tmTime + "").length() == 13) {
				itime = (int) (tmTime / 1000L);
				itimeMS = (int) (tmTime % 1000L);
			} else {
				itime = (int) tmTime;
				itimeMS = 0;
			}

			tmTime = DataGeter.GetLong(vals[i++], 0);//i==2
			if ((tmTime + "").length() == 13) {
				locTime = (int) (tmTime / 1000L);
				locTimeMS = (int) (tmTime % 1000L);
			} else {
				locTime = (int) tmTime;
				locTimeMS = 0;
			}
		} catch (Exception e) {

		} finally {
			// TODO: handle finally clause
		}

		eci = DataGeter.GetInt(vals[i++]);//i==3
		userIP = vals[i++];//i==4
		port = DataGeter.GetInt(vals[i++], 0);//i==5
		serverIP = vals[i++];//i==6
		location = DataGeter.GetInt(vals[i++], 0);//i==7
		loctp = vals[i++];//i==8
		radius = DataGeter.GetDouble(vals[i++], -1);//i==9
		longitude = DataGeter.GetDouble(vals[i++], 0);//i==10
		latitude = DataGeter.GetDouble(vals[i++], 0);//i==11
		i++;// location bak
		if (i <= vals.length - 1) {
			wifiName = vals[i++];
		}

		// 格式化数据
		if (loctp.equals("lll")) {
			loctp = "ll";
		}

		if (itime == 0) {
			itime = locTime;
		}

		return true;
	}

}
