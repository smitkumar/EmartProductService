package com.emart.app.hystrix.command;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.emart.database.couchbase.DataBaseManager;
import com.emart.service.util.Util;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class TimeOutCommand extends HystrixCommand<String>{

	final static private String COMMAND_GROUP="timeout"; 
	
	LatencyResource latentResource=new LatencyResource(5000);
	DataBaseManager resource;
	String productId;
	
	public TimeOutCommand(int timeout,DataBaseManager resource,String productId) {
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(COMMAND_GROUP))
				.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
						.withExecutionIsolationThreadTimeoutInMilliseconds((timeout))));
	
		this.resource = resource;
		this.productId= productId;
	}

	/*
	 *  this falback method will execute if process take more than 7 second to complete.
	 *  timeout set for the thread is 7000 miliseconds.
	 *  this falback method can be utilize to implement retry mechanism .
	 * */
	
	@Override
	protected String getFallback() {
		// TODO Auto-generated method stub
		System.out.println("executing fallback method");
		return "service unavailable";
		//return super.getFallback();
	}
	
	/*
	 * 
	 * Actual process is executed inside the run method of hystrix command api
	 * calling couchbase database call here.
	 * for demo purpose forcefully delaying the process to 5 to 10 second.
	 * 
	 *  Timeout will be throw by Hystrix command api if thread sleep for more than or equal 7 seconds
	 *  otherwise will get proper response
	 * */
	
	@Override
	protected String run() {
		// TODO Auto-generated method stub
		JsonDocument jsonDoc=resource.read(productId);		
		int latent=Util.generateRandom();
		System.out.println(latent*1000);
		latentResource.addLatency(latent*1000);
		JsonObject obj=jsonDoc.content();
		System.out.println("inside hystrix command run method "+obj.toString());
		return obj.toString();
	}
}
