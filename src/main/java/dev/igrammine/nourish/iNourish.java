package dev.igrammine.nourish;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class iNourish implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            var node = commandDispatcher.register(CommandManager.literal("getnourish").executes(context -> {
                var player = context.getSource().getPlayer();
                if (player != null) {
                    started = true;
                    getNearBlock(player);
                } else context.getSource().sendFeedback(() -> Text.literal("Команду может использовать только игрок"), false);
                return 1;
            }));
        });

        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            commandDispatcher.register(CommandManager.literal("stopnourish").executes(context -> {
                var player = context.getSource().getPlayer();
                if (player != null) {
                    started = false;
                } else context.getSource().sendFeedback(() -> Text.literal("Команду может использовать только игрок"), false);
                return 1;
            }));
        });



    }


    public static boolean started = false;

    public static void nourish(BlockState block, BlockPos pos, ServerPlayerEntity player) {
        // AGE
        if (block.getBlock().getStateManager().getProperty("age") != null && (
                block.getBlock() != Blocks.BAMBOO ||
                        block.getBlock() != Registries.BLOCK.get(Identifier.tryParse("vinery:oak_lattice")))
        ) {
            var current_age = Integer.parseInt(block.get(block.getBlock().getStateManager().getProperty("age")).toString());
            var max_age = block.getBlock().getStateManager().getProperty("age").getValues().size()-1;

            if (current_age < max_age) {
                var last = max_age-current_age; // example: 3 =>
                var factor = (last >= 2) ? new Random().nextInt(1, last) : 1; // => factor = 1-3 =>
                var new_age = current_age + factor; // => new_age = 4+3 = 7

                player.getWorld().setBlockState(pos, block.getBlock().getDefaultState().with(
                        IntProperty.of("age", 0, max_age),
                        new_age
                ), Block.NOTIFY_ALL);

                ((ServerWorld) player.getWorld()).spawnParticles(
                        ParticleTypes.TOTEM_OF_UNDYING,
                        pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, 5, 0.5D, 0.5D, 0.5D, 0
                );
            }
        }

        // STAGE
        //if (block.getBlock().getStateManager().getProperty("stage") != null) {
        //    var current_age = Integer.parseInt(block.get(block.getBlock().getStateManager().getProperty("stage")).toString());
        //    var max_age = block.getBlock().getStateManager().getProperty("stage").getValues().size()-1;
//
        //    if (current_age < max_age) {
        //        player.getWorld().setBlockState(pos, block.getBlock().getDefaultState().with(
        //                IntProperty.of("stage", 0, max_age),
        //                ((max_age-current_age) >= 2) ? current_age+2 : current_age+1
        //        ), Block.NOTIFY_ALL);
//
        //        player.getWorld().addParticle(
        //                ParticleTypes.TOTEM_OF_UNDYING, pos.getX(), pos.getY(), pos.getZ(), 0.5D, 0.5D, 0.5D
        //        );
        //    }
        //}
    }

    public static void getNearBlock(ServerPlayerEntity player) {
        var world = player.getWorld();
        var pos = player.getBlockPos();

        for (int x = pos.getX()-10; x <= pos.getX()+10; x++) {
            for (int y = pos.getY()-10; y <= pos.getY()+10; y++) {
                for (int z = pos.getZ()-10; z <= pos.getZ()+10; z++) {
                    var current_pos = new BlockPos(x, y, z);
                    var state = world.getBlockState(current_pos);
                    try {
                        nourish(state, current_pos, player);
                    } catch (Exception E) {
                        E.printStackTrace();
                    }
                }
            }
        }
        if (started) {
            CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS).execute(() -> getNearBlock(player));
        }
    }
}
