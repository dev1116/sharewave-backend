package com.demo.sharewave.exceptions;

public class ResourceNotFoundException extends RuntimeException{

	public ResourceNotFoundException(String s) {
		super(s);
		// TODO Auto-generated constructor stub
	} 

	public ResourceNotFoundException() {
		super("Resource Not Found Exception");
	}
}
