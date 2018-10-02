package com.niraj.fasta.listener;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.niraj.fasta.domain.SequenceFastaReport;
import com.niraj.fasta.domain.SummaryReport;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	@Autowired
	private SequenceFastaReport report;
	
	@Autowired
	private SummaryReport summaryReport;

	@Override
	public void afterJob(JobExecution jobExecution) {


		System.out.println("Size of MAp " + report.getSequenceMap().size());
		System.out.println("Sequence Count " + summaryReport.getSequenceCount());
		System.out.println("SequencesBaseMap" + summaryReport.getSequencesBaseMap());
		
		Duration timeElapsed = Duration.between(jobExecution.getStartTime().toInstant(), jobExecution.getEndTime().toInstant());
		log.info("Total Batch Time : {}  Minutes",timeElapsed.toMinutes() );
		log.info("Total Batch Time : {}  Minutes",timeElapsed.getSeconds() );
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! JOB FINISHED! Time to verify the results");

		}
		
	}
}
