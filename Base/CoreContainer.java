/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import Reika.DragonAPI.Instantiable.GUI.Slot.SlotNoClick;
import Reika.DragonAPI.Interfaces.MultiPageInventory;
import Reika.DragonAPI.Interfaces.XPProducer;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class CoreContainer extends Container {

	protected final TileEntity tile;
	int posX; int posY; int posZ;
	protected EntityPlayer ep;

	protected ItemStack[] oldInv;

	protected final IInventory ii;

	private boolean alwaysCan = false;

	private static final TileEntityChest fakeChest = new TileEntityChest();

	public CoreContainer(EntityPlayer player, TileEntity te)
	{
		tile = te;
		posX = tile.xCoord;
		posY = tile.yCoord;
		posZ = tile.zCoord;
		ep = player;
		//this.detectAndSendChanges();

		if (te instanceof IInventory)
			ii = (IInventory)te;
		else
			ii = null;
	}

	public CoreContainer setAlwaysInteractable() {
		alwaysCan = true;
		return this;
	}

	public boolean hasInventoryChanged(ItemStack[] inv) {
		for (int i = 0; i < oldInv.length; i++)
			if (!ItemStack.areItemStacksEqual(oldInv[i], inv[i]))
				return true;
		return false;
	}

	protected void updateInventory(ItemStack[] inv) {
		for (int i = 0; i < oldInv.length; i++)
			oldInv[i] = inv[i];
	}

	protected void addPlayerInventoryWithOffset(EntityPlayer player, int dx, int dy) {
		for (int i = 0; i < 3; i++)
		{
			for (int k = 0; k < 9; k++)
			{
				this.addSlotToContainer(new Slot(player.inventory, k + i * 9 + 9, dx+8 + k * 18, dy+84 + i * 18));
			}
		}

		for (int j = 0; j < 9; j++)
		{
			this.addSlotToContainer(new Slot(player.inventory, j, dx+8 + j * 18, dy+142));
		}
	}

	protected void addPlayerInventory(EntityPlayer player) {
		this.addPlayerInventoryWithOffset(player, 0, 0);
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		for (int i = 0; i < crafters.size(); i++)
		{
			ICrafting icrafting = (ICrafting)crafters.get(i);
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return alwaysCan || this.isStandard8mReach(player);
	}

	public final boolean isStandard8mReach(EntityPlayer player) {
		double dist = ReikaMathLibrary.py3d(tile.xCoord+0.5-player.posX, tile.yCoord+0.5-player.posY, tile.zCoord+0.5-player.posZ);
		return (dist <= 8);
	}

	@Override
	public final ItemStack transferStackInSlot(EntityPlayer player, int slot)
	{
		ItemStack ret = this.onShiftClickSlot(player, slot, ((Slot)inventorySlots.get(slot)).getStack());
		if (ret != null)
			return ret;
		ItemStack is = null;
		Slot fromSlot = (Slot)inventorySlots.get(slot);
		if (!(tile instanceof IInventory))
			return null;
		int invsize = ((IInventory)tile).getSizeInventory();
		int base = 0;
		if (tile instanceof MultiPageInventory) {
			MultiPageInventory mp = (MultiPageInventory)tile;
			invsize = mp.getSlotsOnPage(mp.getCurrentPage());
			int cur = mp.getCurrentPage();
			for (int i = 0; i < cur; i++) {
				base += mp.getSlotsOnPage(i);
			}
		}

		if (fromSlot != null && fromSlot.getHasStack()) {
			ItemStack inslot = fromSlot.getStack();
			is = inslot.copy();
			boolean toPlayer = slot < invsize+base;

			if (toPlayer) {
				for (int i = invsize+base; i < inventorySlots.size() && is.stackSize > 0; i++) {
					//ReikaJavaLibrary.pConsole(i);
					Slot toSlot = (Slot)inventorySlots.get(i);
					if (toSlot.isItemValid(is) && this.canAdd(is, toSlot.getStack())) {
						if (!toSlot.getHasStack()) {
							toSlot.putStack(is.copy());
							is.stackSize = 0;
						}
						else {
							ItemStack inToSlot = toSlot.getStack();
							int add = inToSlot.getMaxStackSize()-inToSlot.stackSize;
							if (add > is.stackSize)
								add = is.stackSize;
							ItemStack toAdd = ReikaItemHelper.getSizedItemStack(is, inToSlot.stackSize+add);
							//ReikaJavaLibrary.pConsole(is+" to "+inToSlot+" for "+toAdd+", by "+add);
							toSlot.putStack(toAdd);
							is.stackSize -= add;
						}
						if (tile instanceof XPProducer) {
							float xp = ((XPProducer)tile).getXP();
							ep.addExperience((int)xp);
							((XPProducer)tile).clearXP();
						}
					}
					else {

					}
				}
				if (is.stackSize <= 0) {
					fromSlot.putStack(null);
				}
				else {
					fromSlot.putStack(is.copy());
				}
				is = null;
				return is;
			}
			else {
				List<Slot> list = this.getOrderedSlotList();
				for (int i = base; i < ((IInventory)tile).getSizeInventory() && i < list.size() && is.stackSize > 0; i++) {
					Slot toSlot = list.get(i);
					int lim = ((IInventory)tile).getInventoryStackLimit();
					//ReikaJavaLibrary.pConsole(i+" "+toSlot+":"+toSlot.getSlotIndex()+" E ["+base+", "+((IInventory)tile).getSizeInventory()+") > "+toSlot.isItemValid(is), Side.SERVER);
					if (toSlot.isItemValid(is) && (((IInventory)tile).isItemValidForSlot(i, is)) && this.canAdd(is, toSlot.getStack())) {
						if (!toSlot.getHasStack()) {
							if (is.stackSize <= lim) {
								toSlot.putStack(is.copy());
								//ReikaJavaLibrary.pConsole(toSlot.getSlotIndex());
								is.stackSize = 0;
							}
							else {
								toSlot.putStack(ReikaItemHelper.getSizedItemStack(is, lim));
								is.stackSize -= lim;
							}
						}
						else {
							ItemStack inToSlot = toSlot.getStack();
							int add = Math.min(inToSlot.getMaxStackSize()-inToSlot.stackSize, lim-inToSlot.stackSize);
							if (add > is.stackSize)
								add = is.stackSize;
							toSlot.putStack(ReikaItemHelper.getSizedItemStack(is, inToSlot.stackSize+add));
							is.stackSize -= add;
						}
					}
					else {

					}
				}
				if (is.stackSize <= 0) {
					fromSlot.putStack(null);
				}
				else {
					fromSlot.putStack(is.copy());
				}
				is = null;
				return is;
			}
		}

		return null;
	}

	/** Return non-null here to stop all normal shift-click behavior */
	protected ItemStack onShiftClickSlot(EntityPlayer player, int slot, ItemStack is) {
		return null;
	}

	private boolean canAdd(ItemStack is, ItemStack inslot) {
		if (inslot == null)
			return true;
		return ReikaItemHelper.matchStacks(is, inslot) && ItemStack.areItemStackTagsEqual(is, inslot);
	}

	@Override //To avoid a couple crashes with some mods (or vanilla packet system) not checking array bounds
	public Slot getSlot(int index)
	{
		if (index >= inventorySlots.size() || index < 0) {
			Object o = "A mod tried to access an invalid slot "+index+" for TileEntity "+tile+".";
			Object o2 = "It is likely assuming the TileEntity has an inventory, but it does not.";
			Object o3 = "Check for any inventory-modifying mods and items you are carrying.";
			ReikaJavaLibrary.pConsole(o);
			ReikaJavaLibrary.pConsole(o2);
			ReikaJavaLibrary.pConsole(o3);
			ReikaChatHelper.write(o);
			ReikaChatHelper.write(o2);
			ReikaChatHelper.write(o3);
			//Thread.dumpStack();
			return new Slot(fakeChest, index, -20, -20); //create new slot off screen; hacky fix, but should work
		}
		return (Slot)inventorySlots.get(index);
	}

	protected void addSlot(int i, int x, int y) {
		if (ii == null)
			return;
		this.addSlotToContainer(new Slot(ii, i, x, y));
	}

	protected void addSlotNoClick(int i, int x, int y) {
		if (ii == null)
			return;
		this.addSlotToContainer(new SlotNoClick(ii, i, x, y));
	}

	@Override
	public ItemStack slotClick(int ID, int par2, int par3, EntityPlayer ep) {
		//ReikaJavaLibrary.pConsole(ID, Side.SERVER);
		ItemStack is = super.slotClick(ID, par2, par3, ep);
		if (ii != null && tile instanceof XPProducer) {
			if (ID < ii.getSizeInventory()) {
				float xp = ((XPProducer) tile).getXP();
				if (xp > 0) {
					ep.addExperience((int)xp);
					((XPProducer) tile).clearXP();
					ep.playSound("random.orb", 0.3F, 1);
				}
			}
		}
		return is;
	}

	private List<Slot> getOrderedSlotList() {
		List<Slot> copy = new ArrayList(inventorySlots);
		Collections.sort(copy, new SlotComparator());
		Iterator<Slot> it = copy.iterator();
		while (it.hasNext()) {
			Slot s = it.next();
			if (s.inventory instanceof InventoryPlayer)
				it.remove();
		}
		/*
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < copy.size(); i++) {
			Slot slot = copy.get(i);
			sb.append(slot.getSlotIndex()+":"+slot.getClass().getSimpleName()+":"+slot.inventory);
			sb.append(", ");
		}
		sb.append("]");
		ReikaJavaLibrary.pConsole(sb.toString());
		 */
		return copy;
	}

	private static class SlotComparator implements Comparator<Slot> {

		@Override
		public int compare(Slot o1, Slot o2) {
			return o1.getSlotIndex() - o2.getSlotIndex();
		}

	}

}
