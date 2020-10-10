package com.mikeycaine.tweets2kafka

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Producer
import akka.kafka.{ProducerMessage, ProducerSettings}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Framing, Source}
import akka.util.ByteString
import com.jayway.jsonpath.JsonPath
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import play.api.libs.oauth.{ConsumerKey, OAuthCalculator, RequestToken}
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.Future
import scala.util.{Failure, Success}

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
  lazy val stringProducerSettings = ProducerSettings(actorSystem, new StringSerializer, new StringSerializer).withBootstrapServers(kafkaServer)
  
  def createMessage(bs: ByteString) = {
    val str = bs.utf8String
    val partition = 0
    ProducerMessage.Message(new ProducerRecord[Array[Byte], String](topic, partition, null, str), str)
    //new ProducerRecord[Array[Byte], String](topic, partition, null, str)
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
  val topic = "tweetz"
  val kafkaServer = "localhost:9092"

  def sendTweets2Kafka(term: String) = {

    println("WOW")

    val done: Future[Done] =
      Source(1 to 100)
        .map(_.toString)
        .map(value => new ProducerRecord[String, String](topic, value))
        .runWith(Producer.plainSink(stringProducerSettings))



//        val done = Source.future(tweetStream(term))
//          .flatMapConcat { _.bodyAsSource }
//          .via(framing)
//          .map(x => createMessage(x))
//          .via(Producer.flow(producerSettings))
//          .map { result =>
//              println(s"${result.offset}")
//              result
//          }
//          .runWith(Sink.ignore)

    println("STILL HERE")

        terminateWhenDone(done)

  }
  
  def getTweets(term: String) = {
    val tweets = tweetStream(term)
    val src = Source.future(tweets)

    val somethingElse = src
                          .flatMapConcat(_.bodyAsSource)
                          .via(framing)
                          .map(_.utf8String)

    val bits = somethingElse.map { str =>
      try {
        val createdAt = JsonPath.read[String](str, "$.created_at")
        val id = JsonPath.read[Long](str, "$.id")
        val text = JsonPath.read[String](str, "$.text")
        val userName = JsonPath.read[String](str, "$.user.name")
        (id, createdAt, userName, text)
      } catch { case ex: Exception =>
        println(s"*** ${ex} *** from ${str}")
      }
      //(id, createdAt)

    }

    //somethingElse.runForeach(println)
    bits.runForeach {
      case (id: Long, createdAt: String, userName: String, text: String) => println(s"${id} ${text}")
      case _ => println(s"WTF")
    }



//    val done = Source.fromFuture(tweetStream(term))
//      .flatMapConcat { _.bodyAsSource }
//      .via(framing)
//      .map(createMessage(_))
//      .via(Producer.flow(producerSettings))
//      .map { result =>
//          println(s"${result.offset}")
//          result
//      }
//      .runWith(Sink.ignore)
//
//    terminateWhenDone(done)
  }
  
  def main(args: Array[String]): Unit = {
    println("Here i am...")
    //getTweets("Trump")
    sendTweets2Kafka("Trump")

    //getTweets("Grenfell")
  }
}
