# ElastiCacheRedisLoadBalancer

## Summary

Version: 0.0.1
To be used in a non-sharded redis version.

This simple library provides a list of available read replica's for a given ElastiCache ReplicationGroupId and provides options to load balance against them by randomly selecting a node that you can connect to.

static ReadReplicaBalancer readReplicaBalancer = new ReadReplicaBalancer();

<b><i>Example: ArrayList<String> endpoints = readReplicaBalancer.getReadReplicaEndpoints(replicationGroupId, elastiCacheClient);</b></i>

Option 1: Randomly select a node from the available replica's

<b><i>Example 1: String node = readReplicaBalancer.loadBalance(endpoints); </b></i>

Option 2: Randomly select a node from the available replica's excluding a node

<b><i>Example 2: String node = readReplicaBalancer.loadBalance(endpoints, NodetoExclude);</b></i>

Demo code shows how it can be used with Jedis but any Redis Java Library will work. 

The code loops through the library so you can test against it.

<b><i>Jedis jedis = new Jedis(node, 6379);</b></i>

