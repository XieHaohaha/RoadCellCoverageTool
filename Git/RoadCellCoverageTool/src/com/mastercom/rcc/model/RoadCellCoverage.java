package com.mastercom.rcc.model;

public class RoadCellCoverage {
	
	public final int subId;
	
	public int stime;
	
	public int etime;
	
	public final int eci;
	
	public int num = 0;
	
	public RoadCellCoverage(int subId, int stime, int etime, int eci){
		this.stime = stime;
		this.etime = etime;
		this.subId = subId;
		this.eci = eci;
	}
	
}
