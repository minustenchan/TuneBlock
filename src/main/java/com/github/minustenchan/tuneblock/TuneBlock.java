package com.github.minustenchan.tuneblock;

import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class TuneBlock {

    private final Block block;
    private final Block blockAbove;
    private final NamespacedKey noteKey;
    private final NamespacedKey poweredKey;
    private final @Nullable Player player;
    private final PersistentDataContainer container;
    private final boolean isPowered;
    private final byte note;
    private final float pitch;
    private Sound sound;

    public TuneBlock(Block block, @Nullable Player player) {
        this.container = new CustomBlockData(block, TuneBlockPlugin.get());
        this.block = block;
        this.blockAbove = block.getRelative(BlockFace.UP);
        this.noteKey = new NamespacedKey(TuneBlockPlugin.get(), "note");
        this.poweredKey = new NamespacedKey(TuneBlockPlugin.get(), "powered");
        this.player = player;
        this.isPowered = container.getOrDefault(poweredKey, PersistentDataType.BOOLEAN, false);
        this.note = container.getOrDefault(noteKey, PersistentDataType.BYTE, (byte) 0);
        this.pitch = (float) Math.pow(2.0F, (note - 12F) / 12F);
        if (isMobSound()) this.sound = getMobSound();
        else try {
            this.sound = Sound.valueOf("BLOCK_NOTE_BLOCK_" + ((CraftBlock) block.getRelative(BlockFace.DOWN)).getNMS().instrument().toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            this.sound = Sound.BLOCK_NOTE_BLOCK_HARP;
        }
    }

    public Sound getMobSound() {
        if (isSkeleton()) return Sound.BLOCK_NOTE_BLOCK_IMITATE_SKELETON;
        if (isWitherSkeleton()) return Sound.BLOCK_NOTE_BLOCK_IMITATE_WITHER_SKELETON;
        if (isZombie()) return Sound.BLOCK_NOTE_BLOCK_IMITATE_ZOMBIE;
        if (isCreeper()) return Sound.BLOCK_NOTE_BLOCK_IMITATE_CREEPER;
        if (isPiglin()) return Sound.BLOCK_NOTE_BLOCK_IMITATE_PIGLIN;
        if (isDragon()) return Sound.BLOCK_NOTE_BLOCK_IMITATE_ENDER_DRAGON;
        return null;
    }

    public Sound getSound() {
        return sound;
    }

    public Byte getNote() {
        return note;
    }

    public Float getPitch() {
        return pitch;
    }

    public void runClickAction(Action action) {
        playSoundNaturally();
        if (action == Action.RIGHT_CLICK_BLOCK) increaseNote();
    }

    public void playSoundNaturally() {
        if (!blockAbove.isEmpty() && !isMobSound()) return;

        Location loc = block.getLocation().add(0.5, 0.5, 0.5);
        World world = block.getWorld();
        double color = (double) note / 24.0;

        if (!isMobSound()) world.playSound(loc, sound, 1.0F, pitch);
        else world.playSound(loc, sound, 1.0F, 1.0F);

        if (!isMobSound()) world.spawnParticle(Particle.NOTE, loc.add(0, 0.6, 0), 0, color, 0, 0, 1);
        world.sendGameEvent(player, GameEvent.NOTE_BLOCK_PLAY, loc.toVector());
    }

    public void increaseNote() {
        container.set(noteKey, PersistentDataType.BYTE, (byte) ((note + 1) % 25));
    }

    public void setPowered(boolean powered) {
        if (powered) container.set(poweredKey, PersistentDataType.BOOLEAN, powered);
        else container.remove(poweredKey);
    }

    public void removeData() {
        container.remove(noteKey);
        container.remove(poweredKey);
    }

    public boolean isSkeleton() {
        return blockAbove.getType() == Material.SKELETON_SKULL;
    }

    public boolean isWitherSkeleton() {
        return blockAbove.getType() == Material.WITHER_SKELETON_SKULL;
    }

    public boolean isZombie() {
        return blockAbove.getType() == Material.ZOMBIE_HEAD;
    }

    public boolean isCreeper() {
        return blockAbove.getType() == Material.CREEPER_HEAD;
    }

    public boolean isPiglin() {
        return blockAbove.getType() == Material.PIGLIN_HEAD;
    }

    public boolean isDragon() {
        return blockAbove.getType() == Material.DRAGON_HEAD;
    }

    public boolean isMobSound() {
        return isSkeleton() || isWitherSkeleton() || isZombie() || isCreeper() || isPiglin() || isDragon();
    }

    public boolean isPowered() {
        return isPowered;
    }
}
