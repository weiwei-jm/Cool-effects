package ce

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Interp
import arc.math.Mathf
import mindustry.entities.Effect
import mindustry.graphics.Layer

object TargetBoxFX {

    @JvmField
    val targetBox: Effect = TargetBoxEffect()

    class TargetBoxEffect : Effect() {
        var color = Color.white            
        var boxSize = 50f                  
        var cornerLength = 15f             
        var strokeWidth = 3f               
        var rotAngleDeg = 90f              
        var rotInterval = 120f             
        var rotSpeed = 0.2f                
        var breathAmount = 8f              
        var breathSpeed = 0.05f            

        init {
            layer = Layer.effect
            lifetime = 300f 
        }

        override fun render(e: Effect.EffectContainer) {
            val breathOffset = Mathf.sin(e.time * breathSpeed) * breathAmount
            
            val rotRad = rotAngleDeg * Mathf.degRad
            val rotProgress = (e.time % rotInterval) / rotInterval
            
            val rotThreshold = 1f - rotSpeed
            val currentRot = if (rotProgress > rotThreshold) {
                Interp.pow3Out.apply((rotProgress - rotThreshold) / rotSpeed) * rotRad
            } else {
                0f
            }

            val totalRotations = Mathf.floor(e.time / rotInterval)
            val finalRotation = currentRot + (totalRotations * rotRad)

            Draw.color(color)
            Draw.alpha(Mathf.absin(e.time, 0.1f, 0.2f) + 0.8f)
            Lines.stroke(strokeWidth)
            
            val currentRadius = boxSize / 2f + breathOffset

            for (i in 0 until 4) {
                val cornerAngle = i * (Mathf.PI / 2f) + finalRotation
                
                val cx = e.x + Mathf.cos(cornerAngle) * currentRadius * 1.414f
                val cy = e.y + Mathf.sin(cornerAngle) * currentRadius * 1.414f
                
                val dir1 = cornerAngle + Mathf.PI * 0.75f
                val dir2 = cornerAngle - Mathf.PI * 0.75f
                
                val x1 = cx + Mathf.cos(dir1) * cornerLength
                val y1 = cy + Mathf.sin(dir1) * cornerLength
                
                val x2 = cx + Mathf.cos(dir2) * cornerLength
                val y2 = cy + Mathf.sin(dir2) * cornerLength

                Lines.line(x1, y1, cx, cy, false)
                Lines.line(cx, cy, x2, y2, false)
            }

            Lines.stroke(strokeWidth * 0.5f)
            Lines.line(e.x - 3f, e.y, e.x + 3f, e.y, false)
            Lines.line(e.x, e.y - 3f, e.x, e.y + 3f, false)

            Draw.reset()
        }
    }
}
