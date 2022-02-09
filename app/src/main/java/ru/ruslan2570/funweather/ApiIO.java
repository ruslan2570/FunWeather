package ru.ruslan2570.funweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.prefs.Preferences;
import com.google.common.io.*;


public class ApiIO {
	private String path;
	private File file;
		public ApiIO(File file) throws Exception {
			this.path = file.getPath() + "/Api.txt";
			file = new File(path);
			if(file.isFile()){
				ReadApikey();
			}
			else{
				WriteApiKey();
			}

		}

		public String ReadApikey() throws Exception {
			FileInputStream fileInput = new FileInputStream(file);
			byte[] buff = ByteStreams.toByteArray(fileInput);
			fileInput.close();
			return new String(buff);
		}

		public void WriteApiKey() throws Exception{
			FileOutputStream fileOutput = new FileOutputStream(file);

		}



}
