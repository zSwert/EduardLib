package net.eduard.api.lib.game;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface ClickEffect {
	public void onClick(InventoryClickEvent event, int page);
}
