package khozhaev.postalitemstracker.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum MailType {
    @JsonProperty("Letter")
    LETTER,
    @JsonProperty("Package")
    PACKAGE,
    @JsonProperty("Parcel")
    PARCEL,
    @JsonProperty("Postcard")
    POSTCARD
}
