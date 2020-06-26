package hanan.org.kafkaspring;


import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

//creae consumer with spring boot
@Service
public class MessageService {



    @KafkaListener(topics = "test7",groupId = "consumer-ws")
    public void onMessage(ConsumerRecord<String,String> consumerRecords) throws Exception{

        System.out.println("******************************");

        PageEvent pgaeEvent = pageEvent(consumerRecords.value());
        System.out.println("page "+pgaeEvent.getPage()+",duration=> "+pgaeEvent.getDuration()+"Date: "+pgaeEvent.getDate());

        System.out.println("******************************");
    }

    //faire deserialization manuelle du String vers json
    public PageEvent pageEvent(String jsonPageEvent)throws Exception{
        JsonMapper jsonMapper = new JsonMapper();
        PageEvent pgaeEvent = jsonMapper.readValue(jsonPageEvent,PageEvent.class);
        return pgaeEvent;
    }
}
