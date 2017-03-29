package com.vgalcenco.movieswatchlist

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.{Config, ConfigFactory}
import com.vgalcenco.movieswatchlist.repo.MoviesRepo

import scala.concurrent.ExecutionContextExecutor

/**
  * Created by vgalcenco on 3/29/17.
  */
object RunApp extends App with Service {


    override implicit val system = ActorSystem()
    override implicit val executor = system.dispatcher
    override implicit val materializer = ActorMaterializer()

    override val config = ConfigFactory.load()
    override val logger = Logging(system, getClass)

    Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))

}

trait Service {
  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def config: Config
  val logger: LoggingAdapter

  val routes =
    pathPrefix("movie") {
      (get & path(Segment)) { id =>
        complete {
          MoviesRepo.findById(id).map(_.content().toString)
        }
      }
    } ~ pathPrefix("watchlist") {
    (get & path(Segment)) { id =>
      complete {
        MoviesRepo.findById(id).map(_.content().toString)
      }
    }
  }
}