package hanan.kafkas.stream_kafka_spring.web;

import hanan.kafkas.stream_kafka_spring.event.PageViewEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("kafka")
public class RestApi {

    @Autowired
    private KafkaTemplate<String, PageViewEvent> kafkaTemplate;

    private String topic = "topic2";

    @GetMapping("/publish/{user}-{page}-{duration}")
    public PageViewEvent publishMessage(@PathVariable String user, @PathVariable String page, @PathVariable int duration){

        PageViewEvent pageViewEvent = new PageViewEvent(user,page,duration);
        kafkaTemplate.send(topic,pageViewEvent.getUserId(),pageViewEvent);
        return pageViewEvent;
    }


}
