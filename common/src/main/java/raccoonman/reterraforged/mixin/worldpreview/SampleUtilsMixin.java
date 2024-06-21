package raccoonman.reterraforged.mixin.worldpreview;

import java.net.Proxy;
import java.nio.file.Path;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldOptions;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.world.worldgen.RTFRandomState;

@Pseudo
@Mixin(targets = "caeruleusTait.world.preview.backend.worker.SampleUtils", remap = false)
public class SampleUtilsMixin {
	@Shadow 
	@Final
    private RandomState randomState;
	@Shadow
	@Final
	private RegistryAccess registryAccess;
	
	@Inject(
		at = @At("TAIL"),
		require = 1,
		target = @Desc(
			value = "<init>",
			args = { 
				MinecraftServer.class, BiomeSource.class, ChunkGenerator.class, WorldOptions.class, LevelStem.class, LevelHeightAccessor.class
			}
		)
//		method = "<init>(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/world/level/biome/BiomeSource;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/level/levelgen/WorldOptions;Lnet/minecraft/world/level/dimension/LevelStem;Lnet/minecraft/world/level/LevelHeightAccessor;)V",
	)
	private void SampleUtils$1(CallbackInfo callback) {
		this.initRTF();
	}

	@Inject(
		at = @At("TAIL"), 
		require = 1,
		target = @Desc(
			value = "<init>",
			args = { 
				BiomeSource.class, ChunkGenerator.class, LayeredRegistryAccess.class, WorldOptions.class, LevelStem.class, LevelHeightAccessor.class, WorldDataConfiguration.class, Proxy.class, Path.class 
			}
		)
//		method = "<init>(Lnet/minecraft/world/level/biome/BiomeSource;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/core/LayeredRegistryAccess;Lnet/minecraft/world/level/levelgen/WorldOptions;Lnet/minecraft/world/level/dimension/LevelStem;Lnet/minecraft/world/level/LevelHeightAccessor;Lnet/minecraft/world/level/WorldDataConfiguration;Ljava/net/Proxy;Ljava/nio/file/Path;)V"
	) 
	private void SampleUtils$2(CallbackInfo callback) {
		this.initRTF();
	}
	
	private void initRTF() {
		if((Object) this.randomState instanceof RTFRandomState rtfRandomState) {
			rtfRandomState.initialize(this.registryAccess);
			
			RTFCommon.LOGGER.info("initialized rtf data");
		} else {
			throw new IllegalStateException();
		}
	}
	
	static {
		System.out.println("MARKER");
	}
}
