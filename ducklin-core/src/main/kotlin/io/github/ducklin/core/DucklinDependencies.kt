package io.github.ducklin.core

import io.github.ducklin.api.DucklinAPI
import io.github.ducklin.api.services.BlockDataService
import io.github.ducklin.api.services.ItemDataService
import io.github.ducklin.core.services.CustomItemDataService

class DucklinDependencies(val plugin: Slimefun) : DucklinAPI {

    override val itemDataService: ItemDataService = CustomItemDataService(plugin, "slimefun_item")
    override val blockDataService: BlockDataService = io.github.ducklin.core.services.BlockDataService

}