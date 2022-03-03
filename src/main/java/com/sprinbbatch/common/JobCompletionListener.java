package com.sprinbbatch.common;


import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionListener extends JobExecutionListenerSupport {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(JobCompletionListener.class);
	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			LOGGER.info("BATCH JOB COMPLETED SUCCESSFULLY");
		}
}
}