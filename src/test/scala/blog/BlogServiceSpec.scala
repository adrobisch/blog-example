package blog

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import akka.actor.ActorRefFactory
import spray.httpx.Json4sSupport
import org.json4s.{FieldSerializer, DefaultFormats, Formats}
import org.json4s.FieldSerializer._
import java.util.Date
import spray.http.HttpHeaders.`Access-Control-Allow-Origin`

class BlogServiceSpec extends Specification with Specs2RouteTest with Json4sSupport {
  implicit def json4sFormats: Formats = DefaultFormats + FieldSerializer[ArticleResource](renameTo("links", "_links"))

  val bobsArticle = Article(author = "Bob", content = "Bob is cool", creationDate = new Date().getTime)
  val johnsArticle = Article(author = "John", content = "John is cool", creationDate = new Date().getTime)

  def newBlogService = new BlogService {
    override implicit def actorRefFactory: ActorRefFactory = system
  }

  "The Blog server" should {
    "return all articles resources" in {
      val blogService = newBlogService

      blogService.articles = List(bobsArticle, johnsArticle)

      Get("/articles") ~> blogService.route ~> check {
        responseAs[List[ArticleResource]] must haveSize(2)
      }
    }

    "allow posting of new articles" in {
      val blogService = newBlogService

      blogService.articles = List()

      Post("/article", bobsArticle) ~> blogService.route

      blogService.articles must haveSize(1)
    }

    "add CORS headers" in {
      val blogService = newBlogService

      Get("/articles") ~> blogService.route ~> check {
       response.headers.contains(`Access-Control-Allow-Origin`)
      }
    }.pendingUntilFixed
  }
}
