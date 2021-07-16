import com.jsuereth.gl.mesh.ClassloaderResourceLookup
import java.io.*

object SimpleLightShader extends BasicShaderProgram {
  override def fragmentShaderCode: String = readStringResource("BasicFragment.glsl")

  override def vertexShaderCode: String = readStringResource("BasicVertex.glsl")

  def readStringResource(loc:String) = ClassloaderResourceLookup().read(loc){inputStream =>
    val s:String = scala.io.Source.fromInputStream(inputStream).mkString
    s
  }
}