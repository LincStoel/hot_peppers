package com.lincstoel.hotpeppers;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

// A wild, unfarmable pepper plant: a static, single-state ground plant, not a growing crop.
// Deliberately extends BushBlock (like Farmer's Delight's wild crops) rather than CropBlock:
// CropBlock.canSurvive() also requires getRawBrightness(pos, 0) >= 8, i.e. *computed* light, not
// a heightmap estimate. Worldgen's "features" chunk step (where patch_wild_hot_pepper runs) runs
// before the "light" step, so that light check is never satisfiable during placement - a
// CropBlock-based version of this block would place zero instances, ever, no matter the biome or
// rarity. BushBlock.canSurvive() only checks the ground tag below, which is safe during worldgen.
public class WildHotPepperBlock extends BushBlock {

    public static final MapCodec<WildHotPepperBlock> CODEC = simpleCodec(WildHotPepperBlock::new);

    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 10, 14);

    public WildHotPepperBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<WildHotPepperBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    // Deprecated like every other overload on this method (see ClientSetup), but it's still the
    // correct override point - CropBlock itself overrides this exact signature for the same
    // pick-block purpose.
    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(HotPeppers.HOT_PEPPER_SEEDS.get());
    }
}
