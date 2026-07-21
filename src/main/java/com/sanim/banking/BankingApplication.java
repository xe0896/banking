package com.sanim.banking;

import com.sanim.banking.domain.ledger.LedgerEntry;
import org.springframework.boot.SpringApplication;
import com.sanim.banking.domain.*;
import com.sanim.banking.domain.user.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Currency;
import java.math.BigDecimal;

// Marks the source of Spring configuration
@SpringBootApplication
public class BankingApplication {
	// Contains main method meaning it is the starting point of the program
	public static void main(String[] args) {
		SpringApplication.run(BankingApplication.class, args);
	}
}
