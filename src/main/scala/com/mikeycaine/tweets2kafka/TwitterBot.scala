package com.mikeycaine.tweets2kafka

import com.danielasfregola.twitter4s.entities.Tweet
import com.danielasfregola.twitter4s.entities.streaming.StreamingMessage
import com.danielasfregola.twitter4s.{TwitterRestClient, TwitterStreamingClient}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class TwitterBot() {

  private val restClient = TwitterRestClient()
  private val streamingClient = TwitterStreamingClient()


  // REST
  def getTweetData(id: Long): Future[Tweet] = for {
      response <- restClient.getTweet(id)
  } yield response.data

  // Streaming
  def search(terms: Seq[String]) = streamingClient.filterStatuses(tracks = terms) (MyTwitterProcessor.printNice)


}

object MyTwitterProcessor extends LazyLogging  {

  def printNice: PartialFunction[StreamingMessage, Unit] = {
    case tweet: Tweet =>
      logger.info(s"Tweet ID ${tweet.id}")
      logger.info(s"Name: ${tweet.user.get.name}")
      logger.info(s"Text: ${tweet.text}")
  }
}
