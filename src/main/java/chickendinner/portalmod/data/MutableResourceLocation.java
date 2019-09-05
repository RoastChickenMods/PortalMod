package chickendinner.portalmod.data;

import net.minecraft.util.ResourceLocation;

public class MutableResourceLocation {
    private String modId;
    private String path;

    public MutableResourceLocation(ResourceLocation rl) {
        this(rl.getNamespace(), rl.getPath());
    }

    public MutableResourceLocation(String modId, String path) {
        this.modId = modId;
        this.path = path;
    }

    public MutableResourceLocation prependPath(String s) {
        this.path = s + this.path;
        return this;
    }

    public MutableResourceLocation appendPath(String s) {
        this.path = this.path + s;
        return this;
    }

    public String getModId() {
        return modId;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", modId, path);
    }
}
