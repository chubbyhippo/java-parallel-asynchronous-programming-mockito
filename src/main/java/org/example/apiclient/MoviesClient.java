package org.example.apiclient;

import lombok.RequiredArgsConstructor;
import org.example.domain.movie.Movie;
import org.example.domain.movie.MovieInfo;
import org.example.domain.movie.Review;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class MoviesClient {
    private final WebClient webClient;

    public Movie retrieveMovie(Long movieInfoId) {
        MovieInfo movieInfo = invokeMovieInfoService(movieInfoId);
        List<Review> reviews = invokeMovieReviewsService(movieInfoId);
        return new Movie(movieInfo, reviews);
    }

    public List<Movie> retrieveMovies(List<Long> movieInfoIds) {
        return movieInfoIds
                .stream()
                .map(this::retrieveMovie)
                .toList();
    }

    public CompletableFuture<Movie> retrieveMovieCompletableFuture(Long movieInfoId) {
        var movieInfo = CompletableFuture.supplyAsync(() -> invokeMovieInfoService(movieInfoId));
        var reviews = CompletableFuture.supplyAsync(() -> invokeMovieReviewsService(movieInfoId));
        return movieInfo.thenCombine(reviews, Movie::new);
    }

    public List<Movie> retrieveMoviesCompletableFuture(List<Long> movieInfoIds) {
        return movieInfoIds.stream()
                .map(this::retrieveMovieCompletableFuture)
                .toList()
                .stream()
                .map(CompletableFuture::join)
                .toList();
    }

    public List<Movie> retrieveMoviesCompletableFutureAllOf(List<Long> movieInfoIds) {
        List<CompletableFuture<Movie>> movieFutures = movieInfoIds.stream()
                .map(this::retrieveMovieCompletableFuture)
                .toList();

        var completableFutures = CompletableFuture
                .allOf(movieFutures.toArray(new CompletableFuture[0]));

        return completableFutures.thenApply(unused -> movieFutures
                .stream()
                .map(CompletableFuture::join)
                .toList()).join();
    }

    private MovieInfo invokeMovieInfoService(Long movieInfoId) {
        var moviesInfoUrlPath = "/v1/movie_infos/{movieInfoId}";
        return webClient
                .get()
                .uri(moviesInfoUrlPath, movieInfoId)
                .retrieve()
                .bodyToMono(MovieInfo.class)
                .block();
    }

    private List<Review> invokeMovieReviewsService(Long movieInfoId) {
        String reviewUri = UriComponentsBuilder
                .fromUriString("/v1/reviews")
                .queryParam("movieInfoId", movieInfoId)
                .buildAndExpand()
                .toString();

        return webClient
                .get()
                .uri(reviewUri)
                .retrieve()
                .bodyToFlux(Review.class)
                .collectList()
                .block();
    }
}
