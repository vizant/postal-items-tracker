package khozhaev.postalitemstracker.repository;

import java.util.Optional;

import khozhaev.postalitemstracker.model.MailMovement;

public interface CustomMailMovementRepository {
    Optional<MailMovement> findLastMailMovementByTrackingNumber(String trackingNumber);
}
