package ce

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Mathf
import mindustry.entities.Effect
import mindustry.graphics.Layer

object CoolEffectsFX {

    // ==================== 圆环特效 ====================
    // 水平环：tiltAngle = 0，yScale = 1（正圆）
    @JvmField
    val ringHorizontal: Effect = RingEffect(0f, 0f)

    // 偏转环：tiltAngle = 60°，rotation = 30°
    @JvmField
    val ringTilted: Effect = RingEffect(60f, 30f)

    class RingEffect(
        /** 偏转角度（度）：0 = 与地面平行（正圆），60 = 绕横向轴偏转 60°（压扁成椭圆） */
        var tiltAngle: Float,
        /** 绕中心旋转角度（度）：只影响椭圆朝向，不影响压扁程度 */
        var rotationDeg: Float
    ) : Effect() {
        /** 尺寸：空心内半径 = size * 2，环宽 = size * 0.5 */
        var size: Float = 10f
        /** 生命周期（tick），60 tick = 1 秒 */
        var lifetime: Float = 120f
        /** 延迟（tick），第二个环用 30f = 0.5 秒 */
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
            // 这里绝对不能有任何 sound 相关的代码，这是导致第 57 行报错的原因
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
        
            val lifetime = 200f // 特效总时长
            val progress = Mathf.clamp(e.time / lifetime)
            
            // 提取并预先生成固定的随机数（防止每帧闪烁）
            val seed = (e.data as? Int)?.toLong() ?: 123456L
            val rand = Rand(seed)
            val smallRingCount = rand.random(5, 10)
            val triangleCount = rand.random(3, 5)
            
            // 预生成小圆环的偏移位置和延迟
            val smallRings = Array(smallRingCount) {
                Triple(
                    rand.random(360f),
                    rand.random(40f, shockwaveRadius * 0.8f),
                    rand.random(60f, 120f)
                )
            }

            // ================= 1. 大圆环 & 屏幕特效 =================
            if (e.time < 60f) {
                // 屏幕短暂闪白（前3帧）
                if (e.time.toInt() in 0..2) {
                    Draw.blend(Blending.additive)
                    Draw.color(Color.white)
                    Draw.alpha(0.8f - e.time * 0.25f)
                    Fill.rect(Core.camera.position.x, Core.camera.position.y, Core.camera.width, Core.camera.height)
                    Draw.blend()
                }

                // 中心白色三角形向外炸开
                val triProgress = Mathf.clamp(e.time / 20f)
                Draw.color(Color.white, 1f - triProgress)
                for (i in 0 until triangleCount) {
                    val angle = (360f / triangleCount) * i + (rand.random(30f))
                    val len = 150f * Mathf.sin(triProgress * Mathf.PI)
                    val wid = 40f * Mathf.sin(triProgress * Mathf.PI)
                    Drawf.tri(e.x, e.y, wid, len, angle)
                }

                // 渐变圆环
                val ringProgress = Mathf.clamp(e.time / 50f)
                val radius = shockwaveRadius * Interp.pow3Out.apply(ringProgress)
                drawGradientRing(e.x, e.y, radius, 30f, ringColor, 1f - ringProgress)

                // ====== 新增：大圆环上的随机三角形尖刺 ======
                val spikeCount = 24 // 尖刺数量
                Draw.color(ringColor, 1f - ringProgress)
                for (i in 0 until spikeCount) {
                    // 均匀分布在圆环上，加上一点随机偏移
                    val angle = (360f / spikeCount) * i + rand.random(-10f, 10f)
                    
                    // 随机长度：大圆环半径的 10% 到 60%
                    val triLength = radius * rand.random(0.1f, 0.6f)
                    val triWidth = 15f * (1f - ringProgress) // 三角形底部宽度

                    val px = e.x + Mathf.cosDeg(angle) * radius
                    val py = e.y + Mathf.sinDeg(angle) * radius

                    Drawf.tri(px, py, triWidth, triLength, angle)
                }
            }

            // ================= 2. 随机小圆环 & 四芒星 =================
            smallRings.forEach { (angle, dist, delay) ->
                if (e.time > delay) {
                    val smallElapsed = e.time - delay
                    val smallProgress = Mathf.clamp(smallElapsed / 60f)
                    
                    if (smallProgress < 1f) {
                        val sx = e.x + Mathf.cosDeg(angle) * dist
                        val sy = e.y + Mathf.sinDeg(angle) * dist
                        
                        // 小渐变圆环
                        val sRadius = 40f * Interp.pow2Out.apply(smallProgress)
                        drawGradientRing(sx, sy, sRadius, 8f, ringColor, 1f - smallProgress)
                        
                        // X型四芒星
                        val starAlpha = 1f - smallProgress
                        Draw.color(starColor, starAlpha)
                        Lines.stroke(2f)
                        val starSize = 15f * (1f - smallProgress * 0.5f)
                        Lines.line(sx - starSize * 0.7f, sy - starSize * 0.7f, sx + starSize * 0.7f, sy + starSize * 0.7f)
                        Lines.line(sx - starSize * 0.7f, sy + starSize * 0.7f, sx + starSize * 0.7f, sy - starSize * 0.7f)
                    }
                }
            }

            // ================= 3. 地面残留火焰 =================
            if (e.time > 50f) {
                val fireProgress = Mathf.clamp((e.time - 50f) / 150f)
                val fireAlpha = 1f - fireProgress
                
                for (i in 0 until smallRingCount) {
                    val (angle, dist, _) = smallRings[i]
                    val fx = e.x + Mathf.cosDeg(angle) * dist
                    val fy = e.y + Mathf.sinDeg(angle) * dist
                    
                    val flicker = Mathf.absin(e.time * 0.1f + i * 10f, 1f, 0.3f) + 0.7f
                    
                    Draw.color(fireColor, fireAlpha)
                    Fill.circle(fx, fy, 15f * flicker * fireAlpha)
                    
                    Draw.color(Color.valueOf("ffcc00"), fireAlpha)
                    Fill.circle(fx, fy, 8f * flicker * fireAlpha)
                }
            }

            Draw.reset()
        }

        // 画渐变圆环的自定义方法
        private fun drawGradientRing(cx: Float, cy: Float, radius: Float, thickness: Float, color: Color, alpha: Float) {
            val innerRadius = Math.max(0f, radius - thickness)
            
            // 画最外圈清晰边缘
            Lines.stroke(2f)
            Lines.circle(cx, cy, radius)
            Draw.color(color, alpha)

            // 画内圈渐变
            for (j in 0 until 5) {
                val r = innerRadius + (thickness / 5f) * j
                Draw.color(color, alpha * (1f - j / 5f))
                Lines.stroke((thickness / 5f) * 1.5f)
                Lines.circle(cx, cy, r)
            }
        }
    }
}
