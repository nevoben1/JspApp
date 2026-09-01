import java.time.Duration;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class StressSim extends Simulation {

  private HttpProtocolBuilder httpProtocol = http
    .baseUrl("http://localhost:8081")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Gatling MaxLimitTest");

  // Each virtual user hits the endpoint repeatedly (with think time) for the
  // duration of its level, instead of firing once and disappearing. This
  // measures sustained load rather than a single instantaneous burst.
  private ScenarioBuilder scn = scenario("Find Max Limit")
    .during(Duration.ofSeconds(30)).on(
      exec(
        http("open_app")
          .get("/eldad-noam-nevo-itamar-amit/index.jsp?username=gatling")
      )
      //.pause(Duration.ofSeconds(1), Duration.ofSeconds(3)) // think time between requests
    );

  {
    setUp(
      scn.injectOpen(
        // Smooth, staged ramp instead of instantaneous bursts.
        // Each level ramps up over 20s, then holds/loops (via .during above)
        // for 30s per user, giving the server time to reach a steady state
        // and giving you time to observe degradation before the next level.
        rampUsers(4000).during(Duration.ofSeconds(20)),
        nothingFor(Duration.ofSeconds(15)),

        rampUsers(8000).during(Duration.ofSeconds(20)),
        nothingFor(Duration.ofSeconds(15)),

        rampUsers(12000).during(Duration.ofSeconds(20)),
        nothingFor(Duration.ofSeconds(15)),

        rampUsers(15000).during(Duration.ofSeconds(30)),
        nothingFor(Duration.ofSeconds(20))

        
      )
    )
    .protocols(httpProtocol)
    // Assertions give you an objective, repeatable definition of "broke here"
    // instead of eyeballing the report. Tune thresholds to your SLA.
    .assertions(
      global().failedRequests().percent().lt(1.0),
      global().responseTime().percentile3().lt(2000), // p95 < 2000ms
      global().responseTime().max().lt(5000)
    );
  }
}