package actors.results;

import java.util.HashMap;

import lombok.Data;

@Data
public class MasterActorResult {
    private HashMap<String, String> engineResultMap;
}
