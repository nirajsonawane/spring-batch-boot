package com.niraj.fasta.writer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.niraj.fasta.domain.SequenceFastaReport;
import com.niraj.fasta.domain.SummaryReport;

import sun.security.provider.Sun;

@Component
public class FileWritingTasklet implements Tasklet {
	
	private static final Logger log = LoggerFactory.getLogger(FastReportWriter.class);

	@Autowired
	private SequenceFastaReport sequenceFastaReport;

	@Autowired
	private SummaryReport summaryReport;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		log.info("Writing Summary Report");
		
		Path path = Paths.get("C:\\Users\\SUJAN\\Downloads\\files\\REPORT.TXT");		 
		
		try (BufferedWriter writer = Files.newBufferedWriter(path))
		{
		    writer.write("FILE_CNT");
		    writer.write("\t");
		    writer.write(String.valueOf(summaryReport.getFileCount()));
		    writer.write(System.lineSeparator());
		    
		    log.info(summaryReport.getSequenceCount() + "getSequenceCount");
		    writer.write("SEQUENCE_CNT");
		    writer.write("\t");
		    writer.write(String.valueOf(summaryReport.getSequenceCount()));
		    writer.write(System.lineSeparator());
		    
		    log.info(summaryReport.getTotalNumberOfSequenceBases() + "BASE_CNT");
		    writer.write("BASE_CNT");
		    writer.write("\t");
		    writer.write(String.valueOf(summaryReport.getTotalNumberOfSequenceBases()));
		    writer.write(System.lineSeparator());
		
		    
		    summaryReport.getSequencesBaseMap().forEach((k,v)->{
		    	
		    	try {
					writer.write(k);
					writer.write("\t");
					writer.write(v.toString());
					writer.write(System.lineSeparator());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    });
		    writer.write(System.lineSeparator());
		}
		    
		    Path path2 = Paths.get("C:\\Users\\SUJAN\\Downloads\\files\\SEQUENCE.FASTA");	
		    try (BufferedWriter SEQUENCE = Files.newBufferedWriter(path2))
			{
		    				    
		    	sequenceFastaReport.getSequenceMap().forEach((k,v)->{
			    	
			    	try {
			    		SEQUENCE.write(String.valueOf(k));					
			    		SEQUENCE.write(System.lineSeparator());
			    		SEQUENCE.write(v.toString());
			    		SEQUENCE.write(System.lineSeparator());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    });
			
		}
		return null;
	}
	

}
