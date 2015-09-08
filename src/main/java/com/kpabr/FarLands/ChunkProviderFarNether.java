package com.kpabr.FarLands;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderHell;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCavesHell;
import net.minecraft.world.gen.NoiseGenerator;
import net.minecraft.world.gen.feature.WorldGenFire;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenGlowStone1;
import net.minecraft.world.gen.feature.WorldGenGlowStone2;
import net.minecraft.world.gen.feature.WorldGenHellLava;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.structure.MapGenNetherBridge;
import static net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.*;
import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.*;
import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.*;
import static net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable.EventType.*;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.eventhandler.Event.*;
import net.minecraftforge.event.terraingen.*;

public class ChunkProviderFarNether extends ChunkProviderHell implements IChunkProvider
{
    private Random hellRNG;
    /** A NoiseGeneratorFarlandsOctaves used in generating nether terrain */
    private NoiseGeneratorFarlandsOctaves netherNoiseGen1;
    private NoiseGeneratorFarlandsOctaves netherNoiseGen2;
    private NoiseGeneratorFarlandsOctaves netherNoiseGen3;
    /** Determines whether slowsand or gravel can be generated at a location */
    private NoiseGeneratorFarlandsOctaves slowsandGravelNoiseGen;
    /** Determines whether something other than nettherack can be generated at a location */
    private NoiseGeneratorFarlandsOctaves netherrackExculsivityNoiseGen;
    public NoiseGeneratorFarlandsOctaves netherNoiseGen6;
    public NoiseGeneratorFarlandsOctaves netherNoiseGen7;
    /** Is the world that the nether is getting generated. */
    private World worldObj;
    private double[] noiseField;
    public MapGenNetherBridge genNetherBridge = new MapGenNetherBridge();
    /** Holds the noise used to determine whether slowsand can be generated at a location */
    private double[] slowsandNoise = new double[256];
    private double[] gravelNoise = new double[256];
    /** Holds the noise used to determine whether something other than netherrack can be generated at a location */
    private double[] netherrackExclusivityNoise = new double[256];
    private MapGenBase netherCaveGenerator = new MapGenCavesHell();
    double[] noiseData1;
    double[] noiseData2;
    double[] noiseData3;
    double[] noiseData4;
    double[] noiseData5;
    private static final String __OBFID = "CL_00000392";

    {
        genNetherBridge = (MapGenNetherBridge) TerrainGen.getModdedMapGen(genNetherBridge, NETHER_BRIDGE);
        netherCaveGenerator = TerrainGen.getModdedMapGen(netherCaveGenerator, NETHER_CAVE);
    }

    public ChunkProviderFarNether(World p_i2005_1_, long p_i2005_2_)
    {
    	super(p_i2005_1_, p_i2005_2_);
        this.worldObj = p_i2005_1_;
        this.hellRNG = new Random(p_i2005_2_);
        this.netherNoiseGen1 = new NoiseGeneratorFarlandsOctaves(this.hellRNG, 16);
        this.netherNoiseGen2 = new NoiseGeneratorFarlandsOctaves(this.hellRNG, 16);
        this.netherNoiseGen3 = new NoiseGeneratorFarlandsOctaves(this.hellRNG, 8);
        this.slowsandGravelNoiseGen = new NoiseGeneratorFarlandsOctaves(this.hellRNG, 4);
        this.netherrackExculsivityNoiseGen = new NoiseGeneratorFarlandsOctaves(this.hellRNG, 4);
        this.netherNoiseGen6 = new NoiseGeneratorFarlandsOctaves(this.hellRNG, 10);
        this.netherNoiseGen7 = new NoiseGeneratorFarlandsOctaves(this.hellRNG, 16);

        NoiseGenerator[] noiseGens = {netherNoiseGen1, netherNoiseGen2, netherNoiseGen3, slowsandGravelNoiseGen, netherrackExculsivityNoiseGen, netherNoiseGen6, netherNoiseGen7};
        noiseGens = TerrainGen.getModdedNoiseGenerators(p_i2005_1_, this.hellRNG, noiseGens);
        this.netherNoiseGen1 = (NoiseGeneratorFarlandsOctaves)noiseGens[0];
        this.netherNoiseGen2 = (NoiseGeneratorFarlandsOctaves)noiseGens[1];
        this.netherNoiseGen3 = (NoiseGeneratorFarlandsOctaves)noiseGens[2];
        this.slowsandGravelNoiseGen = (NoiseGeneratorFarlandsOctaves)noiseGens[3];
        this.netherrackExculsivityNoiseGen = (NoiseGeneratorFarlandsOctaves)noiseGens[4];
        this.netherNoiseGen6 = (NoiseGeneratorFarlandsOctaves)noiseGens[5];
        this.netherNoiseGen7 = (NoiseGeneratorFarlandsOctaves)noiseGens[6];
    }

