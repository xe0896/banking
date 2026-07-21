package com.sanim.banking;

import com.sanim.banking.domain.ledger.LedgerEntry;
import com.sanim.banking.repository.LedgerEntryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest // forgot
class LedgerRepositoryTests {
    @Autowired
    LedgerEntryRepository ledger;

    @Test
    void findByAccountIdTest() {
        UUID transactionId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();

        LedgerEntry entry = LedgerEntry.builder().
                transactionId(transactionId).
                accountId(accountId).
                amount(new BigDecimal("10.00")).
                currencyCode("GBP").build();

        ledger.save(entry);

        List<LedgerEntry> list = ledger.findByAccountId(accountId);
        assertEquals(list.getFirst().getAccountId(), accountId);
    }

    @Test
    void sumByAccountIdTest() {
        UUID transactionId1 = UUID.randomUUID();
        UUID transactionId2 = UUID.randomUUID();

        UUID accountId = UUID.randomUUID();

        LedgerEntry entry1 = LedgerEntry.builder().
                transactionId(transactionId1).
                accountId(accountId).
                amount(new BigDecimal("20.00")).
                currencyCode("GBP").build();

        LedgerEntry entry2 = LedgerEntry.builder().
                transactionId(transactionId2).
                accountId(accountId).
                amount(new BigDecimal("10.00")).
                currencyCode("GBP").build();

        ledger.save(entry1);
        ledger.save(entry2);

        assertEquals(0, new BigDecimal("30.00").compareTo(ledger.sumByAccountId(accountId)));
    }
}

