package ddulaev.akka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import ddulaev.akka.actors.MasterActor;
import ddulaev.akka.actors.results.MasterActorResult;
import ddulaev.akka.search.SearchQuery;
import ddulaev.akka.search.SearcherDescriptor;
import scala.concurrent.duration.Duration;

public class ActorQueriesService {
    private final List<SearcherDescriptor> searcherDescriptors;

    public ActorQueriesService() {
        this.searcherDescriptors = new ArrayList<>();
    }

    public void addDescriptor(SearcherDescriptor searcherDescriptor) {
        searcherDescriptors.add(searcherDescriptor);
    }

    public HashMap<String, String> search(String queryName, Duration timeout) throws ExecutionException,
            InterruptedException {
        ActorSystem actorSystem = ActorSystem.create("ActorQueries");
        CompletableFuture<MasterActorResult> futureResult = new CompletableFuture<>();
        try {
            ActorRef master = actorSystem.actorOf(Props.create(MasterActor.class,
                    searcherDescriptors,
                    futureResult,
                    timeout
                    )
            );
            master.tell(new SearchQuery(queryName), ActorRef.noSender());
            return futureResult.get().getEngineResultMap();
        } finally {
            actorSystem.terminate();
        }
    }
}
