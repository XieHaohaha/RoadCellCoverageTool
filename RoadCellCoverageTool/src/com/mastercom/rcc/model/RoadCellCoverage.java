package com.mastercom.rcc.model;

public class RoadCellCoverage {
	
	public final int stime;
	
	public final int etime;
	
	public final int subId;
	
	public final int eci;
	
	public final int num = 0;
	
	public RoadCellCoverage(int stime, int etime, int subId, int eci){
		this.stime = stime;
		this.etime = etime;
		this.subId = subId;
		this.eci = eci;
	}
	
}