    /**
     * generates a subset of the level's terrain data. Takes 7 arguments: the [empty] noise array, the position, and the
     * size.
     */
    public double[] initializeNoiseField(double[] p_73164_1_, int p_73164_2_, int p_73164_3_, int p_73164_4_, int p_73164_5_, int p_73164_6_, int p_73164_7_)
    {
    	if(FarLands.threshold > 0)
    	{ 
	    	if(Math.abs(p_73164_2_) > FarLands.threshold/4)
	    	{
	    		p_73164_2_ = (int)(p_73164_2_+(Math.copySign(((12550820-FarLands.threshold)/4), p_73164_2_)));
	    	}
	    	if(Math.abs(p_73164_4_) > FarLands.threshold/4)
	    	{
	    		p_73164_4_ = (int)(p_73164_4_+(Math.copySign(((12550820-FarLands.threshold)/4), p_73164_4_)));
	    	}
    	}
    	if (p_73164_1_ == null)
        {
            p_73164_1_ = new double[p_73164_5_ * p_73164_6_ * p_73164_7_];
        }

        double d0 = 684.412D;
        double d1 = 2053.236D;
        this.noiseData4 = this.netherNoiseGen6.generateNoiseOctaves(this.noiseData4, p_73164_2_, p_73164_3_, p_73164_4_, p_73164_5_, 1, p_73164_7_, 1.0D, 0.0D, 1.0D);
        this.noiseData5 = this.netherNoiseGen7.generateNoiseOctaves(this.noiseData5, p_73164_2_, p_73164_3_, p_73164_4_, p_73164_5_, 1, p_73164_7_, 100.0D, 0.0D, 100.0D);
        this.noiseData1 = this.netherNoiseGen3.generateNoiseOctaves(this.noiseData1, p_73164_2_, p_73164_3_, p_73164_4_, p_73164_5_, p_73164_6_, p_73164_7_, d0 / 80.0D, d1 / 60.0D, d0 / 80.0D);
        this.noiseData2 = this.netherNoiseGen1.generateNoiseOctaves(this.noiseData2, p_73164_2_, p_73164_3_, p_73164_4_, p_73164_5_, p_73164_6_, p_73164_7_, d0, d1, d0);
        this.noiseData3 = this.netherNoiseGen2.generateNoiseOctaves(this.noiseData3, p_73164_2_, p_73164_3_, p_73164_4_, p_73164_5_, p_73164_6_, p_73164_7_, d0, d1, d0);
        int k1 = 0;
        int l1 = 0;
        double[] adouble1 = new double[p_73164_6_];
        int i2;

        for (i2 = 0; i2 < p_73164_6_; ++i2)
        {
            adouble1[i2] = Math.cos((double)i2 * Math.PI * 6.0D / (double)p_73164_6_) * 2.0D;
            double d2 = (double)i2;

            if (i2 > p_73164_6_ / 2)
            {
                d2 = (double)(p_73164_6_ - 1 - i2);
            }

            if (d2 < 4.0D)
            {
                d2 = 4.0D - d2;
                adouble1[i2] -= d2 * d2 * d2 * 10.0D;
            }
        }

        for (i2 = 0; i2 < p_73164_5_; ++i2)
        {
            for (int k2 = 0; k2 < p_73164_7_; ++k2)
            {
                double d3 = (this.noiseData4[l1] + 256.0D) / 512.0D;

                if (d3 > 1.0D)
                {
                    d3 = 1.0D;
                }

                double d4 = 0.0D;
                double d5 = this.noiseData5[l1] / 8000.0D;

                if (d5 < 0.0D)
                {
                    d5 = -d5;
                }

                d5 = d5 * 3.0D - 3.0D;

                if (d5 < 0.0D)
                {
                    d5 /= 2.0D;

                    if (d5 < -1.0D)
                    {
                        d5 = -1.0D;
                    }

                    d5 /= 1.4D;
                    d5 /= 2.0D;
                    d3 = 0.0D;
                }
                else
                {
                    if (d5 > 1.0D)
                    {
                        d5 = 1.0D;
                    }

                    d5 /= 6.0D;
                }

                d3 += 0.5D;
                d5 = d5 * (double)p_73164_6_ / 16.0D;
                ++l1;

                for (int j2 = 0; j2 < p_73164_6_; ++j2)
                {
                    double d6 = 0.0D;
                    double d7 = adouble1[j2];
                    double d8 = this.noiseData2[k1] / 512.0D;
                    double d9 = this.noiseData3[k1] / 512.0D;
                    double d10 = (this.noiseData1[k1] / 10.0D + 1.0D) / 2.0D;

                    if (d10 < 0.0D)
                    {
                        d6 = d8;
                    }
                    else if (d10 > 1.0D)
                    {
                        d6 = d9;
                    }
                    else
                    {
                        d6 = d8 + (d9 - d8) * d10;
                    }

                    d6 -= d7;
                    double d11;

                    if (j2 > p_73164_6_ - 4)
                    {
                        d11 = (double)((float)(j2 - (p_73164_6_ - 4)) / 3.0F);
                        d6 = d6 * (1.0D - d11) + -10.0D * d11;
                    }

                    if ((double)j2 < d4)
                    {
                        d11 = (d4 - (double)j2) / 4.0D;

                        if (d11 < 0.0D)
                        {
                            d11 = 0.0D;
                        }

                        if (d11 > 1.0D)
                        {
                            d11 = 1.0D;
                        }

                        d6 = d6 * (1.0D - d11) + -10.0D * d11;
                    }

                    p_73164_1_[k1] = d6;
                    ++k1;
                }
            }
        }

        return p_73164_1_;
    }

}