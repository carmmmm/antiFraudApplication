package antifraud.databaserepositories;

import antifraud.databaseentities.CardLimits;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface CardLimitRepository extends CrudRepository<CardLimits, Long> {
    CardLimits findByCardNumber(String cardNumber);

}