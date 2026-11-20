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
        var color = Color.white 
        var cubeSize = 20f      
        var strokeWidth = 2f    
        var rotationSpeed = 2f  
        var delay = 0f          

        init {
            layer = Layer.effect
        }

        private class Point3D(var x: Float, var y: Float, var z: Float)

        override fun render(e: Effect.EffectContainer) {
            if (e.time < delay) return

            val elapsed = e.time - delay
            val lifetime = 120f 
            val progress = Mathf.clamp(elapsed / lifetime)
            
            // 强制不透明
            Draw.color(color, 1f) 
            Lines.stroke(strokeWidth) 

            val angleX = progress * rotationSpeed * Mathf.PI2
            val angleY = progress * rotationSpeed * Mathf.PI2 * 1.3f
            val angleZ = progress * rotationSpeed * Mathf.PI2 * 0.7f

            val s = cubeSize / 2f
            val originalPoints = arrayOf(
                Point3D(-s, -s, -s), Point3D( s, -s, -s), Point3D( s,  s, -s), Point3D(-s,  s, -s), 
                Point3D(-s, -s,  s), Point3D( s, -s,  s), Point3D( s,  s,  s), Point3D(-s,  s,  s)  
            )

            val projectedPoints = Array(8) { Point3D(0f, 0f, 0f) }
            for (i in 0 until 8) {
                val p = originalPoints[i]
                
                var x1 = p.x * Mathf.cos(angleZ) - p.y * Mathf.sin(angleZ)
                var y1 = p.x * Mathf.sin(angleZ) + p.y * Mathf.cos(angleZ)
                var z1 = p.z
                
                val y2 = y1 * Mathf.cos(angleX) - z1 * Mathf.sin(angleX)
                val z2 = y1 * Mathf.sin(angleX) + z1 * Mathf.cos(angleX)
                
                val x3 = x1 * Mathf.cos(angleY) + z2 * Mathf.sin(angleY)
                val z3 = -x1 * Mathf.sin(angleY) + z2 * Mathf.cos(angleY)
                
                val focalLength = 1200f 
                val projectionScale = focalLength / (focalLength + z3)
                
                projectedPoints[i] = Point3D(x3 * projectionScale, y2 * projectionScale, z3)
            }

            val edges = arrayOf(
                intArrayOf(0, 1), intArrayOf(1, 2), intArrayOf(2, 3), intArrayOf(3, 0),
                intArrayOf(4, 5), intArrayOf(5, 6), intArrayOf(6, 7), intArrayOf(7, 4),
                intArrayOf(0, 4), intArrayOf(1, 5), intArrayOf(2, 6), intArrayOf(3, 7)
            )

            for (edge in edges) {
                val p1 = projectedPoints[edge[0]]
                val p2 = projectedPoints[edge[1]]
                Lines.line(e.x + p1.x, e.y + p1.y, e.x + p2.x, e.y + p2.y, false)
            }

            Draw.reset()
        }
    }
}
