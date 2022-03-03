package com.sprinbbatch.processer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

public class UserProcesser<I, O> implements ItemProcessor<I, O> {

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
				
		try {
			
			String fileName =System.getenv("FILE_NAME")!=null ?System.getenv("FILE_NAME") : "test.csv";
			@SuppressWarnings("resource")
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName), true));
			
			
			StringBuilder sb = new StringBuilder();
			sb.append("id");
			sb.append(',');
			sb.append("Name");
			sb.append(',');
			sb.append("Type");
			sb.append(',');
			sb.append("Password");
			sb.append('\n');
			 s3BucketFactoryBuilder.getBucket()
			 					.list(jobExecution, S3BUCKT_PATH).getObjectSummaries()
			 					.stream().filter(d -> !d.getKey().equalsIgnoreCase(S3BUCKT_PATH))
			 					.map(key -> key.getKey()).forEach(keyObject ->{
						
						S3Object read = s3BucketFactoryBuilder.getBucket().read(jobExecution, keyObject);
						
						BufferedReader reader = new BufferedReader((new InputStreamReader(read.getObjectContent())));
						
						String line =null;
						List<String> s3Data = new ArrayList<>();
						try {
							while((line=reader.readLine())!=null) {
								
								String[] split = line.replace("|", ",").split(",");

								
								for(String s :split) {
									
								      sb.append(s);
								      sb.append(",");
								     
								      
								      
									s3Data.add(line);
								}
								sb.append("\n");
								
							}
							
							writer.write(sb.toString());
							//writer.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						s3BucketFactoryBuilder.getBucket().put(jobExecution, fileName,S3BUCKT_PATH +fileName);	
						
						LOGGER.info("User data file Data reading from S3Bucket::{} ",S3BucketKey.ObjectoJson(s3Data));
					});
		} catch (Exception e) {

			LOGGER.error("UserProcesser.process()::{}", e.getMessage());
		}

		return null;

	}
	


}