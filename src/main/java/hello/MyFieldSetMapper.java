package hello;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class MyFieldSetMapper implements FieldSetMapper<FieldSet> {

	@Override
	public FieldSet mapFieldSet(FieldSet fieldSet) throws BindException {
		// TODO Auto-generated method stub
		return fieldSet;
	}

	/*@Override
	public Fasta mapFieldSet(FieldSet fieldSet) throws BindException {
		if (fieldSet == null) {
			return null;
		}

		Fasta fasta = new Fasta();
		
		if(fieldSet.getFieldCount()==2)
		{
			fasta.setId(fieldSet.readString("id"));
			fasta.setData(fieldSet.readString("data"));
			
		}
		return fasta;
	}*/

}
