package ce

import arc.graphics.Color
import arc.math.Mathf
import mindustry.gen.Unit
import mindustry.type.UnitType

object CustomUnits {
    @JvmField
    val testUnit: UnitType = CustomUnit()
}

class CustomUnit : UnitType("ce-custom-unit") {
    init {
        // 基础数值
        health = 1500f
        armor = 10f
        speed = 1.5f
        flying = true
        lowAltitude = true
        hitSize = 20f
        engineOffset = 15f
    }

override fun draw(unit: Unit) {
    // 先画单位原本的贴图
    super.draw(unit)
    
    // ====== 1. 自己身上的旋转立方体 ======
    val breathSize = 224f + Mathf.sin(arc.util.Time.time * 0.1f) * 16f
    
    RotatingCubeFX.RotatingCubeEffect().apply {
        cubeSize = breathSize
        color = Color.valueOf("ff3333")
        strokeWidth = 2f
        rotationSpeed = 2f
        lifetime = 99999f 
    }.render(createContainer(unit.x, unit.y, 0f))

    // ====== 2. 锁定敌人时的瞄准框 ======
    // unit.target 就是单位当前锁定的目标（如果没有目标就是 null）
    val target = unit.target
    
    if (target != null && target.isValid) {
        // 如果目标存在且还活着，就把瞄准框画在目标的位置
        TargetBoxFX.TargetBoxEffect().apply {
            boxSize = 60f // 瞄准框大小，可调
            // 根据敌人的血量变颜色：满血是白色，残血变成深红色
            color = Color.white.cpy().lerp(Color.valueOf("ff3333"), 1f - (target.health / target.maxHealth))
            lifetime = 99999f
        }.render(createContainer(target.x, target.y, 0f))
    }
}
