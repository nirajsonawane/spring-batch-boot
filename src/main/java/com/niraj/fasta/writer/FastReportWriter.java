package com.niraj.fasta.writer;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.niraj.fasta.domain.Fasta;
import com.niraj.fasta.domain.SummaryReport;

public class FastReportWriter implements ItemWriter<Fasta> {

	private static final Logger log = LoggerFactory.getLogger(FastReportWriter.class);
	@Autowired
	private SummaryReport report;

	@Override   
	public void write(List<? extends Fasta> list) throws Exception {
		log.info("Size of List  {}" ,list.size() );

		report.getSequenceCount().addAndGet(list.size());
		//report.setSequenceCount(report.getSequenceCount().addAndGet(list.size()) );
		
		int sum = list.stream()
				.mapToInt(Fasta::getBaseCount)
				.sum();  
		Map<String, Long> sequencesBaseMap = report.getSequencesBaseMap();
		report.getTotalNumberOfSequenceBases().addAndGet(sum);
		//report.setTotalNumberOfSequenceBases(report.getTotalNumberOfSequenceBases() + sum);
		
		Instant start = ZonedDateTime.now().toInstant();
		
		list.stream()
					.map(Fasta::getSequencesBase)
					 .forEach(map->sum(map,sequencesBaseMap));	
	
		Instant end = ZonedDateTime.now().toInstant();
		Duration timeElapsed = Duration.between(start, end);
		
		log.info("Time Taken to merge Map  {}" ,timeElapsed.getSeconds() );
		
	
	}

	private void sum(Map<String, Long> map, Map<String, Long> sequencesBaseMap) {		
		map.forEach((k,v)->sequencesBaseMap.merge(k, v, Long::sum));
		
	}

}
