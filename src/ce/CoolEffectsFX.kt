package ce

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Mathf
import mindustry.entities.Effect
import mindustry.graphics.Layer

object CoolEffectsFX {
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
        fun setTilt(tiltDeg: Float, rotationDeg: Float): RingEffect {
            this.tiltAngle = tiltDeg
            this.rotationDeg = rotationDeg
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
}
