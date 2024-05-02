package de.jeff_media.chestsort.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import de.jeff_media.chestsort.ChestSortPlugin;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Utils {

	public static ItemStack[] getStorageContents(Inventory inv) {
		return inv.getStorageContents();
	}
	
	// We need this to write the category files inside the .jar to the disk
	// Maybe there is a smarter way, i don't know.
	public static byte[] getBytes(InputStream is) throws IOException {

	    int len;
	    int size = 1024;
	    byte[] buf;

	    if (is instanceof ByteArrayInputStream) {
	      size = is.available();
	      buf = new byte[size];
	      len = is.read(buf, 0, size);
	    } else {
	      ByteArrayOutputStream bos = new ByteArrayOutputStream();
	      buf = new byte[size];
	      while ((len = is.read(buf, 0, size)) != -1)
	        bos.write(buf, 0, len);
	      buf = bos.toByteArray();
	    }
	    return buf;
	  }
	
	public static String shortToStringWithLeadingZeroes(short number) {
		return String.format("%05d", number);
	}
	
	public static void renameFileInPluginDir(ChestSortPlugin plugin,String oldName, String newName) {
		File oldFile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + oldName);
		File newFile = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + newName);
		oldFile.getAbsoluteFile().renameTo(newFile.getAbsoluteFile());
	}
}
