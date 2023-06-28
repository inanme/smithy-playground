import software.amazon.smithy.build.ProjectionTransformer
import software.amazon.smithy.build.TransformContext
import software.amazon.smithy.model.Model
import software.amazon.smithy.model.shapes.ShapeId
import software.amazon.smithy.model.shapes.ToShapeId

class RemoveTracingHeader extends ProjectionTransformer {
  override def getName: String = getClass.getSimpleName

  private val tracingHeader = ShapeId.from("smithy4s.hello#tracingHeader")

  override def transform(context: TransformContext): Model = {
    val toRemove = context.getModel
      .getShapesWithTrait(new ToShapeId() {
        override def toShapeId: ShapeId = tracingHeader
      })
    context.getTransformer.removeShapes(context.getModel, toRemove)
  }
}
