import cats.effect._
import cats.syntax.all._

import com.comcast.ip4s._
import org.http4s._
import org.http4s.client.Client
import org.http4s.ember.client._
import org.http4s.ember.server._
import org.http4s.syntax.all._
import smithy4s._
import smithy4s.hello._
import smithy4s.http4s._

object HelloWorldImpl extends HelloWorldService[IO] {
  def hello(name: String, town: Option[String]): IO[Greeting] =
    town match {
      case None    => Greeting(s"Hello $name!").pure[IO]
      case Some(t) => Greeting(s"Hello $name from $t!").pure[IO]
    }
}

object Routes {
  private val example: Resource[IO, HttpRoutes[IO]] =
    SimpleRestJsonBuilder.routes(HelloWorldImpl).resource

  private val docs: HttpRoutes[IO] =
    smithy4s.http4s.swagger.docs[IO](HelloWorldService)

  val all: Resource[IO, HttpRoutes[IO]] = example.map(_ <+> docs)
}

object ServerMain extends IOApp.Simple {

  val run = Routes.all
    .flatMap { routes =>
      EmberServerBuilder
        .default[IO]
        .withPort(port"9000")
        .withHost(host"localhost")
        .withHttpApp(routes.orNotFound)
        .build
    }
    .use(_ => IO.never)

}

object ClientMain extends IOApp.Simple {
  def apply[Alg[_[_, _, _, _, _]]](
    baseUri: Uri,
    httpClient: Client[IO],
    service: smithy4s.Service[Alg]
  ): Resource[IO, service.Impl[IO]] =
    SimpleRestJsonBuilder(service)
      .client(httpClient)
      .uri(baseUri)
      .middleware(middleware)
      .resource

  private case class InvalidResponse(expected: Int, actual: Int) extends Exception

  private val middleware: ClientEndpointMiddleware[IO] =
    new ClientEndpointMiddleware.Simple[IO] {
      def prepareWithHints(
        serviceHints: Hints,
        endpointHints: Hints
      ): Client[IO] => Client[IO] =
        endpointHints.get[smithy.api.Http] match {
          case Some(http) =>
            inputClient =>
              Client[IO] { request =>
                inputClient
                  .run(request)
                  .flatMap { response =>
                    Resource.eval {
                      if (response.status.code != http.code) {
                        IO.raiseError(InvalidResponse(http.code, response.status.code))
                      } else
                        IO.pure(response)
                    }
                  }
              }
          case None => identity
        }
    }

  private val helloWorldClient: Resource[IO, HelloWorldService[IO]] = for {
    client      <- EmberClientBuilder.default[IO].build
    uri         <- Resource.eval(IO.fromEither(Uri.fromString("http://localhost:9000")))
    helloClient <- ClientMain(uri, client, HelloWorldService)
  } yield helloClient

  val run = helloWorldClient.use(c =>
    c.hello("Sam", Some("New York City"))
      .flatMap(greeting => IO.println(greeting.message))
  )

}
