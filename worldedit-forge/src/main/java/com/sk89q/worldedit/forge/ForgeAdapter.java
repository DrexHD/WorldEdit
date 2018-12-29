/*
 * WorldEdit, a Minecraft world manipulation toolkit
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldEdit team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.worldedit.forge;

import com.google.common.collect.ImmutableList;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.registry.state.BooleanProperty;
import com.sk89q.worldedit.registry.state.DirectionalProperty;
import com.sk89q.worldedit.registry.state.EnumProperty;
import com.sk89q.worldedit.registry.state.IntegerProperty;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.World;

import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldedit.world.item.ItemType;
import com.sk89q.worldedit.world.item.ItemTypes;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.stream.Collectors;

final class ForgeAdapter {

    private ForgeAdapter() {
    }

    public static World adapt(net.minecraft.world.World world) {
        return new ForgeWorld(world);
    }

    public static Biome adapt(BiomeType biomeType) {
        return Biome.REGISTRY.getObject(new ResourceLocation(biomeType.getId()));
    }

    public static BiomeType adapt(Biome biome) {
        return BiomeTypes.get(biome.getRegistryName().toString());
    }

    public static Vector3 adapt(Vec3d vector) {
        return Vector3.at(vector.x, vector.y, vector.z);
    }

    public static BlockVector3 adapt(BlockPos pos) {
        return BlockVector3.at(pos.getX(), pos.getY(), pos.getZ());
    }

    public static Vec3d toVec3(BlockVector3 vector) {
        return new Vec3d(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    public static EnumFacing adapt(Direction face) {
        switch (face) {
            case NORTH: return EnumFacing.NORTH;
            case SOUTH: return EnumFacing.SOUTH;
            case WEST: return EnumFacing.WEST;
            case EAST: return EnumFacing.EAST;
            case DOWN: return EnumFacing.DOWN;
            case UP:
            default:
                return EnumFacing.UP;
        }
    }

    public static Direction adaptEnumFacing(EnumFacing face) {
        switch (face) {
            case NORTH: return Direction.NORTH;
            case SOUTH: return Direction.SOUTH;
            case WEST: return Direction.WEST;
            case EAST: return Direction.EAST;
            case DOWN: return Direction.DOWN;
            case UP:
            default:
                return Direction.UP;
        }
    }

    public static BlockPos toBlockPos(BlockVector3 vector) {
        return new BlockPos(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    public static Property<?> adaptProperty(IProperty<?> property) {
        if (property instanceof net.minecraft.state.BooleanProperty) {
            return new BooleanProperty(property.getName(), ImmutableList.copyOf(((net.minecraft.state.BooleanProperty) property).getAllowedValues()));
        }
        if (property instanceof net.minecraft.state.IntegerProperty) {
            return new IntegerProperty(property.getName(), ImmutableList.copyOf(((net.minecraft.state.IntegerProperty) property).getAllowedValues()));
        }
        if (property instanceof DirectionProperty) {
            return new DirectionalProperty(property.getName(), ((DirectionProperty) property).getAllowedValues().stream()
                    .map(ForgeAdapter::adaptEnumFacing)
                    .collect(Collectors.toList()));
        }
        if (property instanceof net.minecraft.state.EnumProperty) {
            return new EnumProperty(property.getName(), ((net.minecraft.state.EnumProperty<?>) property).getAllowedValues().stream()
                    .map(IStringSerializable::getName)
                    .collect(Collectors.toList()));
        }
        return new IPropertyAdapter<>(property);
    }

    public static Block adapt(BlockType blockType) {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockType.getId()));
    }

    public static BlockType adapt(Block block) {
        return BlockTypes.get(ForgeRegistries.BLOCKS.getKey(block).toString());
    }

    public static Item adapt(ItemType itemType) {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemType.getId()));
    }

    public static ItemType adapt(Item item) {
        return ItemTypes.get(ForgeRegistries.ITEMS.getKey(item).toString());
    }

    public static ItemStack adapt(BaseItemStack baseItemStack) {
        NBTTagCompound forgeCompound = null;
        if (baseItemStack.getNbtData() != null) {
            forgeCompound = NBTConverter.toNative(baseItemStack.getNbtData());
        }
        return new ItemStack(adapt(baseItemStack.getType()), baseItemStack.getAmount(), forgeCompound);
    }

    public static BaseItemStack adapt(ItemStack itemStack) {
        CompoundTag tag = NBTConverter.fromNative(itemStack.serializeNBT());
        return new BaseItemStack(adapt(itemStack.getItem()), tag, itemStack.getCount());
    }
}
