package aji.carpetajiaddition.data;

import aji.carpetajiaddition.CarpetAjiAdditionSettings;
import aji.carpetajiaddition.mixin.rules.betterLogCommand.LoggerRegistryAccessor;
import carpet.logging.LoggerRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BetterLogCommandData implements Data{
    public static final String DATA_NAME = "betterLogCommand";
    private final Map<String, Map<String, String>> playerSubscriptions = ((LoggerRegistryAccessor) new LoggerRegistry()).getPlayerSubscriptions();
    private boolean isFirstLoad = true;

    @Override
    public String name() {
        return DATA_NAME;
    }

    @Override
    public Tag toNbt() {
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<String, Map<String, String>> entry : playerSubscriptions.entrySet()) {
            CompoundTag tag1 = new CompoundTag();
            for (Map.Entry<String, String> entry1 : entry.getValue().entrySet()) {
                String value = entry1.getValue();
                if (value == null) {
                    value = "null";
                }
                tag1.putString(entry1.getKey(), value);
            }
            tag.put(entry.getKey(), tag1);
        }
        return tag;
    }

    @Override
    public void load(@Nullable Tag tag) {
        if (tag == null) {
            return;
        }
        if (isFirstLoad && CarpetAjiAdditionSettings.betterLogCommand) {
            for (Map.Entry<String, Tag> entry : ((CompoundTag) tag).entrySet()) {
                for (Map.Entry<String, Tag> entry1 : ((CompoundTag) entry.getValue()).entrySet()) {
                    String s = entry1.getValue().asString().orElseThrow();
                    if (s.equals("null")) {
                        s = null;
                    }
                    LoggerRegistry.subscribePlayer(entry.getKey(), entry1.getKey(), s);
                }
            }
        }
        isFirstLoad = false;
    }
}
