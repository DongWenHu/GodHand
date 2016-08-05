package com.rzx.godhand.mark.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class Assets {
	public static void CopyAssets(Context context, String assetDir, String dir) {
		try {
			String str[] = context.getAssets().list(assetDir);
			if (str.length > 0) {//如果是目录
                String curDir = assetDir;
				for (String string : str) {
                    assetDir = curDir + "/" + string;
                    CopyAssets(context, assetDir, dir);
				}
			} else {//如果是文件
				InputStream is = context.getAssets().open(assetDir);
                String copyPathFile = dir + assetDir.substring(assetDir.indexOf("/") + 1);
                File f = new File(copyPathFile);
                if(f.getParentFile() != null && !f.getParentFile().exists()){
                    f.getParentFile().mkdirs();
                } else if (f.exists()){
                    f.delete();
                }

				FileOutputStream fos = new FileOutputStream(f);
				byte[] buffer = new byte[1024];
				while (true) {
					int len = is.read(buffer);
					if (len == -1) {
						break;
					}
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	public static void CopyAssets(Context context, String assetDir, String dir) {
//		String[] files;
//		try {
//			// 获得Assets一共有几多文件
//			files = context.getResources().getAssets().list(assetDir);
//		} catch (IOException e1) {
//			return;
//		}
//		delAllFile(dir);
//		File mWorkingPath = new File(dir);
//		// 如果文件路径不存在
//		if (!mWorkingPath.exists()) {
//			// 创建文件夹
//			if (!mWorkingPath.mkdirs()) {
//				// 文件夹创建不成功时调用
//			}
//		}
//		for (int i = 0; i < files.length; i++) {
//			try {
//				// 获得每个文件的名字
//				String fileName = files[i];
//				// 根据路径判断是文件夹还是文件
//				if (!fileName.contains(".")) {
//					if (0 == assetDir.length()) {
//						CopyAssets(context, fileName, dir + fileName);
//					} else {
//						CopyAssets(context, assetDir + "/" + fileName, dir
//								+ "/" + fileName);
//					}
//					continue;
//				}
//				File outFile = new File(mWorkingPath, fileName);
//				if (outFile.exists())
//					outFile.delete();
//				InputStream in = null;
//				if (0 != assetDir.length())
//					in = context.getAssets().open(assetDir + "/" + fileName);
//				else
//					in = context.getAssets().open(fileName);
//				OutputStream out = new FileOutputStream(outFile);
//				// Transfer bytes from in to out
//				byte[] buf = new byte[1024];
//				int len;
//				while ((len = in.read(buf)) > 0) {
//					out.write(buf, 0, len);
//				}
//				in.close();
//				out.close();
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}

	// param path 文件夹完整绝对路径
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}

	// 删除文件夹
	// param folderPath 文件夹完整绝对路径

	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
