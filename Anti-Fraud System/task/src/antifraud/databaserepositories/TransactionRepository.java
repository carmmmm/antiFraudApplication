package antifraud.databaserepositories;

import antifraud.databaseentities.Transaction;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    Transaction findByTransactionId(Long number);
    List<Transaction> findAllByNumber(String number);

}