package ce

import arc.graphics.Color
import arc.math.Mathf
import mindustry.gen.Unit
import mindustry.type.UnitType

class CustomUnit : UnitType("custom-unit") {
    init {
        // ================= 基础数值区 =================
        health = 1500f          // 血量
        armor = 10f             // 护甲
        speed = 1.5f            // 移速
        flying = true           // 飞行单位
        lowAltitude = true      // 低空飞行
        hitSize = 20f           // 碰撞体积
        engineOffset = 15f      // 引擎位置
    }

    // ================= 持续特效逻辑 =================
    override fun update(unit: Unit) {
        super.update(unit)

        // 1. 旋转立方体特效
        // 因为是 30 格，而游戏里 1 格 = 8 个单位像素，所以半径是 30 * 8 = 240
        // 每帧更新，并让大小在 28格(224) 到 30格(240) 之间浮动
        val breathSize = 224f + Mathf.sin(unit.id + arc.util.Time.time * 0.1f) * 16f
        
        RotatingCubeFX.rotatingCube.setConfig(
            breathSize, 
            Color.valueOf("ff3333"), // 参考 collapser 的红色
            0.25f * 8f,               // 粗细 0.25格 = 2像素
            2f                        // 旋转速度
        ).at(unit.x, unit.y)

        // 2. 瞄准框特效（我们之前写的 TargetBoxFX）
        // 为了防止太瞎眼，我们可以给它加一点随机性，让它每秒更新一次
        if (Mathf.chanceDelta(0.05f)) {
            TargetBoxFX.targetBox.setConfig(80f, Color.white).at(unit.x, unit.y)
        }
    }
}
