package android.support.v4.app.reflectmaster.Utils;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Utils {
    public static final String BASEPATH = Environment.getExternalStorageDirectory().toString() + "/ReflectMaster";

    //	public static  int getid(String str)
//	{
//		int result = 0;
//		for(char c:str.toCharArray())
//		{
//			int i = c;
//			result+=i;
//
//
//
//		}
//		return result*2;
//	}
    public static int $(String str) {
        int[] ii = $(Integer.parseInt(str));
        return ii[0] * ii[1] + ii[2] - ii[3];
    }




    private static int[] $(int i) {
        int[] ii = new int[4];
        ii[0] = (i >> 24) & 0xFF;
        ii[1] = ((i >> 16) & 0xFF);
        ii[2] = (i >> 8) & 0xFF;
        ii[3] = (i & 0xFF);
        return ii;
    }

    public static void writeClipboard(Context context, String str) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", str);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }

    public static void writeText(String file, String content) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File(file));
            fileWriter.write(content);
            fileWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readText(String file) {
        StringBuilder stringBuilder = new StringBuilder();
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            int c = 0;
            while ((c = fileReader.read()) != -1) {
                stringBuilder.append((char) c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    public static void showDialog(Context context,
                                  String title,
                                  String message,
                                  String potext, DialogInterface.OnClickListener polistener,
                                  String negtext, DialogInterface.OnClickListener neglistener,
                                  String neutext, DialogInterface.OnClickListener neulistene,
                                  DialogInterface.OnCancelListener cancelListenerlistener,
                                  boolean outsidecancel,
                                  boolean cancel) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        if (title != null)
            alert.setTitle(title);
        if (message != null)
            alert.setMessage(message);
        if (potext != null)
            alert.setPositiveButton(potext, polistener);
        if (negtext != null)
            alert.setNegativeButton(negtext, neglistener);
        if (neutext != null)
            alert.setNeutralButton(neutext, neglistener);
        if (cancelListenerlistener != null)
            alert.setOnCancelListener(cancelListenerlistener);
        alert.setCancelable(cancel);
        alert.show().setCanceledOnTouchOutside(outsidecancel);


    }


    public static byte[] readFile(String path) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(path);
            byte[] byt = new byte[1024];
            int len = -1;
            while ((len = fileInputStream.read(byt)) != -1) {
                byteArrayOutputStream.write(byt, 0, len);
            }
            byteArrayOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                byteArrayOutputStream.close();
                if (fileInputStream != null)
                    fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void writeFile(String path, byte[] data) {
        FileOutputStream fileOutputStream = null;
        try {
            File file = new File(path);
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data);
            fileOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void showToast(Context context, String str, int dur) {
        Toast.makeText(context, str, dur).show();
    }

    public static void installAssetsApk(Context c, String name) {

        AssetManager am = c.getAssets();
        InputStream is = null;


        try {
            is = am.open(name);
        } catch (IOException e) {
            return;
        }

        byte[] buff = null;

        try {
            buff = new byte[is.available()];
        } catch (IOException e) {
        }

        try {
            is.read(buff);
        } catch (IOException e) {
        }


        FileOutputStream fos = null;
        try {
            fos = c.openFileOutput(name, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
        } catch (FileNotFoundException e) {
        }
        try {
            fos.write(buff);
        } catch (IOException e) {
        }

        try {
            fos.flush();
        } catch (IOException e) {
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        Uri u = Uri.fromFile(c.getFileStreamPath(name));

        i.setDataAndType(u, "application/vnd.android.package-archive");
        c.startActivity(i);

    }


    public static String sdtoString(String path) {


        File f = new File(path);

        InputStream is = null;

        try {
            is = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            return null;
        }


        byte[] buff;
        try {
            buff = new byte[is.available()];
        } catch (IOException e) {
            return null;
        }

        try {
            is.read(buff);
        } catch (IOException e) {
            return null;
        }

        try {
            is.close();
        } catch (IOException e) {
            return null;
        }

        return new String(buff);
    }


    public static byte[] Encryption(byte[] data, byte[] key, int mode) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(getRawKey(key), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(mode, skeySpec);
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static byte[] getRawKey(byte[] key) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = new SecureRandom(key);
            secureRandom.setSeed(key);
            keyGenerator.init(256, secureRandom);
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static byte[] hexToBytes(String hex) {
        byte[] byt = new byte[hex.length() / 2];
        for (int i = 0; i < byt.length; i = i + 2) {
            byt[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return byt;
    }

    public static String bytesToHex(byte[] byt) {
        StringBuilder stringBuilder = new StringBuilder(byt.length * 2);
        for (int i = 0; i < byt.length; i++) {
            stringBuilder.append(String.format("%02x", byt[i] & 0xFF));
        }
        return stringBuilder.toString();
    }


    public static int zipDe(String from, String to, String name) {
        try {
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(from));
            if (!to.startsWith("/"))
                to = "/" + to;
            if (!to.endsWith("/"))
                to = to + "/";
            ZipEntry zipEntry = null;
            while ((zipEntry = zipInputStream.getNextEntry()) != null && !zipEntry.isDirectory()) {
                String zname = zipEntry.getName();
                if (zname.indexOf(name) != -1) {
                    String na = zname.replace(name, "");
                    File f = new File(to, na);
                    if (!f.exists())
                        f.getParentFile().mkdirs();
                    FileOutputStream fileOutputStream = new FileOutputStream(f);
                    int len = 0;
                    byte[] byt = new byte[1024];
                    while ((len = zipInputStream.read(byt, 0, byt.length)) != -1) {
                        fileOutputStream.write(byt, 0, len);
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            }
            zipInputStream.closeEntry();
            zipInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return -1;
        } catch (IOException e) {
            e.printStackTrace();
            return -2;
        }
        return 0;
    }

    public static Boolean stringtosd(String str, String path) {
        File f = new File(path);
        if (!f.getParentFile().exists())
            f.getParentFile().mkdirs();
        try {
            f.createNewFile();
        } catch (IOException e) {
            return false;
        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            return false;
        }
        try {
            os.write(str.getBytes());
        } catch (IOException e) {
            return false;
        }
        try {
            os.close();
        } catch (IOException e) {

            return false;
        }
        return true;
    }

    public static String download(String url) {

        String result = null;


        HttpURLConnection connection;

        URL Url = null;


        try {

            Url = new URL(url);
            connection = (HttpURLConnection) Url.openConnection();

        } catch (Exception e) {
            return null;
        }

        try {
            InputStream is = connection.getInputStream();

            byte[] buff = getBytesByInputStream(is);
            result = new String(buff);
            is.close();

        } catch (IOException e) {
        }


        return result;
    }

    public static String donwloadFile(String url, String out) {

        HttpURLConnection connection;

        URL Url = null;


        try {

            Url = new URL(url);
            connection = (HttpURLConnection) Url.openConnection();

        } catch (Exception e) {
            return e.toString();
        }

        try {
            InputStream is = connection.getInputStream();

            OutputStream os = new FileOutputStream(out);
            writeStream(is, os);

        } catch (IOException e) {
            return e.toString();
        }


        return null;


    }

    public static boolean writeStream(InputStream is, OutputStream os) throws IOException {
        if (is == null) return false;
        byte[] buff = new byte[1024 * 4];

        int leng = 0;

        while ((leng = is.read(buff)) != -1) {
            os.write(buff, 0, leng);

        }
        os.flush();
        os.close();

        return true;
    }

    public static byte[] getBytesByInputStream(InputStream is) {
        byte[] bytes = null;
        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        byte[] buffer = new byte[1024 * 8];
        int length = 0;
        try {
            while ((length = bis.read(buffer)) > 0) {
                bos.write(buffer, 0, length);
            }
            bos.flush();
            bytes = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bytes;
    }

    public static void Fdialog(Context c, Object title, Object msg, Object button1, Object button2, DialogInterface.OnClickListener listener1, DialogInterface.OnClickListener listener2) {
        String t, m = null, b1, b2;
        t = title instanceof String ? (String) title : (c.getString((int) title));
        if (msg != null)
            m = msg instanceof String ? (String) msg : (c.getString((Integer) msg));
        b1 = button1 instanceof String ? (String) button1 : (c.getString((Integer) button1));

        b2 = button1 instanceof String ? (String) button2 : (c.getString((Integer) button2));


        Fdialog(c, t, m, b1, b2, listener1, listener2);

    }

}
