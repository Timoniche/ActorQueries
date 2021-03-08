package actors;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import actors.results.ChildActorResult;
import akka.actor.AbstractActor;
import lombok.RequiredArgsConstructor;
import search.SearchQuery;
import search.SearcherDescriptor;

@RequiredArgsConstructor
public class ChildActor extends AbstractActor {
    private final SearcherDescriptor searcherDescriptor;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SearchQuery.class, this::processQuery)
                .build();
    }

    private void processQuery(SearchQuery query) {
        URI uri = URI.create(String.format("http://%s:%d/search?q=%s",
                searcherDescriptor.getHost(),
                searcherDescriptor.getPort(),
                searcherDescriptor.getEngine()));

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
