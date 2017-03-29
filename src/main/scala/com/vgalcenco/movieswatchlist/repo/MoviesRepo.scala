package com.vgalcenco.movieswatchlist.repo

import rx.lang.scala.JavaConversions._
import rx.lang.scala.Observable

/**
  * Created by vgalcenco on 3/29/17.
  */

object MoviesRepo extends MoviesRepo {

}

class MoviesRepo extends CouchBaseDbConnection {

  def findById(id: String) = toScalaObservable(asynchBucket("imdb").get(id)).toBlocking.toFuture

  def findMoviesById(ids: Seq[String]) =
    Observable.from(ids)
      .flatMap(id => asynchBucket("watchlist").get(id))
      .toIterable.toBlocking.toFuture

  def findWatchlistById(userId: String) = toScalaObservable(asynchBucket("watchlist").get(userId)).toBlocking.toFuture

}
