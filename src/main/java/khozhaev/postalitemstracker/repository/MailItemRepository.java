package khozhaev.postalitemstracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import khozhaev.postalitemstracker.model.MailItem;

public interface MailItemRepository extends JpaRepository<MailItem, Long> {
    Optional<MailItem> findByTrackingNumber(String trackingNumber);
}
