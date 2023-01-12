package deathitemprotection.deathitemprotection

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.wrappers.BlockPosition
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
import java.util.concurrent.TimeUnit

class DeathItemProtectionEvents : Listener {
    companion object {
        private val deathKey = NamespacedKey(Bukkit.getPluginManager().getPlugin("deathitemprotection")!!, "death_items")
        private val locationKey = NamespacedKey(Bukkit.getPluginManager().getPlugin("deathitemprotection")!!, "location")
        private val protocolManager = ProtocolLibrary.getProtocolManager()
    }


    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity
        for (item in event.drops) {
            val meta = item.itemMeta
            val dataContainer = meta.persistentDataContainer
            dataContainer.set(deathKey, PersistentDataType.STRING, player.uniqueId.toString())
            item.itemMeta = meta

            // Add hover text to show the timer and the owner's name
            val packet = protocolManager.createPacket(PacketType.Play.Server.WORLD_EVENT)
            packet.integers.write(0, 2001) // 2001 is the id of the "create marker" event
            val position = BlockPosition(item.location.x, item.location.y, item.location.z)
            packet.blockPositionModifier.write(0, position)
            packet.integers.write(1, 0) // Color of the marker, encoded as an ARGB integer (0 = white)
            packet.strings.write(0, "Item Owned by ${player.name}") // Hover text
            packet.integers.write(2, TimeUnit.SECONDS.toMillis(30).toInt()) // Lifetime of the marker in milliseconds
            protocolManager.sendServerPacket(player, packet)
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