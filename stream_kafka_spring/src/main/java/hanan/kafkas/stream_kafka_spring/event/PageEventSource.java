package hanan.kafkas.stream_kafka_spring.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class PageEventSource implements ApplicationRunner {

    @Autowired
    private KafkaTemplate<String,PageViewEvent> kafkaTemplate;


    @Override
    public void run(ApplicationArguments args) throws Exception {

        System.out.println("-----------------------------------");
        List<String> names = Arrays.asList("sami","hanane","ghali","samia");
        List<String> pages = Arrays.asList("blog","chat","profile","search","Home","About","index");
        Runnable runnable= ()->{
            String rNames = names.get(new Random().nextInt(names.size()));
            String rPages = pages.get(new Random().nextInt(pages.size()));
            PageViewEvent pageViewEvent = new PageViewEvent(rNames,rPages,Math.random()>0.5?100:1000);
            System.out.println("Send");
            kafkaTemplate.send("topic2",pageViewEvent.getUserId(),pageViewEvent);
            log.info("sending message"+pageViewEvent.toString());
        };

        System.out.println("---------------------------------------------");
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(runnable,1,1, TimeUnit.SECONDS);

    }
}
