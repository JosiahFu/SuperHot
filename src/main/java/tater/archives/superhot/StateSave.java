package tater.archives.superhot;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.Objects;

public class StateSave extends PersistentState {

    public Boolean superhotMode = false;

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putBoolean("superhotMode", superhotMode);
        return nbt;
    }

    public static StateSave createFromNbt(NbtCompound tag) {
        StateSave state = new StateSave();
        state.superhotMode = tag.getBoolean("superhotMode");
        return state;
    }

    private static final Type<StateSave> type = new Type<>(
            StateSave::new, // If there's no 'StateSaverAndLoader' yet create one
            StateSave::createFromNbt, // If there is a 'StateSaverAndLoader' NBT, parse it with 'createFromNbt'
            null // Supposed to be an 'DataFixTypes' enum, but we can just pass null
    );

    public static StateSave getServerState(MinecraftServer server) {
        // (Note: arbitrary choice to use 'World.OVERWORLD' instead of 'World.END' or 'World.NETHER'.  Any work)
        PersistentStateManager persistentStateManager = Objects.requireNonNull(server.getWorld(World.OVERWORLD)).getPersistentStateManager();

        // The first time the following 'getOrCreate' function is called, it creates a brand new 'StateSaverAndLoader' and
        // stores it inside the 'PersistentStateManager'. The subsequent calls to 'getOrCreate' pass in the saved
        // 'StateSaverAndLoader' NBT on disk to our function 'StateSaverAndLoader::createFromNbt'.
        StateSave state = persistentStateManager.getOrCreate(type, SuperHot.MOD_ID);

        // If state is not marked dirty, when Minecraft closes, 'writeNbt' won't be called and therefore nothing will be saved.
        // Technically it's 'cleaner' if you only mark state as dirty when there was actually a change, but the vast majority
        // of mod writers are just going to be confused when their data isn't being saved, and so it's best just to 'markDirty' for them.
        // Besides, it's literally just setting a bool to true, and the only time there's a 'cost' is when the file is written to disk when
        // there were no actual change to any of the mods state (INCREDIBLY RARE).
        state.markDirty();

        return state;
    }
}
