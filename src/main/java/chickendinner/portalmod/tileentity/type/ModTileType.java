package chickendinner.portalmod.tileentity.type;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ModTileType<T extends TileEntity> extends TileEntityType<T> {
    private final Supplier<T> factory;
    private final Predicate<Block> blockPredicate;

    public ModTileType(Supplier<T> factory, Predicate<Block> blockPredicate) {
        super(null, null, null);
        this.factory = factory;
        this.blockPredicate = blockPredicate;
    }

    public ModTileType(Supplier<T> factory, Block validBlock) {
        this(factory, validBlock::equals);
    }

    @Nullable
    @Override
    public T create() {
        return factory.get();
    }

    @Override
    public boolean isValidBlock(Block blockIn) {
        return blockPredicate.test(blockIn);
    }
}
