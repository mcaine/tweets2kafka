package com.mikeycaine.tweets2kafka

case class Tweet(
                  created_at: String,
                  id: Long,
                  id_str: String,
                  text: String,
                  source: String,
                  truncated: Boolean,
                  in_reply_to_status_id: Option[Long],
                  in_reply_to_status_id_str: Option[String],
                  in_reply_to_user_id: Option[Long],
                  in_reply_to_user_id_str: Option[String],
                  in_reply_to_screen_name: Option[String],
                  user: Users,
                  geo: Option[String],
                  coordinates: Option[Coordinates],
                  place: Option[Place],
                  contributors: Option[Seq[Contributor]],
                  retweeted_status: Option[Tweet],
                  is_quote_status: Boolean,
                  quote_count: Option[Int],
                  reply_count: Option[Int],
                  retweet_count: Option[Int],
                  favourite_count: Option[Int],
                  entities: Entities,
                  favorited: Option[Boolean],
                  retweeted: Option[Boolean],
                  filter_level: Option[String],
                  lang: Option[String],
                  timestamp_ms: Option[Long],
                  //                  current_user_retweet: Option[Map[String, String]],
                  //                  favorite_count: Option[Int],
                  //                  filter_level: String,
                  //                  possibly_sensitive: Option[Boolean],
                  //                  quoted_status_id: Option[Long],
                  //                  quoted_status_id_str: Option[String],
                  //                  quoted_status: Option[Tweet],
                  //                  scopes: Option[Map[String, String]],
                  //                  withheldCopyright: Option[Boolean],
                  //                  withheldInCountries: Option[Seq[String]],
                  //                  withheldScope: Option[String]
                )

case class Users(
                  contributors_enabled: Boolean,
                  created_at: String,
                  default_profile: Boolean,
                  default_profile_image: Boolean,
                  description: Option[String],
                  entities: Entities,
                  favourites_count: Int,
                  follow_request_sent: Option[Boolean],
                  following: Option[Boolean],
                  followers_count: Int,
                  friends_count: Int,
                  geo_enabled: Boolean,
                  id: Long,
                  id_str: String,
                  is_translator: Boolean,
                  //                 lang: String,
                  listed_count: Int,
                  //                  location: Option[String],
                  name: String,
                  //                  notifications: Option[Boolean],
                  //                  profile_background_color: String,
                  //                  profile_background_image_url: String,
                  //                  profile_background_image_url_https: String,
                  //                  profile_background_tile: Boolean,
                  //                  profile_banner_url: Option[String],
                  profile_image_url: String,
                  profile_image_url_https: String,
                  profile_link_color: String,
                  //                  profile_sidebar_border_color: String,
                  //                  profile_sidebar_fill_color: String,
                  profile_text_color: String,
                  //                  profile_use_background_image: Boolean,
                  //                  `protected`: Boolean,
                  screen_name: String,
                  //                  show_all_inline_media: Option[Boolean],
                  //                  status: Option[Tweets],
                  //                  statuses_count: Int,
                  //                  time_zone: Option[String],
                  //                  url: Option[String],
                  //                  utc_offset: Option[Int],
                  //                  verified: Boolean,
                  //                  withheld_in_countries: Option[String],
                  //                  withheld_scope: Option[String]
                )


case class Tweets(
                   contributors: Seq[Contributor],
                   coordinates: Coordinates,
                   created_at: String,
                   current_user_retweet: Option[Map[String, String]],
                   entities: Entities,
                   favorite_count: Option[Int],
                   favorited: Option[Boolean],
                   filter_level: String,
                   id: Long,
                   id_str: String,
                   in_reply_to_screen_name: Option[String],
                   in_reply_to_status_id: Option[Long],
                   in_reply_to_status_id_str: Option[String],
                   in_reply_to_user_id: Option[Long],
                   in_reply_to_user_id_str: Option[String],
                   lang: Option[String],
                   place: Option[Place],
                   possibly_sensitive: Option[Boolean],
                   quoted_status_id: Option[Long],
                   quoted_status_id_str: Option[String],
                   quoted_status: Option[Tweet],
                   scopes: Option[Map[String, String]],
                   retweet_count: Int,
                   retweeted: Option[Boolean],
                   retweeted_status: Option[Tweet],
                   source: String,
                   text: String,
                   truncated: Boolean,
                   user: Users,
                   withheld_copyright: Option[Boolean],
                   withheld_in_countries: Option[Seq[String]],
                   withheld_scope: Option[String]
                 )

case class Place(
                  attributes: Map[String, String],
                  bounding_box: BoundingBox,
                  country: String,
                  country_code: String,
                  full_name: String,
                  id: String,
                  name: String,
                  place_type: String,
                  url: String
                )

case class Media(
                  display_url: String,
                  expanded_url: String,
                  id: Long,
                  id_str: String,
                  indices: Seq[Int],
                  media_url: String,
                  media_url_https: String,
                  sizes: Sizes,
                  source_status_id: Option[Long],
                  source_status_id_str: Option[String],
                  `type`: String,
                  url: String
                )

case class BoundingBox(coordinates: Seq[Seq[Seq[Float]]], `type`: String)

case class Contributor(id: Long, id_str: String, screen_name: String)

case class Coordinates(coordinates: Seq[Float], `type`: String)

case class Entities(hashtags: Option[Seq[Hashtag]], media: Option[Seq[Media]], urls: Option[Seq[Url]], user_mentions: Option[Seq[UserMention]])

case class Sizes(thumb: Size, large: Size, medium: Size, small: Size)

case class Size(h: Int, w: Int, resize: String)

case class Hashtag(indices: Seq[Int], text: String)

case class Url(display_url: String, expanded_url: String, indices: Seq[Int], url: String)

case class UserMention(id: Long, id_str: String, indices: Seq[Int], name: String, screen_name: String)

//object Tweet {
//  implicit val boundingBoxReads = Json.reads[BoundingBox]
//  implicit val placeReads = Json.reads[Place]
//  implicit val urlReads = Json.reads[Url]
//  implicit val sizeReads = Json.reads[Size]
//  implicit val sizesReads = Json.reads[Sizes]
//  implicit val mediaReads = Json.reads[Media]
//  implicit val userMentionReads = Json.reads[UserMention]
//  implicit val hashTagReads = Json.reads[Hashtag]
//  implicit val entitiesReads = Json.reads[Entities]
//  implicit val contributorReads = Json.reads[Contributor]
//  implicit val coordinatesReads = Json.reads[Coordinates]
//  implicit val usersReads = Json.reads[Users]
//  implicit val tweetReads: Reads[Tweet] = Json.reads[Tweet]
//}