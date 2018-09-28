package hello;

import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.support.SingleItemPeekableItemReader;

public class MyPeekingReader extends SingleItemPeekableItemReader<FieldSet>{
	
	
	
	/*public Trade read() throws Exception {
	    Trade t = null;

	    for (FieldSet line = null; (line = this.delegate.read()) != null;) {
	        String prefix = line.readString(0);
	        if (prefix.equals("HEA")) {
	            t = new Trade(); // Record must start with header
	        }
	        else if (prefix.equals("NCU")) {
	            Assert.notNull(t, "No header was found.");
	            t.setLast(line.readString(1));
	            t.setFirst(line.readString(2));
	            ...
	        }
	        else if (prefix.equals("BAD")) {
	            Assert.notNull(t, "No header was found.");
	            t.setCity(line.readString(4));
	            t.setState(line.readString(6));
	          ...
	        }
	        else if (prefix.equals("FOT")) {
	            return t; // Record must end with footer
	        }
	    }
	    Assert.isNull(t, "No 'END' was found.");
	    return null;
	}*/
	
	@Override
    public FieldSet read() throws UnexpectedInputException, ParseException, Exception {
		
		boolean matches = false; 

        

        for (FieldSet line = null; (line = super.read()) != null;) {
        	 String prefix = line.readString(0);
        }
		return null;
        
        

    }

}
