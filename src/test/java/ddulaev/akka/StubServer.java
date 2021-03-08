package ddulaev.akka;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.mockserver.integration.ClientAndServer;
import scala.concurrent.duration.Duration;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.HttpStatusCode.OK_200;

public class StubServer implements AutoCloseable {
    private final ClientAndServer stubServer;

    public StubServer(int port, Duration timeout) {
        stubServer = startClientAndServer(port);
        stubServer.when(request()
                .withMethod("GET")
                .withPath("/search")
        ).respond(
                request -> {
                    long timeoutMillis = timeout.toMillis();
                    if (timeoutMillis > 0) {
                        Thread.sleep(timeoutMillis);
                    }
                    String queryName = request.getFirstQueryStringParameter("q");
                    String responseBody = genResponseBody(queryName);
                    return response()
                            .withStatusCode(OK_200.code())
                            .withBody(responseBody);
                }
        );
    }

    @Override
    public void close() {
        stubServer.close();
    }

    private String genResponseBody(String queryName) {
        int linksCount = 5;
        return IntStream.range(1, 1 + linksCount)
                .mapToObj(i -> queryName.concat(Integer.toString(i)))
                .collect(Collectors.joining("\n"));
    }
}
