package deathitemprotection.deathitemprotection

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class DeathItemProtection : JavaPlugin() {
    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(DeathItemProtectionEvents(), this)
    }
}