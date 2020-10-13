package com.mikeycaine.tweets2kafka

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class TwitterBotSpec extends AnyFlatSpec with should.Matchers{

  val SAVANNAH_ACT_TWEET_ID = 1315664497890082816L

  "Twitter Bot" should "get tweet" in {
    val twitterBot = TwitterBot()
    val tweetFuture = twitterBot.getTweetData(SAVANNAH_ACT_TWEET_ID)
    val tweet = Await.result(tweetFuture, 5.seconds)
    tweet.id should equal(SAVANNAH_ACT_TWEET_ID)

    val name = tweet.user.map(_.name).get
    name should equal("One America News")

  }

  it should "stream" in {
    val twitterBot = TwitterBot()
    val fut = twitterBot.search(List("Mnuchin", "Grassley", "Lindsay"))
    Thread.sleep(30000L)
    Await.result(fut, 1.seconds)
  }


}
