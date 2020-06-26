import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class KafkaStreamsConsumer {

    public static void main(String[] args) {
        new KafkaStreamsConsumer().start();
    }

    private void start() {

        Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG,"streams_consumer1");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,Serdes.String().getClass().getName());
        properties.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG,1000);
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        //subscribe to topic1 , once data received consume as String for key and value
        KStream<String,String> kStream = streamsBuilder.stream("Topic1", Consumed.with(Serdes.String(),Serdes.String()));

        KTable<Windowed<String>, Long> resultStream  = kStream.flatMapValues(textLine->Arrays.asList(textLine.split("\\W+")))
                .map((k,v)->new KeyValue<>(k,v.toLowerCase()))
                .filter((k,v)->v.equals("a") || v.equals("b"))
                .groupBy((k,v)->v)
               //TimeWindows ->faire le count seulement sur les donnes recus d'une duree precise
                .windowedBy(TimeWindows.of(Duration.ofSeconds(5)))
                //count()-> produit un objet de type KTable et pas Kstream
                .count(Materialized.as("count-analytics"));
        //ToStream -> pour convertir le KTable vers KStream
         resultStream.toStream().map((k,v)->new KeyValue<>(k.window().startTime()+"-"+k.window().endTime()+"-"+k.key(),v))
                .to("resTopic", Produced.with(Serdes.String(),Serdes.Long()));



        kStream.foreach((k,v)->{
            System.out.println(k+ "=> "+v);
        });

        //start kafkaStream
        Topology topology = streamsBuilder.build();
        KafkaStreams kafkaStreams = new KafkaStreams(topology,properties);
        kafkaStreams.start();
    }
}
