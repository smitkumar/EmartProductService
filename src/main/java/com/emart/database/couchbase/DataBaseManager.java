package com.emart.database.couchbase;



import java.util.Collection;
import java.util.List;

import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.view.ViewQuery;
import com.couchbase.client.java.view.ViewResult;
import com.emart.app.main.CauchbaseConfiguration;

import rx.Observable;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;

public class DataBaseManager {
	
	private static final Logger log = Logger.getLogger( DataBaseManager.class.getName());
	
	  private final CauchbaseConfiguration config;

	    private final Bucket bucket;
	    private final Cluster cluster;

	    public DataBaseManager(final CauchbaseConfiguration config) {
	        this.config = config;

	    	System.out.println("inside DataBaseManager constructor  :: "+config);
	    	log.info("*************************Database contructor********************************");
	        //connect to the cluster and open the configured bucket
	        this.cluster = CouchbaseCluster.create(config.getCouchbaseNodes());
	        this.bucket = cluster.openBucket(config.getCouchbaseBucket(), config.getCouchbasePassword());
	       // this.bucket = cluster.openBucket("Product-Info", config.getCouchbasePassword());
	        System.out.println("Bucket name  :: "+this.bucket.name());
	    }

	    @PreDestroy
	    public void preDestroy() {
	        if (this.cluster != null) {
	            this.cluster.disconnect();
	        }
	    }

	    /**
	     * Prepare a new JsonDocument with some JSON content
	     */
	    public static JsonDocument createDocument(String id, JsonObject content) {
	        System.out.println("id" +id+" content "+content);
	    	return JsonDocument.create(id, content);
	    }

	    /**
	     * CREATE the document in database
	     * @return the created document, with up to date metadata
	     */
	    public JsonDocument create(JsonDocument doc) {
	        return bucket.insert(doc);
	    }

	    /**
	     * CREATE the document in database asynchronously
	     * @return the created document, with up to date metadata
	     */
	    public Observable<JsonDocument> createAsync(JsonDocument doc) {
	    	System.out.println("JsonDocument doc  :::::::: " +doc);
	        return bucket.async().insert(doc);
	    }
	    
	    /**
	     * READ the document from database
	     */
	    public JsonDocument read(String id) {
	        return bucket.get(id);
	    }

	    /**
	     * READ the document from database
	     */
	    public Observable<JsonDocument> readAsync(String id) {	    	
	        return bucket.async().get(id);
	    }
	    
	    /**
	     * UPDATE the document in cauchbase database
	     * @return the updated document, with up to date metadata
	     */
	    public JsonDocument update(JsonDocument doc) {
	        return bucket.replace(doc);
	    }

	    /**
	     * DELETE the document from database
	     * @return the deleted document, with only metadata (since content has been deleted)
	     */
	    public JsonDocument delete(String id) {
	        return bucket.remove(id);
	    }
	    
	    /**
	     * 
	     * @param productType
	     * @param offset
	     * @param limit
	     * @return
	     */
	    public ViewResult findAllByCriteria(String designName, String viewName, String productType, Integer offset, Integer limit) {
	        //ViewQuery query = ViewQuery.from("dev_product", "ProductByType").key(productType);
	    	ViewQuery query = ViewQuery.from(designName, viewName).key(productType);
	        if (limit != null && limit > 0) {
	            query.limit(limit);
	        }
	        if (offset != null && offset > 0) {
	            query.skip(offset);
	        }
	        ViewResult result = bucket.query(query);
	        return result;
	    }
	   
	    /**
	     * 
	     * @param ids
	     * @return
	     */
	   public List<JsonDocument> batchGet(final Collection<String> ids) {
	        return Observable
	            .from(ids)
	            .flatMap(new Func1<String, Observable<JsonDocument>>() {
	                @Override
	                public Observable<JsonDocument> call(String id) {
	                    return readAsync(id);
	                }
	            })
	            .toList()
	            .toBlocking()
	            .single();
	    }

}
