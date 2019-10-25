package com.flemmli97.wdr;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class WhiteListData extends WorldSavedData {

	private static final String id = "WLRData";

	private Map<UUID, String> roles = Maps.newHashMap();

	public WhiteListData(String id) {
		super(id);
	}

	public WhiteListData() {
		this(id);
	}

	public static WhiteListData get(World world) {
		MapStorage storage = world.getMapStorage();
		WhiteListData data = (WhiteListData) storage.getOrLoadData(WhiteListData.class, id);
		if (data == null) {
			data = new WhiteListData();
			storage.setData(id, data);
		}
		return data;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagCompound tag = nbt.getCompoundTag("Players");
		tag.getKeySet().forEach(s -> this.roles.put(UUID.fromString(s), tag.getString(s)));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound tag = new NBTTagCompound();
		this.roles.forEach((uuid, name) -> tag.setString(uuid.toString(), name));
		compound.setTag("Players", tag);
		return compound;
	}

	public boolean removePlayer(MinecraftServer server, String player) {
		GameProfile gameprofile1 = server.getPlayerProfileCache().getGameProfileForUsername(player);
		if (gameprofile1 == null)
			return false;
		this.roles.remove(gameprofile1.getId());
		this.markDirty();
		return true;
	}

	public boolean addPlayer(MinecraftServer server, String player) {
		GameProfile gameprofile1 = server.getPlayerProfileCache().getGameProfileForUsername(player);
		if (gameprofile1 == null)
			return false;
		this.roles.put(gameprofile1.getId(), gameprofile1.getName());
		this.markDirty();
		return true;
	}

	public boolean addPlayer(MinecraftServer server, UUID player) {
		GameProfile gameprofile1 = server.getPlayerProfileCache().getProfileByUUID(player);
		if (gameprofile1 == null)
			return false;
		this.roles.put(gameprofile1.getId(), gameprofile1.getName());
		this.markDirty();
		return true;
	}

	public boolean hasRole(EntityPlayer player) {
		return this.roles.keySet().contains(player.getUniqueID());
	}

	public Set<String> players(MinecraftServer server) {
		Map<UUID, String> toUpdate = Maps.newHashMap();
		this.roles.forEach((uuid, string) -> {
			GameProfile gameprofile1 = server.getPlayerProfileCache().getProfileByUUID(uuid);
			if (!gameprofile1.getName().equals(string))
				toUpdate.put(gameprofile1.getId(), gameprofile1.getName());
		});
		toUpdate.forEach((uuid, string) -> this.roles.put(uuid, string));
		return new TreeSet<String>(this.roles.values());
	}
}
