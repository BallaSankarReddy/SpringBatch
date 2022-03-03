package com.sprinbbatch.common.batch.writer;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

//@Component
public class EmployeeItemWriter<T> implements ItemWriter<T> {
	
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(EmployeeItemWriter.class);

	@Override
	public void write(List<? extends T> items) throws Exception {
		
		LOGGER.info(" Inside EmployeeItemWriter started.....");
		if(!items.isEmpty()) {
		
			for (T t : items) {
				
				LOGGER.info(" Looging EmployeeItemWriter:.....{}",t);
			}
			
			
		}
		
	}

}
