package com.emart.service.resources;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;


import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.container.TimeoutHandler;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.error.DocumentAlreadyExistsException;
import com.couchbase.client.java.error.DocumentDoesNotExistException;
import com.couchbase.client.java.view.ViewResult;
import com.emart.app.main.CauchbaseConfiguration;
import com.emart.database.couchbase.DataBaseManager;
import com.emart.service.model.ProductInfo;
import com.emart.service.util.Constant;
import com.emart.service.util.Util;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;



@Path("/product")
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {
	
	private static final Logger log=Logger.getLogger(ProductResource.class.getName());
	
    
	private DataBaseManager service =null; 
	
	public ProductResource(){
		   
	}
	public ProductResource(CauchbaseConfiguration config){
		
		   service = new DataBaseManager(config);
		   System.out.println("service object initilized :"+service);
		   log.info("service object initilized :"+service);
	}
	
	/*@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response saveProduct(@FormParam("product_id") String product_id,
			@FormParam("product_price") String product_price,
			@FormParam("product_name") boolean product_name,
			@FormParam("product_quantity") String product_quantity) {
		return Response.created(null).build();
	}	
	*/
	
	/*
	 * This is the Rest Interface to stored data in the couchbase database.
	 * */
	
	@POST
	@Path("/save")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createAsyncProduct(ProductInfo product){
		 String prod_id = "";
	        try {
	        	System.out.println("product.getId()  ::" +product.getId());
	        	prod_id = Constant.CATALOG_PROD_ID + product.getId();
	        	product.setId(prod_id);
	        	System.out.println("product.getId()  setting ::" +product.getId());
	        	JsonObject productJson = parseObject(product);
	        	 System.out.println("service1 ::::::::::: :"+service);
	            JsonDocument doc = service.createDocument(prod_id, productJson);
	            System.out.println("doc :"+doc);
	            System.out.println("service2 ::::::::::: :"+service);
	            Observable<JsonDocument> returnObservable = service.createAsync(doc);
	            /*returnObservable.forEach(jsonDoc->{
	            		
	            });*/
	            
	            //Response.ok().status(Status.CREATED).build();
	            return Response.created(null).status(Status.CREATED).build();
	        } catch (IllegalArgumentException e) {
	            return Response.status(Status.BAD_REQUEST).build();
	        } catch (DocumentAlreadyExistsException e) {
	        	 return Response.status(Status.CONFLICT).build();
	        } catch (Exception e) {
	        	e.printStackTrace();
	        	return Response.status(Status.INTERNAL_SERVER_ERROR).build();
	        }
		
	}

	/*
	 * This Rest Interface is to fetch product based on ID from  couchbase database.
	 * */
	
	
	@GET
	@Path("/{id}")
	public Response fetchProductById(@PathParam("id") String productId,@Suspended final AsyncResponse asyncResponse) {
		// retrieve information about the reward with the provided id
		 JsonDocument doc=null;
		System.out.println("productId"+productId);
		log.info("productId"+productId);
		 asyncResponse.setTimeoutHandler(new TimeoutHandler() {
			  
	         @Override
	         public void handleTimeout(AsyncResponse asyncResponse) {
	             asyncResponse.resume(Response.status(Response.Status.SERVICE_UNAVAILABLE)
	                     .entity("Service is taking long time to responde !!! Please retry again after some time.").build());
	         }
	     });
	     asyncResponse.setTimeout(7, TimeUnit.SECONDS);  // time out after waiting for response till 7 second.
	     
	     new Thread(new Runnable() {
	  
	         @Override
	         public void run() {
	        	 JsonDocument result = veryExpensiveOperation();
	        	 System.out.println(result);
	        	 JsonObject jsonValue = result.content();
	             asyncResponse.resume(jsonValue.toString());
	            
	         }
	  
	         private JsonDocument veryExpensiveOperation() {
	             /* very expensive operation that typically finishes within 5 to 10 seconds
	              * If service take less than or equal to 6 second then resturn success otherwise service unavailable response to retry.
	              * 
	              * Ideally if service take more than 5 second to respond than app should
	              *  fall back to same service running on different node and return the proper response to the user.
	        	 */
	        	 JsonDocument doc=null;
	        	 try{	        		
	        		System.out.println("Inside the fetchproductById");
	        		 doc = service.read(productId);	  
	        		 int time=Util.generateRandom();
	        		 System.out.println(time*1000);
	        		 Thread.sleep(time*1000);	
	        	}catch(InterruptedException e){
	        		
	        		e.printStackTrace();
	        	} 
	        	 
	        	return doc;
	         }
	     }).start();
		
		
		/*JsonDocument doc=null;
		try{
			System.out.println("Inside the fetchproductById");
		 doc = service.read(productId);
			Thread.sleep(5000);
		}catch(Exception e){
			e.printStackTrace();
		}	*/	
		
		if (doc != null) {
	            return Response.ok(doc).build();
	    } else {
	            return Response.status(Status.NOT_FOUND).build();
	    }
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateProductById(@PathParam("id") String productId,ProductInfo product) {
		try {
			JsonObject productJson = parseObject(product);
			service.update(DataBaseManager.createDocument(productId, productJson));
			return Response.ok(productId).build();
           
        } catch (IllegalArgumentException e) {
        	return Response.status(Status.BAD_REQUEST).build();
        } catch (DocumentDoesNotExistException e) {
        	return Response.status(Status.NOT_FOUND).build();
        } catch (Exception e) {
        	return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
		
	}

	@DELETE
	@Path("/{id}")
	public Response deleteProdcutById(@PathParam("id") String productId) {
		String message="";
		JsonDocument deleted = service.delete(productId);
		if(deleted != null){
			message="successfully deleted product with id "+productId;
		}else{
			message="product deletion of product with id "+productId+" failed ";
		}
        return Response.status(Status.OK).entity(message).build();
	}
	
	@Path("productType/{type}")
	@GET
	public Response getProductsByType(@QueryParam("type") String productType) {
		ViewResult result = service.findAllByCriteria("dev_product","ProductByType",productType,0,10);
		if (!result.success()) {
            return  Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } else {
            JsonArray keys = JsonArray.create();
            result.allRows().stream().forEach(row->{
            	keys.add(row.document().content());
            });           
        	
            if (keys.isEmpty()) {
	            return Response.ok(Status.NOT_FOUND).build();
            }
            else{
            	return Response.ok(keys.toString()).build();
            }
        }
	}
	
	
	
	@GET
	@Path("/batch/{ids}")
	public Response getBatchProductsById(@PathParam("ids") PathSegment prodcutSeg) {
		
		
		MultivaluedMap<String, String> map = prodcutSeg.getMatrixParameters();
		log.info(map.size());
		List<String> productIDList = new ArrayList<String>();
		map.values().stream().forEach(valueList->productIDList.add(valueList.get(0)));		
		List<JsonDocument> docList = service.batchGet(productIDList);
		JsonArray keys = JsonArray.create();
		docList.forEach(jsonDoc->keys.add(jsonDoc.content()));
		System.out.println(docList.toString());
		if (docList != null && !docList.isEmpty()) {
	            return Response.ok(keys.toString()).build();
	    } else {
	            return Response.status(Status.NOT_FOUND).build();
	    }
	}
	
	
	
	
	/*@GET
	@Path("/batch/{ids}")
	public Response getBatchProductsById(@PathParam("ids") PathSegment prodcutSeg) {
		
		System.out.println(prodcutSeg);
		MultivaluedMap<String, String> map = prodcutSeg.getMatrixParameters();
		System.out.println(map.size());
		List<String> productIDList = new ArrayList<String>();
		map.values().stream().forEach(valueList->productIDList.add(valueList.get(0)));
		System.out.println("----------"+productIDList);
		List<JsonDocument> docList = service.batchGet(productIDList);
		JsonArray keys = JsonArray.create();
		docList.forEach(jsonDoc->keys.add(jsonDoc.content()));
		System.out.println(docList.toString());
		if (docList != null && !docList.isEmpty()) {
	            return Response.ok(keys.toString()).build();
	    } else {
	            return Response.status(Status.NOT_FOUND).build();
	    }
	}
	*/
	 private JsonObject parseObject(Map<String, Object> dataMap) {
	        JsonObject dataJsonObj = JsonObject.create();
	        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
	        	dataJsonObj.put(entry.getKey(), entry.getValue());
	        }
	        return dataJsonObj;
	 }
	 
	  
	 private JsonObject parseObject(ProductInfo product) {
	        JsonObject dataJsonObj = JsonObject.create();
	        Field fields[] =product.getClass().getDeclaredFields();
	        Arrays.asList(fields).stream().forEach(filed->{
	        	String getMethodName = "get"+ Character.toUpperCase(filed.getName().charAt(0)) + filed.getName().substring(1);
	        	Object val = null;
				try {
					val = product.getClass().getMethod(getMethodName).invoke(product);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	dataJsonObj.put(filed.getName(), val);
	        	System.out.println("json formatter :"+dataJsonObj.toString());
	        	}
	        );
	        
	        return dataJsonObj;
	  }
	 
	 
	 @Path("/test/timeout")
	 @GET
	 public void asyncGetWithTimeout(@Suspended final AsyncResponse asyncResponse) {
	     asyncResponse.setTimeoutHandler(new TimeoutHandler() {
	  
	         @Override
	         public void handleTimeout(AsyncResponse asyncResponse) {
	             asyncResponse.resume(Response.status(Response.Status.SERVICE_UNAVAILABLE)
	                     .entity("Operation time out.").build());
	         }
	     });
	     asyncResponse.setTimeout(30, TimeUnit.SECONDS);
	  
	     new Thread(new Runnable() {
	  
	         @Override
	         public void run() {
	             String result = veryExpensiveOperation();
	             asyncResponse.resume(result);
	         }
	  
	         private String veryExpensiveOperation() {
	             // ... very expensive operation that typically finishes within 30 seconds
	        	try{
	        		Thread.sleep(30000);	
	        	}catch(InterruptedException e){
	        		
	        		e.printStackTrace();
	        	} 
	        	 
	        	return "success";
	         }
	     }).start();
	 }
	
	
	/*
	 * 
	 * 
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response updateReward(@PathParam("id") int rewardId,
			@FormParam("points_to_redeem") String points,
			@FormParam("image_url") String url,
			@FormParam("redeemable") boolean redeemable,
			@FormParam("name") String name) {
		return Response.ok("{\"message\"	:\"done\"}").build();
	}

	@DELETE
	@Path("/{id}")
	public Response deleteReward(@PathParam("id") int rewardId) {
		return Response.noContent().build();
	}*/
}
