package tater.archives.superhot;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.tick.TickManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SuperHot implements ModInitializer {
	public static final String MOD_ID = "superhot";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final double MAX_SPEED = 0.28; // Achieved while sprinting
	private static final int MAX_TICK_RATE = 20;

	private static VelocityWatcher velocityWatcher = null;


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ServerTickEvents.END_WORLD_TICK.register(world -> {
			final List<ServerPlayerEntity> players = world.getPlayers();
			if (players.isEmpty()) return;
			final ServerPlayerEntity player = players.get(0);

			if (velocityWatcher == null || velocityWatcher.entity != player) {
				velocityWatcher = new VelocityWatcher(player);
			}

			TickManager manager = world.getTickManager();

			if (!StateSave.getServerState(world.getServer()).superhotMode) {
				if (manager.getTickRate() == MAX_TICK_RATE) return;
				manager.setTickRate(MAX_TICK_RATE);
				return;
			}

			final double speed = velocityWatcher.getEntityVelocity();
			final int tickRate = Math.min(Math.max((int) (MAX_TICK_RATE * speed / MAX_SPEED), 1), MAX_TICK_RATE);
			if (manager.getTickRate() == tickRate) return;
			world.getTickManager().setTickRate(tickRate);
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("superhot")
				.then(CommandManager.literal("on")
						.executes(context -> {
							StateSave.getServerState(context.getSource().getServer()).superhotMode = true;
							context.getSource().sendMessage(Text.literal("Superhot mode activated"));
							return 1;
						})
				)
				.then(CommandManager.literal("off")
						.executes(context -> {
							StateSave.getServerState(context.getSource().getServer()).superhotMode = false;
							context.getSource().sendMessage(Text.literal("Superhot mode deactivated"));
							return 1;
						})
				)
		));

		LOGGER.info("Hello Fabric world!");
	}
}
