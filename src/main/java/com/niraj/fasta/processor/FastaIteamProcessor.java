package com.niraj.fasta.processor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemProcessor;

import com.niraj.fasta.domain.Fasta;

public class FastaIteamProcessor implements ItemProcessor<Fasta, Fasta> {

	@Override
	public Fasta process(Fasta item) throws Exception {

		Map<String, Long> sequencesBaseCountMap;
		if(item.getSequences().length()>5000)
		{
			sequencesBaseCountMap = Arrays.stream(item.getSequences()
					.split(""))
					.parallel()
					.collect(Collectors.groupingBy(s -> s, Collectors.counting()));
	
		}else {
			sequencesBaseCountMap = Arrays.stream(item.getSequences()
					.split(""))					
					.collect(Collectors.groupingBy(s -> s, Collectors.counting()));
		}
		
		
		item.setSequencesBase(sequencesBaseCountMap);
		item.setBaseCount(item.getSequences()
				.length());

		return item;
	}

}
