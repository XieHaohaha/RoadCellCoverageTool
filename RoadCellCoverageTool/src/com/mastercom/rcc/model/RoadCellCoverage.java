package com.mastercom.rcc.model;

public class RoadCellCoverage {
	
	public int stime;
	
	public int etime;
	
	public final int subId;
	
	public final int eci;
	
	public String userIP;
	
	public int num = 0;
	
	public RoadCellCoverage(int stime, int etime, int subId, int eci){
		this.stime = stime;
		this.etime = etime;
		this.subId = subId;
		this.eci = eci;
	}
	
}
