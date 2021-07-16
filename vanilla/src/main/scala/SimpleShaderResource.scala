import com.jsuereth.gl.mesh.ClassloaderResourceLookup
import java.io.*

import com.jsuereth.gl.math.{given, *}
import com.jsuereth.gl.texture.Texture2D
import com.jsuereth.gl.io.ShaderUniformLoadable

case class WorldData(light: Vec3[Float], eye: Vec3[Float], view: Matrix4[Float], projection: Matrix4[Float]) derives ShaderUniformLoadable

trait SimpleShaderResource(vertexShaderResource:String, fragmentShaderResource:String) extends BasicShaderProgram {
  override def fragmentShaderCode: String = readStringResource(fragmentShaderResource)

  override def vertexShaderCode: String = readStringResource(vertexShaderResource)

  def readStringResource(loc:String) = ClassloaderResourceLookup().read(loc){inputStream =>
    val s:String = scala.io.Source.fromInputStream(inputStream).mkString
    s
  }
}