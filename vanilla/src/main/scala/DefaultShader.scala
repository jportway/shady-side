import com.jsuereth.gl.math.Vec4

object DefaultShader extends SimpleShaderResource("BasicVertex.glsl", "BasicFragment.glsl") {

  val colour = makeUniform[Vec4[Float]]("ourColor")

}
