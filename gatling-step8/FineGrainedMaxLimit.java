import java.time.Duration;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class FineGrainedMaxLimit extends Simulation {

  private HttpProtocolBuilder httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Gatling FineGrainedTest");

  private ScenarioBuilder scn = scenario("Fine-Grained Max Limit")
    .exec(
      http("open_app")
        .get("/eldad_gatling_test/index.jsp?username=gatling")
    );

  {
    setUp(
      scn.injectOpen(
        atOnceUsers(300),
        nothingFor(Duration.ofSeconds(3)),
        atOnceUsers(320),
        nothingFor(Duration.ofSeconds(3)),
        atOnceUsers(340),
        nothingFor(Duration.ofSeconds(3)),
        atOnceUsers(360),
        nothingFor(Duration.ofSeconds(3)),
        atOnceUsers(380),
        nothingFor(Duration.ofSeconds(3)),
        atOnceUsers(400),
        nothingFor(Duration.ofSeconds(3)),
        atOnceUsers(420),
        nothingFor(Duration.ofSeconds(3)),
        atOnceUsers(440),
        nothingFor(Duration.ofSeconds(3)),
        atOnceUsers(460),
        nothingFor(Duration.ofSeconds(3)),
        atOnceUsers(480),
        nothingFor(Duration.ofSeconds(3)),
        atOnceUsers(500)
      )
    ).protocols(httpProtocol);
  }
}
