package ddulaev.akka.actors.results;

import lombok.AllArgsConstructor;
import lombok.Data;
import ddulaev.akka.search.SearcherDescriptor;

@Data
@AllArgsConstructor
public class ChildActorResult {
    private String response;
    private SearcherDescriptor searcher;
}
