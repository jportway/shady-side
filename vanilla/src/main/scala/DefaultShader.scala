import com.jsuereth.gl.math.*
import com.jsuereth.gl.io.ShaderUniformLoadable
import com.jsuereth.gl.texture.Texture2D

case class WorldData(light: Vec3[Float], eye: Vec3[Float], view: Matrix4[Float], projection: Matrix4[Float]) derives ShaderUniformLoadable



object DefaultShader extends SimpleShaderResource("BasicVertex.glsl", "BasicFragment.glsl") {
  val colour = makeUniform[Vec4[Float]]("ourColor")
  val world = makeUniform[WorldData]("world")
  val modelMatrix = makeUniform[Matrix4[Float]]("modelMatrix")
  val materialShininess = makeUniform[Float]("materialShininess")
  val materialKd = makeUniform[Vec3[Float]]("materialKd")
  val materialKs = makeUniform[Vec3[Float]]("materialKs")
  // Textures in lighting model.
  val materialKdTexture = makeUniform[Texture2D]("materialKdTexture")
}
