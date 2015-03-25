package joshie.crafting.rewards;

import java.util.UUID;

import joshie.crafting.api.Bus;
import joshie.crafting.api.CraftingAPI;
import joshie.crafting.api.IReward;
import joshie.crafting.plugins.minetweaker.Rewards;
import minetweaker.MineTweakerAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@ZenClass("mods.craftcontrol.rewards.FallDamage")
public class RewardFallDamage extends RewardBase {
	private int maxAbsorbed;
	
	public RewardFallDamage() {
		super("Fall Damage");
	}
	
	@Override
	public Bus getBusType() {
		return Bus.FORGE;
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingFallEvent event) {
		if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            int damage = (int) (event.distance - 3);
            int maxAbsorbed = CraftingAPI.players.getPlayerData(null).getFallDamagePrevention();
            if (damage < maxAbsorbed) {
            	event.setCanceled(true);
            } else {
            	event.distance = event.distance - maxAbsorbed;
            }
        }
	}
	
	@ZenMethod
	public void add(String unique, int maxAbsorbed) {
		RewardFallDamage reward = new RewardFallDamage();
		reward.maxAbsorbed = maxAbsorbed;		
		MineTweakerAPI.apply(new Rewards(unique, reward));
	}
	
	@Override
	public IReward deserialize(JsonObject data) {
		RewardFallDamage reward = new RewardFallDamage();
		reward.maxAbsorbed = data.get("Max Absorption").getAsInt();
		return reward;
	}

	@Override
	public void serialize(JsonObject elements) {
		elements.addProperty("Max Absorption", maxAbsorbed);
	}
	
	@Override
	public void reward(UUID uuid) {
		CraftingAPI.players.getServerPlayer(uuid).addFallDamagePrevention(maxAbsorbed);
	}
}
