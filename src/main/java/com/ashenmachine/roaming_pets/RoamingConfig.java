package com.ashenmachine.roaming_pets;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Per-player roam radius (blocks) set with /roaming <radius>. 0 means unlimited. Resets on server restart. */
public final class RoamingConfig {
	public static final int DEFAULT_RADIUS = 16;

	private static final Map<UUID, Integer> RADII = new ConcurrentHashMap<>();

	private RoamingConfig() {
	}

	public static int radius(UUID player) {
		return RADII.getOrDefault(player, DEFAULT_RADIUS);
	}

	public static void setRadius(UUID player, int radius) {
		RADII.put(player, radius);
	}
}
