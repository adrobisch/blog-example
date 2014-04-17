package blog

import akka.actor.{Actor, Props, ActorSystem}
import spray.routing._
import akka.io.IO
import spray.can.Http
import spray.httpx.Json4sSupport
import org.json4s.{FieldSerializer, DefaultFormats, Formats}
import spray.http.{AllOrigins, StatusCodes}
import spray.http.HttpHeaders.Location
import org.json4s.FieldSerializer._

case class Article(author: String, content: String, creationDate: Long)

class ArticleResource(val id: Int, val article: Article) extends Resource(Map("self" -> s"article/$id"))

class Resource(val links: Map[String, String] = Map())

trait RestService extends HttpService with Json4sSupport {
  implicit def json4sFormats: Formats = DefaultFormats + FieldSerializer[ArticleResource](renameTo("links", "_links"))

  val route: Route
}

abstract class BlogService extends RestService with CORSSupport {
  var articles: List[Article] = List()

  val route: Route = allowOrigins(AllOrigins)({
    path("article") {
      post {
        entity(as[Article]) { newArticle =>
          articles = newArticle :: articles
          val index = articles.indexOf(newArticle)

          respondWithHeader(Location(s"article/$index")) {
            complete {
              StatusCodes.Created
            }
          }
        }
      }
    } ~ path("articles") {
      get {
        complete {
          for ((article, index) <- articles.view.zipWithIndex)
          yield new ArticleResource(index, article)
        }
      }
    }
  })
}

class BlogServiceActor extends BlogService with Actor {
  def receive = runRoute(route)

  def actorRefFactory = context
}

object Blog extends App {
  implicit val system = ActorSystem()

  val service = system.actorOf(Props[BlogServiceActor])

  IO(Http) ! Http.Bind(service, "0.0.0.0", port = 9000)
}
