import java.time.Duration;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * Step 9 — 3-minute load test.
 *
 * Stays below Eldad's measured ceiling (~350-450 concurrent).
 * 150 closed-model users + 1s think time keeps concurrency real without
 * flooding Tomcat the way a no-pause closed model would (a 2ms JSP would
 * otherwise generate tens of thousands of requests per second).
 */
public class LoadSimulation extends Simulation {

  private static final String BASE_URL =
      System.getenv().getOrDefault("APP_BASE_URL", "http://localhost:8080");
  private static final String APP_PATH =
      System.getenv().getOrDefault("APP_PATH", "/eldad-noam-nevo-itamar-amit/index.jsp");

  private HttpProtocolBuilder httpProtocol = http
      .baseUrl(BASE_URL)
      .shareConnections()
      .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
      .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Gatling LoadTest");

  private ScenarioBuilder scn = scenario("Step 9 Load - 150 concurrent for 3 minutes")
      .exec(
          http("open_app")
              .get(APP_PATH + "?username=gatling")
              .check(status().is(200))
      )
      .pause(Duration.ofSeconds(2));

  {
    setUp(
        scn.injectClosed(
            constantConcurrentUsers(150).during(Duration.ofMinutes(3))
        )
    ).protocols(httpProtocol)
        .assertions(
            global().failedRequests().percent().lt(1.0)
        );
  }
}
