package ce

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Mathf
import mindustry.entities.Effect
import mindustry.graphics.Layer

object RotatingCubeFX {

    @JvmField
    val rotatingCube: Effect = RotatingCubeEffect()

    class RotatingCubeEffect : Effect() {
        var color = Color.white // 线条颜色
        var cubeSize = 20f      // 立方体大小 (边长)
        var strokeWidth = 2f    // 线条粗细
        var rotationSpeed = 2f  // 旋转速度 (圈/秒)
        var delay = 0f          // 延迟时间

        init {
            layer = Layer.effect
        }

        fun setConfig(size: Float, col: Color, stroke: Float, speed: Float, delayTime: Float = 0f): RotatingCubeEffect {
            this.cubeSize = size
            this.color = col
            this.strokeWidth = stroke
            this.rotationSpeed = speed
            this.delay = delayTime
            return this
        }

        // 3D 顶点结构体
        private class Point3D(var x: Float, var y: Float, var z: Float)

        override fun render(e: Effect.EffectContainer) {
            if (e.time < delay) return

            val elapsed = e.time - delay
            val lifetime = 120f // 默认持续 2 秒
            val progress = Mathf.clamp(elapsed / lifetime)
            
            // 整体淡出
            val alpha = 1f - progress
            Draw.color(color, alpha)
            Lines.stroke(strokeWidth * (1f - progress * 0.5f))

            // 计算当前旋转角度
            val angleX = progress * rotationSpeed * Mathf.PI2
            val angleY = progress * rotationSpeed * Mathf.PI2 * 1.3f
            val angleZ = progress * rotationSpeed * Mathf.PI2 * 0.7f

            // 1. 定义立方体的 8 个顶点 (边长的一半作为半径)
            val s = cubeSize / 2f
            val originalPoints = arrayOf(
                Point3D(-s, -s, -s), Point3D( s, -s, -s), Point3D( s,  s, -s), Point3D(-s,  s, -s), // 后面4个
                Point3D(-s, -s,  s), Point3D( s, -s,  s), Point3D( s,  s,  s), Point3D(-s,  s,  s)  // 前面4个
            )

            // 2. 旋转并投影到 2D 屏幕
            val projectedPoints = Array(8) { Point3D(0f, 0f, 0f) }
            for (i in 0 until 8) {
                val p = originalPoints[i]
                
                // 绕 Z 轴旋转
                var x1 = p.x * Mathf.cos(angleZ) - p.y * Mathf.sin(angleZ)
                var y1 = p.x * Mathf.sin(angleZ) + p.y * Mathf.cos(angleZ)
                var z1 = p.z
                
                // 绕 X 轴旋转
                val y2 = y1 * Mathf.cos(angleX) - z1 * Mathf.sin(angleX)
                val z2 = y1 * Mathf.sin(angleX) + z1 * Mathf.cos(angleX)
                
                // 绕 Y 轴旋转
                val x3 = x1 * Mathf.cos(angleY) + z2 * Mathf.sin(angleY)
                val z3 = -x1 * Mathf.sin(angleY) + z2 * Mathf.cos(angleY)
                
                // 透视投影 (增加 3D 纵深感)
                val focalLength = 1200f // 焦距，值越小透视越夸张
                val projectionScale = focalLength / (focalLength + z3)
                
                projectedPoints[i] = Point3D(x3 * projectionScale, y2 * projectionScale, z3)
            }

            // 3. 连线 (立方体共有 12 条边)
            val edges = arrayOf(
                // 后面的面
                intArrayOf(0, 1), intArrayOf(1, 2), intArrayOf(2, 3), intArrayOf(3, 0),
                // 前面的面
                intArrayOf(4, 5), intArrayOf(5, 6), intArrayOf(6, 7), intArrayOf(7, 4),
                // 连接前后面的边
                intArrayOf(0, 4), intArrayOf(1, 5), intArrayOf(2, 6), intArrayOf(3, 7)
            )

            for (edge in edges) {
                val p1 = projectedPoints[edge[0]]
                val p2 = projectedPoints[edge[1]]
                // 绘制线段
                Lines.line(e.x + p1.x, e.y + p1.y, e.x + p2.x, e.y + p2.y, false)
            }

            Draw.reset()
        }
    }
}
