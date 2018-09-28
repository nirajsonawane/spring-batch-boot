package hello;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.transform.FieldSet;

public class MultiLineCaseItemReader implements ItemReader<Fasta> {

	private FlatFileItemReader<FieldSet> delegate;

	Fasta fasta;

	public MultiLineCaseItemReader(FlatFileItemReader<FieldSet> delegate) {
		this.delegate = delegate;
	}

	@Override
	public Fasta read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

		for (FieldSet line = null; (line = this.delegate.read()) != null;) {
			String prefix = line.readString(0);
			if (prefix.equals(">")) {
				fasta = new Fasta(); // Record must start with header
				System.out.println(prefix);
			} else {
				System.out.println(prefix);
			}

		}

		return null;
	}

}
