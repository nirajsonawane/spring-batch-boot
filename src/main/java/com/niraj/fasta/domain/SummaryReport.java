package com.niraj.fasta.domain;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Component
public class SummaryReport {

	private int fileCount = 0;
	
	private AtomicInteger sequenceCount = new AtomicInteger(0);
	private AtomicInteger totalNumberOfSequenceBases = new AtomicInteger(0);
	private ConcurrentHashMap<String, Long> sequencesBaseMap = new ConcurrentHashMap<>();

}
