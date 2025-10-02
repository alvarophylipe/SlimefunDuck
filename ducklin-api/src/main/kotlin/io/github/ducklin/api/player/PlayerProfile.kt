package io.github.ducklin.api.player

import io.github.bakedlibs.dough.config.Config
import org.bukkit.OfflinePlayer
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class PlayerProfile(
    protected val p: OfflinePlayer,
    protected val data: PlayerData
) {
    val loading: Map<UUID, Boolean> = ConcurrentHashMap()

    val uuid: UUID
    val name: String

    val config: Config

}
