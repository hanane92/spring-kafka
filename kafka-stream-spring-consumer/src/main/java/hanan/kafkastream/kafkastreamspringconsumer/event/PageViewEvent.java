package hanan.kafkastream.kafkastreamspringconsumer.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PageViewEvent {

    private String userId;
    private String page;
    private int duration;
}
