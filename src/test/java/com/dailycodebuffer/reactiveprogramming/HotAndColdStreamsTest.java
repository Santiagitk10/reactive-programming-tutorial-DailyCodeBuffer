package com.dailycodebuffer.reactiveprogramming;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;


//Un coldStream es una en que la data que se recibe es siempre la misma, según lo que haya en
//en regitro. Por ejemplo llamadas a la base de datos con el mismo query
//un hotStream es el que solo recibe los datos nuevos a partir de la subscripción
//, por ejemplo los tikers de las acciones


public class HotAndColdStreamsTest {

    //ColdStream siempre se recibe la misma data
    @Test
    public void colStreamTest() {
        var numbers = Flux.range(1,10);

        numbers.subscribe(integer -> System.out.println("Subscriber 1 = " + integer));
        numbers.subscribe(integer -> System.out.println("Subscriber 2 = " + integer));
    }

    //el subscriber 2 no se conecta hasta 4 segundos después, por eso empieza a recibir 
    //los valores del publisher desde el 4 en adelante
    @SneakyThrows
    @Test
    public void hotStreamTest() {
        var numbers = Flux.range(1,10)
                .delayElements(Duration.ofMillis(1000));

        //el método publish devuelve un ConnectableFlux, un flux al que me puedo conectar
        ConnectableFlux<Integer> publisher = numbers.publish();
        publisher.connect();

        publisher.subscribe(integer -> System.out.println("Subscriber 1 = " + integer));
        Thread.sleep(4000);
        publisher.subscribe(integer -> System.out.println("Subscriber 2 = " + integer));
        Thread.sleep(10000);
    }
}
