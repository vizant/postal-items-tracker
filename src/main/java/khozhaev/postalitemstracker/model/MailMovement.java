package khozhaev.postalitemstracker.model;

import java.time.LocalDate;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "mail_movement")
public class MailMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "mail_item_id")
    private MailItem mailItem;

    @ManyToOne
    @JoinColumn(name = "post_office_id")
    private PostOffice postOffice;

    private MovementType movementType;

    private LocalDate eventTime;

    public MailMovement(MailItem mailItem,
                        PostOffice postOffice,
                        MovementType movementType,
                        LocalDate eventTime) {
        this.mailItem = mailItem;
        this.postOffice = postOffice;
        this.movementType = movementType;
        this.eventTime = eventTime;
    }
}
