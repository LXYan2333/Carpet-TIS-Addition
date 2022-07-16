package carpettisaddition.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

//#if MC >= 11900
//$$ import net.minecraft.command.CommandRegistryAccess;
//#endif

public abstract class CommandTreeContext
{
	//#if MC >= 11900
	//$$ public final CommandRegistryAccess commandBuildContext;
	//#endif

	protected CommandTreeContext(
			//#if MC >= 11900
			//$$ CommandRegistryAccess commandBuildContext
			//#endif
	)
	{
		//#if MC >= 11900
		//$$ this.commandBuildContext = commandBuildContext;
		//#endif
	}

	public static Register of(
			CommandDispatcher<ServerCommandSource> dispatcher
			//#if MC >= 11900
			//$$ , CommandRegistryAccess commandBuildContext
			//#endif
	)
	{
		return new Register(
				dispatcher
				//#if MC >= 11900
				//$$ , commandBuildContext
				//#endif
		);
	}

	public static Extend of(
			LiteralArgumentBuilder<ServerCommandSource> node
			//#if MC >= 11900
			//$$ , CommandRegistryAccess commandBuildContext
			//#endif
	)
	{
		return new Extend(
				node
				//#if MC >= 11900
				//$$ , commandBuildContext
				//#endif
		);
	}

	public static class Register extends CommandTreeContext
	{
		public final CommandDispatcher<ServerCommandSource> dispatcher;

		private Register(
				CommandDispatcher<ServerCommandSource> dispatcher
				//#if MC >= 11900
				//$$ , CommandRegistryAccess commandBuildContext
				//#endif
		)
		{
			super(
					//#if MC >= 11900
					//$$ commandBuildContext
					//#endif
			);
			this.dispatcher = dispatcher;
		}
	}

	public static class Extend extends CommandTreeContext
	{
		public final LiteralArgumentBuilder<ServerCommandSource> node;

		private Extend(
				LiteralArgumentBuilder<ServerCommandSource> node
				//#if MC >= 11900
				//$$ , CommandRegistryAccess commandBuildContext
				//#endif
		)
		{
			super(
					//#if MC >= 11900
					//$$ commandBuildContext
					//#endif
			);
			this.node = node;
		}
	}
}
