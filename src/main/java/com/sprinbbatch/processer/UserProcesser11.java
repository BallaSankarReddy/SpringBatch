package com.sprinbbatch.processer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.sprinbbatch.common.S3Bucket;
import com.sprinbbatch.common.S3BucketFactoryBuilder;
import com.sprinbbatch.proxy.dto.UserDto;
import com.sprinbbatch.util.S3BucketKey;

@Component

public class UserProcesser11<I, O> implements ItemProcessor<I, O> {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(UserProcesser.class);

	@Autowired
	private S3BucketFactoryBuilder s3BucketFactoryBuilder;

	private JobExecution jobExecution;

	private String S3BUCKT_PATH = "archive/";

	@BeforeStep
	public void beforeStep(StepExecution execution) {
		jobExecution = execution.getJobExecution();

	}

	@Autowired
	private com.sprinbbatch.proxy.UserProxyService userProxyService;

	@Override
	public O process(I item) throws Exception {

		LOGGER.info("Inside UserProcesser.process()::{},{}", jobExecution.getStatus(), jobExecution.getStartTime());

		S3Bucket bucket = s3BucketFactoryBuilder.getBucket();

		LOGGER.info("S3Bucket object::{}", bucket);
		String fileName = "USER_DATA.txt";

		List<UserDto> users = userProxyService.getUsers();

		LOGGER.info("User Proxy object Response::{}", S3BucketKey.ObjectoJson(users));

		List<Object> data = new ArrayList<>();
		data.add("EMPLOYEE_ID" + "|" + "EMPLOYEE_NAME" + "| " + "EMPLOYEE_TYPE" + " |" + "EMPLOYEE_PASSWORD");

		try {

			users.stream().forEach(user -> {

				data.add(user.getId() + "|" + user.getName() + "| " + user.getType() + " |" + user.getPassword());
			});
			LOGGER.info("List of user data writing into file .....  ");

			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName), true));

			for (Object userObj : data) {

				writer.write(userObj.toString());
				writer.newLine();

			}
			writer.close();

			LOGGER.info("User data file uploading into S3Bucket .....  ");
		//File file = converteMultipartFileToFile(fileName);
			
			ListObjectsV2Result list = s3BucketFactoryBuilder.getBucket().list(jobExecution, S3BUCKT_PATH);
			
			String keyObject = list.getObjectSummaries().stream().map(key -> key.getKey()).findFirst().get();
			
			
			s3BucketFactoryBuilder.getBucket().copy(jobExecution, keyObject, S3BUCKT_PATH, fileName);
			//put(jobExecution, file.getName(), file.getName());
			
			
			
			s3BucketFactoryBuilder.getBucket().uploadFileFromLocalSytem(jobExecution, fileName, keyObject);
			

			LOGGER.info("User data file uploading into S3Bucket successfully completed .....  ");
			
			S3Object read = s3BucketFactoryBuilder.getBucket().read(jobExecution, s3BucketFactoryBuilder.getBucket().list(jobExecution, S3BUCKT_PATH).getObjectSummaries().stream()
					.filter(d -> !d.getKey().equalsIgnoreCase("archive/")).map(key -> key.getKey()).findFirst().get());
			
			BufferedReader reader = new BufferedReader((new InputStreamReader(read.getObjectContent())));
			
			String line =null;
			List<String> s3Data = new ArrayList<>();
			while((line=reader.readLine())!=null) {
				s3Data.add(line);
				
			}
				
			
			LOGGER.info("User data file after uploading into S3Bucket::{} ",S3BucketKey.ObjectoJson(s3Data));
		} catch (Exception e) {

			LOGGER.error("UserProcesser.process()::{}", e.getMessage());
		}

		return null;

	}
	


}