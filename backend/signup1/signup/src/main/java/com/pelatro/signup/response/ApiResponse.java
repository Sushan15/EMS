package com.pelatro.signup.response;



public class ApiResponse<T> {
    private T data;
    private String message;
    private int statusCode;
    private String status;
	private T body;

    // Constructor
    public ApiResponse(T data, String message, int statusCode) {
        this.data = data;
        this.message = message;
        this.statusCode = statusCode;
    }
    public ApiResponse( String status, T body ) {
		super();
		this.status = status;
		this.body = body;
	}

    // Getters and Setters
    
    public T getData() {
        return data;
    }

    public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public T getBody() {
		return body;
	}
	public void setBody(T body) {
		this.body = body;
	}
	public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
