package com.dailycodebuffer.reactiveprogramming;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

public class BackPressureTest {

    @Test
    public void testBackPressure() {
        //Se usa el backpressure cuando el receiver no se puede manejar toda el flujo o 
        //cantidad de data que viene del llamado, entonces se limita
        var numbers = Flux.range(1,100).log();
        //numbers.subscribe(integer -> System.out.println("integer = " + integer));

        numbers.subscribe(new BaseSubscriber<Integer>() {

            //Acá estoy limitando la respuesta a 3 records
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(3);
            }

            //Cuando ya haya recibido la cantidad que necesito se cancela el proceso
            @Override
            protected void hookOnNext(Integer value) {
                System.out.println("value = " + value);
                if(value ==3) cancel();
            }

            @Override
            protected void hookOnComplete() {
                System.out.println("Completed!!");
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                super.hookOnError(throwable);
            }

            @Override
            protected void hookOnCancel() {
                super.hookOnCancel();
            }
        });
    }


    //onBackpressureDrop maneja la data que no se está teniendo en cuenta. Entonces el 
    //publisher envía la información una vez y los métodos manejan la data que se queda 
    //por fuera de lo que se pidió. Esa data que no se considera se mantiene en el queue
    //interno y no se llama al publisher de nuevo
    @Test
    public void testBackPressureDrop() {
        var numbers = Flux.range(1,100).log();
        //numbers.subscribe(integer -> System.out.println("integer = " + integer));

        numbers
                .onBackpressureDrop(integer -> {
                    System.out.println("Dropped Values = " + integer);
                })
                .subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(3);
            }

            @Override
            protected void hookOnNext(Integer value) {
                System.out.println("value = " + value);
                if(value ==3) hookOnCancel();
            }

            @Override
            protected void hookOnComplete() {
                System.out.println("Completed!!");
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                super.hookOnError(throwable);
            }

            @Override
            protected void hookOnCancel() {
                super.hookOnCancel();
            }
        });
    }


    //De igual manera se toman 3 datos, péro el buffer toma los adicionales y quedan 
    //guardados en el queue interno.
    @Test
    public void testBackPressureBuffer() {
        var numbers = Flux.range(1,100).log();
        //numbers.subscribe(integer -> System.out.println("integer = " + integer));

        numbers
                .onBackpressureBuffer(10,
                        i -> System.out.println("Buffered Value = " + i))
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(3);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        System.out.println("value = " + value);
                        if(value ==3) hookOnCancel();
                    }

                    @Override
                    protected void hookOnComplete() {
                        System.out.println("Completed!!");
                    }

                    @Override
                    protected void hookOnError(Throwable throwable) {
                        super.hookOnError(throwable);
                    }

                    @Override
                    protected void hookOnCancel() {
                        super.hookOnCancel();
                    }
                });
    }


    //Muestra el error de que más de 3 datos han sido proveídos y que el receiver 
    //no puede tomar más de la cantidad estipulada    
    @Test
    public void testBackPressureError() {
        var numbers = Flux.range(1,100).log();
        //numbers.subscribe(integer -> System.out.println("integer = " + integer));

        numbers
                .onBackpressureError()
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(3);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        System.out.println("value = " + value);
                        if(value ==3) hookOnCancel();
                    }

                    @Override
                    protected void hookOnComplete() {
                        System.out.println("Completed!!");
                    }

                    @Override
                    protected void hookOnError(Throwable throwable) {
                        System.out.println("throwable = " + throwable);
                    }

                    @Override
                    protected void hookOnCancel() {
                        super.hookOnCancel();
                    }
                });
    }
}
