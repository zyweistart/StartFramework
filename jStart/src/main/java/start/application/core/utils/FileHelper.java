package start.application.core.utils;

import java.io.File;
import java.io.FilenameFilter;

public class FileHelper {

    /**
     * 获取文件名
     */
    public static String getFileName(String input){
        int fIndex = input.lastIndexOf("\\");
        if (fIndex == -1) {
            fIndex = input.lastIndexOf("/");
            if (fIndex == -1) {
                return input;
            }
        } 
        return input.substring(fIndex + 1);
    }
	
    /**
     * 获取文件名除开扩展名
     */
    public static String getFileNameNotExtension(String input){
    	String fileName=getFileName(input);
        Integer exint=fileName.indexOf(".");
        if(exint!=-1){
        	return fileName.substring(0,exint);
        }else{
        	return fileName;
        }
    }
    
	/**
	 * 获取扩展名
	 */
	public static String getExtension(String fileName) {
		int index = fileName.lastIndexOf(".");
		if (index != -1) {
			return fileName.substring(index);
		}
		return "";
	}

	/**
	 * 判断文件是否存在
	 */
	public static boolean isFileExists(String filename) {
		File file = new File(filename);
		return (file.isFile() && file.exists());
	}
    
    /**
	 * 判断目录是否存在
	 */
	public static boolean isDirExists(String filename) {
		File file = new File(filename);
		return (file.isDirectory() && file.exists());
	}
    
    /**
	 * 目录不存在则创建目录
	 */
    public static File createDirectory(String path) {
        final File dir = new File(path);
        if (!dir.exists()) {
        	dir.mkdirs();
        }
        return dir;
    }

	/**
	 * 级联创建目录
	 */
	public static boolean mkdirs(String filepath) {
		return mkdirs(new File(filepath));
	}

	/**
	 * 级联创建目录
	 */
	public static boolean mkdirs(File file) {
		if (!file.exists()) {
			return file.mkdirs();
		}
		return true;
	}

	/**
	 * 获取文件长度
	 */
	public static long getFileLength(String filename) {
		File file = new File(filename);
		if (file.isFile() && file.exists()) {
			return file.length();
		}
		return 0L;
	}

	/**
	 * 删除文件
	 */
	public static boolean deleteFile(String filename) {
		return deleteFile(new File(filename));
	}

	/**
	 * 删除文件，如果为目录则删除失败
	 */
	public static boolean deleteFile(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				return file.delete();
			} else if (file.isDirectory()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 删除目录
	 */
	public static boolean deleteDir(String dir) {
		return deleteDir(new File(dir));
	}

	/**
	 * 删除目录,如果为文件删除失败
	 */
	public static boolean deleteDir(File file) {
		if (file.exists()) {
			if (file.isFile()) {
				return false;
			} else if (file.isDirectory()) {
				if (!deleteAllFile(file.getPath())) {
					return false;
				}
				return file.delete();
			}
		}
		return true;
	}
	
	/**
	 * 删除目录，删除当前目录下的所有文件
	 */
	public static boolean deleteAllFile(String dir) {
		File file = new File(dir);
		if (!file.exists()) {
			return true;
		}
		if (!file.isDirectory()) {
			return false;
		}
		String[] tempList = file.list();
		File temp = null;
		boolean flag = false;
		for (int i = 0; i < tempList.length; i++) {
			if (dir.endsWith(File.separator)) {
				temp = new File(dir + tempList[i]);
			} else {
				temp = new File(dir + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				flag = temp.delete();
			} else if (temp.isDirectory()) {
				flag = deleteAllFile(dir + File.separator + tempList[i]);
				if (!flag) {
					return false;
				}
				flag = deleteDir(dir + File.separator + tempList[i]);
			}
			if (!flag) {
				return false;
			}
		}
		return true;
	}
	
	/**
     * 移出文件或文件夹
     */
    public static void remove(File directory) {
    	if(directory.exists()){
    		if (!directory.delete()) {
                File[] files = directory.listFiles();
                for (int i = 0, n = files.length; i < n; i++) {
                    if (files[i].isDirectory()) {
                        remove(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
            directory.delete();
    	}
    }

	public static boolean renameTo(String filenamefrom, String fileto) {
		return renameTo(new File(filenamefrom), new File(fileto));
	}

	public static boolean renameTo(File filefrom, String fileto) {
		return renameTo(filefrom, new File(fileto));
	}

	public static boolean renameTo(String filenamefrom, String filetopath, String filetoname) {
		File file = new File(filetopath);
		if (!file.exists()) {
			if (!file.mkdirs()) {
				return false;
			}
		}
		return renameTo(new File(filenamefrom), new File(filetopath + filetoname));
	}

	public static boolean renameTo(File filefrom, File fileto) {
		if (!filefrom.exists() || fileto.exists()) {
			return false;
		}
		if (!fileto.getParentFile().exists()) {
			if (!fileto.getParentFile().mkdirs()) {
				return false;
			}
		}
		return filefrom.renameTo(fileto);
	}

	/**
	 * 根据正则表达式过滤出当前目录下满足条件的文件名
	 */
	public static File[] getFilesByFilterRegex(String dirpath, final String regex) {
		File dir = new File(dirpath);
		if (dir.exists() && dir.isDirectory()) {
			return dir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.matches(regex);
				}
			});
		} else {
			return null;
		}
	}

}
