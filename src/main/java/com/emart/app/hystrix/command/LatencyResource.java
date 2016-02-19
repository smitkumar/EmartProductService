package com.emart.app.hystrix.command;

public class LatencyResource {
	
	final  private long latency;
	
	public LatencyResource(long latency){
		
		this.latency = ((latency) < 0L ? 0L : latency);
	}
	
	public String getData(){
		//addLatency();		
		return "Returning some success data";
	}
	
	public void addLatency(int latency){
		
		try{
			Thread.sleep(latency);
		}catch(InterruptedException e){
			e.printStackTrace();
			
		}
	}

}
