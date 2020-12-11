package net.glasslauncher.hmifabric;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Item;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.item.ItemInstance;
import net.minecraft.packet.AbstractPacket;
import net.minecraft.server.MinecraftServer;
import net.modificationstation.stationloader.api.common.event.packet.PacketRegister;
import net.modificationstation.stationloader.api.common.factory.GeneralFactory;
import net.modificationstation.stationloader.api.common.mod.StationMod;
import net.modificationstation.stationloader.api.common.packet.CustomData;
import net.modificationstation.stationloader.api.common.packet.PacketHelper;
import net.modificationstation.stationloader.api.common.packet.StationHandshake;
import net.modificationstation.stationloader.api.server.event.network.HandleLogin;
import uk.co.benjiweber.expressions.functions.QuadConsumer;

import java.util.Map;
import java.util.function.BiConsumer;

public class HowManyItemsServer implements StationMod, PacketRegister {
    @Override
    public void init() {
        PacketRegister.EVENT.register(this, getModID());
        HandleLogin.EVENT.register(((pendingConnection, handshakeC2S) -> {
            if (((StationHandshake) handshakeC2S).getMods().get("hmifabric") != null) {
                CustomData customData = GeneralFactory.INSTANCE.newInst(CustomData.class, "hmifabric:handshake");
                customData.set(new boolean[]{true});
                PacketHelper.INSTANCE.sendTo(((MinecraftServer) FabricLoader.getInstance().getGameInstance()).serverPlayerConnectionManager.getServerPlayer(handshakeC2S.username), customData.getPacketInstance());
            }
        }));
    }

    @Override
    public void registerPackets(QuadConsumer<Integer, Boolean, Boolean, Class<? extends AbstractPacket>> quadConsumer, Map<String, BiConsumer<PlayerBase, CustomData>> map) {
        map.put("giveItem", HowManyItemsServer::handleGivePacket);
    }

    public static void handleGivePacket(PlayerBase player, CustomData packet) {
        if (((MinecraftServer) FabricLoader.getInstance().getGameInstance()).serverPlayerConnectionManager.isOp(player.name)) {
            int[] data = packet.ints();
            ItemInstance itemInstance = new ItemInstance(data[0], data[1], data[2]);
            Item itemEntity = new Item(player.level, player.x, player.y, player.z, itemInstance);
            player.level.spawnEntity(itemEntity);
        }
    }
}
