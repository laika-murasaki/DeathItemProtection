package deathitemprotection.deathitemprotection

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class DeathItemProtection : JavaPlugin() {
    var armorStandNameFormat = "Owner: %player%"
    override fun onEnable() {
        saveDefaultConfig()
        val config = config
        armorStandNameFormat = config.getString("armorstand-name-format")!!
        Bukkit.getPluginManager().registerEvents(DeathItemProtectionEvents(armorStandNameFormat), this)

    }
}