package com.mikeycaine.tweets2kafka

import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success

import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.StringSerializer

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ProducerMessage
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Framing
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import akka.util.ByteString
import play.api.libs.oauth.ConsumerKey
import play.api.libs.oauth.OAuthCalculator
import play.api.libs.oauth.RequestToken
import play.api.libs.ws.ahc.StandaloneAhcWSClient

trait MyActorSystem {
  implicit def actorSystem: ActorSystem
  implicit lazy val ec = actorSystem.dispatcher
  implicit lazy val materializer = ActorMaterializer.create(actorSystem)
}

trait TwitterReader extends MyActorSystem {
  //import MySecrets._

  val apiKey = MySecrets.apiKey
  val apiSecret = MySecrets.apiSecret
  val token = MySecrets.token
  val tokenSecret = MySecrets.tokenSecret

  lazy val consumerKey = ConsumerKey(apiKey, apiSecret)
  lazy val requestToken = RequestToken(token, tokenSecret)
  lazy val oAuthCalculator = OAuthCalculator(consumerKey, requestToken)

  val framing = Framing.delimiter(ByteString("\r"), maximumFrameLength = 100000, allowTruncation = true)

  def tweetStream(term: String) = StandaloneAhcWSClient()
    .url("https://stream.twitter.com/1.1/statuses/filter.json")
    .sign(oAuthCalculator)
    .withQueryStringParameters("track" -> term)
    .withMethod("GET")
    .stream()
}

trait KafkaSender extends MyActorSystem {

  val topic: String
  val kafkaServer: String
  
  lazy val producerSettings = ProducerSettings(actorSystem, new ByteArraySerializer, new StringSerializer).withBootstrapServers(kafkaServer)
  
  def createMessage(bs: ByteString) = {
    val str = bs.utf8String
    val partition = 0
    ProducerMessage.Message(new ProducerRecord[Array[Byte], String](topic, partition, null, str), str)
  }

  def terminateWhenDone(result: Future[Done]): Unit = {
    result.onComplete {
      case Failure(e) =>
        actorSystem.log.error(e, e.getMessage)
        actorSystem.terminate()
      case Success(_) => actorSystem.terminate()
    }
  }
}

object TweetStreamer extends TwitterReader with KafkaSender {

  val actorSystem: ActorSystem = ActorSystem("Tweetz")
  val topic = "tweetz-Trump"
  val kafkaServer = "kafka-1594993188:9092"
  
  def getTweets(term: String) = {
    val done = Source.fromFuture(tweetStream(term))
      .flatMapConcat { _.bodyAsSource }
      .via(framing)
      .map(createMessage(_))
      .via(Producer.flow(producerSettings))
      .map { result =>
          println(s"${result.offset}")
          result
      }
      .runWith(Sink.ignore)
      
    terminateWhenDone(done)
  }
  
  def main(args: Array[String]): Unit = {
    getTweets("Trump")
    //getTweets("Grenfell")
  }
}
