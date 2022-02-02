package net.glasslauncher.hmifabric;

import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.unsafeevents.listener.EventListener;
import net.minecraft.entity.Item;
import net.minecraft.entity.player.PlayerBase;
import net.minecraft.item.ItemInstance;
import net.minecraft.server.MinecraftServer;
import net.modificationstation.stationapi.api.event.registry.MessageListenerRegistryEvent;
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint;
import net.modificationstation.stationapi.api.network.ModdedPacketHandler;
import net.modificationstation.stationapi.api.packet.Message;
import net.modificationstation.stationapi.api.packet.PacketHelper;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.registry.ModID;
import net.modificationstation.stationapi.api.server.event.network.PlayerLoginEvent;

public class HowManyItemsServer {

    @Entrypoint.ModID
    private static ModID modID;

    @EventListener
    public void handleLogin(PlayerLoginEvent event) {
        if (((ModdedPacketHandler) event.loginPacket).isModded()) {
            Message customData = new Message(Identifier.of("hmifabric:handshake"));
            customData.booleans = new boolean[]{true};
            PacketHelper.sendTo(((MinecraftServer) FabricLoader.getInstance().getGameInstance()).serverPlayerConnectionManager.getServerPlayer(event.loginPacket.username), customData);
        }
    }

    @EventListener
    public void registerMessageListeners(MessageListenerRegistryEvent event) {
        event.registry.register(Identifier.of(modID, "giveItem"), HowManyItemsServer::handleGivePacket);
        event.registry.register(Identifier.of(modID, "heal"), HowManyItemsServer::handleHealPacket);
    }

    public static void handleGivePacket(PlayerBase player, Message packet) {
        if (((MinecraftServer) FabricLoader.getInstance().getGameInstance()).serverPlayerConnectionManager.isOp(player.name) && packet.strings.length == 1) {
            ItemInstance itemInstance = new Gson().fromJson(packet.strings[0], ItemInstance.class); // Is this stupid? I have no idea.
            Item itemEntity = new Item(player.level, player.x, player.y, player.z, itemInstance);
            player.level.spawnEntity(itemEntity);
        }
    }

    public static void handleHealPacket(PlayerBase player, Message packet) {
        if (((MinecraftServer) FabricLoader.getInstance().getGameInstance()).serverPlayerConnectionManager.isOp(player.name)) {
            player.addHealth(Integer.MAX_VALUE/2); // High to allow mods that mess with player health to be supported.
        }
    }
}
