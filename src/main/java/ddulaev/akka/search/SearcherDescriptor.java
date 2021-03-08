package ddulaev.akka.search;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearcherDescriptor {
    private String host;
    private int port;
    private String engine;
}
