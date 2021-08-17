package net.glasslauncher.hmifabric;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Item;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.item.ItemInstance;
import net.minecraft.server.MinecraftServer;
import net.modificationstation.stationapi.api.event.registry.MessageListenerRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.packet.Message;
import net.modificationstation.stationapi.api.packet.PacketHelper;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.registry.ModID;
import net.modificationstation.stationapi.api.server.event.network.PlayerLoginEvent;

public class HowManyItemsServer {

    @Entrypoint.ModID
    private static ModID modID;

//    @EventListener
//    public void handleLogin(PlayerLoginEvent event) {
//        if (((StationHandshake) event.handshakePacket).getMods().get("hmifabric") != null) {
//            Message customData = new Message(Identifier.of("hmifabric:handshake"));
//            customData.put(new boolean[]{true});
//            PacketHelper.INSTANCE.sendTo(((MinecraftServer) FabricLoader.getInstance().getGameInstance()).serverPlayerConnectionManager.getServerPlayer(event.handshakePacket.username), customData);
//        }
//    }
//
//    @EventListener
//    public void registerMessageListeners(MessageListenerRegistryEvent event) {
//        event.registry.register(Identifier.of(modID, "giveItem"), HowManyItemsServer::handleGivePacket);
//    }
//
//    public static void handleGivePacket(PlayerBase player, Message packet) {
//        if (((MinecraftServer) FabricLoader.getInstance().getGameInstance()).serverPlayerConnectionManager.isOp(player.name)) {
//            int[] data = packet.ints;
//            ItemInstance itemInstance = new ItemInstance(data[0], data[1], data[2]);
//            Item itemEntity = new Item(player.level, player.x, player.y, player.z, itemInstance);
//            player.level.spawnEntity(itemEntity);
//        }
//    }
}
