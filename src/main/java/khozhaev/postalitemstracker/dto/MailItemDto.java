package khozhaev.postalitemstracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import khozhaev.postalitemstracker.model.MailType;
import lombok.Data;

@Data
public class MailItemDto {
    @Pattern(regexp = "\\d{6}", message = "Tracking number should consists of 6 digits!")
    private String trackingNumber;

    @NotBlank(message = "Sender should be specified!")
    private String sender;

    @NotBlank(message = "Recipient should be specified!")
    private String recipient;

    @NotBlank(message = "MailType should be specified!")
    private MailType mailType;
}
