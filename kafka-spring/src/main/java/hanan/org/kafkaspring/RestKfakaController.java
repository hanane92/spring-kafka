package hanan.org.kafkaspring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Random;

//producer with spring boot
@RestController
public class RestKfakaController {

    @Autowired
    private KafkaTemplate<String,PageEvent> kafkaTemplate;
    //private KafkaTemplate<String,String> kafkaTemplate;


   /* @GetMapping("/send/{message}/{topic}")
    public String send(@PathVariable String message, @PathVariable String topic){

        kafkaTemplate.send(topic,message);
        return "message sent";
    }*/

    //envoyer un objet PageEvent[JSON] au lieu d'un String
    @GetMapping("/send/{page}/{topic}")
    public String send(@PathVariable String page, @PathVariable String topic){

        PageEvent pageEvent = new PageEvent(page,new Date(),new Random().nextInt(1000));
        kafkaTemplate.send(topic,"key "+pageEvent.getPage(),pageEvent);
        return "message sent";
    }

}
