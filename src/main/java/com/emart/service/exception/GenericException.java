package com.emart.service.exception;
	
	public class GenericException extends RuntimeException{

		/**
		 * 
		 */
		private static final long serialVersionUID = -4037098934185005662L;
		private int errorCode = 1;
	    
	    public int getErrorCode() {
	        return this.errorCode;
	    }
	    
	    public GenericException() {
	        super();
	    }

	    public GenericException(int errorCode) {
	        super(String.valueOf(errorCode));
	        this.errorCode = errorCode;
	    }
	    
	    public GenericException(String msg) {
	        super(msg);
	    }

	    public GenericException(Throwable e) {
	        super(e);
	    }

	    public GenericException(String msg, Throwable e) {
	        super(msg, e);
	    }
	}


