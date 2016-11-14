/*
 * MIT License
 * 
 * Copyright (c) 2016 Michael Labib
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 */

import java.util.ArrayList;

import redis.clients.jedis.Jedis;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.elasticache.AmazonElastiCacheClient;
import com.amazonaws.services.elasticache.loadbalancer.ReadReplicaBalancer;


public class ReadReplicaBalancerExample {
	
	//Hardcode keys for testing purposes only. Use Role based Security instead.
	static String awsAccessKey = "Your Access Key";
	static String awsSecretKey = "Your Secret Key";
	
	static AmazonElastiCacheClient elastiCacheClient = new AmazonElastiCacheClient(new BasicAWSCredentials(awsAccessKey, awsSecretKey));
	
    static String replicationGroupId = "Your Replication Group Id";
	static ReadReplicaBalancer readReplicaBalancer = new ReadReplicaBalancer();
	static int count = 0, maxTries = 10;

	public static void main(String[] args) {
		
		ArrayList<String> endpoints = null;
		String node = null;

		while (true) {			

			try {				
							
				  endpoints = readReplicaBalancer.getReadReplicaEndpoints(replicationGroupId, elastiCacheClient);
			      
				  if (!endpoints.isEmpty()) {			     
					
					  //example 1: returns randomly selected read replica
				      node = readReplicaBalancer.loadBalance(endpoints);									  
					  
				      //example 2: returns randomly selected read replica but excludes node passed
				      String toExclude = node;
				      node = readReplicaBalancer.loadBalance(endpoints, toExclude);
										  			  				
					  //Connect with replica node. Use any client you choose.
					  Jedis jedis = new Jedis(node, 6379);
					  jedis.set("someKey", "someValue");

					jedis.close();
				        jedis.disconnect();
				  
				  }				
				 

			} catch (Exception e) {
				
				if (++count == maxTries) {
					System.out.println("max connection attempts reached");
					e.printStackTrace();
				}
						
			}
		}
	}
}


