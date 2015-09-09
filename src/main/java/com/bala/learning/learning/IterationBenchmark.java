package com.bala.learning.learning;

import java.util.ArrayList;
import java.util.List;

public class IterationBenchmark {

	public static void main(String args[]){
		List<String> persons = new ArrayList<String>();
		persons.add("AAA");
		persons.add("BBB");
		persons.add("CCC");
		persons.add("DDD");
		long timeMillis = System.currentTimeMillis();
		for(String person : persons)
			System.out.println(person);
		System.out.println("Time taken for legacy for loop : "+(System.currentTimeMillis() - timeMillis));
		timeMillis = System.currentTimeMillis();
		persons.stream().forEach(System.out::println);
		System.out.println("Time taken for sequence stream : "+(System.currentTimeMillis() - timeMillis));
		timeMillis = System.currentTimeMillis();
		persons.parallelStream().forEach(System.out::println);
		System.out.println("Time taken for parellel stream : "+(System.currentTimeMillis() - timeMillis));
		
	}
}
