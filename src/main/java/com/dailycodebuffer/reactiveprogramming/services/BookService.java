package com.dailycodebuffer.reactiveprogramming.services;

import com.dailycodebuffer.reactiveprogramming.domain.Book;
import com.dailycodebuffer.reactiveprogramming.domain.Review;
import com.dailycodebuffer.reactiveprogramming.exception.BookException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.List;

@Slf4j
public class BookService {

    private BookInfoService bookInfoService;
    private ReviewService reviewService;

    public BookService(BookInfoService bookInfoService, ReviewService reviewService) {
        this.bookInfoService = bookInfoService;
        this.reviewService = reviewService;
    }

    public Flux<Book> getBooks() {
        //en realidad esta variable se debería llamar allBooksInfo
        var allBooks = bookInfoService.getBooks();
        return allBooks
                .flatMap(bookInfo -> {
                        //Me traigo con el id del libro todos los review que vienen en la forma
                        //de un flux de review y con collectList los convierto a Lista
                    Mono<List<Review>> reviews =
                            reviewService.getReviews(bookInfo.getBookId()).collectList();
                    return reviews
                            .map(review -> new Book(bookInfo,review));
                })
                .onErrorMap(throwable -> {
                    log.error("Exception is :" + throwable);
                    return new BookException("Exception occurred while fetching Books");
                })
                .log();
    }

    public Flux<Book> getBooksRetry() {
        var allBooks = bookInfoService.getBooks();
        return allBooks
                .flatMap(bookInfo -> {
                    Mono<List<Review>> reviews =
                            reviewService.getReviews(bookInfo.getBookId()).collectList();
                    return reviews
                            .map(review -> new Book(bookInfo,review));
                })
                .onErrorMap(throwable -> {
                    log.error("Exception is :" + throwable);
                    return new BookException("Exception occurred while fetching Books");
                })
                .retry(3)
                .log();
    }

    public Flux<Book> getBooksRetryWhen() {
        //var retrySpecs = getRetryBackoffSpec();
        var allBooks = bookInfoService.getBooks();
        return allBooks
                .flatMap(bookInfo -> {
                    Mono<List<Review>> reviews =
                            reviewService.getReviews(bookInfo.getBookId()).collectList();
                    return reviews
                            .map(review -> new Book(bookInfo,review));
                })
                .onErrorMap(throwable -> {
                    log.error("Exception is :" + throwable);
                    return new BookException("Exception occurred while fetching Books");
                })
                .retryWhen(getRetryBackoffSpec())
                .log();
    }

    private RetryBackoffSpec getRetryBackoffSpec() {
        return Retry.backoff(
                3,
                Duration.ofMillis(1000)
        ).filter(throwable -> throwable instanceof BookException)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                        Exceptions.propagate(retrySignal.failure())
                );
    }

        //El llamado a bookInfoService trae un mono y el reviewService trae un Flux, 
        //el zipWith me une en el orden que le indico 
    public Mono<Book> getBookById(long bookId) {
        var book = bookInfoService.getBookById(bookId);
        var review = reviewService
                .getReviews(bookId)
                .collectList();

        return  book
                .zipWith(review,(b,r) -> new Book(b,r));

    }
}
