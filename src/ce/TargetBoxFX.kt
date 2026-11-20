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
        // ================= 可调参数区 =================
        var color = Color.white            // 颜色（默认白色）
        var boxSize = 50f                  // 基础大小（四个角的中心距离）
        var cornerLength = 15f             // 每个角的边长（延伸长度）
        var strokeWidth = 3f               // 线条粗细
        
        var rotAngleDeg = 45f              // 每次旋转的角度（度）
        var rotInterval = 120f             // 每次旋转的间隔时间（tick，120=2秒）
        var rotSpeed = 0.2f                // 旋转速度（占比：0.2 表示用这 2 秒内的最后 20% 时间转过去）
        
        var breathAmount = 8f              // 呼吸扩张的幅度
        var breathSpeed = 0.05f            // 呼吸速度

        init {
            layer = Layer.effect
            lifetime = 300f // 默认持续5秒
        }

        fun setConfig(
            size: Float, 
            col: Color = Color.white, 
            stroke: Float = 3f, 
            cLength: Float = 15f,
            rotDeg: Float = 45f,
            rotSpd: Float = 0.2f,
            breatheAmt: Float = 8f,
            breatheSpd: Float = 0.05f
        ): TargetBoxEffect {
            this.boxSize = size
            this.color = col
            this.strokeWidth = stroke
            this.cornerLength = cLength
            this.rotAngleDeg = rotDeg
            this.rotSpeed = rotSpd
            this.breathAmount = breatheAmt
            this.breathSpeed = breatheSpd
            return this
        }

        override fun render(e: Effect.EffectContainer) {
            val progress = Mathf.clamp(e.time / lifetime)
            
            // 呼吸效果
            val breathOffset = Mathf.sin(e.time * breathSpeed) * breathAmount
            
            // 旋转逻辑
            val rotRad = rotAngleDeg * Mathf.degRad
            val rotProgress = (e.time % rotInterval) / rotInterval
            
            // 如果当前周期进入了设定的旋转速度区间，就开始顺滑旋转
            val rotThreshold = 1f - rotSpeed
            val currentRot = if (rotProgress > rotThreshold) {
                Interp.pow3Out.apply((rotProgress - rotThreshold) / rotSpeed) * rotRad
            } else {
                0f
            }

            // 计算已经完成了多少个旋转周期
            val totalRotations = Mathf.floor(e.time / rotInterval)
            val finalRotation = currentRot + (totalRotations * rotRad)

            Draw.color(color)
            // 呼吸闪烁：保持 0.8 的基础透明度，上下浮动 0.2
            Draw.alpha(Mathf.absin(e.time, 0.1f, 0.2f) + 0.8f)
            Lines.stroke(strokeWidth)
            
            val currentRadius = boxSize / 2f + breathOffset

            // 画四个角
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

            // 中心小十字准星
            Lines.stroke(strokeWidth * 0.5f)
            Lines.line(e.x - 3f, e.y, e.x + 3f, e.y, false)
            Lines.line(e.x, e.y - 3f, e.x, e.y + 3f, false)

            Draw.reset()
        }
    }
}
