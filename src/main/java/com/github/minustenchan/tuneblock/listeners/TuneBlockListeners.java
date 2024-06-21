package com.github.minustenchan.tuneblock.listeners;

import com.github.minustenchan.tuneblock.TuneBlock;
import io.th0rgal.oraxen.api.OraxenBlocks;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;

public class TuneBlockListeners implements Listener {

    @EventHandler
    public void onNotePlay(final NotePlayEvent event) {
        new TuneBlock(event.getBlock(), null).runClickAction(Action.LEFT_CLICK_BLOCK);
        event.setCancelled(true);
    }

    @EventHandler
    public void onNoteblockPower(final BlockPhysicsEvent event) {
        Block block = event.getBlock();
        if (!isRegularNoteblock(block)) return;
        TuneBlock tuneBlock = new TuneBlock(block, null);

        if (!block.isBlockIndirectlyPowered()) {
            tuneBlock.setPowered(false);
            return;
        }

        if (!tuneBlock.isPowered()) tuneBlock.playSoundNaturally();
        tuneBlock.setPowered(true);
    }

    @EventHandler
    public void onRightClick(final PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        if (block == null || !isRegularNoteblock(block)) return;

        Player player = event.getPlayer();
        PlayerInventory playerInventory = player.getInventory();
        ItemStack mainHandItem = playerInventory.getItemInMainHand();

        if (mainHandItem.getItemMeta() instanceof SkullMeta && event.getBlockFace() == BlockFace.UP) return;

        ItemStack offHandItem = playerInventory.getItemInOffHand();

        boolean isSneaking = player.isSneaking();
        boolean isMainHandEmpty = mainHandItem.isEmpty();
        boolean isOffHandEmpty = offHandItem.isEmpty();

        TuneBlock tuneBlock = new TuneBlock(block, player);

        if (!(tuneBlock.isMobSound() && !(isSneaking && (!isMainHandEmpty || !isOffHandEmpty)) || !(isSneaking && (!isMainHandEmpty || !isOffHandEmpty))))
            return;

        event.setUseInteractedBlock(Event.Result.DENY);
        tuneBlock.runClickAction(Action.RIGHT_CLICK_BLOCK);
    }

    public static boolean isRegularNoteblock(Block block) {
        return !OraxenBlocks.isOraxenBlock(block) && block.getType() == Material.NOTE_BLOCK;
    }
}
