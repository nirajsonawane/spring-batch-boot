package com.niraj.fasta.mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class FastFieldSetMapper implements FieldSetMapper<FieldSet> {

	@Override
	public FieldSet mapFieldSet(FieldSet fieldSet) throws BindException {
		return fieldSet;
	}

}
