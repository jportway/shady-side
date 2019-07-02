/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.jsuereth.gl.shaders._
import com.jsuereth.gl.math._

/** This is our target syntax. */
// Attempt at cel-shading
import com.jsuereth.gl.math._
import delegate com.jsuereth.gl.math._

object CartoonShader extends DslShaderProgram {
  // Used for vertex shader
  val modelMatrix = Uniform[Matrix4[Float]]()
  val viewMatrix = Uniform[Matrix4[Float]]()
  val projectionMatrix = Uniform[Matrix4[Float]]()

  // User for pixel shader / lighting model
  val lightPosition = Uniform[Vec3[Float]]()
  val eyePosition = Uniform[Vec3[Float]]()
  val materialShininess = Uniform[Float]()
  val materialKd = Uniform[Float]()
  val materialKs = Uniform[Float]()

  val (vertexShaderCode, fragmentShaderCode) = defineShaders {
    val inPosition = Input[Vec3[Float]](location=0)
    val inNormal = Input[Vec3[Float]](location=1)
    // TODO - convert to matrix3...
    val worldPos = (modelMatrix() * Vec4(inPosition, 1)).xyz
    val worldNormal = (modelMatrix() * Vec4(inNormal, 0)).xyz

    glPosition(projectionMatrix() * viewMatrix() * modelMatrix() * Vec4(inPosition, 1))
    // Fragment shader
    fragmentShader {
      val L = (lightPosition() - worldPos).normalize
      val N = worldNormal.normalize
      val lambertian = Math.max(L.dot(N), 0.0f).toFloat
      val V = (eyePosition() - worldPos).normalize
      val H = (L + V).normalize
      val diffuse = materialKd() * lambertian
      val specular =
        if (lambertian > 0.0f) materialKs() * Math.pow(Math.max(0, H.dot(N)).toFloat, materialShininess()).toFloat
        else 0.0f
      //Black color if dot product is smaller than 0.3
      //else keep the same colors
      val edgeDetection = if (V.dot(worldNormal) > 0.3f) 1f else 0.7f
      val light = edgeDetection * (diffuse + specular)
      Output("color", 0, Vec4(light, light, light, 1f))
    }
  }
}


object SimpleShader extends DslShaderProgram {
  val (vertexShaderCode, fragmentShaderCode) = defineShaders {
      val position = Input[Vec3[Float]](location = 0)
      glPosition(Vec4(position, 1.0f))
      fragmentShader {
          Output("color", 0, Vec4(0.0f, 0.5f, 0.5f, 1.0f))
      }
  }
}
