package raccoonman.reterraforged.world.worldgen.noise.domain;

import com.mojang.serialization.MapCodec;

import raccoonman.reterraforged.world.worldgen.noise.module.Noise.Visitor;

public record DirectWarp() implements Domain {
	public static final MapCodec<DirectWarp> CODEC = MapCodec.unit(DirectWarp::new);
	
	@Override
	public float getOffsetX(float x, float z, int seed) {
		return 0.0F;
	}

	@Override
	public float getOffsetZ(float x, float z, int seed) {
		return 0.0F;
	}

	@Override
	public Domain mapAll(Visitor visitor) {
		return this;
	}

	@Override
	public MapCodec<DirectWarp> codec() {
		return CODEC;
	}
}
