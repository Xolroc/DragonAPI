/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Resources;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class GuiGuide extends GuiScreen {

	private static final ArrayList<ModList> mods = new ArrayList();

	private int screen;
	private int page;

	protected final int xSize = 256;
	protected final int ySize = 220;

	public GuiGuide() {

	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2 - 8;

		String file = "/Reika/DragonAPI/Textures/guidetab.png";
		buttonList.add(new GuiButton(10, j-19, 17+k+163, 20, 20, "-")); //Prev Page
		buttonList.add(new GuiButton(11, j-19, 17+k+143, 20, 20, "+"));	//Next page
		buttonList.add(new GuiButton(15, j-19, 17+k+183, 20, 20, "<<"));	//1st page
		buttonList.add(new GuiButton(12, j+xSize-27, k+6, 20, 20, "X"));	//Close gui button


		if (screen > 0) {
			String[] labels = {"Info", "Getting Started", "Useful Notes", "", ""};
			for (int i = 0; i < 5; i++) {
				buttonList.add(new GuiButton(i, j-19-65+1, k+20*i, 85, 20, labels[i])); //Prev Page
			}
		}
	}

	/**
	 * Returns true if this GUI should pause the game when it is displayed in single-player
	 */
	@Override
	public boolean doesGuiPauseGame()
	{
		return true;
	}

	private void drawModLogo(ModList mod) {

	}

	@Override
	public void actionPerformed(GuiButton button) {
		if (button.id == 12) {
			mc.thePlayer.closeScreen();
			return;
		}
		if (button.id == 15) {
			screen = 0;
			page = 0;
			this.initGui();
			return;
		}
		if (button.id == 10) {
			if (screen > 0) {
				screen--;
				page = 0;
			}
			this.initGui();
			return;
		}
		if (button.id == 11) {
			if (screen < this.getMaxPage()) {
				screen++;
				page = 0;
			}
			else {
				screen = 0;
				page = 0;
			}
			this.initGui();
			return;
		}/*
		if (screen == HandbookRegistry.TOC.getScreen()) {
			switch(button.id) {
			case 0:
				screen = HandbookRegistry.TERMS.getScreen();
				break;
			case 1:
				screen = HandbookRegistry.MISCDESC.getScreen();
				break;
			case 2:
				screen = HandbookRegistry.ENGINEDESC.getScreen();
				break;
			case 3:
				screen = HandbookRegistry.TRANSDESC.getScreen();
				break;
			case 4:
				screen = HandbookRegistry.PRODMACHINEDESC.getScreen();
				break;
			case 5:
				screen = HandbookRegistry.TOOLDESC.getScreen();
				break;
			case 6:
				screen = HandbookRegistry.RESOURCEDESC.getScreen();
				break;
			}
			this.initGui();
			page = 0;
			return;
		}*/
		page = button.id;
		this.initGui();
	}

	private int getMaxPage() {
		return mods.size();
	}

	private void drawTabIcons() {
		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2;
	}

	private void drawGraphics() {
		int posX = (width - xSize) / 2-2;
		int posY = (height - ySize) / 2-8;

		String s = "";
		if (screen >= 1)
			s = mods.get(screen-1).getDisplayName();
		else
			s = "Reika's Mods";
		fontRenderer.drawString(String.format("%s", s), posX+10, posY+8, 0);
	}

	@Override
	public void drawScreen(int x, int y, float f)
	{
		String var4 = "/Reika/DragonAPI/Textures/guidebcg.png";
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ReikaTextureHelper.bindTexture(DragonAPICore.class, var4);

		int posX = (width - xSize) / 2;
		int posY = (height - ySize) / 2 - 8;

		this.drawTexturedModalRect(posX, posY, 0, 0, xSize, ySize);

		int xo = 0;
		int yo = 0;
		/*
		fontRenderer.drawString(HandbookRegistry.getEntry(screen, page).getTitle(), posX+xo+6, posY+yo+6, 0x000000);
		HandbookRegistry h = HandbookRegistry.getEntry(screen, page);

		fontRenderer.drawSplitString(String.format("%s", h.getData()), posX+descX, posY+descY, 242, 0xffffff);
		 */
		this.drawGraphics();

		super.drawScreen(x, y, f);

		this.drawTabIcons();
	}

	static {
		List<ModList> reika = ModList.getReikasMods();
		for (int i = 0; i < reika.size(); i++) {
			if (reika.get(i).isLoaded())
				mods.add(reika.get(i));
		}
	}
}