package carpettisaddition.mixins.rule.presistentLoggerSubcription;

import carpet.logging.LoggerRegistry;
import carpettisaddition.CarpetTISAdditionSettings;
import carpettisaddition.helpers.rule.persistentLoggerSubscription.LoggerSubscriptionStorage;
import carpettisaddition.utils.GameUtil;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LoggerRegistry.class)
public abstract class LoggerRegistryMixin
{
	private static final ThreadLocal<Boolean> appliedPersistentLoggerSubscription = ThreadLocal.withInitial(() -> false);

	@Inject(
			method = "playerConnected",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/Map;values()Ljava/util/Collection;",
					remap = false
			),
			locals = LocalCapture.CAPTURE_FAILHARD,
			remap = false
	)
	private static void tweaksLoggingOptions(PlayerEntity player, CallbackInfo ci, boolean firstTime)
	{
		if (CarpetTISAdditionSettings.persistentLoggerSubscription && firstTime)
		{
			if (LoggerSubscriptionStorage.getInstance().restoreSubscription(player))
			{
				appliedPersistentLoggerSubscription.set(true);
			}
		}
	}

	@ModifyArg(
			method = "playerConnected",
			at = @At(
					value = "INVOKE",
					target = "Lcarpet/logging/Logger;onPlayerConnect(Lnet/minecraft/entity/player/PlayerEntity;Z)V",
					remap = true
			),
			index = 1,
			remap = false
	)
	private static boolean dontSetDefaultLogger(boolean value)
	{
		if (appliedPersistentLoggerSubscription.get())
		{
			value = false;
			appliedPersistentLoggerSubscription.set(false);
		}
		return value;
	}

	@Inject(method = "subscribePlayer", at = @At("HEAD"), remap = false)
	private static void onPlayerLogSomething(String playerName, String logName, String option, CallbackInfo ci)
	{
		PlayerEntity player = GameUtil.getPlayerFromName(playerName);
		if (player != null)
		{
			LoggerSubscriptionStorage.getInstance().addSubscription(player, logName, option);
		}
	}

	@Inject(method = "unsubscribePlayer", at = @At("HEAD"), remap = false)
	private static void onPlayerUnlogSomething(String playerName, String logName, CallbackInfo ci)
	{
		PlayerEntity player = GameUtil.getPlayerFromName(playerName);
		if (player != null)
		{
			LoggerSubscriptionStorage.getInstance().removeSubscription(player, logName);
		}
	}
}
