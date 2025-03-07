package raccoonman.reterraforged.world.worldgen.structure.rule;

import java.util.function.Function;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.RandomState;
import raccoonman.reterraforged.registries.RTFBuiltInRegistries;

public interface StructureRule {
    public static final Codec<StructureRule> CODEC = RTFBuiltInRegistries.STRUCTURE_RULE_TYPE.byNameCodec().dispatch(StructureRule::codec, Function.identity());

	boolean test(RandomState randomState, BlockPos pos);
	
	MapCodec<? extends StructureRule> codec();
}
