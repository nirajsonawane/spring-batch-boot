package com.niraj.fasta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	//    ftp://ftp.ncbi.nlm.nih.gov/blast/db/FASTA
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}

 