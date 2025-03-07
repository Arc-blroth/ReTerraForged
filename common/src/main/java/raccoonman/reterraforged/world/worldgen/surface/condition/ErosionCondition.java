package raccoonman.reterraforged.world.worldgen.surface.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;

class ErosionCondition extends ThresholdCondition {
	
	public ErosionCondition(Context context, Noise threshold, Noise variance) {
		super(context, threshold, variance);
	}

	@Override
	protected float sample(Cell cell) {
		return cell.localErosion2;
	}
	
	public record Source(Holder<Noise> threshold, Holder<Noise> variance) implements SurfaceRules.ConditionSource {
		public static final MapCodec<Source> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Noise.CODEC.fieldOf("threshold").forGetter(Source::threshold),
			Noise.CODEC.fieldOf("variance").forGetter(Source::variance)
		).apply(instance, Source::new));

		@Override
		public ErosionCondition apply(Context ctx) {
			return new ErosionCondition(ctx, this.threshold.value(), this.variance.value());
		}

		@Override
		public KeyDispatchDataCodec<Source> codec() {
			return new KeyDispatchDataCodec<>(CODEC);
		}
	}
}
