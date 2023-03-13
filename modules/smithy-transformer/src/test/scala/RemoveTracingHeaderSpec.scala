import scala.jdk.CollectionConverters._
import scala.jdk.OptionConverters._

import software.amazon.smithy.build.ProjectionTransformer
import software.amazon.smithy.build.TransformContext
import software.amazon.smithy.model.Model
import software.amazon.smithy.model.shapes.ShapeId

import weaver.Expectations
import weaver.FunSuite
import weaver.TestName

object RemoveTracingHeaderSpec extends FunSuite {

  test("remove tracing headers") {
    val unparsedModel =
      """
        |$version: "2"
        |namespace smithy4s.hello
        |
        |@trait(selector: "structure > member")
        |structure tracingHeader {}
        |
        |@mixin
        |structure Tracing {
        |    @tracingHeader
        |    shouldBeRemoved1: String,
        |
        |    member1: String,
        |}
        |
        |structure SomeStructure with [Tracing] {
        |    @tracingHeader
        |    shouldBeRemoved2: String,
        |
        |    member2: String
        |}
        |""".stripMargin
    val model = Model.assembler().addUnparsedModel("test.smithy", unparsedModel).assemble().unwrap()

    val maybeTransformation =
      ProjectionTransformer.createServiceFactory().apply("RemoveTracingHeader")

    val maybeTransformedShape = maybeTransformation.toScala.map { transformer =>
      val ctx = TransformContext.builder().model(model).build()
      transformer
        .transform(ctx)
        .expectShape(
          ShapeId.from("smithy4s.hello#SomeStructure")
        )
    }

    for {
      transformedShape <- maybeTransformedShape
    } yield assert(transformedShape.getAllMembers.keySet().asScala == Set("member1", "member2"))
  }

  def test(testName: TestName)(maybeExpectation: Option[Expectations]): Unit = {
    super.test(testName)(maybeExpectation.getOrElse(failure("missing expectation")))
  }

}
