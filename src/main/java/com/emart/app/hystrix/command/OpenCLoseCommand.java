package com.emart.app.hystrix.command;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.emart.database.couchbase.DataBaseManager;
import com.emart.service.util.Util;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class OpenCLoseCommand extends HystrixCommand<String>{

	public static final String COMMAND_GROUP="open_close";
	
	LatencyResource latentResource=new LatencyResource(5000);
	DataBaseManager resource;
	String productId;
	
	public OpenCLoseCommand(DataBaseManager resource,String type,boolean open) {
		// TODO Auto-generated constructor stub
		super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(COMMAND_GROUP))
				.andCommandKey(HystrixCommandKey.Factory.asKey(type)).andCommandPropertiesDefaults(
						HystrixCommandProperties.Setter().withCircuitBreakerForceOpen(open)));
		
		
		this.resource = resource;
	}
	
	@Override
	protected String run() {
		// TODO Auto-generated method stub
		String result="success result";
		JsonDocument jsonDoc=resource.read(productId);		
		int latent=Util.generateRandom();
		JsonObject obj=jsonDoc.content();
		System.out.println("inside hystrix command run method "+obj.toString());
		return obj.toString();
	}
	
	@Override
	protected String getCacheKey() {
		// TODO Auto-generated method stub
		return super.getCacheKey();
	}
	
	@Override
	protected String getFallback() {
		// TODO Auto-generated method stub
		return "fallback response";
	}
	
}
