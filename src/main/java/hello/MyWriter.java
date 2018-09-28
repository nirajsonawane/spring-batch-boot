package hello;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

public class MyWriter implements ItemWriter<Fasta>{

	@Override
	public void write(List<? extends Fasta> list) throws Exception {
		System.out.println("###### " + list.size() +" ################");
		list.stream().forEach(System.out::println);
	}

}
