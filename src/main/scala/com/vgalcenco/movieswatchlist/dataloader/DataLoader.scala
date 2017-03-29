package com.vgalcenco.movieswatchlist.dataloader

import com.couchbase.client.java.CouchbaseCluster
import com.couchbase.client.java.document.JsonDocument
import com.couchbase.client.java.document.json.JsonArray.from
import com.couchbase.client.java.document.json.JsonValue._
import com.couchbase.client.java.document.json.{JsonArray, JsonObject, JsonValue}

import scala.collection.JavaConverters.seqAsJavaListConverter
import scala.io.Source
import scala.util.Try

/**
  * Created by vgalcenco on 3/29/17.
  */
object DataLoader {

  val imdbBucket = CouchbaseCluster.create().openBucket("imdb")

  def main(args: Array[String]): Unit = {

    Source.fromFile("/Users/vgalcenco/Downloads/movie_metadata.csv").getLines()
      .drop(1)
      .map(movieToJson)
      .foreach(obj => Try(imdbBucket.insert(JsonDocument.create("tt\\d+".r.findFirstIn(obj.getString("movieImdbLink")).get, obj))))
  }

  def movieToJson(csv: String) = {
    val csvArr = csv.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1)
    val jsObj = jo()
      .put("color", csvArr(0))
      .put("directorName", csvArr(1))
      .put("genres", from(csvArr(9).split("\\|").toList.asJava))
      .put("movieTitle", csvArr(11))
      .put("plotKeywords", from(csvArr(16).split("\\|").toList.asJava))
      .put("movieImdbLink", csvArr(17))
      .put("language", csvArr(19))
      .put("country", csvArr(20))
      .put("contentRating", csvArr(21))
      .put("actors", JsonArray.from(
        jo.put("name", csvArr(10)),
        jo.put("name", csvArr(6)),
        jo.put("name", csvArr(14))
      ))

    toInt(csvArr(2)) .map(v => jsObj.put("numCriticForReviews", v))
    toInt(csvArr(3)) .map(v => jsObj.put("duration", v))
    toInt(csvArr(4)) .map(v => jsObj.put("directorFacebookLikes", v))
    toInt(csvArr(8)) .map(v => jsObj.put("gross", v))
    toInt(csvArr(12)).map(v => jsObj.put("numVotedUsers", v))
    toInt(csvArr(13)).map(v => jsObj.put("castTotalFacebookLikes", v))
    toInt(csvArr(15)).map(v => jsObj.put("facenumberInPoster", v))
    toInt(csvArr(18)).map(v => jsObj.put("numUserForReviews", v))
    toInt(csvArr(22)).map(v => jsObj.put("budget", v))
    toInt(csvArr(23)).map(v => jsObj.put("titleYear", v))
    toInt(csvArr(27)).map(v => jsObj.put("movieFacebookLikes", v))

    toInt(csvArr(7) ).map(v => jsObj.getArray("actors").getObject(0).put("facebookLikes", v))
    toInt(csvArr(24)).map(v => jsObj.getArray("actors").getObject(1).put("facebookLikes", v))
    toInt(csvArr(5) ).map(v => jsObj.getArray("actors").getObject(2).put("facebookLikes", v))

    toDouble(csvArr(24)).map(v => jsObj.put("imdbScore", v))
    toDouble(csvArr(25)).map(v => jsObj.put("aspectRatio", v))

    jsObj

  }


  def toInt(string: String): Option[Long] = string match {
    case null | "" => None
    case _ => Some(string.toLong)
  }

  def toDouble(string: String): Option[Double] = string match {
    case null | "" => None
    case _ => Some(string.toDouble)
  }

}


