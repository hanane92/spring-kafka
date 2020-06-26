package hanan.kafkastream.kafkastreamspringconsumer.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hanan.kafkastream.kafkastreamspringconsumer.event.PageViewEvent;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.internals.TimeWindow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaStreamConfig {
    ObjectMapper objectMapper = new ObjectMapper();

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kafkaStreamsConfiguration(){

        Map<String,Object> conf = new HashMap<>();
        conf.put(StreamsConfig.APPLICATION_ID_CONFIG,"page-event-stream");
        conf.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        conf.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        conf.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        conf.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG,1000);
        conf.put(StreamsConfig.POLL_MS_CONFIG,1000);

        return new KafkaStreamsConfiguration(conf);
    }

    @Bean
    public StreamsBuilderFactoryBean streamsBuilderFactoryBean(){

        return new StreamsBuilderFactoryBean(kafkaStreamsConfiguration());
    }

   // @Bean
    public KStream<String,Integer> process() throws Exception {

        KStream<String,String> kStream = streamsBuilderFactoryBean().getObject().stream("topic2");
        KStream<String,Integer> counts = kStream.
                map((k,v) -> new KeyValue<>(k,pageViewEvent(v)))
                .filter((k,v) -> v.getDuration() >200)
                .map((k,v) -> new KeyValue<>(k,v.getDuration()));

        //System.out.println("couuuneting: "+counts.toString());
        counts.to("topicRes", Produced.valueSerde(Serdes.Integer()));


        return counts;
    }

    @Bean
    public KStream<String,Long> process2() throws Exception {

        KStream<String,String> kStream = streamsBuilderFactoryBean().getObject().stream("topic2");
        KStream<String,Long> counts = kStream.
                map((k,v) -> new KeyValue<>(k,pageViewEvent(v)))
                .filter((k,v) -> v.getDuration() >80)
                .map((k,v) -> new KeyValue<>(v.getPage(),"0"))
                .groupByKey()
                //.windowedBy(TimeWindows.of(5000))
                .count(Materialized.as("count-page")).toStream();
               // .map((k,v)->new KeyValue<>(k.key(),v));
        counts.to("topicRes2",Produced.valueSerde(Serdes.Long()));

        return counts;
    }

    public PageViewEvent pageViewEvent(String objStr){

        PageViewEvent pageViewEvent = new PageViewEvent();
        try {
            //convert string of JSON received from producer to Object PageViewtEvent
            pageViewEvent = objectMapper.readValue(objStr,PageViewEvent.class);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return pageViewEvent;
    }
}
