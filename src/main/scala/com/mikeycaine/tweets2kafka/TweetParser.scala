package com.mikeycaine.tweets2kafka

import java.util.concurrent.{TimeUnit, TimeoutException}

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Producer
import akka.kafka.{ProducerMessage, ProducerSettings}
import akka.stream.SystemMaterializer
import akka.stream.scaladsl.{Framing, Sink, Source}
import akka.util.ByteString
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import play.api.libs.json._
import play.api.libs.oauth._
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

object TweetParser extends MySecrets {
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val ec = actorSystem.dispatcher
  implicit val materializer = SystemMaterializer(actorSystem).materializer

  actorSystem.registerOnTermination {
    println("GOODBYE")
    System.exit(0)
  }


  val topic = "tweetz"
  val kafkaServer = "localhost:9092"

  val producerSettings = ProducerSettings(actorSystem, new ByteArraySerializer, new StringSerializer).withBootstrapServers(kafkaServer)
  val framing = Framing.delimiter(ByteString("\r"), maximumFrameLength = 100000, allowTruncation = true)

  lazy val consumerKey = ConsumerKey(apiKey, apiSecret)
  lazy val requestToken = RequestToken(token, tokenSecret)
  lazy val oAuthCalculator = OAuthCalculator(consumerKey, requestToken)

  def tweet(id: Long) = {
    StandaloneAhcWSClient()
      .url("https://api.twitter.com/1.1/statuses/show.json")
      .sign(oAuthCalculator)
      .withQueryStringParameters("id" -> id.toString)
      .withMethod("GET")
      .withRequestTimeout(Duration(5, TimeUnit.SECONDS))
      .get()
  }

  def tweetStream(term: String, requestTimeout: Duration) = StandaloneAhcWSClient()
    .url("https://stream.twitter.com/1.1/statuses/filter.json")
    .sign(oAuthCalculator)
    .withQueryStringParameters("track" -> term)
    .withMethod("GET")
    .withRequestTimeout(requestTimeout)
    .stream()

  def createMessage(bs: ByteString) = {
    val str = bs.utf8String
    val partition = 0
    val key = "KEY"
    val record = new ProducerRecord[Array[Byte], String](topic, partition, null, str)
    ProducerMessage.Message(record, bs)
  }

  def terminateWhenDone(result: Future[_], term:String): Unit = {
    val shutItDown = false

    result.onComplete {
      case Failure(t) =>
        if (t.isInstanceOf[TimeoutException]) {
          println("Got expected timeout exception")
        } else {
          actorSystem.log.error(t, t.getMessage)
          if (shutItDown) actorSystem.terminate()
        }
      case Success(_) =>
        println(s"OK DONE FOR ${term}")
        if (shutItDown) actorSystem.terminate()
    }
  }

  def sendTweets(term: String, requestTimeout: Duration) = {

    val done =
      Source.future(tweetStream(term, requestTimeout))
      .flatMapConcat { _.bodyAsSource }
      .via(framing)
      .map(createMessage)
      .via(Producer.flow(producerSettings))
      .map { result =>
        //println(s"${result.offset}")
        result
      }
      .runWith(Sink.ignore)

    terminateWhenDone(done, term)
  }

  def main(args: Array[String]): Unit = {
    //sendTweets("Trump", Duration(1, TimeUnit.HOURS))

    import Tweet._



    val f = tweet(1315296304948563974L)

    f.onComplete {
      case Success(v) => {
        //println(v.body)
        val jsonString: JsValue = Json.parse(v.body)

        val tweetResult: JsResult[Tweet] = Json.fromJson[Tweet](jsonString)
        tweetResult match {
          case JsSuccess(tweet, path) => println(s"Tweet:\n${tweet.text}\nby ${tweet.user.screen_name}")
        }



      }
      case Failure(t) => println(t.getMessage)
    }

  }
}
