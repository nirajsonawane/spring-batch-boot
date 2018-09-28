package hello;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.PatternMatchingCompositeLineTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.batch.item.support.SingleItemPeekableItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

  
    @Bean
    public FlatFileItemReader<FieldSet> reader() {
        return new FlatFileItemReaderBuilder<FieldSet>()
            .name("fileReader")
            .resource(new ClassPathResource("fasta.txt"))
            .lineTokenizer(orderFileTokenizer())
            .fieldSetMapper(new MyFieldSetMapper())
            .build();
    }
    
    @Bean
    public SingleItemPeekableItemReader<FieldSet> readerPeek() {
        SingleItemPeekableItemReader<FieldSet> reader = new SingleItemPeekableItemReader<FieldSet>() {{
            setDelegate(reader());
        }};
        return reader;
    }
    
    @Bean
    public MultiLineTradeItemReader itemReader() {
            MultiLineTradeItemReader itemReader = new MultiLineTradeItemReader();
            //itemReader.setDelegate(reader());
            itemReader.setSingalPeekable(readerPeek());
            return itemReader;
    }
    
/*    @Bean
    public MultiLineCaseItemReader readerMultirecord() {
        MultiLineCaseItemReader multiReader = new MultiLineCaseItemReader(reader());
        return multiReader;
    }*/
    
    @Bean
    public PatternMatchingCompositeLineTokenizer orderFileTokenizer() {
            PatternMatchingCompositeLineTokenizer tokenizer =
                            new PatternMatchingCompositeLineTokenizer();

            Map<String, LineTokenizer> tokenizers = new HashMap<>(2);

            tokenizers.put(">*", head());
            tokenizers.put("*", tail());
        

            tokenizer.setTokenizers(tokenizers);

            return tokenizer;
    }
    
    public FixedLengthTokenizer head()
    {
    	FixedLengthTokenizer token = new FixedLengthTokenizer();
    	token.setNames("id");
    	token.setColumns(new Range(1,6));
    	return token;
    }
    public FixedLengthTokenizer tail()
    {
    	FixedLengthTokenizer token = new FixedLengthTokenizer();
    	token.setNames("data");
    	token.setColumns(new Range(1,6));
    	return token;
    }
    
    @Bean
    public MyPeekingReader getMyMyPeekingReader()
    {
    	MyPeekingReader reader = new MyPeekingReader();
    	reader.setDelegate(reader());
    	return reader;
    }

    @Bean
    public MyWriter writer()
    {
    	return new MyWriter();
    }

    
    
    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importUserJob")
            .incrementer(new RunIdIncrementer())
            .listener(listener)
            .flow(step1)
            .end()
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .<Fasta, Fasta> chunk(10)
            .reader(itemReader())
            .processor(new PassThroughItemProcessor<Fasta>())
            .writer(writer())
            .build();
    }
 
}
