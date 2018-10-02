package com.niraj.fasta.reader;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.support.SingleItemPeekableItemReader;
import org.springframework.beans.factory.annotation.Autowired;

import com.niraj.fasta.confg.BatchConfiguration;
import com.niraj.fasta.domain.Fasta;
import com.niraj.fasta.domain.SequenceFastaReport;

public class MultiLineFastaItemReader implements ItemReader<Fasta>, ItemStream {

	private static final Logger log = LoggerFactory.getLogger(MultiLineFastaItemReader.class);
	private SingleItemPeekableItemReader<FieldSet> singalPeekable;

	AtomicInteger iteamCounter = new AtomicInteger(0);
	//int iteamCounter = 0;

	ConcurrentHashMap<String, AtomicInteger> fileNameAndCounterMap = new ConcurrentHashMap<>();
	//HashMap<String, AtomicInteger> fileNameAndCounterMap = new HashMap<>();

	@Autowired
	private SequenceFastaReport sequenceFastaReport;

	private MultiResourceItemReader<FieldSet> resourceItemReader;

	public MultiLineFastaItemReader(MultiResourceItemReader<FieldSet> multiResourceItemReader) {

		this.resourceItemReader = multiResourceItemReader;
	}

	public SingleItemPeekableItemReader<FieldSet> getSingalPeekable() {
		return singalPeekable;
	}

	public void setSingalPeekable(SingleItemPeekableItemReader<FieldSet> singalPeekable) {
		this.singalPeekable = singalPeekable;

	}

	@Override
	public Fasta read() throws Exception {
		FieldSet item = singalPeekable.read();
		if (item == null) {
			return null;
		}
		Fasta fastaObject = new Fasta();

		log.info("ID {} fileName {}",item.readString(0),resourceItemReader.getCurrentResource());
		fastaObject.setSequenceIdentifier(item.readString(0).toUpperCase());
		fastaObject.setFileName(resourceItemReader.getCurrentResource()
				.getFilename());

		if (!fileNameAndCounterMap.containsKey(fastaObject.getFileName())) {
			fileNameAndCounterMap.put(fastaObject.getFileName(), new AtomicInteger(0));

		}

		while (true) {

			FieldSet possibleRelatedObject = singalPeekable.peek();
			if (possibleRelatedObject == null) {
				if(fastaObject.getSequenceIdentifier().length() < 1)
					throw new InvalidParameterException("Somwthing Wrong in file");
				sequenceFastaReport.addToReport(fileNameAndCounterMap.get(fastaObject.getFileName())
						.incrementAndGet(), fastaObject.getSequences());		
				return fastaObject;
			}

			if (possibleRelatedObject.readString(0)
					.startsWith(">")) {
				if(fastaObject.getSequenceIdentifier().length() < 1)
					throw new InvalidParameterException("Somwthing Wrong in file");
				
				sequenceFastaReport.addToReport(fileNameAndCounterMap.get(fastaObject.getFileName())
						.incrementAndGet(), fastaObject.getSequences());	
				
				return fastaObject;
			}
			String data = fastaObject.getSequences().toUpperCase();
			fastaObject.setSequences(data + singalPeekable.read().readString(0).toUpperCase());
			
		}

	}

	@Override
	public void close() {
		this.singalPeekable.close();
	}

	@Override
	public void open(ExecutionContext executionContext) {
		this.singalPeekable.open(executionContext);

	}

	@Override
	public void update(ExecutionContext executionContext) {

		this.singalPeekable.update(executionContext);
	}

}