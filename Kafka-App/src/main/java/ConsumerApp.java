import kafka.tools.ConsoleConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConsumerApp {

    public static void main(String[] args) {
        //config KAFKA CONSUMER
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");//suppose broker demare en localhost
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,"1000");//dans chaque 1 seconde je fais un pull des enregistrements depuis topics
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"true");//une fois je fais un pull c'est fait j'attends pas un commit
        properties.put(ConsumerConfig.GROUP_ID_CONFIG,"test-group-1");
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,30000);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<String, String>(properties);
        kafkaConsumer.subscribe(Collections.singletonList("test4"));
        //creer un thread chaque seconde il demarre un pull avec un decalage de 1 seconde,des enregistrements ajoute dans
        //les derniers 100milisecondes

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(()->{
            System.out.println("-------------");
            ConsumerRecords<String, String> consumerRecord = kafkaConsumer.poll(Duration.ofMillis(1000));//je fais un poll pr
            //les donnees de la derniere seconde,c'est le consomateur qui decide la quantites des donnees a consomer
            consumerRecord.forEach(cr->{
                System.out.println("key=> "+cr.key()+" => "+cr.value()+" => "+cr.offset());
            });
        },1000,1000, TimeUnit.MILLISECONDS);

    }

}
