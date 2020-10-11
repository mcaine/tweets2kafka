package com.mikeycaine.tweets2kafka

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Producer
import akka.kafka.{ProducerMessage, ProducerSettings}
import akka.stream.scaladsl.{Framing, Sink, Source}
import akka.util.ByteString
import play.api.libs.oauth.{ConsumerKey, OAuthCalculator, RequestToken}
import play.api.libs.ws.ahc.StandaloneAhcWSClient
//import com.mikeycaine.tweets2kafka.TweetStreamer.{framing, tweetStream}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

import scala.concurrent.Future
import scala.util.{Failure, Success}

object TweetReader extends MySecrets {

  implicit val actorSystem: ActorSystem = ActorSystem("TweetReaderActors")
  implicit val ec = actorSystem.dispatcher
  //implicit val materializer = ActorMaterializer.create(actorSystem)

  val topic = "tweetz"
  val kafkaServer = "localhost:9092"

  lazy val producerSettings = ProducerSettings(actorSystem, new ByteArraySerializer, new StringSerializer).withBootstrapServers(kafkaServer)
  val framing = Framing.delimiter(ByteString("\r"), maximumFrameLength = 100000, allowTruncation = true)

//  val apiKey = MySecrets.apiKey
//  val apiSecret = MySecrets.apiSecret
//  val token = MySecrets.token
//  val tokenSecret = MySecrets.tokenSecret

  lazy val consumerKey = ConsumerKey(apiKey, apiSecret)
  lazy val requestToken = RequestToken(token, tokenSecret)
  lazy val oAuthCalculator = OAuthCalculator(consumerKey, requestToken)

  def tweetStream(term: String) = StandaloneAhcWSClient()
    .url("https://stream.twitter.com/1.1/statuses/filter.json")
    .sign(oAuthCalculator)
    .withQueryStringParameters("track" -> term)
    .withMethod("GET")
    .stream()


  def createMessage(bs: ByteString) = {
    val str = bs.utf8String
    val partition = 0
    val key = "KEY"
    val record = new ProducerRecord[Array[Byte], String](topic, partition, null, str)
    ProducerMessage.Message(record, str)
  }

  def terminateWhenDone(result: Future[Done]): Unit = {
    result.onComplete {
      case Failure(e) =>
        actorSystem.log.error(e, e.getMessage)
        actorSystem.terminate()
      case Success(_) => actorSystem.terminate()
    }
  }

  def sendTweets(term: String) = {
    val done = Source.future(tweetStream(term))
      .flatMapConcat { _.bodyAsSource }
      .via(framing)
      .map(createMessage)
      .via(Producer.flow(producerSettings))
      .map { result =>
        println(s"${result.offset}")
        result
      }
      .runWith(Sink.ignore)

    terminateWhenDone(done)
  }


  def main(args: Array[String]): Unit = {
    println("TWEETREADER")
    sendTweets("Trump")
  }

}
