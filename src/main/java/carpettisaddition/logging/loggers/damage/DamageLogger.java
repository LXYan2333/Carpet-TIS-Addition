package carpettisaddition.logging.loggers.damage;

import carpet.logging.LoggerRegistry;
import carpet.utils.Messenger;
import carpettisaddition.interfaces.ILivingEntity_damageLogger;
import carpettisaddition.logging.ExtensionLoggerRegistry;
import carpettisaddition.logging.loggers.AbstractLogger;
import carpettisaddition.logging.loggers.damage.modifyreasons.Modification;
import carpettisaddition.logging.loggers.damage.modifyreasons.ModifyReason;
import carpettisaddition.translations.Translatable;
import carpettisaddition.utils.TextUtil;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.BaseText;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;

import java.util.List;
import java.util.function.Supplier;

public class DamageLogger extends AbstractLogger
{
	public static final String NAME = "damage";
	private static final DamageLogger translator = new DamageLogger(null, null, 0);

	private final LivingEntity entity;
	private final DamageSource damageSource;
	private final float initialAmount;
	private float currentAmount;
	private final List<Modification> modificationList = Lists.newArrayList();
	private boolean flushed;

	public DamageLogger(LivingEntity entity, DamageSource damageSource, float initialAmount)
	{
		super(NAME);
		this.entity = entity;
		this.damageSource = damageSource;
		this.initialAmount = initialAmount;
		this.currentAmount = initialAmount;
		this.flushed = false;
	}

	public static boolean isLoggerActivated()
	{
		return ExtensionLoggerRegistry.__damage;
	}

	public static Translatable getTranslator()
	{
		return translator;
	}

	public static void create(LivingEntity entity, DamageSource source, float amount)
	{
		if (DamageLogger.isLoggerActivated())
		{
			ILivingEntity_damageLogger iEntity = (ILivingEntity_damageLogger)entity;
			if (!iEntity.getDamageLogger().isPresent())
			{
				iEntity.setDamageLogger(new DamageLogger(entity, source, amount));
			}
		}
	}

	public void modifyDamage(float newAmount, ModifyReason reason)
	{
		if (newAmount != this.currentAmount)
		{
			this.modificationList.add(new Modification(this.currentAmount, newAmount, reason));
			this.currentAmount = newAmount;
		}
	}

	private static BaseText[] verifyAndProduceMessage(String option, PlayerEntity player, Entity from, Entity to, Supplier<BaseText[]> messageFuture)
	{
		if ("all".equals(option)
				|| ("players".equals(option) && (from instanceof PlayerEntity || to instanceof PlayerEntity))
				|| ("me".equals(option) && (from == player || to == player)))
		{
			return messageFuture.get();
		}
		return null;
	}

	private static BaseText getAmountText(String style, float amount)
	{
		String display = String.format("%.2f", amount);
		String detail = String.format("%.6f", amount);
		return TextUtil.getFancyText(
				style,
				Messenger.s(display),
				Messenger.s(detail),
				new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, detail)
		);
	}

	public void flush(float finalAmount, float remainingHealth)
	{
		if (!isLoggerActivated() || this.flushed)
		{
			return;
		}
		this.flushed = true;
		Entity source = this.damageSource.getAttacker();
		LivingEntity target = this.entity;
		LoggerRegistry.getLogger(NAME).log((option, player) ->
				verifyAndProduceMessage(option, player, source, target, () -> {
					List<Object> lines = Lists.newArrayList();
					lines.add(Messenger.c(
							TextUtil.attachClickEvent(
									(BaseText)target.getDisplayName(),
									new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, TextUtil.getTeleportCommand(target))
							),
							"g  " + this.tr("receiving"),
							TextUtil.getSpaceText(),
							getAmountText("r", this.initialAmount),
							TextUtil.getSpaceText(),
							"g " + this.tr("damage"),
							String.format("g , %s: ", this.tr("damage type")),
							TextUtil.getFancyText(
									"w",
									Messenger.s(this.damageSource.getName()),
									source != null ? Messenger.c(String.format("w %s: ", this.tr("Source")), source.getName()) : null,
									source != null ? new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, TextUtil.getTeleportCommand(source)) : null
							)
					));
					for (Modification modification : this.modificationList)
					{
						float oldAmount = modification.getOldAmount();
						float newAmount = modification.getNewAmount();
						float delta = Math.abs(newAmount - oldAmount);
						String sig = newAmount > oldAmount ? "+" : "-";
						String radio = oldAmount != 0.0F ? String.format("%.1f%%", 100.0F * delta / oldAmount) : "N/A%";
						lines.add(Messenger.c(
								"g  - ",
								getAmountText("r", oldAmount),
								"g  -> ",
								getAmountText(newAmount > oldAmount ? "r" : "d", newAmount),
								String.format("g  (%s", sig),
								TextUtil.attachHoverEvent(getAmountText("g", delta), new HoverEvent(HoverEvent.Action.SHOW_TEXT, Messenger.s(String.format("%s%.6f", sig, delta)))),
								String.format("g , %s%s) %s", sig, radio, this.tr("due to")),
								TextUtil.getSpaceText(),
								modification.getReason().toText()
						));
					}
					lines.add(Messenger.c(
							"g  - ",
							"w " + this.tr("Actually received"),
							TextUtil.getSpaceText(),
							getAmountText(finalAmount > 0.0F ? "r" : "w", finalAmount),
							TextUtil.getSpaceText(),
							String.format("g %s, ", this.tr("damage")),
							String.format("w %s: ", this.tr("Remaining health")),
							getAmountText(remainingHealth > 0 ? "l" : "r", remainingHealth)
					));
					return lines.stream().map(Messenger::c).toArray(BaseText[]::new);
				})
		);
	}
}
