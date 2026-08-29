import java.time.Duration;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * Step 10 — 3-minute stress test.
 *
 * Starts below Eldad's ceiling and ramps past it (100 → 800 concurrent)
 * so the graphs should show the same refuse behaviour he found (~350-450).
 * No think-time pause: closed-model users re-issue as soon as a request ends,
 * which is what actually stacks connections on a fast JSP.
 *
 * This job must still finish SUCCESS even when the app returns KO —
 * breaking the app is the point of a stress test.
 */
public class StressSimulation extends Simulation {

  private static final String BASE_URL =
      System.getenv().getOrDefault("APP_BASE_URL", "http://localhost:8080");
  private static final String APP_PATH =
      System.getenv().getOrDefault("APP_PATH", "/eldad-noam-nevo-itamar-amit/index.jsp");

  private HttpProtocolBuilder httpProtocol = http
      .baseUrl(BASE_URL)
      .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
      .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Gatling StressTest");

  private ScenarioBuilder scn = scenario("Step 10 Stress - ramp 100 to 800 concurrent")
      .exec(
          http("open_app")
              .get(APP_PATH + "?username=gatling")
              .check(status().is(200))
      );

  {
    setUp(
        scn.injectClosed(
            rampConcurrentUsers(100).to(800).during(Duration.ofMinutes(3))
        )
    ).protocols(httpProtocol);
  }
}
