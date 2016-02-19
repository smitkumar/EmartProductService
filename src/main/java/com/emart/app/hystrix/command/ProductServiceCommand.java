package com.emart.app.hystrix.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class ProductServiceCommand extends HystrixCommand<String> {

	public ProductServiceCommand() {
		 super(HystrixCommandGroupKey.Factory.asKey("ProductService"));
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected String run() {
		// TODO Auto-generated method stub
		return "Product Service handler";
	}
	
	@Override
	protected String getFallback() {
		// TODO Auto-generated method stub
		return super.getFallback();
	}
}
