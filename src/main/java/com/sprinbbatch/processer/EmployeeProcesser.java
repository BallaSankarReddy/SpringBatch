package com.sprinbbatch.processer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.CollectionUtils;
import com.sprinbbatch.common.S3Bucket;
import com.sprinbbatch.common.S3BucketFactoryBuilder;
import com.sprinbbatch.proxy.dto.UserDto;


@Component
public class EmployeeProcesser<I, O> implements ItemProcessor<I, O> {

	
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(EmployeeProcesser.class);
	
	
	
	@Autowired
	private S3BucketFactoryBuilder s3BucketFactoryBuilder;
	
	private JobExecution jobExecution;
	
	
	@Autowired
	private com.sprinbbatch.proxy.UserProxyService userProxyService;
	
	
	@BeforeStep
	public void beforeStep(StepExecution execution) {
		jobExecution = execution.getJobExecution();

	}
	
	public String getFileNameFromKey(String keyPath) {
		String[] bits = keyPath.split("/");
		String fileName = bits[bits.length - 1];
		return fileName;
	}
	
	@Override
	public O process(I item) throws Exception {
		// TODO Auto-generated method stub
		
		String fileName="user.txt";
		LOGGER.info("Inside EmployeeProcesser.process()::{},{]",jobExecution.getStatus(),jobExecution.getStartTime());
		
		S3Bucket bucket = s3BucketFactoryBuilder.getBucket();
		
		LOGGER.info("S3Bucket object::{}",bucket);
		
		ListObjectsV2Result list = bucket.list(null, "error/");

		List<S3ObjectSummary> objectSummaries = list.getObjectSummaries();
	 objectSummaries=objectSummaries.stream().filter(key -> !key.getKey().equals("error/")).collect(Collectors.toList());
		
		LOGGER.info("S3ObjectSummary::{}",objectSummaries);
		
		List<UserDto> users = userProxyService.getUsers();
		List<Object> data = new ArrayList<>();
		data.add("EMPLOYEE_ID" +"|"+"EMPLOYEE_NAME"+"| "+"EMPLOYEE_TYPE"+" |"+"EMPLOYEE_PASSWORD");
		
		if(!org.springframework.util.CollectionUtils.isEmpty(users)) {
			System.out.println(users);
			
			
			try {
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName),true));
				
				for(UserDto user :users) {
					
					data.add(user.getId() +"|"+user.getName()+"| "+user.getType()+" |"+user.getPassword());
				}
				
				
				
for(Object d:data) {
	
	writer.write(d.toString());
	writer.newLine();
	
}
		
writer.close();
				
				File file = new File(fileName);
				bucket.copy(jobExecution, "test/", "test/", file.getName());
				
			}catch (Exception e) {

			System.out.println(e);
			
			
			}finally {
				
			}
		}
		for (S3ObjectSummary s3obj : objectSummaries) {
			String fileNameFromKey = getFileNameFromKey(s3obj.getKey());
			
		
			List<String> s3Data = new ArrayList<>();
		if(null!=item) {
			
			LOGGER.info("Inside item::{}",item);
			
			 S3Object s3Object=  bucket.read(jobExecution, "error/"+fileNameFromKey);
			 try {
					
					String destFilName="";
					@SuppressWarnings("resource")
					BufferedReader reader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()));
					
					String line =null;
					
					while((line=reader.readLine())!=null) {
						s3Data.add(line);
						
					}
						
					File converteMultipartFileToFile = converteMultipartFileToFile(fileNameFromKey);
					destFilName=converteMultipartFileToFile.getName();
					bucket.copy(jobExecution, s3obj.getKey(), "test/", fileName);
					
					//deleting file in from location 
					bucket.removeOrDelete(jobExecution, s3Object.getKey());
					
				}catch (Exception e) {
					// TODO: handle exception
					
					LOGGER.error("Error...{}",e.getMessage());
				}
			 
			 LOGGER.info("JOB STATUS ::{}",jobExecution.getStatus());

		}
		}
		return null;
	}
	
	
private File converteMultipartFileToFile(String file) {
		
		File convertedFile = new File(file);
		
		try (FileOutputStream fos= new FileOutputStream(convertedFile)){
			fos.write(file.getBytes());
			
		}catch (Exception e) {
			
			LOGGER.error("Error converted MultipartFile To File:{} ",e);
		}
		return convertedFile;
	}
	

}
