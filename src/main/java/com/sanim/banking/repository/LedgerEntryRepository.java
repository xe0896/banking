package com.sanim.banking.repository;

import com.sanim.banking.domain.ledger.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {
    List<LedgerEntry> findByAccountId(UUID accountId);

    @Query("select sum(le.amount) from LedgerEntry le where le.accountId = :id")
    BigDecimal sumByAccountId(@Param("id") UUID id);

    @Query("select sum(le.amount) from LedgerEntry le")
    BigDecimal sumWholeLedger();

    @Query("select count(*) from LedgerEntry")
    long count();
}
