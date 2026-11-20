package ce

import arc.graphics.Color
import arc.math.Mathf
import arc.util.Time
import mindustry.entities.Effect
import mindustry.gen.Unit
import mindustry.type.UnitType
import mindustry.type.Weapon
import mindustry.entities.bullet.BulletType
import mindustry.graphics.Layer

object CustomUnits {
    @JvmField
    val testUnit: UnitType = CustomUnit()
}

class CustomUnit : UnitType("ce-custom-unit") {
    init {
        health = 1500f
        armor = 10f
        speed = 1.5f
        flying = true
        lowAltitude = true
        hitSize = 20f
        engineOffset = 15f
        
        weapons.add(Weapon("dummy-weapon").apply {
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

        // 把当前游戏时间作为时间流逝，这样它就会一直动！
        val currentTime = Time.time 

        // ================= 1. 旋转立方体 =================
        val breathSize = 224f + Mathf.sin(currentTime * 0.05f) * 16f
        
        RotatingCubeFX.RotatingCubeEffect().apply {
            cubeSize = breathSize
            color = Color.valueOf("ff3333")
            strokeWidth = 2f
            rotationSpeed = 2f
            // 关键：生命周期依然设为 2 秒，但通过外部传入时间让它一直转
            lifetime = 120f 
        }.render(createContainer(unit.x, unit.y, currentTime))

        // ================= 2. 瞄准框 =================
        val target = mindustry.entities.Units.closestEnemy(unit.team, unit.x, unit.y, 600f) { u -> u != unit }
        
        if (target != null && target.isValid) {
            TargetBoxFX.TargetBoxEffect().apply {
                boxSize = 60f
                val healthFrac = target.health / target.maxHealth
                color = Color.white.cpy().lerp(Color.valueOf("ff3333"), 1f - healthFrac)
                lifetime = 120f // 同样给它 2 秒的周期
            }.render(createContainer(target.x, target.y, currentTime))
        }
    }

    // 传入时间参数
    private fun createContainer(x: Float, y: Float, time: Float): Effect.EffectContainer {
        val container = Effect.EffectContainer()
        container.x = x
        container.y = y
        // 这里的取余运算（% lifetime）会循环时间，让特效一直播放下去
        // 120f 是我们在上面设置的 lifetime
        container.time = time % 120f 
        return container
    }
}
