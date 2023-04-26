package khozhaev.postalitemstracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DeliveryStatus {
    @JsonProperty("Created")
    CREATED,
    @JsonProperty("In delivery")
    IN_DELIVERY,
    @JsonProperty("Delivered")
    DELIVERED
}
