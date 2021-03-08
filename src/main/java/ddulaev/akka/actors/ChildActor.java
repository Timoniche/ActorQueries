package ddulaev.akka.actors;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import ddulaev.akka.actors.results.ChildActorResult;
import akka.actor.AbstractActor;
import lombok.RequiredArgsConstructor;
import ddulaev.akka.search.SearchQuery;
import ddulaev.akka.search.SearcherDescriptor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ChildActor extends AbstractActor {
    private final SearcherDescriptor searcherDescriptor;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SearchQuery.class, this::processQuery)
                .build();
    }

    private void processQuery(SearchQuery query) {
        log.info("Query '{}' to engine '{}' with port '{}'", query.getQuery(), searcherDescriptor.getEngine(),
                searcherDescriptor.getPort());

        URI uri = URI.create(String.format("http://%s:%d/search?q=%s",
                searcherDescriptor.getHost(),
                searcherDescriptor.getPort(),
                query.getQuery()));
        log.debug("URI is '{}'", uri.getQuery());

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .build();
        try {
            String response = client
                    .send(request, HttpResponse.BodyHandlers.ofString())
                    .body()
                    .intern();
            sender().tell(new ChildActorResult(response, searcherDescriptor), getSelf());
        } catch (Throwable ignored) {
        }
    }
}
