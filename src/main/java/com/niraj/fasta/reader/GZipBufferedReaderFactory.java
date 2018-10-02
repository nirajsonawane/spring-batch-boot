package com.niraj.fasta.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.springframework.batch.item.file.BufferedReaderFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class GZipBufferedReaderFactory implements BufferedReaderFactory {

	

	private List<String> supportedFileExtensions = Arrays.asList(".gz", "gzip");

	
	@Override
	public BufferedReader create(Resource resource, String encoding) throws UnsupportedEncodingException, IOException {
		for (String suffix : supportedFileExtensions) {			
			if (resource.getFilename()
					.endsWith(suffix)
					|| resource.getDescription()
							.endsWith(suffix)) {
				
				return new BufferedReader(
						new InputStreamReader(new GZIPInputStream(resource.getInputStream()), encoding));
			}
		}
		
		return new BufferedReader(new InputStreamReader(resource.getInputStream(), encoding));
	}

	public List<String> getGzipSuffixes() {
		return supportedFileExtensions;
	}

	public void setGzipSuffixes(List<String> gzipSuffixes) {
		this.supportedFileExtensions = gzipSuffixes;
	}
}
