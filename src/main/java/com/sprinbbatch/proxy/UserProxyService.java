package com.sprinbbatch.proxy;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.sprinbbatch.proxy.dto.UserDto;

@Component
public class UserProxyService {
	
	private String URL ="http://localhost:1998/users";
	
	
	
	public List<UserDto> getUsers(){
		
		RestTemplate restTemplate  = new RestTemplate();
		UserDto[] forObject = restTemplate.getForObject(URL, UserDto[].class);
		
		return Arrays.asList(forObject);
	}
	
	

}
