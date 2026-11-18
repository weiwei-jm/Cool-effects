package ce

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.math.Rand
import mindustry.entities.Effect
import mindustry.graphics.Layer

object CoolEffectsFX {
    
    // 直接把 UltimateExplosionFX 里的特效“借”过来用
    @JvmField
    val ultimateExplosion: Effect = UltimateExplosionFX.ultimateExplosion

    // ... 下面继续保留你之前的 ringHorizontal 等代码 ...

    // ==================== 圆环特效 ====================
    @JvmField
    val ringHorizontal: Effect = RingEffect(0f, 0f)

    @JvmField
    val ringTilted: Effect = RingEffect(60f, 30f)

    class RingEffect(
        var tiltAngle: Float,
        var rotationDeg: Float
    ) : Effect() {
        var size: Float = 10f
        var lifetime: Float = 120f
        var delay: Float = 0f

        init {
            layer = Layer.effect
        }

        fun setSize(s: Float): RingEffect { size = s; return this }
        fun setDelay(d: Float): RingEffect { delay = d; return this }
        fun setTilt(tiltDeg: Float, rotDeg: Float): RingEffect {
            this.tiltAngle = tiltDeg
            this.rotationDeg = rotDeg
            return this
        }

        override fun render(e: Effect.EffectContainer) {
            if (e.time < delay) return
            val progress = Mathf.clamp((e.time - delay) / lifetime)
            val alpha = 0.7f * (1f - progress)

            val baseInner = size * 2f
            val ringWidth = size * 0.5f
            val currentInner = baseInner + progress * size * 2f
            val currentOuter = currentInner + ringWidth
            
            val yScale = Mathf.cosDeg(tiltAngle)
            val rotation = rotationDeg * Mathf.degRad

            Draw.color(Color.white, alpha)
            Lines.stroke(2f)
            
            drawEllipse(e.x, e.y, currentOuter, currentOuter * yScale, rotation, 48)
            if (currentInner > 0f) {
                drawEllipse(e.x, e.y, currentInner, currentInner * yScale, rotation, 48)
            }
            Draw.reset()
        }

        private fun drawEllipse(cx: Float, cy: Float, rx: Float, ry: Float, rot: Float, seg: Int) {
            val cosR = Mathf.cos(rot)
            val sinR = Mathf.sin(rot)
            Lines.beginLine()
            for (i in 0..seg) {
                val angle = Mathf.PI2 * i / seg
                val px = Mathf.cos(angle) * rx
                val py = Mathf.sin(angle) * ry
                Lines.linePoint(
                    cx + px * cosR - py * sinR,
                    cy + px * sinR + py * cosR
                )
            }
            Lines.endLine()
        }
    }

    // ==================== 四芒星特效 ====================
    @JvmField
    val starBurst: Effect = StarBurstEffect()

    class StarBurstEffect : Effect() {
        var spread = 60f
        var spreadSpeed = 80f
        var delay = 0f
        var starColor = Color.valueOf("00ffff")
        var countMin = 20
        var countMax = 25
        var sizeMin = 4f
        var sizeMax = 10f
        var spinMin = 0.5f
        var spinMax = 1.5f

        init {
            layer = Layer.effect
        }

        fun setConfig(
            countMin: Int, countMax: Int,
            sizeMin: Float, sizeMax: Float,
            spinMin: Float, spinMax: Float,
            spread: Float, spreadSpeed: Float,
            color: Color,
            delay: Float = 0f
        ): StarBurstEffect {
            this.countMin = countMin
            this.countMax = countMax
            this.sizeMin = sizeMin
            this.sizeMax = sizeMax
            this.spinMin = spinMin
            this.spinMax = spinMax
            this.spread = spread
            this.spreadSpeed = spreadSpeed
            this.starColor = color
            this.delay = delay
            return this
        }

        override fun render(e: Effect.EffectContainer) {
            if (e.time < delay) return
            
            val elapsed = e.time - delay
            val lifetime = 60f
            val progress = Mathf.clamp(elapsed / lifetime)
            
            val alpha = 1f - progress
            Draw.color(starColor, alpha)
            Lines.stroke(2f)

            val seed = (e.data as? Int)?.toLong() ?: (Mathf.random(99999).toLong())
            val rand = Rand(seed)
            val finalCount = rand.random(countMin, countMax)

            for (i in 0 until finalCount) {
                val angle = rand.random(Mathf.PI2)
                val currentSpread = spread + spreadSpeed * progress * rand.random(0.5f, 1.5f)
                val dist = currentSpread * Mathf.sqrt(progress)
                
                val px = e.x + Mathf.cos(angle) * dist
                val py = e.y + Mathf.sin(angle) * dist
                
                val starSize = rand.random(sizeMin, sizeMax)
                val currentSize = starSize * (1f - progress * 0.5f)
                
                val rotSpeed = rand.random(spinMin, spinMax) * Mathf.PI2
                val rot = progress * rotSpeed + angle
                
                Lines.line(
                    px + Mathf.cos(rot) * currentSize, py + Mathf.sin(rot) * currentSize,
                    px - Mathf.cos(rot) * currentSize, py - Mathf.sin(rot) * currentSize
                )
                val rot2 = rot + Mathf.PI / 2f
                Lines.line(
                    px + Mathf.cos(rot2) * currentSize, py + Mathf.sin(rot2) * currentSize,
                    px - Mathf.cos(rot2) * currentSize, py - Mathf.sin(rot2) * currentSize
                )
            }
            
            Draw.reset()
        }
    }
}
