package com.sanim.banking.repository;

import com.sanim.banking.domain.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// This is an interface so we cannot create one, @Autowired does some magic to make there own
// we need to make our own without @Autowired in tests though so this motivates the use of
// implementing a Service
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Optional<Transaction> findByIdempotencyKeyAndInitiatedByUserId(String idempotencyKey, UUID initiatedByUserId);
}
