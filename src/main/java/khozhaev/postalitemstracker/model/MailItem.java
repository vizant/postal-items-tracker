package khozhaev.postalitemstracker.model;

import java.util.List;

import jakarta.persistence.*;

import lombok.Data;
import org.hibernate.annotations.Fetch;

@Data
@Entity
@Table(name = "mail_item")
public class MailItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tracking_number", unique = true)
    private String trackingNumber;

    @Column(name = "sender")
    private String sender;

    @Column(name = "recipient")
    private String recipient;

    @Column(name = "status")
    private DeliveryStatus status;

    @Column(name = "mail_type")
    private MailType mailType;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "mailItem")
    private List<MailMovement> mailMovements;

    public void addMailMovement(MailMovement mailMovement) {
        mailMovements.add(mailMovement);
    }
}
