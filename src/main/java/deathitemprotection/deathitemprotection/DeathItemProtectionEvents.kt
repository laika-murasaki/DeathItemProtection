package deathitemprotection.deathitemprotection

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.ItemMergeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.persistence.PersistentDataType
import java.util.*

class DeathItemProtectionEvents : Listener {
    companion object {
        private val deathKey = NamespacedKey(Bukkit.getPluginManager().getPlugin("deathitemprotection")!!, "death_items")
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity
        for (item in event.drops) {
            val meta = item.itemMeta
            val dataContainer = meta.persistentDataContainer
            dataContainer.set(deathKey, PersistentDataType.STRING, player.uniqueId.toString())
            item.itemMeta = meta
        }
    }

    @EventHandler
    fun onItemMerge(event: ItemMergeEvent) {
        val target = event.target.itemStack
        val meta = target.itemMeta
        if (meta != null) {
            val dataContainer = meta.persistentDataContainer
            if (dataContainer.has(deathKey, PersistentDataType.STRING)) {
                val value = dataContainer.get(deathKey, PersistentDataType.STRING)
                if (value != null) {
                    val uuid = UUID.fromString(value)
                    val player = Bukkit.getPlayer(uuid)
                    if (player != null && event.entity.uniqueId != player.uniqueId) {
                        event.isCancelled = true
                    }
                }
            }
        }
    }

    @EventHandler
    fun onEntityPickupItem(event: EntityPickupItemEvent) {
        if (event.entity is Player) {
            val item = event.item.itemStack
            val meta = item.itemMeta
            if (meta != null) {
                val dataContainer = meta.persistentDataContainer
                if (dataContainer.has(deathKey, PersistentDataType.STRING)) {
                    val value = dataContainer.get(deathKey, PersistentDataType.STRING)
                    if (value != null) {
                        val uuid = UUID.fromString(value)
                        val player = event.entity as Player
                        if (player.uniqueId != uuid) {
                            event.isCancelled = true
                        }
                    }
                }
            }
        }
    }
}