package dev.igrammine.nourish;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class iNourish implements ModInitializer {

    @Override
    public void onInitialize() {



        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            commandDispatcher.register(CommandManager.literal("getnourish").executes(context -> {
                context.getSource().sendFeedback(() -> Text.literal("sosi"), false);
                return 1;
            }));
        });

    }


    public static Void task;

    public static @Nullable Void startTask() {




        return null;
    }
}
