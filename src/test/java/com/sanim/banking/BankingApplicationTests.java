package com.sanim.banking;

import com.sanim.banking.domain.*;
import com.sanim.banking.repository.LedgerEntryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class BankingApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void addingSameCurrencySums() {
		Money a = Money.of("10.00", "GBP");
		Money b = Money.of("5.00", "GBP");

		assertEquals(Money.of("15.00", "GBP"), a.sum(b));
	}

	@Test
	void accountNumberLength() {
		AccountNumber num = AccountNumber.generate();
		assertEquals(8, num.accountNumber().length());
	}
}
