import com.jsuereth.gl.math.*
import com.jsuereth.gl.io.ShaderUniformLoadable

case class WorldData(light: Vec3[Float], eye: Vec3[Float], view: Matrix4[Float], projection: Matrix4[Float]) derives ShaderUniformLoadable

object DefaultShader extends SimpleShaderResource("BasicVertex.glsl", "BasicFragment.glsl") {
  val colour = makeUniform[Vec4[Float]]("ourColor")
  val world = makeUniform[WorldData]("world")
  val modelMatrix = makeUniform[Matrix4[Float]]("modelMatrix")
}
