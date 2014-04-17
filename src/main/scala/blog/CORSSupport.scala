package blog

import spray.routing.{Route, HttpService}
import spray.http.HttpHeaders.{`Access-Control-Allow-Headers`, `Access-Control-Allow-Credentials`, `Access-Control-Allow-Origin`}
import spray.http._

trait CORSSupport {
  this: HttpService =>
  def respondWithCORSHeaders(origin: AllowedOrigins) =
    respondWithHeaders(
      `Access-Control-Allow-Origin`(origin),
      `Access-Control-Allow-Credentials`(allow = true),
      `Access-Control-Allow-Headers`("Content-Type"))

  def allowOrigins(origins: AllowedOrigins)(route: Route): Route = {
    extract(_.request.method) {
      case HttpMethods.OPTIONS => respondWithCORSHeaders(origins)(complete(StatusCodes.OK))
      case _ ⇒ respondWithCORSHeaders(AllOrigins)(route)
    }
  }
}
