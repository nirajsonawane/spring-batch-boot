package hello;

import javax.swing.text.MaskFormatter;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.support.SingleItemPeekableItemReader;

public class MultiLineTradeItemReader implements ItemReader<Fasta>, ItemStream {
	
	private FlatFileItemReader<FieldSet> delegate;
	
	private SingleItemPeekableItemReader<FieldSet> singalPeekable;

	public SingleItemPeekableItemReader<FieldSet> getSingalPeekable() {
		return singalPeekable;
	}

	public void setSingalPeekable(SingleItemPeekableItemReader<FieldSet> singalPeekable) {
		this.singalPeekable = singalPeekable;
		
	}

	/**
	 * @see org.springframework.batch.item.ItemReader#read()
	 */
	@Override
	public Fasta read() throws Exception {
		Fasta fastaObject = null;		
		
		FieldSet item = singalPeekable.read();	

        if (item == null) {
            return null;
        }
        fastaObject = new Fasta();
        while (true) {
        	
        	FieldSet possibleRelatedObject = singalPeekable.peek();
            if (possibleRelatedObject == null) {
                return fastaObject;
            }

            //logic to determine if next line in file relates to same object
            boolean matches = false; 
            if(possibleRelatedObject.getNames().equals("id"))
            {
            	matches=true;            	
            	//fastaObject.setId(possibleRelatedObject.getValues().toString());
            }
            
            if (!matches) {
            	String data = fastaObject.getData(); 
            	if(singalPeekable.read().getNames().equals("id"))
            	{
            		fastaObject.setData(data+singalPeekable.read().readString("id"));            		
            	}
            	else
            	{
            		
            		fastaObject.setData(data+singalPeekable.read().readString("data"));
            	}
              
            } else {
                return fastaObject;
            }
        	
        }
        	

		/*for (FieldSet line; (line = this.delegate.read()) != null;) {
			System.out.println("Data%%%%%%%%%%%%%%%%");
			System.out.println(line);
			System.out.println(line.getNames()[0]);
			String prefix = line.readString(0);
			

			if(line.getNames()[0].equals("id"))
			{
				t = new Fasta(); // Record must start with 'BEGIN'
				
				
			}
			if (prefix.equals("<")) {
				t.setId(prefix);
			} else {
				// String string = t.getData();
				System.out.println(prefix);
				// t.setData(string+prefix);
			}
		}
		Assert.isNull(t, "No 'END' was found.");*/
		
	}

	public void setDelegate(FlatFileItemReader<FieldSet> delegate) {
		this.delegate = delegate;
	}

	@Override
	public void close() throws ItemStreamException {
		this.singalPeekable.close();
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
			this.singalPeekable.open(executionContext);
		//this.delegate.open(executionContext);
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		//this.delegate.update(executionContext);
		this.singalPeekable.update(executionContext);
	}
}