package ce

import arc.graphics.Color
import arc.math.Mathf
import mindustry.gen.Unit
import mindustry.type.UnitType

class CustomUnit : UnitType("custom-unit") {
    init {
        // ================= 基础数值区 =================
        health = 1500f
        armor = 10f
        speed = 1.5f
        flying = true
        lowAltitude = true
        hitSize = 20f
        engineOffset = 15f
    }

    override fun update(unit: Unit) {
        super.update(unit)

        // 1. 旋转立方体特效
        // 使用 unit.id 和当前时间做正弦运算，大小在 224 到 240 之间浮动
        // 注意：Mathf.sin() 返回 Double，所以必须加 .toFloat() 强转
        val breathSize = 224f + Mathf.sin(unit.id + arc.util.Time.time * 0.1f).toFloat() * 16f
        
        // 直接实例化 Effect 并调用，不用管原本的 setConfig 返回类型
        RotatingCubeFX.RotatingCubeEffect().apply {
            cubeSize = breathSize
            color = Color.valueOf("ff3333")
            strokeWidth = 2f // 0.25格 = 2像素
            rotationSpeed = 2f
        }.at(unit.x, unit.y)

        // 2. 瞄准框特效（保持原样，但同样直接实例化避开类型推断）
        if (Mathf.chanceDelta(0.05)) {
            TargetBoxFX.TargetBoxEffect().apply {
                boxSize = 80f
                color = Color.white
            }.at(unit.x, unit.y)
        }
    }
}
