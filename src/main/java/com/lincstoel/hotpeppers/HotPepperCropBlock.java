package com.lincstoel.hotpeppers;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HotPepperCropBlock extends CropBlock {

    public static final MapCodec<HotPepperCropBlock> CODEC = simpleCodec(HotPepperCropBlock::new);

    public static final TagKey<Item> SAFE_HARVEST_TOOLS =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(HotPeppers.MOD_ID, "safe_harvest_tools"));

    private static final int MAX_AGE = 3;
    private static final VoxelShape[] SHAPE_BY_AGE = {
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(0, 0, 0, 16, 4, 16),
            Block.box(0, 0, 0, 16, 6, 16),
            Block.box(0, 0, 0, 16, 8, 16),
    };

    public HotPepperCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<HotPepperCropBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_AGE[state.getValue(getAgeProperty())];
    }

    @Override
    protected IntegerProperty getAgeProperty() {
        return BlockStateProperties.AGE_3;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    // CropBlock.createBlockStateDefinition() adds its own hardcoded AGE_7 field rather than calling
    // getAgeProperty(), so this override is needed to actually register AGE_3 on our state.
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(getAgeProperty());
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return HotPeppers.HOT_PEPPER_SEEDS.get();
    }

    // ~3-4x slower than wheat: only let one in eight random ticks actually attempt growth.
    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(8) == 0) {
            super.randomTick(state, level, pos, random);
        }
    }

    @Override
    protected int getBonemealAgeIncrease(Level level) {
        return 1;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && !player.isCreative() && isMaxAge(state)
                && !player.getMainHandItem().is(SAFE_HARVEST_TOOLS)) {
            player.hurt(level.damageSources().source(HotPeppers.SPICY_BURN), 1.0F);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
}
