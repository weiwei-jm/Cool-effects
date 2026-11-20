package ce

import arc.graphics.Color
import arc.math.Mathf
import arc.util.Time
import mindustry.entities.Effect
import mindustry.gen.Unit
import mindustry.type.UnitType
import mindustry.type.Weapon
import mindustry.entities.bullet.BulletType
import mindustry.gen.Healthc

object CustomUnits {
    @JvmField
    val testUnit: UnitType = CustomUnit()
}

class CustomUnit : UnitType("ce-custom-unit") {
    init {
        // ================= 基础数值区 =================
        health = 1500f
        armor = 10f
        speed = 1.5f
        flying = true
        lowAltitude = true
        hitSize = 20f
        engineOffset = 15f
        
        // 必须给他一把武器，他才会去锁定敌人（射程很远，但没伤害）
        weapons.add(Weapon("dummy-weapon").apply {
            x = 0f
            y = 0f
            reload = 999999f // 永远不开火，只用来让 AI 找敌人
            bullet = BulletType().apply {
                damage = 0f
                range = 600f // 600像素的索敌范围
                speed = 0f
            }
        })
    }

    override fun draw(unit: Unit) {
        super.draw(unit)
        
        // 1. 自己身上的旋转立方体
        val breathSize = 224f + Mathf.sin(Time.time * 0.1f) * 16f
        
        RotatingCubeFX.RotatingCubeEffect().apply {
            cubeSize = breathSize
            color = Color.valueOf("ff3333")
            strokeWidth = 2f
            rotationSpeed = 2f
            lifetime = 99999f // 强制常驻
        }.render(createContainer(unit.x, unit.y))

        // 2. 锁定敌人时的瞄准框
        val target = unit.target
        // 检查目标是不是一个活着的单位（健康组件）
        if (target != null && target is Healthc && target.isValid) {
            TargetBoxFX.TargetBoxEffect().apply {
                boxSize = 60f
                // 根据血量变色：满血白色 -> 残血红色
                color = Color.white.cpy().lerp(Color.valueOf("ff3333"), 1f - (target.health / target.maxHealth))
                lifetime = 99999f
            }.render(createContainer(target.x, target.y))
        }
    }

    // 创建一个临时的 EffectContainer 给特效渲染使用
    private fun createContainer(x: Float, y: Float): Effect.EffectContainer {
        val container = Effect.EffectContainer()
        container.x = x
        container.y = y
        container.time = 0f
        return container
    }
}
