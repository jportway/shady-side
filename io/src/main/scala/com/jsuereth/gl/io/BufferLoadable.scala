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

package com.jsuereth.gl
package io


import java.nio.ByteBuffer
import scala.compiletime.{erasedValue,summonFrom}

/** 
 * Loads a value into a byte buffer. 
 * This can be derived for most final case classes (i.e. PODs).
 */
trait BufferLoadable[T] {
    /** Loads a value into a byte buffer. */
    def load(value: T, buf: ByteBuffer): Unit
}
object BufferLoadable {
    import deriving._

    /* A quick and dirty mechanism to derive loading product-types into a buffer. */
    inline def derived[A](using m: Mirror.Of[A]): BufferLoadable[A] = new BufferLoadable[A] {
        def load(v: A, buf: ByteBuffer): Unit = {
            inline m match {
                case m: Mirror.ProductOf[A] =>  writeElems[m.MirroredElemTypes](buf, v.asInstanceOf[Product], 0)
                case _ => compiletime.error("Cannot derive buffer loading for non-product classes")
            }
        }
    }
    // peels off a layer of the tuple and writes it into the buffer.
    inline def writeElems[Elems <: Tuple](buf: ByteBuffer, v: Product, idx: Int): Unit =
      inline erasedValue[Elems] match {
          case _: (elem *: elems1) => 
            loadInl[elem](v.productElement(idx).asInstanceOf[elem], buf)
            writeElems[elems1](buf, v, idx+1)
          case _: EmptyTuple =>
      }
    // Looks up the implicit a buffer loadable for a given type.
    inline def loadInl[A](value: A, buf: ByteBuffer) =
      summonFrom {
        case loader: BufferLoadable[A] => loader.load(value,buf)
      }

    // Defines all the buffer loading for primitives.
    // TODO - figure out if we can autogenerate this or clean it up in otherways.
    given BufferLoadable[Float] with {
        def load(value: Float, buf: ByteBuffer): Unit =
          buf.putFloat(value)
    }
    given BufferLoadable[Double]  with {
        def load(value: Double, buf: ByteBuffer): Unit =
          buf.putDouble(value)
    }
    given BufferLoadable[Boolean]  with {
        def load(value: Boolean, buf: ByteBuffer): Unit =
          buf.put(if(value) 1.toByte else 0.toByte)
    }
    given BufferLoadable[Byte]  with {
        def load(value: Byte, buf: ByteBuffer): Unit =
          buf.put(value)
    }
    given BufferLoadable[Short]  with {
        def load(value: Short, buf: ByteBuffer): Unit =
          buf.putShort(value)
    }
    given BufferLoadable[Int]  with {
        def load(value: Int, buf: ByteBuffer): Unit =
          buf.putInt(value)
    }
    given BufferLoadable[Long]  with {
        def load(value: Long, buf: ByteBuffer): Unit =
          buf.putLong(value)
    }

    given [T](using BufferLoadable[T]):BufferLoadable[Seq[T]] with {
        def load(value: Seq[T], buf: ByteBuffer): Unit = {
            val i = value.iterator
            while(i.hasNext) {
                buf.load(i.next)
            }
        }
    }

    import com.jsuereth.gl.math._
    given [T](using io.BufferLoadable[T]): BufferLoadable[Matrix3x3[T]]  with {
      def load(value: Matrix3x3[T], buf: java.nio.ByteBuffer): Unit = {
        buf.loadArray(value.values, 9, 0)
      }
    }
    given [T](using io.BufferLoadable[T]): BufferLoadable[Matrix4x4[T]]  with {
      def load(value: Matrix4x4[T], buf: java.nio.ByteBuffer): Unit = {
        buf.loadArray(value.values, 16, 0)
      }
    }
    given [T](using io.BufferLoadable[T]): BufferLoadable[Vec2[T]]  with {
      def load(value: Vec2[T], buf: java.nio.ByteBuffer): Unit = {
        buf.load(value.x)
        buf.load(value.y)
      }
    }
    given [T](using io.BufferLoadable[T]): BufferLoadable[Vec4[T]]  with {
      def load(value: Vec4[T], buf: java.nio.ByteBuffer): Unit = {
        buf.load(value.x)
        buf.load(value.y)
        buf.load(value.z)
        buf.load(value.w)
      }
    }
    given [T](using io.BufferLoadable[T]): BufferLoadable[Vec3[T]]  with {
      def load(value: Vec3[T], buf: java.nio.ByteBuffer): Unit = {
        buf.load(value.x)
        buf.load(value.y)
        buf.load(value.z)
      }
    }
}
/** 
 * Extension method for ByteBuffer. 
 * Allows calling 'load' on non-primitive types.
 */
extension (buf: ByteBuffer) def load[T](value: T)(using loader: BufferLoadable[T]): Unit = loader.load(value, buf)

/** 
 * Extension method for ByteBuffer. 
 * Allows calling 'load' for partial array values.
 */
extension  (buf: ByteBuffer) def loadArray[T](value: Array[T], length: Int, offset: Int)(using loader: BufferLoadable[T]): Unit = {
  for (i <- offset until (length+offset) if i < value.length)
    loader.load(value(i), buf)
}

import org.lwjgl.system.MemoryStack
/** 
 * Loads a buffer with the given data, flips it for reading and passes it to the lambda.
 * Cleans up memory when done.
 */
inline def withLoadedBuf[T : BufferLoadable, A](value: Seq[T])(f: ByteBuffer => A)(using MemoryStack): A = {
    val size = sizeOf[T]*value.length
    withByteBuffer(size) { buf =>
      buf.clear()
      buf.load(value)
      buf.flip()
      f(buf)
    }
}