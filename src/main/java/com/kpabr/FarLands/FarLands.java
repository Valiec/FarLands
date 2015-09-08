package com.kpabr.FarLands;
/*
 * Created by Valiec2019
 * on July 25, 2015
 * currently using Minecraft Forge 10.13.4.1448
 */

import java.io.IOException;
import java.net.UnknownHostException;

import com.kpabr.FarLands.CommonProxy;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.ChunkProviderEnd;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.ChunkProviderHell;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;
@Mod(modid = FarLands.MODID, version = FarLands.VERSION, name = FarLands.NAME)
public class FarLands
{
    @SidedProxy(clientSide="com.kpabr.FarLands.client.ClientProxy", serverSide="com.kpabr.FarLands.CommonProxy")
    public static CommonProxy proxy;
 
    /*Mod ID and Version declarations*/
    public static final String MODID = "farlands";
    public static final String VERSION = "1.0.1";
    public static final String NAME = "FarLands";
    
    public static WorldType farlands; 
    
    public static FarLands instance;
    
    public static int threshold;
    
    static FarVersionChecker versionChecker = new  FarVersionChecker();
    
    public static Configuration config;
    
    public static int versionID = 2;
    

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	config = new Configuration(event.getSuggestedConfigurationFile()); //gets default config file
    	
    	FarLands.config.load();
        this.threshold = FarLands.config.getInt("FarLandsStart", Configuration.CATEGORY_GENERAL, -1, -1, 12550820, "Approximate Far Lands start distance (set to -1 for default distance)");
        FarLands.config.save();
        
    }
    @EventHandler
    public void load(FMLInitializationEvent event)
    {
    	ClientCommandHandler.instance.registerCommand(new VersionCommand());
    	
    	farlands = new FarWorldType("FarLands");
        
        this.instance = this;
        
        FMLCommonHandler.instance().bus().register(versionChecker);
        MinecraftForge.EVENT_BUS.register(versionChecker);
        
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.TERRAIN_GEN_BUS.register(this);
        
     	proxy.registerRenderers();
    }
    @SubscribeEvent
    public void onGen(ChunkProviderEvent.InitNoiseField event)
    {
    	
        if (event.chunkProvider instanceof ChunkProviderEnd && Minecraft.getMinecraft().theWorld.provider.terrainType instanceof FarWorldType)
        {
            ChunkProviderFarEnd end = (ChunkProviderFarEnd)(new ChunkProviderFarEnd(DimensionManager.getWorld(1), DimensionManager.getWorld(1).getSeed()));
            event.setResult(Result.DENY);

            event.noisefield = end.initializeNoiseField(null, event.posX, event.posY, event.posZ, event.sizeX, event.sizeY, event.sizeZ);
    
        }
        else if (event.chunkProvider instanceof ChunkProviderHell && Minecraft.getMinecraft().theWorld.provider.terrainType instanceof FarWorldType)
        {
        	
            ChunkProviderFarNether nether = (ChunkProviderFarNether)(new ChunkProviderFarNether(DimensionManager.getWorld(-1), DimensionManager.getWorld(-1).getSeed()));
            event.setResult(Result.DENY);

            event.noisefield = nether.initializeNoiseField(null, event.posX, event.posY, event.posZ, event.sizeX, event.sizeY, event.sizeZ);
    
        }  
    }
}

