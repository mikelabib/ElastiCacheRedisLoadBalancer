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

package com.amazonaws.services.elasticache.loadbalancer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import com.amazonaws.services.elasticache.AmazonElastiCacheClient;
import com.amazonaws.services.elasticache.model.DescribeReplicationGroupsRequest;
import com.amazonaws.services.elasticache.model.DescribeReplicationGroupsResult;
import com.amazonaws.services.elasticache.model.NodeGroup;
import com.amazonaws.services.elasticache.model.ReplicationGroup;

public class ReadReplicaBalancer {

	public ArrayList<String> getReadReplicaEndpoints(String replicationGroupId, AmazonElastiCacheClient elastiCacheClient) throws Exception {

		ArrayList<String> replicaEndpoints = new ArrayList<String>();

		if (replicationGroupId != null) {

			try {

				DescribeReplicationGroupsRequest request = new DescribeReplicationGroupsRequest();
				request.setReplicationGroupId(replicationGroupId);
				DescribeReplicationGroupsResult result = elastiCacheClient.describeReplicationGroups(request);
				Object[] nodeMembers;

				if (result != null) {

					for (ReplicationGroup replicationGroup : result.getReplicationGroups()) {

						for (NodeGroup node : replicationGroup.getNodeGroups()) {

							nodeMembers = node.getNodeGroupMembers().toArray();

							for (int i = 0; i < nodeMembers.length; i++) {

								String nodeDescriptions = nodeMembers[i].toString();

								if (nodeDescriptions.contains("replica")) {

									int DNSEndIndex = nodeDescriptions.indexOf(".com");

									int DNSStartIndex = nodeDescriptions.indexOf("Address: ");

									replicaEndpoints.add(nodeDescriptions.substring(DNSStartIndex + 9,DNSEndIndex + 4));

								}

							}

						}

					}

				}

			} catch (Exception e) {

				System.err.println("Exception caught in getReplicationGroupEndpoints " + e);
				System.err.println("replicationGroupId: " + replicationGroupId);

			}

		}

		return replicaEndpoints;
	}

	public String loadBalance(List<String> readOnlyEndpoints) throws Exception {

		int node = 0;

		if (!readOnlyEndpoints.isEmpty()) {

			try {

				node = new Random().nextInt(readOnlyEndpoints.size());

			} catch (Exception e) {

				System.err.println("Exception caught in loadBalance " + e);
				System.err.println("Node endpoint= " + node);

			}
		}

		return readOnlyEndpoints.get(node).toString();
	}

	public String loadBalance(List<String> ROEndpoints, String endpoint) throws Exception {

		int node = 0;

		if (!ROEndpoints.isEmpty()) {

			
			List<String> toRemove = new ArrayList<String>(Arrays.asList(endpoint));
			ROEndpoints.removeAll(toRemove);
			

			try {

				node = new Random().nextInt(ROEndpoints.size());

			} catch (Exception e) {

				System.err.println("Exception caught in loadBalance " + e);
				System.err.println("Node endpoint= " + node);

			}
		}

		return ROEndpoints.get(node).toString();
	} 

}
