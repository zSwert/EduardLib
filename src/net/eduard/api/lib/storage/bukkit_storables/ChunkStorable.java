package net.eduard.api.lib.storage.bukkit_storables;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import net.eduard.api.lib.Extra;
import net.eduard.api.lib.storage.Storable;

public class ChunkStorable implements Storable {

	@Override
	public Object restore(Object object) {
		if (object instanceof String) {
			String string = (String) object;
			String[] split = string.split(";");
			return Bukkit.getWorld(split[0]).getChunkAt(Extra.toInt(split[1]), Extra.toInt(split[2]));

		}
		return null;
	}

	@Override
	public Object store(Object object) {
		if (object instanceof Chunk) {
			Chunk chunk = (Chunk) object;
			return chunk.getWorld().getName() + ";" + chunk.getX() + ";" + chunk.getZ();
		}

		return null;
	}

	@Override
	public boolean saveInline() {
		return true;
	}

}
