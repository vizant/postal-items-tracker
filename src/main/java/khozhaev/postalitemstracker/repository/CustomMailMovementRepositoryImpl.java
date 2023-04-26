package khozhaev.postalitemstracker.repository;

import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import khozhaev.postalitemstracker.model.MailMovement;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CustomMailMovementRepositoryImpl implements CustomMailMovementRepository {

    private final SessionFactory sessionFactory;

    @Override
    public Optional<MailMovement> findLastMailMovementByTrackingNumber(String trackingNumber) {
        Session session = sessionFactory.openSession();
        MailMovement mailMovement = session.createQuery(
                "SELECT m FROM MailMovement m JOIN MailItem i ON m.mailItem.id = i.id " +
                        "WHERE i.trackingNumber =: trackingNumber ORDER BY m.eventTime DESC",
                MailMovement.class)
                .setParameter("trackingNumber", trackingNumber)
                .setMaxResults(1)
                .getSingleResult();
        session.close();
        return Optional.of(mailMovement);
    }
}
