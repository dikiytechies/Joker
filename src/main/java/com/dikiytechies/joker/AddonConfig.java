package com.dikiytechies.joker;

import com.dikiytechies.joker.network.AddonPackets;
import com.dikiytechies.joker.network.packets.fromserver.CommonConfigPacket;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.InMemoryCommentedFormat;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.network.packets.fromserver.ResetSyncedCommonConfigPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = AddonMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AddonConfig {
    public static final Client CLIENT;
    static final ForgeConfigSpec commonSpec;
    static final ForgeConfigSpec clientSpec;
    private static final Common COMMON_FROM_FILE;
    private static final Common COMMON_SYNCED_TO_CLIENT;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON_FROM_FILE = specPair.getLeft();

        final Pair<Common, ForgeConfigSpec> syncedSpecPair = new ForgeConfigSpec.Builder().configure(builder -> new Common(builder, "synced"));
        CommentedConfig config = CommentedConfig.of(InMemoryCommentedFormat.defaultInstance());
        ForgeConfigSpec syncedSpec = syncedSpecPair.getRight();
        syncedSpec.correct(config);
        syncedSpec.setConfig(config);
        COMMON_SYNCED_TO_CLIENT = syncedSpecPair.getLeft();
    }

    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();

    }

    @SuppressWarnings("unused")
    private static boolean isElementNonNegativeFloat(Object num, boolean moreThanZero) {
        if (num instanceof Double) {
            Double numDouble = (Double) num;
            return (numDouble > 0 || !moreThanZero && numDouble == 0) && Float.isFinite(numDouble.floatValue());
        }
        return false;
    }

    public static Common getCommonConfigInstance(boolean isClientSide) {
        return isClientSide && !ClientUtil.isLocalServer() ? COMMON_SYNCED_TO_CLIENT : COMMON_FROM_FILE;
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfig.ModConfigEvent event) {
        ModConfig config = event.getConfig();
        if (AddonMain.MOD_ID.equals(config.getModId()) && config.getType() == ModConfig.Type.COMMON) {
            COMMON_FROM_FILE.onLoadOrReload();
        }
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfig.Reloading event) {
        ModConfig config = event.getConfig();
        if (AddonMain.MOD_ID.equals(config.getModId()) && config.getType() == ModConfig.Type.COMMON) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                server.getPlayerList().getPlayers().forEach(Common.SyncedValues::syncWithClient);
            }
        }
    }

    public static class Common {
        public final ForgeConfigSpec.BooleanValue enableShrineGeneration;
        private boolean loaded = false;

        private Common(ForgeConfigSpec.Builder builder) {
            this(builder, null);
        }

        private Common(ForgeConfigSpec.Builder builder, @Nullable String mainPath) {
            if (mainPath != null) {
                builder.push(mainPath);
            }

            builder.push("Global Settings");
            enableShrineGeneration = builder
                    .translation("joker.config.enableShrineGeneration")
                    .comment("    Enables Joker Iggy's Shrine generation",
                            "    Default is to true.")
                    .define("enableShrineGeneration", true);
            builder.pop();

            if (mainPath != null) {
                builder.pop();
            }
        }

        public boolean isConfigLoaded() {
            return loaded;
        }

        private void onLoadOrReload() {
            loaded = true;
        }

        public static class SyncedValues {
            private final boolean enableShrineGeneration;

            public SyncedValues(PacketBuffer buf) {
                enableShrineGeneration = buf.readBoolean();
            }

            private SyncedValues(Common config) {
                enableShrineGeneration = config.enableShrineGeneration.get();
            }

            public static void resetConfig() {
                COMMON_SYNCED_TO_CLIENT.enableShrineGeneration.clearCache();
            }

            public static void syncWithClient(ServerPlayerEntity player) {
                AddonPackets.sendToClient(new CommonConfigPacket(new SyncedValues(COMMON_FROM_FILE)), player);
            }

            public static void onPlayerLogout(ServerPlayerEntity player) {
                AddonPackets.sendToClient(new ResetSyncedCommonConfigPacket(), player);
            }

            public void writeToBuf(PacketBuffer buf) {
                buf.writeBoolean(enableShrineGeneration);
            }

            public void changeConfigValues() {
                COMMON_SYNCED_TO_CLIENT.enableShrineGeneration.set(enableShrineGeneration);
            }
        }
    }

    public static class Client {
        private Client(ForgeConfigSpec.Builder builder) {

        }
    }
}
