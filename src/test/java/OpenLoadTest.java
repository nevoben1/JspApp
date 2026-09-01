import java.time.Duration;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * Sustained load test at 90% of the discovered max capacity, using the
 * OPEN injection model (rampUsers), as an alternative to ClosedLoadTest.
 *
 * MaxLimitSimulation found the app's ceiling to be ~8500 users.
 * This test ramps up to 90% of that (7650 users) and holds sustained
 * load there, to confirm the app performs within SLA at a realistic
 * "near-max but not breaking" operating point rather than at the
 * absolute edge of failure.
 *
 * Open model peak-concurrency rule: with rampUsers(...).during(rampTime)
 * and a scenario hold of scn.during(holdTime), peak concurrency only
 * reaches the full injected total when holdTime >= rampTime (otherwise
 * early arrivals expire before the ramp finishes and peak concurrency
 * caps out at roughly (users / rampTime) * holdTime).
 *
 * Here: 60s ramp, 120s hold (hold >= ramp) so peak hits TARGET_USERS,
 * then a ~60s taper as users expire in arrival order. 60 + 120 = 180s
 * (3 min) total, matching ClosedLoadTest's overall runtime.
 */
public class OpenLoadTest extends Simulation {

  private static final int MAX_USERS_FOUND = 8500;
  private static final int TARGET_USERS = (int) (MAX_USERS_FOUND * 0.9); // 7650

  private HttpProtocolBuilder httpProtocol = http
    .baseUrl("http://localhost:8081")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Gatling LoadTest");

  // Each virtual user holds for 120s once it arrives. Since this hold is
  // >= the 60s ramp below, no user expires before the ramp completes, so
  // peak concurrency actually reaches TARGET_USERS.
  private ScenarioBuilder scn = scenario("90% of Max Load (" + TARGET_USERS + " users)")
    .during(Duration.ofSeconds(120)).on(
      exec(
        http("open_app")
          .get("/eldad-noam-nevo-itamar-amit/index.jsp?username=gatling")
      )
    );

  {
    setUp(
      scn.injectOpen(
        // Open model: controls arrival rate. Peak concurrency reaches
        // TARGET_USERS because the scenario's 120s hold >= this 60s ramp.
        rampUsers(TARGET_USERS).during(Duration.ofSeconds(60))
      )
    )
    .protocols(httpProtocol)
    // At 90% of max this should stay within SLA — if it doesn't, the
    // "max" found earlier was likely already past a safe operating point.
    .assertions(
      global().failedRequests().percent().lt(1.0),
      global().responseTime().percentile3().lt(2000), // p95 < 2000ms
      global().responseTime().max().lt(5000)
    );
  }
}
