package com.mikeycaine.tweets2kafka

//object TweetReader extends MySecrets {
//
//  implicit val actorSystem: ActorSystem = ActorSystem("TweetReaderActors")
//
//  actorSystem.registerOnTermination {
//    println("GOODBYE")
//    System.exit(0)
//  }
//
//  implicit val ec = actorSystem.dispatcher
//  implicit val materializer = SystemMaterializer(actorSystem).materializer
//
//  val topic = "tweetz"
//  val kafkaServer = "localhost:9092"
//
//  val producerSettings = ProducerSettings(actorSystem, new ByteArraySerializer, new StringSerializer).withBootstrapServers(kafkaServer)
//  val framing = Framing.delimiter(ByteString("\r"), maximumFrameLength = 100000, allowTruncation = true)
//
//  lazy val consumerKey = ConsumerKey(apiKey, apiSecret)
//  lazy val requestToken = RequestToken(token, tokenSecret)
//  lazy val oAuthCalculator = OAuthCalculator(consumerKey, requestToken)
//
//  def tweetStream(term: String, requestTimeout: Duration) = StandaloneAhcWSClient()
//    .url("https://stream.twitter.com/1.1/statuses/filter.json")
//    .sign(oAuthCalculator)
//    .withQueryStringParameters("track" -> term)
//    .withMethod("GET")
//    .withRequestTimeout(requestTimeout)
//    .stream()
//
//  def createMessage(bs: ByteString) = {
//    val str = bs.utf8String
//    val partition = 0
//    val key = "KEY"
//    val record = new ProducerRecord[Array[Byte], String](topic, partition, null, str)
//    ProducerMessage.Message(record, bs)
//  }
//
//  def terminateWhenDone(result: Future[_], term:String): Unit = {
//    val shutItDown = false
//
//    result.onComplete {
//      case Failure(t) =>
//        if (t.isInstanceOf[TimeoutException]) {
//          println("Got expected timeout exception")
//        } else {
//          actorSystem.log.error(t, t.getMessage)
//          if (shutItDown) actorSystem.terminate()
//        }
//      case Success(_) =>
//        println(s"OK DONE FOR ${term}")
//        if (shutItDown) actorSystem.terminate()
//    }
//  }
//
//  def sendTweets(term: String, requestTimeout: Duration) = {
//
//    val done =
//      Source.future(tweetStream(term, requestTimeout))
//      .flatMapConcat { _.bodyAsSource }
//      .via(framing)
//      .map(createMessage)
//      .via(Producer.flow(producerSettings))
//      .map { result =>
//        //println(s"${result.offset}")
//        result
//      }
//      .runWith(Sink.ignore)
//
//    terminateWhenDone(done, term)
//  }
//
//  def main(args: Array[String]): Unit = {
//    sendTweets("Trump", Duration(1, TimeUnit.HOURS))
//  }
//}
