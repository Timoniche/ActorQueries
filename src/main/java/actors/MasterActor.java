package actors;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import actors.results.ChildActorResult;
import actors.results.MasterActorResult;
import akka.actor.AbstractActor;

import akka.actor.Props;
import akka.actor.ReceiveTimeout;
import lombok.Getter;
import scala.concurrent.duration.Duration;
import search.SearchQuery;
import search.SearcherDescriptor;

@Getter
public class MasterActor extends AbstractActor {
    private final List<SearcherDescriptor> searcherDescriptors;
    private final MasterActorResult result;
    private final CompletableFuture<MasterActorResult> futureResult;

    public MasterActor(List<SearcherDescriptor> searcherDescriptors,
                       CompletableFuture<MasterActorResult> futureResult,
                       Duration duration) {
        this.searcherDescriptors = searcherDescriptors;
        this.futureResult = futureResult;
        result = new MasterActorResult();

        this.getContext().setReceiveTimeout(duration);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SearchQuery.class, this::sendQueryToChildren)
                .match(ChildActorResult.class, this::collectChildResult)
                .match(ReceiveTimeout.class, ignored -> completeResultAndStop())
                .build();
    }

    private void sendQueryToChildren(SearchQuery query) {
        searcherDescriptors.forEach(searcherDescriptor ->
                getContext()
                        .actorOf(Props.create(ChildActor.class, searcherDescriptor))
                        .tell(query, getSelf())
        );
    }

    private void collectChildResult(ChildActorResult childResult) {
        result.getEngineResultMap().put(
                childResult.getSearcher().getEngine(),
                childResult.getResponse()
        );
        if (result.getEngineResultMap().size() == searcherDescriptors.size()) {
            completeResultAndStop();
        }
    }

    private void completeResultAndStop() {
        futureResult.complete(result);
        getContext().system().stop(getSelf());
    }
}
