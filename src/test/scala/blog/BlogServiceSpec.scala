package blog

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import akka.actor.ActorRefFactory
import spray.httpx.Json4sSupport
import org.json4s.Formats
import java.util.Date
import spray.http.HttpHeaders.{Location, `Access-Control-Allow-Credentials`, `Access-Control-Allow-Headers`, `Access-Control-Allow-Origin`}
import spray.http.{HttpResponse, StatusCodes, AllOrigins}

class BlogServiceSpec extends Specification with Specs2RouteTest with Json4sSupport {
  implicit def json4sFormats: Formats = RestService.formats

  val bobsArticle = Article(author = "Bob", content = "Bob is cool", creationDate = new Date().getTime)
  val johnsArticle = Article(author = "John", content = "John is cool", creationDate = new Date().getTime)

  def newBlogService = new BlogService {
    override implicit def actorRefFactory: ActorRefFactory = system
  }

  "The blog service" should {
    "return all article resources as list" in {
      val blogService = newBlogService

      blogService.articles = List(bobsArticle, johnsArticle)

      Get("/articles") ~> blogService.route ~> check {
        val articlesList = responseAs[ArticleListRepresentation]

        forall(articlesList.list) { representation: ArticleRepresentation =>
          isValidArticleRepresentation(representation, blogService.articles(representation.id))
        }
      }
    }

    "return article at index with path param" in {
      val blogService = newBlogService

      blogService.articles = List(bobsArticle, johnsArticle)

      Get("/article/0") ~> blogService.route ~> check {
        val representation = responseAs[ArticleRepresentation]
        isValidArticleRepresentation(representation, blogService.articles(representation.id))
      }
    }

    "allow posting of new articles" in {
      val blogService = newBlogService

      blogService.articles = List()

      Post("/article", bobsArticle) ~> blogService.route ~> check {
        response.headers must contain(Location("http://example.com/article/0"))
      }

      blogService.articles must contain(bobsArticle)
    }

    "return service document at root path" in {
      val blogService = newBlogService

      Get("/") ~> blogService.route ~> check {
        responseAs[Resource[_]].links must havePairs("articles" -> "http://example.com:0/articles", "article" -> "http://example.com:0/article")
      }
    }

    "add CORS headers to responses" in {
      val blogService = newBlogService

      Get("/articles") ~> blogService.route ~> check {
        isResponseWithCorsHeaders(response)
      }
    }

    "allow OPTIONS request with CORS headers" in {
      val blogService = newBlogService

      Options("/articles") ~> blogService.route ~> check {
        response.status === StatusCodes.OK
        isResponseWithCorsHeaders(response)
      }
    }

    def isResponseWithCorsHeaders(response: HttpResponse) = {
      response.headers must containAllOf(List(
        `Access-Control-Allow-Headers`("Content-Type"),
        `Access-Control-Allow-Origin`(AllOrigins),
        `Access-Control-Allow-Credentials`(allow = true)
      ))
    }

    def isValidArticleRepresentation(representation: ArticleRepresentation, article: Article) = {
      representation.links must havePair("self" -> s"http://example.com:0/article/${representation.id}")
      representation.article must beEqualTo(article)
    }
  }
}
