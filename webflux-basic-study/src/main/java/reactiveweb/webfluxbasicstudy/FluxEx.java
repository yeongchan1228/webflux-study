package reactiveweb.webfluxbasicstudy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.*;
import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by YC on 2022/09/30.
 */
@Slf4j
public class FluxEx {
    @RestController
    public static class FluxExampleController {
        @GetMapping("/flux/ex1")
        public Flux<Event> fluxEx1() {
            return Flux.just(
                    new Event(1, "event1"),
                    new Event(2, "event2")
                    );
        }

        @GetMapping("/mono/ex1")
        public Mono<List<Event>> monoEx1() {
            return Mono.just(
                    List.of(
                        new Event(1, "event1"),
                        new Event(2, "event2")
                    )
            );
        }

        @GetMapping("/flux/ex2")
        public Flux<Event> fluxEx2() {
            List<Event> events = List.of(new Event(1, "event1"), new Event(2, "event2"));
            return Flux.fromIterable(events);
        }

        @GetMapping(value = "/flux/ex3", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
        public Flux<Event> fluxEx3() {
            List<Event> events = List.of(new Event(1, "event1"), new Event(2, "event2"));
            return Flux.fromIterable(events);
        }

        @GetMapping(value = "/flux/ex4", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
        public Flux<Event> fluxEx4() {
            return Flux
                    .fromStream(Stream.generate(() -> new Event(System.currentTimeMillis(), "value")))
                    .take(10);
        }

        @GetMapping(value = "/flux/ex5", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
        public Flux<Event> fluxEx5() {
            return Flux
                    .fromStream(Stream.generate(() -> new Event(System.currentTimeMillis(), "value")))
                    .delayElements(Duration.ofSeconds(1))
                    .take(10);
        }

        @GetMapping(value = "/flux/ex6", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
        public Flux<Event> fluxEx6() {
            return Flux
                    .<Event, Long>generate(() -> 1L, (id, sink) -> {
                        sink.next(new Event(id, "value" + id));
                        return ++id;
                    })
                    .delayElements(Duration.ofSeconds(1))
                    .take(10);
        }

        @GetMapping(value = "/flux/ex7", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
        public Flux<Event> fluxEx7() {
            Flux<String> flux1 = Flux
                    .generate(sink -> sink.next("value"));

            Flux<Long> flux2 = Flux.interval(Duration.ofSeconds(1));

            return Flux.zip(flux1, flux2)
                    .map(tuple -> new Event(tuple.getT2(), tuple.getT1() + String.valueOf(tuple.getT2())))
                    .take(10);
        }
    }

    @Data
    @AllArgsConstructor
    public static class Event {
        long id;
        String value;
    }
}
