package fr.skyforce77.prn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class IconManager {

	public static void setIcon(String icon) {
		if(!new File(PRN.getDirectory(), "resources/textures/"+icon+".png").exists()) {
			try {
				File tdir = new File(PRN.getDirectory(), "resources/textures/");
				tdir.mkdirs();
				File ic = new File(tdir, icon+".png");
				ic.createNewFile();
				InputStream in = PRN.class.getResourceAsStream("/resources/textures/"+icon+".png");
				OutputStream out = new FileOutputStream(ic);
				byte[] buffer = new byte[1024];
				int len = in.read(buffer);
				while (len != -1) {
				    out.write(buffer, 0, len);
				    len = in.read(buffer);
				}
				in.close();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		PRN.icon.setFromFile(new File(PRN.getDirectory(), "resources/textures/"+icon+".png").getPath());
	}
}
