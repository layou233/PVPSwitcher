package com.launium.pvpswitcher;

import com.launium.pvpswitcher.interfaces.IPlayerEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class PVPSwitcher implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
                dispatcher.register(CommandManager.literal("pvp").executes(context -> {
                    Entity entity = context.getSource().getEntity();
                    if (entity instanceof PlayerEntity) {
                        IPlayerEntity player = (IPlayerEntity) entity;
                        boolean result = !player.pvpswitcher$getEnabledPVP();
                        player.pvpswitcher$setEnabledPVP(result);
                        LiteralText text = new LiteralText(entity.getEntityName() + " switched PVP to " + result);
                        context.getSource().sendFeedback(text, true);
                        ((ServerPlayerEntity) player).networkHandler.sendPacket(
                                new TitleS2CPacket(TitleS2CPacket.Action.ACTIONBAR, text, 4, 20, 10));
                    }
                    return 1;
                })));
    }

}
