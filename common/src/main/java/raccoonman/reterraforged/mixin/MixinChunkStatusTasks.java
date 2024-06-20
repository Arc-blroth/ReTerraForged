package raccoonman.reterraforged.mixin;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.google.common.base.Suppliers;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.datafixers.util.Either;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.RTFRandomState;
import raccoonman.reterraforged.world.worldgen.WorldGenFlags;

@Mixin(ChunkStatusTasks.class)
public class MixinChunkStatusTasks {

	//structure starts
	@Inject(
		at = @At("HEAD"),
		method = "generateStructureStarts"
	)
	private static void generateStructureStarts$HEAD(WorldGenContext worldGenContext, ChunkStep chunkStep, StaticCache2D<GenerationChunkHolder> staticCache2D, ChunkAccess chunkAccess, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> callback) {
		RandomState randomState = worldGenContext.level().getChunkSource().randomState();
		if((Object) randomState instanceof RTFRandomState rtfRandomState) {
			ChunkPos chunkPos = chunkAccess.getPos();
			@Nullable
			GeneratorContext context = rtfRandomState.generatorContext();
			
			if(context != null) {
				context.cache.queueAtChunk(chunkPos.x, chunkPos.z);

				WorldGenFlags.setFastCellLookups(false);
			}
		}
	}
	
	@Inject(
		at = @At("TAIL"),
		method = "generateStructureStarts"
	)
	private static void generateStructureStarts$TAIL(WorldGenContext worldGenContext, ChunkStep chunkStep, StaticCache2D<GenerationChunkHolder> staticCache2D, ChunkAccess chunkAccess, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> callback) {
		RandomState randomState = worldGenContext.level().getChunkSource().randomState();
		if((Object) randomState instanceof RTFRandomState rtfRandomState) {
			@Nullable
			GeneratorContext context = rtfRandomState.generatorContext();
			if(context != null) {
				WorldGenFlags.setFastCellLookups(true);
			}
		}
	}

	//features
	@Inject(
		at = @At("TAIL"),
		method = "generateFeatures"
	)
	private static void generateFeatures(WorldGenContext worldGenContext, ChunkStep chunkStep, StaticCache2D<GenerationChunkHolder> staticCache2D, ChunkAccess chunkAccess, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> callback) {
		RandomState randomState = worldGenContext.level().getChunkSource().randomState();
		if((Object) randomState instanceof RTFRandomState rtfRandomState) {
			ChunkPos chunkPos = chunkAccess.getPos();
			@Nullable
			GeneratorContext context = rtfRandomState.generatorContext();
			
			if(context != null) {
				context.cache.dropAtChunk(chunkPos.x, chunkPos.z);
			}
		}
	}
}
