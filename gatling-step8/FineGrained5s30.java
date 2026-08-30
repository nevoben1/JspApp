import java.time.Duration;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class FineGrained5s30 extends Simulation {

  private HttpProtocolBuilder httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Gatling FineGrained5s30");

  private ScenarioBuilder scn = scenario("Fine-Grained 30 every 5s")
    .exec(
      http("open_app")
        .get("/eldad_gatling_test/index.jsp?username=gatling")
    );

  {
    setUp(
      scn.injectOpen(
        atOnceUsers(300),
        nothingFor(Duration.ofSeconds(5)),
        atOnceUsers(330),
        nothingFor(Duration.ofSeconds(5)),
        atOnceUsers(360),
        nothingFor(Duration.ofSeconds(5)),
        atOnceUsers(390),
        nothingFor(Duration.ofSeconds(5)),
        atOnceUsers(420),
        nothingFor(Duration.ofSeconds(5)),
        atOnceUsers(450),
        nothingFor(Duration.ofSeconds(5)),
        atOnceUsers(480),
        nothingFor(Duration.ofSeconds(5)),
        atOnceUsers(510)
      )
    ).protocols(httpProtocol);
  }
}
