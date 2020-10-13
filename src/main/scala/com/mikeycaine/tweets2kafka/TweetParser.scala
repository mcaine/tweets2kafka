package com.mikeycaine.tweets2kafka

//object TweetParser extends MySecrets {
//  implicit val actorSystem: ActorSystem = ActorSystem()
//  implicit val ec = actorSystem.dispatcher
//  implicit val materializer = SystemMaterializer(actorSystem).materializer
//
//  lazy val consumerKey = ConsumerKey(apiKey, apiSecret)
//  lazy val requestToken = RequestToken(token, tokenSecret)
//  lazy val oAuthCalculator = OAuthCalculator(consumerKey, requestToken)
//
//  def tweet(id: Long) = StandaloneAhcWSClient()
//      .url("https://api.twitter.com/1.1/statuses/show.json")
//      .sign(oAuthCalculator)
//      .withQueryStringParameters("id" -> id.toString)
//      .withMethod("GET")
//      .withRequestTimeout(Duration(5, TimeUnit.SECONDS))
//      .get()
//
//  def main(args: Array[String]): Unit = {
//
//    implicit val formats = DefaultFormats
//
//    //tweet(1315755406828990464L).onComplete
//    tweet(1315664497890082816L).onComplete {
//
//      case Success(v) =>
//        println(v.body)
//        println
//        val jsValue = JsonParser.parse(v.body)
//        val tweet = jsValue.extract[Tweet]
//        println(s"Tweet:\n${tweet.text}\nby ${tweet.user.screen_name}")
//
//
//
////        val jsonString: JsValue = Json.parse(v.body)
////
////        val tweetResult: JsResult[Tweet] = Json.fromJson[Tweet](jsonString)
////        tweetResult match {
////          case JsSuccess(tweet, path) => /*println(s"Tweet:\n${tweet.text}\nby ${tweet.user.screen_name}")*/
////          case _ =>
////        }
//      case Failure(t) => println(t.getMessage)
//    }
//
//  }
//}
