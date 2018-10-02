package com.niraj.fasta.domain;

import java.util.Map;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Fasta {

	private String sequenceIdentifier;
	private String sequences = "";
	private Map<String, Long> sequencesBase;
	private Integer baseCount;
	private String fileName;
	private Integer itemCount;

}
