package com.jamiexu.utils;

import com.jamiexu.utils.Base64Utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {

    public static boolean putString(String path, String content) {
        FileWriter fileWriter = null;
        try {
            File file = new File(path);
            if (!file.exists())
                file.createNewFile();

            fileWriter = new FileWriter(path);
            fileWriter.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (fileWriter != null) {
                try {
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return false;
    }


    public static String getString(String path) {
        FileInputStream fileInputStream = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            fileInputStream = new FileInputStream(path);
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String s = null;
            while ((s = bufferedReader.readLine()) != null) {
                stringBuilder.append(s).append("\n");
            }
            return stringBuilder.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    public static boolean writeFile(String path, byte[] byt) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(path);
            fileOutputStream.write(byt);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    public static byte[] readFile(String path) {
        FileInputStream fileInputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            fileInputStream = new FileInputStream(path);
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] byt = new byte[8 * 1024];
            int len;
            while ((len = fileInputStream.read(byt, 0, byt.length)) != -1) {
                byteArrayOutputStream.write(byt, 0, len);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.flush();
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    public static String getFileMd5(String path) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(path);
            MessageDigest messageDigest = MessageDigest.getInstance("md5");
            byte[] byt = new byte[1024 * 8];
            int len = -1;
            while ((len = fileInputStream.read(byt, 0, byt.length)) != -1) {
                messageDigest.update(byt, 0, byt.length);
            }
            return Base64Utils.encode(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getFileSha1(String path) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(path);
            MessageDigest messageDigest = MessageDigest.getInstance("sha1");
            byte[] byt = new byte[1024 * 8];
            int len = -1;
            while ((len = fileInputStream.read(byt, 0, byt.length)) != -1) {
                messageDigest.update(byt, 0, byt.length);
            }
            return Base64Utils.encode(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean makeDir(String path) {
        File file = new File(path);
        if (file.exists())
            return false;
        file.mkdirs();
        return true;
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        if (file.exists())
            return false;
        file.delete();
        return true;
    }

    public static boolean copyFile(String path, String path2, boolean n, boolean ismake) {
        File file = new File(path2);
        if (!file.getParentFile().exists() && ismake)
            file.getParentFile().mkdirs();

        if (!new File(path).exists())
            return false;

        if (file.exists() && n) {
            file.delete();
            return false;
        }

        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(path);
            fileOutputStream = new FileOutputStream(path2);
            byte[] byt = new byte[8 * 1024];
            int len;
            while ((len = fileInputStream.read(byt, 0, byt.length)) != -1) {
                fileOutputStream.write(byt, 0, len);
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    public static boolean deZip(String from, String to, String name) {
        ZipInputStream zipInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            if (!name.endsWith("/"))
                name += "/";
            zipInputStream = new ZipInputStream(new FileInputStream(from));
            ZipEntry zipEntry = null;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (!zipEntry.isDirectory()) {
                    String entryName = zipEntry.getName();
                    if (entryName.startsWith(name)) {
                        entryName = entryName.replace(name, "");
                        File file = new File(to, entryName);
                        if (!file.getParentFile().exists())
                            file.getParentFile().mkdirs();
//
//                        //开始写出文件
                        fileOutputStream = new FileOutputStream(new File(to, entryName));
                        byte[] byt = new byte[8 * 1024];
                        int len = -1;
                        while ((len = zipInputStream.read(byt, 0, byt.length)) != -1) {
                            fileOutputStream.write(byt, 0, len);
                        }
                    }
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipInputStream != null) {
                try {
                    zipInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


}
