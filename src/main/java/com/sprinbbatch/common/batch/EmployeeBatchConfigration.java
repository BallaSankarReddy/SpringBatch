package com.sprinbbatch.common.batch;

import java.util.Arrays;

import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sprinbbatch.common.JobCompletionListener;
import com.sprinbbatch.common.batch.writer.EmployeeItemWriter;
import com.sprinbbatch.processer.EmployeeProcesser;
import com.sprinbbatch.processer.UserProcesser;

@EnableBatchProcessing
@Configuration
public class EmployeeBatchConfigration<I, O> {
	
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(EmployeeBatchConfigration.class);
	
	private static String  JOB_NAME="EMPLOYEE_TEST_JOB";
	
	private static String  JOB_NAME1="USER_TEST_JOB";
	
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public Step employeeStep() {
		
		
		if(System.getenv("OMS_JOB_TYPE").equalsIgnoreCase(JOB_NAME)) {
			LOGGER.info(" inside employeeItemReader.... ");
			
			
			return stepBuilderFactory.get(JOB_NAME).<String, String>
			chunk(6).reader(employeeItemReader()).processor(employeeItemProcessor()).writer(employeeItemWriter())
			.build();
		}
		
		else if(System.getenv("OMS_JOB_TYPE").equalsIgnoreCase(JOB_NAME1)) {
			LOGGER.info(" inside employeeItemReader.... ");
			
			
			return stepBuilderFactory.get(JOB_NAME1).<String, String>
			chunk(6).reader(employeeItemReader()).processor(employeeItemProcessor()).writer(employeeItemWriter())
			.build();
		}
		return null;
	}
	
	@Bean
	public ItemReader<String> employeeItemReader(){
		
		if(System.getenv("OMS_JOB_TYPE").equalsIgnoreCase(JOB_NAME)) {
			LOGGER.info(" inside employeeItemReader.... ");
			
			return new ListItemReader<>(Arrays.asList(JOB_NAME));
		}

		
		else	if(System.getenv("OMS_JOB_TYPE").equalsIgnoreCase(JOB_NAME1)) {
			LOGGER.info(" inside employeeItemReader.... ");
			
			return new ListItemReader<>(Arrays.asList(JOB_NAME1));
		}return null;
	}

	
	@Bean 
	public ItemWriter<String> employeeItemWriter(){
		LOGGER.info(" inside employeeItemWriter.... ");
		
		if(System.getenv("OMS_JOB_TYPE").equalsIgnoreCase(JOB_NAME)) {
			return new EmployeeItemWriter<>();

		}
		else if(System.getenv("OMS_JOB_TYPE").equalsIgnoreCase(JOB_NAME1)) {
			return new EmployeeItemWriter<>();
			
		}
		return null;
		
	}
	
	
	
	@Bean
	public ItemProcessor<String,String> employeeItemProcessor(){
		
		
		if(System.getenv("OMS_JOB_TYPE").equalsIgnoreCase(JOB_NAME)) {
			return new EmployeeProcesser<>();

		}
		else if(System.getenv("OMS_JOB_TYPE").equalsIgnoreCase(JOB_NAME1)) {
			return new UserProcesser<>();
			
		}
		return null;
	}
	
	@Bean
	public Job employeeJob(JobCompletionListener listener ) {
		LOGGER.info(" inside employeeJob.... ");
		
		if(System.getenv("OMS_JOB_TYPE").equalsIgnoreCase(JOB_NAME)) {
			return jobBuilderFactory.get(JOB_NAME).listener(listener).flow(employeeStep()).end().build();
		}
		else if(System.getenv("OMS_JOB_TYPE").equalsIgnoreCase(JOB_NAME1)) {
			return jobBuilderFactory.get(JOB_NAME1).listener(listener).flow(employeeStep()).end().build();			
		}
		return null;
		
		 
	}
	
}
