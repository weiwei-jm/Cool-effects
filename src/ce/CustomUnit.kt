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
        
        // 必须给他一把武器，他才会去锁定敌人
        weapons.add(Weapon("dummy-weapon").apply {
            x = 0f
            y = 0f
            reload = 999999f
            bullet = BulletType().apply {
                damage = 0f
                range = 600f
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
            lifetime = 99999f
        }.render(createContainer(unit.x, unit.y))

        // 2. 锁定敌人时的瞄准框
        // 强制把 target 当作 Unit 处理，并使用 Unit 的属性
        val target = unit.target() as? Unit
        
        if (target != null && target.isValid) {
            TargetBoxFX.TargetBoxEffect().apply {
                boxSize = 60f
                // 直接用 target.health 和 target.maxHealth 属性
                val healthFrac = target.health / target.maxHealth
                color = Color.white.cpy().lerp(Color.valueOf("ff3333"), 1f - healthFrac)
                lifetime = 99999f
            }.render(createContainer(target.x, target.y))
        }
    }

    private fun createContainer(x: Float, y: Float): Effect.EffectContainer {
        val container = Effect.EffectContainer()
        container.x = x
        container.y = y
        container.time = 0f
        return container
    }
}
