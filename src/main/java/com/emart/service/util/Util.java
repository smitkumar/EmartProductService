package com.emart.service.util;

import java.util.Random;

public class Util {

	
	
	public static int generateRandom(){
		
		Random r = new Random();
		int Low = 5;
		int High = 10;
		int Result = r.nextInt(High-Low) + Low;
		
		return Result;
	}
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int num=generateRandom();
		System.out.println(num);
	}

}
