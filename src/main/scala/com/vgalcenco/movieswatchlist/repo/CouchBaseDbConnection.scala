package com.vgalcenco.movieswatchlist.repo

import com.couchbase.client.java.CouchbaseCluster
import com.vgalcenco.movieswatchlist.repo.CouchBaseDbConnection._
/**
  * Created by vgalcenco on 3/29/17.
  */

private object CouchBaseDbConnection {
  val cbCluster = CouchbaseCluster.create()
}

trait CouchBaseDbConnection {

  def asynchBucket(bucketName: String) = cbCluster.openBucket(bucketName).async()

  def bucket(bucketName: String) = cbCluster.openBucket(bucketName)

}
