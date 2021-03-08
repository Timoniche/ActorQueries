package ddulaev.akka.actors.results;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MasterActorResult {
    private HashMap<String, String> engineResultMap;

    public MasterActorResult() {
        this.engineResultMap = new HashMap<>();
    }
}
