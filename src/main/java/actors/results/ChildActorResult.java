package actors.results;

import lombok.AllArgsConstructor;
import lombok.Data;
import search.SearcherDescriptor;

@Data
@AllArgsConstructor
public class ChildActorResult {
    private String response;
    private SearcherDescriptor searcher;
}
