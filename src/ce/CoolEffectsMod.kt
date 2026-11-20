package ce

import mindustry.mod.Mod

class CoolEffectsMod : Mod() {
    
    override fun loadContent() {
        // 强制唤醒我们的单位
        val unit = CustomUnits.testUnit
        arc.util.Log.info("Cool Effects: Custom Unit Loaded!")
    }
}
