package com.sprinbbatch.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class S3BucketKey {

	
	public static String getFileNameFromKey(String keyPath) {
		String[] bits = keyPath.split("/");
		String fileName = bits[bits.length - 1];
		return fileName;
	}
	
	
	public static String ObjectoJson(Object t) {
		
		try {
			String mapper = new ObjectMapper().writeValueAsString(t);
			return mapper;
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
