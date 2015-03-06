package Reika.DragonAPI.ModInteract.ItemHandlers;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityCrystalPlant;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Interfaces.CropHandler;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public class CrystalPlantHandler implements CropHandler {

	@Override
	public int getHarvestedMeta(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}

	@Override
	public boolean isCrop(Block id) {
		return id == ChromaBlocks.PLANT.getBlockInstance();
	}

	@Override
	public boolean isRipeCrop(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		return te instanceof TileEntityCrystalPlant && ((TileEntityCrystalPlant)te).canHarvest();
	}

	@Override
	public void makeRipe(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityCrystalPlant) {
			((TileEntityCrystalPlant)te).makeRipe();
		}
	}

	@Override
	public boolean isSeedItem(ItemStack is) {
		return ChromaItems.SEED.matchWith(is);
	}

	@Override
	public ArrayList<ItemStack> getAdditionalDrops(World world, int x, int y, int z, Block id, int meta, int fortune) {
		if (ChromaOptions.CRYSTALFARM.getState() && ReikaRandomHelper.doWithChance(0.01)) {
			ArrayList li = new ArrayList();
			ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(world.getBlockMetadata(x, y, z));
			li.add(shard);
			return li;
		}
		return null;
	}

	@Override
	public void editTileDataForHarvest(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityCrystalPlant) {
			((TileEntityCrystalPlant)te).harvest(false);
		}
	}

	@Override
	public boolean initializedProperly() {
		return ModList.CHROMATICRAFT.isLoaded() && ChromaBlocks.PLANT.getBlockInstance() != null;
	}

}
