package com.niraj.fasta.domain;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.ToString;

@Component
@ToString
@Data
public class SequenceFastaReport {

	private ConcurrentHashMap<Integer, String> sequenceMap = new ConcurrentHashMap<>();

	public void addToReport(int iteamCount, String sequences) {
		if (sequenceMap.containsKey(iteamCount)) {
			String string = sequenceMap.get(iteamCount);
			sequenceMap.put(iteamCount, string + sequences);
		} else {
			sequenceMap.put(iteamCount, sequences);
		}

	}

}
