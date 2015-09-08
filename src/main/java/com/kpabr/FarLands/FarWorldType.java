package com.kpabr.FarLands;

import com.kpabr.FarLands.ChunkProviderFarlands;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraft.world.gen.ChunkProviderGenerate;

public class FarWorldType extends WorldType{

	public FarWorldType(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	@Override
	public IChunkProvider getChunkGenerator(World world, String generatorOptions)
    {
        return new ChunkProviderFarlands(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled());
    }

}
