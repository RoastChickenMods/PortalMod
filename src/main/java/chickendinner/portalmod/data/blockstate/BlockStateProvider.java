package chickendinner.portalmod.data.blockstate;

import chickendinner.portalmod.PortalMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.Rotations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class BlockStateProvider implements IDataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).disableHtmlEscaping().setPrettyPrinting().create();
    protected final DataGenerator generator;
    private List<IFinishedBlockState> blockStates = new ArrayList<>();

    public BlockStateProvider(DataGenerator generator) {
        this.generator = generator;
    }

    private void populateBlockStates() {
        add(PortalMod.Blocks.PORTAL);
        add(PortalMod.Blocks.SLIT_BLOCK);
        add(PortalMod.Blocks.ENTANGLEMENT_CATCHER);
        add(PortalMod.Blocks.SLIT_CANNON);
        add(PortalMod.Blocks.SOLID_FUEL_GENERATOR);
        add(PortalMod.Blocks.MACHINE_BASE);
    }

    private void add(Block block) {
        if (block.getStateContainer().getProperties().contains(BlockStateProperties.HORIZONTAL_FACING)) {
            add(new HorizontalBlockState(block));
        } else {
            add(new FinishedBlockState(block));
        }
    }

    private void add(Block block, IBlockStateModelProvider modelProvider) {
        if (block.getStateContainer().getProperties().contains(BlockStateProperties.HORIZONTAL_FACING)) {
            add(new HorizontalBlockState(block, modelProvider));
        } else {
            add(new FinishedBlockState(block, modelProvider));
        }
    }

    private void add(FinishedBlockState blockState) {
        blockStates.add(blockState);
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        populateBlockStates();
        for (IFinishedBlockState blockState : blockStates) {
            writeFinishedBlockState(cache, blockState);
        }
    }

    private void writeFinishedBlockState(DirectoryCache cache, IFinishedBlockState finishedState) throws IOException {
        Block block = finishedState.getBlock();
        JsonObject blockstateJSON = new JsonObject();
        JsonObject variantsJSON = new JsonObject();

        for (BlockState state : block.getStateContainer().getValidStates()) {
            // Build string made of property values as variant key
            JsonObject variantJSON = new JsonObject();
            StringJoiner joiner = new StringJoiner(",");
            for (IProperty<?> prop : state.getProperties()) {
                String format = String.format("%s=%s", prop.getName(), Util.getValueName(prop, state.get(prop)));
                joiner.add(format);
            }
            String variantKey = joiner.toString();

            // Use model provider to get the model location
            variantJSON.addProperty("model", finishedState.getModelProvider().getModel(state).prependPath("block/").toString());

            // Add rotations
            Rotations rotation = finishedState.getRotation(state);
            if (rotation.getX() != 0) {
                variantJSON.addProperty("x", rotation.getX());
            }
            if (rotation.getY() != 0) {
                variantJSON.addProperty("y", rotation.getY());
            }
            if (rotation.getZ() != 0) {
                variantJSON.addProperty("z", rotation.getZ());
            }

            // Add the variant to the variants object
            variantsJSON.add(variantKey, variantJSON);
        }

        // Add the variants object to the blockstate json file
        blockstateJSON.add("variants", variantsJSON);

        // Write the blockstate json file
        ResourceLocation name = block.getRegistryName();
        Path path = generator.getOutputFolder().resolve(String.format("assets/%s/blockstates/%s.json", name.getNamespace(), name.getPath()));
        IDataProvider.save(GSON, cache, blockstateJSON, path);
    }

    @Override
    public String getName() {
        return "Blockstates";
    }
}
