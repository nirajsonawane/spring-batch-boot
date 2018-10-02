package com.niraj.fasta.confg;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.PatternMatchingCompositeLineTokenizer;
import org.springframework.batch.item.support.SingleItemPeekableItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.niraj.fasta.domain.Fasta;
import com.niraj.fasta.domain.SummaryReport;
import com.niraj.fasta.listener.JobCompletionNotificationListener;
import com.niraj.fasta.mapper.FastFieldSetMapper;
import com.niraj.fasta.processor.FastaIteamProcessor;
import com.niraj.fasta.reader.GZipBufferedReaderFactory;
import com.niraj.fasta.reader.MultiLineFastaItemReader;
import com.niraj.fasta.writer.FastReportWriter;
import com.niraj.fasta.writer.FileWritingTasklet;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	private static final Logger log = LoggerFactory.getLogger(BatchConfiguration.class);
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private GZipBufferedReaderFactory gzipBufferedReaderFactory;
	
	  @Value("input/*.gz")
	  private Resource[] resources;
	  
	  @Autowired
	 private FileWritingTasklet fileWritingTasklet; 
	  @Autowired
	  private SummaryReport report;
	  
	  
	  
	  @Bean
      public Partitioner partitioner() {
          MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
          partitioner.setResources(resources);
          partitioner.partition(10);      
          return partitioner;
      }
	  @Bean
      public TaskExecutor taskExecutor() {
          ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
          taskExecutor.setMaxPoolSize(4);
          taskExecutor.afterPropertiesSet();
          return taskExecutor;
      }   
	  
	  @Bean
      @Qualifier("masterStep")
      public Step masterStep() {
          return stepBuilderFactory.get("masterStep")                  
                  .partitioner("step1",partitioner())
                  .step(step1())
                  .taskExecutor(taskExecutor())                  
                  .build();
      }

	
	 @Bean
	  public MultiResourceItemReader<FieldSet> multiResourceItemReader() {
		log.info("Total Number of Files to be process {}",resources.length);
		report.setFileCount(resources.length);
	    MultiResourceItemReader<FieldSet> resourceItemReader = new MultiResourceItemReader<FieldSet>();	 
	    resourceItemReader.setResources(resources);	    
	    resourceItemReader.setDelegate(reader());	    
	    return resourceItemReader;
	  }

	@Bean
	public FlatFileItemReader<FieldSet> reader() {
		 FlatFileItemReader<FieldSet> build = new FlatFileItemReaderBuilder<FieldSet>().name("fileReader")				
				.lineTokenizer(orderFileTokenizer())
				.fieldSetMapper(new FastFieldSetMapper())				
				.recordSeparatorPolicy(new BlankLineRecordSeparatorPolicy())
				.build();		 
		 build.setBufferedReaderFactory(gzipBufferedReaderFactory);
		 return build;
	}

	@Bean
	public SingleItemPeekableItemReader<FieldSet> readerPeek() {
		SingleItemPeekableItemReader<FieldSet> reader = new SingleItemPeekableItemReader<>();
		reader.setDelegate(multiResourceItemReader());
		return reader;
	}

	@Bean
	public MultiLineFastaItemReader itemReader() {
		MultiLineFastaItemReader itemReader = new MultiLineFastaItemReader(multiResourceItemReader());
		itemReader.setSingalPeekable(readerPeek());
		return itemReader;
	}

	@Bean
	public PatternMatchingCompositeLineTokenizer orderFileTokenizer() {
		PatternMatchingCompositeLineTokenizer tokenizer = new PatternMatchingCompositeLineTokenizer();
		Map<String, LineTokenizer> tokenizers = new HashMap<>(2);
		tokenizers.put(">*", head());
		tokenizers.put("*", tail());
		tokenizer.setTokenizers(tokenizers);
		return tokenizer;
	}

	public DelimitedLineTokenizer head() {
		DelimitedLineTokenizer token = new DelimitedLineTokenizer();
		token.setNames("sequenceIdentifier");
		token.setDelimiter(" ");
		token.setStrict(false);
		return token;
	}

	public DelimitedLineTokenizer tail() {
		DelimitedLineTokenizer token = new DelimitedLineTokenizer();
		token.setNames("sequences");
		token.setDelimiter(" ");
		return token;
	}

	@Bean
	public FastReportWriter writer() {
		return new FastReportWriter();
	}

	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
		return jobBuilderFactory.get("importUserJob")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				//.flow(masterStep())
				.flow(step1)
				.next(step2())
				.end()
				.build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.<Fasta, Fasta>chunk(5000)
				.reader(itemReader())
				.processor(new FastaIteamProcessor())
				//.processor(new PassThroughItemProcessor<>())
				.writer(writer())
				.build();
	}
	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2")
				.tasklet(fileWritingTasklet)
				.build();
	}

}
