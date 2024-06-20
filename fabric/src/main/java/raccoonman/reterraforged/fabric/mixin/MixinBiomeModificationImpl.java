package raccoonman.reterraforged.fabric.mixin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import com.google.common.base.Suppliers;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.impl.biome.modification.BiomeModificationImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import raccoonman.reterraforged.registries.RTFRegistries;
import raccoonman.reterraforged.world.worldgen.biome.modifier.BiomeModifier;
import raccoonman.reterraforged.world.worldgen.biome.modifier.fabric.FabricBiomeModifier;

@Mixin(BiomeModificationImpl.class)
public class MixinBiomeModificationImpl {
	@Shadow(remap = false)
	@Final
	private static Comparator<Object> MODIFIER_ORDER_COMPARATOR;
	
	private static final Constructor<?> CTOR;
	
	@Redirect(
		method = "finalizeWorldGen",
		at = @At(
			value = "INVOKE",
			target = "getSortedModifiers"
		),
		remap = false
	)
	public List<Object> getSortedModifiers(BiomeModificationImpl self, RegistryAccess registries) {
		List<Object> modifiers = new ArrayList<>(this.getSortedModifiers());
		for(Holder.Reference<BiomeModifier> holder : registries.lookupOrThrow(RTFRegistries.BIOME_MODIFIER).listElements().toList()) {
			if(holder.value() instanceof FabricBiomeModifier modifier) {
				modifiers.add(this.makeModifierRecord(holder.key().location(), ModificationPhase.POST_PROCESSING, (ctx) -> {
					return true;
				}, modifier::apply));
			}
		}
		modifiers.sort(MODIFIER_ORDER_COMPARATOR);
		return modifiers;
    }

	// NOTE(arc) I am so confused on why biomes features magically appear here
	@Inject(
		method = "finalizeWorldGen",
		at = @At("TAIL"),
		remap = false
	)
	private void forciblyRememoizeFeaturesPerStep(RegistryAccess impl, CallbackInfo ci) {
		impl.registryOrThrow(Registries.LEVEL_STEM).stream().forEach(dimensionOptions -> {
			dimensionOptions.generator().featuresPerStep = Suppliers.memoize(
				() -> FeatureSorter.buildFeaturesPerStep(
					List.copyOf(dimensionOptions.generator().getBiomeSource().possibleBiomes()),
					biomeEntry -> dimensionOptions.generator().getBiomeGenerationSettings(biomeEntry).features(),
					true
				)
			);
		});
	}
	
	@Shadow(remap = false)
	private List<Object> getSortedModifiers() {
		throw new UnsupportedOperationException();
	}
	
	private Object makeModifierRecord(ResourceLocation id, ModificationPhase phase, Predicate<BiomeSelectionContext> selector, BiConsumer<BiomeSelectionContext, BiomeModificationContext> modifier) {
		try {
			return CTOR.newInstance(phase, id, selector, modifier);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	static {
		Constructor<?> ctor;
		try {
			ctor = Class.forName("net.fabricmc.fabric.impl.biome.modification.BiomeModificationImpl$ModifierRecord").getDeclaredConstructor(ModificationPhase.class, ResourceLocation.class, Predicate.class, BiConsumer.class);
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
			ctor = null;
		}
		CTOR = ctor;
	}
}
