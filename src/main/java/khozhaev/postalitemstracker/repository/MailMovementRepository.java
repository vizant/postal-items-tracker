package khozhaev.postalitemstracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import khozhaev.postalitemstracker.model.MailMovement;

public interface MailMovementRepository extends JpaRepository<MailMovement, Long>, CustomMailMovementRepository {
}
