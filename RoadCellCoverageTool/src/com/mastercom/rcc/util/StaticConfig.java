package com.mastercom.rcc.util;

import java.io.Serializable;
import java.util.HashMap;

public class StaticConfig implements Serializable
{
	public final static String DataSlipter = "~!";
	public final static String DataSliper2 = "~!";
	public final static String DataSliper3 = "\\|";

	// data type
	public final static int DataType_SIGNAL_MR_All = 1;
	public final static int DataType_SIGNAL_XDR = 2;

	// user type
	public final static int TestType_DT = 1;
	public final static int TestType_CQT = 2;
	public final static int TestType_DT_EX = 3;
	public final static int TestType_CPE = 4;
	public final static int TestType_HiRail = 5;// 高铁类型
	public final static int TestType_OTHER = 100;
	public final static int TestType_ERROR = 101;

	// hive split
	public final static char HiveSplit = 0x01;

	// code type
	public final static String UTFCode = "UTF-8";

	// input data
	public final static String InputPath_NoData = "NODATA";

	// abnormal value define
	public final static double Double_Abnormal = -1000000;
	public final static long Long_Abnormal = -1000000;
	public final static float Float_Abnormal = -1000000;
	public final static int Int_Abnormal = -1000000;
	public final static short Short_Abnormal = -255;
	public final static short TinyInt_Abnormal = 255;
	public final static String String_Abnormal = "";
	public final static int Natural_Abnormal = -1;

	// 新添加字段表示经纬度来源
	public static final int LOCTYPE_HIGH = 1;
	public static final int LOCTYPE_MID = 2;
	public static final int LOCTYPE_LOW = 3;

	// TODO:spark版本中字段，后续需要改正
	// 新添加字段表示经纬度来源
	public static final int LOCTYPE_OTT = 1;
	public static final int LOCTYPE_GPS = 2;
	public static final int LOCTYPE_FG = 3;
	// 移动联通电信
	public static final int SOURCE_YD = 1;
	public static final int SOURCE_LT = 2;
	public static final int SOURCE_DX = 3;
	// 20170920 添加全网通
	public static final int SOURCE_YDLT = 4;
	public static final int SOURCE_YDDX = 5;

	// 新添加字段采样点运动状态
	public static final int ACTTYPE_IN = 1;
	public static final int ACTTYPE_OUT = 2;
	public static final int ACTTYPE_IN_2 = 3;
	public static final int ACTTYPE_OUT_2 = 4;

	// 接口类型
	public static final int INTERFACE_UU = 1;
	public static final int INTERFACE_X2 = 2;
	public static final int INTERFACE_UE_MR = 3;
	public static final int INTERFACE_CELL_MR = 4;
	public static final int INTERFACE_MME = 5;
	public static final int INTERFACE_S6A = 6;
	public static final int INTERFACE_S11 = 7;
	public static final int INTERFACE_S10 = 8;
	public static final int INTERFACE_SGs = 9;
	public static final int INTERFACE_S5_S8 = 10;
	public static final int INTERFACE_S1_U = 11;
	public static final int INTERFACE_GN_C = 12;
	public static final int INTERFACE_GM = 13;
	public static final int INTERFACE_MW = 14;
	public static final int INTERFACE_MG = 15;
	public static final int INTERFACE_MI = 16;
	public static final int INTERFACE_MJ = 17;
	public static final int INTERFACE_ISC = 18;
	public static final int INTERFACE_SV = 19;
	public static final int INTERFACE_CX = 20;
	public static final int INTERFACE_DX = 21;
	public static final int INTERFACE_SH = 22;
	public static final int INTERFACE_DH = 23;
	public static final int INTERFACE_ZH = 24;
	public static final int INTERFACE_GX = 25;
	public static final int INTERFACE_RX = 26;
	public static final int INTERFACE_I2 = 27;

	public static final int INTERFACE_NEW_S1U = 33;

	public static final int INTERFACE_MDT_RLFHOF=100;
	
	public static final int INTERFACE_S1U_HTTP = 1101;
	public static final int INTERFACE_S1U_FTP = 1102;

	public static final int INTERFACE_MOS_BEIJING = 1001;
	public static final int INTERFACE_WJTDH_BEIJING = 1002;
	// new String[] { "", "AM", "BM", "BT", "CF", "EEDS", "HLBE", "HHHT", "TL",
	// "WM", "XM", "XAM", "WH" };

	public static final int SUCCESS_SAMPLE = 1;
	public static final int FAILURE_SAMPLE = 2;

	public static HashMap<Integer, String> cityId_Name;

	// 处理的数据类型
	public static final int DATA_TYPE_MR = 0;// 默认
	public static final int DATA_TYPE_MDT = 1;

	public static void putCityNameByCityId()
	{
		if (cityId_Name == null)
		{
			cityId_Name = new HashMap<>();
			cityId_Name.put(1529, "AM");
			cityId_Name.put(1508, "BM");
			cityId_Name.put(1502, "BT");
			cityId_Name.put(1504, "CF");
			cityId_Name.put(1506, "EEDS");
			cityId_Name.put(1507, "HM");
			cityId_Name.put(1501, "HHHT");
			cityId_Name.put(1505, "TL");
			cityId_Name.put(1509, "WM");
			cityId_Name.put(1525, "XM");
			cityId_Name.put(1522, "XA");
			cityId_Name.put(1503, "WH");
		}
	}

	// 采样点的置信度
	public static final int OH = 1;// 室外高精度
	public static final int OM = 2;// 室外中经度
	public static final int OL = 3;// 室外低精度
	public static final int IH = 4;// 室内高精度
	public static final int IM = 5;// 室内中精度
	public static final int IL = 6;// 室内低精度
	public static final int NLoc = 7;// 没有没有经纬度

	// pushdata 成功，失败，异常
	public static final int SUCCESS = 0;
	public static final int FAILURE = -1;
	public static final int EXCEPTION = 1;

	// 上下行
	public static final int UL = 1;
	public static final int DL = 2;

	public static int successProdoceStatusValue = 0;
	
	public static final int OUTCOVER = 0;
	public static final int INCOVER = 1; 
}
