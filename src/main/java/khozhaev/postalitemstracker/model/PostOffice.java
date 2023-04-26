package khozhaev.postalitemstracker.model;

import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "post_office")
public class PostOffice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String index;

    private String name;

    private String address;
}
