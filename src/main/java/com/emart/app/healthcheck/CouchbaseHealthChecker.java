package com.emart.app.healthcheck;

import com.codahale.metrics.health.HealthCheck;
import com.couchbase.client.java.Bucket;



public class CouchbaseHealthChecker extends HealthCheck{
	
	/*private CouchbaseClientFactory factory;

    public CouchbaseHealthChecker(CouchbaseClientFactory factory) {
        this.factory = factory;
    }*/
	Bucket bucket=null;
	
	public CouchbaseHealthChecker() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected Result check() throws Exception {
		// TODO Auto-generated method stub
		StringBuilder versionString = new StringBuilder();
       /* factory.client().getVersions().entrySet().stream().forEach(version -> {
                    if (versionString.length() > 0) {
                        versionString.append("; ");
                    }
                    versionString.append("Host: ")
                            .append(((InetSocketAddress)version.getKey()).getHostString())
                            .append(", Version: ")
                            .append(version.getValue());
                }
        );
        if (versionString.length() > 0) {
            return Result.healthy(versionString.toString());
        }*/
        return Result.unhealthy("No servers connected.");
	}

}
