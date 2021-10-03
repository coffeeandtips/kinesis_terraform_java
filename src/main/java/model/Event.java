package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class Event {

    @JsonProperty("event_date")
    private String eventDate;

    @JsonProperty("event_id")
    private UUID eventId;

    @JsonProperty("provider")
    private String provider;

    @JsonProperty("blog")
    private String blog;

    @JsonProperty("post_id")
    private UUID postId;
}
