package ddulaev.akka;


import java.util.concurrent.TimeUnit;

import ddulaev.akka.search.SearcherDescriptor;
import org.junit.Test;
import scala.concurrent.duration.Duration;

import static org.junit.Assert.assertEquals;

public class ActorsTest {
    private final String LOCAL_HOST = "127.0.0.1";
    private final int PORT_1 = 1234;
    private final int PORT_2 = 2345;

    private final Duration ZERO_TIMEOUT = Duration.create(0, TimeUnit.MILLISECONDS);
    private final Duration ONE_SEC_TIMEOUT = Duration.create(1, TimeUnit.SECONDS);
    private final Duration TWO_SEC_TIMEOUT = Duration.create(2, TimeUnit.SECONDS);

    @Test
    public void oneEngineOneQueryTest() {
        try (StubServer server = new StubServer(PORT_1, ZERO_TIMEOUT)) {
            ActorQueriesService searcher = new ActorQueriesService();
            searcher.addDescriptor(new SearcherDescriptor(LOCAL_HOST, PORT_1, "Yandex"));
            var response = searcher.search("some_query", ONE_SEC_TIMEOUT);
            System.out.println(response);
            assertEquals(1, response.size());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
