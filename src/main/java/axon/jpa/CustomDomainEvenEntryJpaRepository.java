package axon.jpa;

import axon.config.axon.jpa.CustomDomainEventEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomDomainEvenEntryJpaRepository extends JpaRepository<CustomDomainEventEntry, Long> {
}
