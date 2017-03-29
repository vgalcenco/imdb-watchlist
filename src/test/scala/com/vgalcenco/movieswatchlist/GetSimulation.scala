package com.vgalcenco.movieswatchlist

import io.gatling.commons.util.RoundRobin
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.io.Source
import scala.concurrent.duration._

/**
  * Created by vgalcenco on 3/29/17.
  */
class GetSimulation extends Simulation {

  val httpConf = http
    .baseURL("http://localhost:9000")
    .acceptHeader("text/html,application/xhtml+xml,application/xml,application/json;q=0.9,*/*;q=0.8") // Here are the common headers
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val feeder = RoundRobin(Source.fromInputStream(classOf[GetSimulation].getResourceAsStream("/data.csv")).getLines().map(id => Map("movieId" -> id)).toIndexedSeq)

  val scnFindUsers = scenario("Find Users with Circular feed")
    .during(120 seconds) {
      feed(feeder)
        .exec(
          http("Find Movie")
            .get("/movie/${movieId}")
        )
    }

  setUp(
    scnFindUsers.inject(atOnceUsers(60)).protocols(httpConf)
  )
}
