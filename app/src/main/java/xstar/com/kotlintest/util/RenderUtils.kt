package xstar.com.kotlintest.util

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * @author xstar
 * @since 6/29/17.
 */

object RenderUtils {
    /**
     *
     * @param vertexs 顶点数组
     * @return 浮点型缓冲数据
     */
    fun getFloatBuffer(vertexs: FloatArray): FloatBuffer {
        val buffer: FloatBuffer
        val qbb = ByteBuffer.allocateDirect(vertexs.size * 4)
        qbb.order(ByteOrder.nativeOrder())
        buffer = qbb.asFloatBuffer()
        //写入数组
        buffer.put(vertexs)
        //设置默认的读取位置
        buffer.position(0)
        return buffer
    }

    fun getShortBuffer(shorts: ShortArray): ShortBuffer {
        val shortBuffer: ShortBuffer
        val bb = ByteBuffer.allocateDirect(shorts.size * 2)
        bb.order(ByteOrder.nativeOrder())
        shortBuffer = bb.asShortBuffer()
        shortBuffer.put(shorts)
        shortBuffer.position(0)
        return shortBuffer
    }
}
