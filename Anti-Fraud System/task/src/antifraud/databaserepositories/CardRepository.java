package antifraud.databaserepositories;

import antifraud.databaseentities.StolenCard;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface CardRepository extends CrudRepository<StolenCard, Long> {
    StolenCard findStolenCardByCardNumber (String cardNumber);

}
