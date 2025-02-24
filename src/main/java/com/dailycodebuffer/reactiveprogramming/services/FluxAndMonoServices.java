package com.dailycodebuffer.reactiveprogramming.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FluxAndMonoServices {

    //con el método log() se puede ver en consola todos los eventos de reactive streams como
    //la subscripción, el request unbounded, todos los onNext y el onComplete
    public Flux<String> fruitsFlux() {
        return Flux.fromIterable(List.of("Mango","Orange","Banana")).log();
    }

    //Map para mapear la data en una forma diferente de data
    public Flux<String> fruitsFluxMap() {
        return Flux.fromIterable(List.of("Mango","Orange","Banana"))
                .map(String::toUpperCase);
    }

    public Flux<String> fruitsFluxFilter(int number) {
        return Flux.fromIterable(List.of("Mango","Orange","Banana"))
                .filter(s -> s.length() > number);
    }

    //Combinación de operadores, primero filter y luego map
    public Flux<String> fruitsFluxFilterMap(int number) {
        return Flux.fromIterable(List.of("Mango","Orange","Banana"))
                .filter(s -> s.length() > number)
                .map(String::toUpperCase);
    }

    //FlatMap me convierte cada elemento emitido en un flujo, por eso este método en el test
    //tiene 17  en el count de onNext. 17 flujos en total para el split de todas las letras.
    //El flatMap me pide que retorne otro flujo de datos, no el dato como tal. Esto se utiliza
    //por ejemplo cuando en llamados a bases de datos, lo que yo estoy obteniendo son flujos de
    //la información encontrada y necesito aplanarlo. 
    //Dicho de otra forma flatMap se usa cuando lo que me llega es un flujo externo 
    //(Un publisher) (Asincrono) y necesito aplanarlo
    public Flux<String> fruitsFluxFlatMap() {
        return Flux.fromIterable(List.of("Mango","Orange","Banana"))
                .flatMap(s -> Flux.just(s.split("")))
                .log();
    }

    //Mismo método de flatMap arriba pero probando que es algo asíncrono, porque cada elemento
    //va llegando según el delay que le haya tocado de manera random
    public Flux<String> fruitsFluxFlatMapAsync() {
        return Flux.fromIterable(List.of("Mango","Orange","Banana"))
                .flatMap(s -> Flux.just(s.split(""))
                .delayElements(Duration.ofMillis(
                        new Random().nextInt(1000)
                )))
                .log();
    }

    //La diferencia con flatMap es que concatMap va a mantener el orden de los elementos
    //emitidos así se tenga un delay diferente para cada elemento emitido
    public Flux<String> fruitsFluxConcatMap() {
        return Flux.fromIterable(List.of("Mango","Orange","Banana"))
                .concatMap(s -> Flux.just(s.split(""))
                        .delayElements(Duration.ofMillis(
                                new Random().nextInt(1000)
                        )))
                .log();
    }

    //Como con flatMap se tiene que devolver un flujo en este caso un Mono se tienen que 
    //meter todas las letras del split dentro de una lista
    public Mono<List<String>> fruitMonoFlatMap() {
        return Mono.just("Mango")
                .flatMap(s -> Mono.just(List.of(s.split(""))))
                .log();
    }

    //Se usa FLatMapMany  cuando se quiere hacer flatMap sobre un Mono pero se quiere 
    //devolver un Flux y no un mono. Es decir convertir el Mono en un Flux
    public Flux<String> fruitMonoFlatMapMany() {
        return Mono.just("Mango")
                .flatMapMany(s -> Flux.just(s.split("")))
                .log();
    }


    //Para cambiar de un tipo a otro
    public Flux<String> fruitsFluxTransform(int number) {

        //Se convirtió el filtro que está comentado abajo en una variable. Es una función que 
        //recibe un flux de string y devuelve lo mismo. Esa variable se puede usar en 
        //múltiples lugares
        Function<Flux<String>,Flux<String>> filterData
                = data -> data.filter(s -> s.length() > number);

        return Flux.fromIterable(List.of("Mango","Orange","Banana"))
                .transform(filterData)
                .log();
                //.filter(s -> s.length() > number);
    }

    public Flux<String> fruitsFluxTransformDefaultIfEmpty(int number) {

        Function<Flux<String>,Flux<String>> filterData
                = data -> data.filter(s -> s.length() > number);

        return Flux.fromIterable(List.of("Mango","Orange","Banana"))
                .transform(filterData)
                .defaultIfEmpty("Default")
                .log();

    }

    public Flux<String> fruitsFluxTransformSwitchIfEmpty(int number) {

        Function<Flux<String>,Flux<String>> filterData
                = data -> data.filter(s -> s.length() > number);

        return Flux.fromIterable(List.of("Mango","Orange","Banana"))
                .transform(filterData)
                .switchIfEmpty(Flux.just("Pineapple","Jack Fruit")
                            .transform(filterData))
                .log();

    }

    //Para concatenar dos flujos. concat Es un método estático de la clase Flux
    public Flux<String> fruitsFluxConcat() {
        var fruits = Flux.just("Mango","Orange");
        var veggies = Flux.just("Tomato","Lemon");

        return Flux.concat(fruits,veggies);
    }

    //concatWith es un método de instancia del Flux. No es estático
    public Flux<String> fruitsFluxConcatWith() {
        var fruits = Flux.just("Mango","Orange");
        var veggies = Flux.just("Tomato","Lemon");

        return fruits.concatWith(veggies);
    }


    public Flux<String> fruitsMonoConcatWith() {
        var fruits = Mono.just("Mango");
        var veggies = Mono.just("Tomato");

        return fruits.concatWith(veggies);
    }

    //Respeta el orden en que se emiten los elementos, en este caso se espera Mango, Orange
    //Tomato y por último Lemon. Método Estático
    public Flux<String> fruitsFluxMerge() {
        var fruits = Flux.just("Mango","Orange")
                .delayElements(Duration.ofMillis(50));
        var veggies = Flux.just("Tomato","Lemon")
                .delayElements(Duration.ofMillis(75));

        return Flux.merge(fruits,veggies);
    }

    //Método de instancia
    public Flux<String> fruitsFluxMergeWith() {
        var fruits = Flux.just("Mango","Orange")
                .delayElements(Duration.ofMillis(50));
        var veggies = Flux.just("Tomato","Lemon")
                .delayElements(Duration.ofMillis(75));

        return fruits.mergeWith(veggies);
    }


    //Método estático. Emite la data de manera secuencial. Es decir en orden sin importar 
    //el delay
    public Flux<String> fruitsFluxMergeWithSequential() {
        var fruits = Flux.just("Mango","Orange")
                .delayElements(Duration.ofMillis(50));
        var veggies = Flux.just("Tomato","Lemon")
                .delayElements(Duration.ofMillis(75));

        return Flux.mergeSequential(fruits,veggies);
    }

    //Me concatena los valores de los dos flujos, toma una función me va concatenando
    //así: MontoTomato, OrangeLemon. El método toma hasta 8 combinaciones
    public Flux<String> fruitsFluxZip() {
        var fruits = Flux.just("Mango","Orange");
        var veggies = Flux.just("Tomato","Lemon");

        return Flux.zip(fruits,veggies,
                (first,second) -> first+second).log();
    }

    //Mismo de arriba pero método de instancia
    public Flux<String> fruitsFluxZipWith() {
        var fruits = Flux.just("Mango","Orange");
        var veggies = Flux.just("Tomato","Lemon");

        return fruits.zipWith(veggies,
                (first,second) -> first+second).log();
    }

    //Cuando se va a hacer un zip de más de dos publishers se debe hacer un map sobre 
    //la data para modificarla y se usan los métodos getT
    public Flux<String> fruitsFluxZipTuple() {
        var fruits = Flux.just("Mango","Orange");
        var veggies = Flux.just("Tomato","Lemon");
        var moreVeggies = Flux.just("Potato","Beans");

        return Flux.zip(fruits,veggies,moreVeggies)
                .map(objects -> objects.getT1() + objects.getT2() + objects.getT3());
    }

    public Mono<String> fruitsMonoZipWith() {
        var fruits = Mono.just("Mango");
        var veggies = Mono.just("Tomato");

        return fruits.zipWith(veggies,
                (first,second) -> first+second).log();
    }


    public Mono<String> fruitMono() {
        return Mono.just("Mango").log();
    }


    //Cosas que se hacen como side effects luego de que pasan los eventos de la subscripción
    //como OnNext, OnSubscribe, etc.
    public Flux<String> fruitsFluxFilterDoOn(int number) {
        return Flux.fromIterable(List.of("Mango","Orange","Banana"))
                .filter(s -> s.length() > number)
                .doOnNext(s -> {
                    System.out.println("s = " + s);
                })
                .doOnSubscribe(subscription -> {
                    System.out.println("subscription.toString() = " + subscription.toString());
                })
                .doOnComplete(() -> System.out.println("Completed!!!"));
    }

    //En un caso real el error vendría del fallo real, acá se está generando el error 
    //manualmente para la demostración
    public Flux<String> fruitsFluxOnErrorReturn() {
        return Flux.just("Apple","Mango")
                .concatWith(Flux.error(
                        new RuntimeException("Exception Occurred")
                ))
                .onErrorReturn("Orange");
    }

    //Cuando el error pasa va a seguir con el procesamiento y va a excluir la data que está 
    //causando el error, en este caso ignora Mango. f en este caso es el objeto que causó 
    //el error, e es el error.
    public Flux<String> fruitsFluxOnErrorContinue() {
        return Flux.just("Apple","Mango","Orange")
                .map(s -> {
                    if (s.equalsIgnoreCase("Mango"))
                        throw new RuntimeException("Exception Occurred");
                    return s.toUpperCase();
                })
                .onErrorContinue((e,f) -> {
                    System.out.println("e = " + e);
                    System.out.println("f = " + f);
                });
    }


    //Para mapear la excepción a una excepción personalizada que tenga. Una vez pasa el error
    //ya no se emiten más elementos del flujo
    public Flux<String> fruitsFluxOnErrorMap() {
        return Flux.just("Apple","Mango","Orange")
                .checkpoint("Error Checkpoint1")
                .map(s -> {
                    if (s.equalsIgnoreCase("Mango"))
                        throw new RuntimeException("Exception Occurred");
                    return s.toUpperCase();
                })
                .checkpoint("Error Checkpoint2")
                .onErrorMap(throwable -> {
                    System.out.println("throwable = " + throwable);
                    return new IllegalStateException("From onError Map");
                });
    }

    public Flux<String> fruitsFluxOnError() {
        return Flux.just("Apple","Mango","Orange")
                .map(s -> {
                    if (s.equalsIgnoreCase("Mango"))
                        throw new RuntimeException("Exception Occurred");
                    return s.toUpperCase();
                })
                .doOnError(throwable -> {
                    System.out.println("throwable = " + throwable);

                });
    }



    public static void main(String[] args) {

        FluxAndMonoServices fluxAndMonoServices
                = new FluxAndMonoServices();

        fluxAndMonoServices.fruitsFlux()
                .subscribe(s -> {
                    System.out.println("s = " + s);
                });

        fluxAndMonoServices.fruitMono()
                .subscribe(s -> {
                    System.out.println("Mono -> s = " + s);
                });
    }
}
