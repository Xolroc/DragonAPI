/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Libraries.ReikaChatHelper;
import Reika.DragonAPI.Libraries.ReikaJavaLibrary;

public class ModLogger {

	private final boolean logLoading;
	private final boolean printDebug;
	private final boolean shouldWarn;

	private final DragonAPIMod mod;

	public ModLogger(DragonAPIMod mod, boolean load, boolean debug, boolean warn) {
		this.mod = mod;
		logLoading = load;
		printDebug = debug;
		shouldWarn = warn;
	}

	public void debug(Object o) {
		if (printDebug) {
			ReikaJavaLibrary.pConsole(o);
			ReikaChatHelper.write(o);
		}
	}

	public void log(Object o) {
		if (logLoading)
			ReikaJavaLibrary.pConsole(mod.getTechnicalName()+": "+o);
	}

	public void logError(Object o) {
		ReikaJavaLibrary.pConsole(mod.getTechnicalName()+": There was an error: "+o);
	}

	public boolean shouldLog() {
		return logLoading;
	}

	public boolean shouldDebug() {
		return printDebug;
	}

	public boolean shouldWarn() {
		return shouldWarn;
	}

	public void warn(Object o) {
		if (shouldWarn) {
			ReikaJavaLibrary.pConsole(o);
			ReikaChatHelper.write(o);
		}
	}

}
