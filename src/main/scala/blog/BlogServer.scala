package blog

import akka.actor.{Actor, Props, ActorSystem}
import spray.routing._
import akka.io.IO
import spray.can.Http
import spray.httpx.Json4sSupport
import org.json4s.{FieldSerializer, DefaultFormats, Formats}
import spray.http.{Uri, AllOrigins, StatusCodes}
import spray.http.HttpHeaders.Location
import org.json4s.FieldSerializer._

case class Article(author: String, content: String, creationDate: Long)

case class ArticleRepresentation(id: Int, article: Article) extends Resource[ArticleRepresentation]

case class ArticleListRepresentation(list: Seq[ArticleRepresentation]) extends Resource[ArticleListRepresentation]

case class ServiceDocument() extends Resource[ServiceDocument]

class Resource[T] {
  var links: Map[String, String] = Map()

  def withLink(link: (String, String)*): T = {
    links ++= link
    this.asInstanceOf[T]
  }
}

object RestService {
  def formats: Formats = DefaultFormats + FieldSerializer[Resource[_]](renameTo("links", "_links"), renameFrom("_links", "links"))
}

trait RestService extends HttpService with Json4sSupport {
  implicit def json4sFormats: Formats = RestService.formats

  def hostPrefix(uri: Uri) = s"${uri.scheme}://${uri.authority.host}:${uri.authority.port}"

  val route: Route
}

abstract class BlogService extends RestService with CORSSupport {
  var articles: List[Article] = List()

  val paths = requestUri {uri: Uri =>
    path("") {
      complete {
        ServiceDocument().withLink(
          "self" -> (hostPrefix(uri) + "/"),
          "articles"-> (hostPrefix(uri) + "/articles"),
          "article"-> (hostPrefix(uri) + "/article")
        )
      }
    } ~ pathPrefix("article") {
      post {
        entity(as[Article]) { newArticle =>
          articles = newArticle :: articles
          val index = articles.indexOf(newArticle)

          respondWithHeader(Location(hostPrefix(uri) + s"/article/$index")) {
            complete {
              StatusCodes.Created
            }
          }
        }
      }
    } ~ pathPrefix("articles") {
      get {
        complete {
          val indexedArticles = (for ((article, index) <- articles.view.zipWithIndex)
          yield ArticleRepresentation(index, article).withLink("self" -> (hostPrefix(uri) + s"/article/${index}")))

          ArticleListRepresentation(list = indexedArticles)
            .withLink("self" -> (hostPrefix(uri) + "/articles"))
        }
      }
    }
  }

  val route: Route = allowOrigins(AllOrigins)(paths)
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
