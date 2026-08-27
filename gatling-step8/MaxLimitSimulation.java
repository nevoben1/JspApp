import java.time.Duration;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class MaxLimitSimulation extends Simulation {

  private HttpProtocolBuilder httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Gatling MaxLimitTest");

  private ScenarioBuilder scn = scenario("Find Max Limit")
    .exec(
      http("open_app")
        .get("/eldad_gatling_test/index.jsp?username=gatling")
    );

  {
    setUp(
      scn.injectOpen(
        atOnceUsers(100),
        nothingFor(Duration.ofSeconds(8)),
        atOnceUsers(300),
        nothingFor(Duration.ofSeconds(8)),
        atOnceUsers(600),
        nothingFor(Duration.ofSeconds(8)),
        atOnceUsers(1000),
        nothingFor(Duration.ofSeconds(8)),
        atOnceUsers(3000),
        nothingFor(Duration.ofSeconds(8)),
        atOnceUsers(6000)
      )
    ).protocols(httpProtocol);
  }
}
