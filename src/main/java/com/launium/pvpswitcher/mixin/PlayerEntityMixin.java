package com.launium.pvpswitcher.mixin;

import com.launium.pvpswitcher.interfaces.IPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements IPlayerEntity {

    public boolean pvpswitcher$enabledPVP;

    public boolean pvpswitcher$getEnabledPVP() {
        return pvpswitcher$enabledPVP;
    }

    public void pvpswitcher$setEnabledPVP(boolean enabled) {
        pvpswitcher$enabledPVP = enabled;
    }

    @Shadow
    public abstract String getEntityName();

    @Inject(method = "shouldDamagePlayer", at = @At("HEAD"), cancellable = true)
    public void shouldDamagePlayer(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        String whoDisablesPVP = null;
        if (!((PlayerEntityMixin) (Object) player).pvpswitcher$enabledPVP)
            whoDisablesPVP = player.getEntityName();
        if (!this.pvpswitcher$enabledPVP) {
            if (whoDisablesPVP != null) whoDisablesPVP = "both of you";
            else whoDisablesPVP = this.getEntityName();
        }
        if (whoDisablesPVP != null) {
            try {
                ((ServerPlayerEntity) (Object) this).networkHandler.sendPacket(
                        new TitleS2CPacket(TitleS2CPacket.Action.ACTIONBAR,
                                new LiteralText("PVP is disabled by " + whoDisablesPVP).formatted(Formatting.RED),
                                4, 20, 10
                        ));
            } catch (ClassCastException ignored) {
            }
            cir.setReturnValue(false);
        }
    }

/*    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    public void isInvulnerableTo(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        Entity attacker = damageSource.getAttacker();
        if (attacker instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) attacker;
            String whoDisabledPVP = null;
            if (!((PlayerEntityMixin) (Object) player).pvpswitcher$enabledPVP)
                whoDisabledPVP = player.getEntityName();
            if (!this.pvpswitcher$enabledPVP) {
                if (whoDisabledPVP != null) whoDisabledPVP = "both of you";
                else whoDisabledPVP = this.getEntityName();
            }
            if (whoDisabledPVP != null) {
                try {
                    ((ServerPlayerEntity) player).networkHandler.sendPacket(
                            new TitleS2CPacket(TitleS2CPacket.Action.ACTIONBAR,
                                    new LiteralText("PVP is disabled by " + whoDisabledPVP).formatted(Formatting.RED),
                                    4, 20, 10
                            ));
                } catch (ClassCastException ignored) {
                }
                cir.setReturnValue(true);
            }
        }
    }*/

}
