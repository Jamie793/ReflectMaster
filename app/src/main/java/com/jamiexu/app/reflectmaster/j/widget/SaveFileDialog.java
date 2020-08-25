package com.jamiexu.app.reflectmaster.j.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.jamiexu.app.reflectmaster.MainActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveFileDialog extends AlertDialog.Builder {

    public SaveFileDialog(Context context, String title, String hint, OnSaveListener onSaveListener) {
        super(context);
        EditText editText = new EditText(context);
        editText.setHint(hint);
        editText.setText(MainActivity.BASE_PATH);

        this.setTitle(title);
        this.setView(editText);
        this.setPositiveButton("保存", (dialog, which) -> {
            String path = editText.getText().toString().trim();
            if (path.length() == 0) {
                Toast.makeText(context, "请输入路径", Toast.LENGTH_SHORT).show();
                return;
            }
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(path);
                onSaveListener.onSave(path, fileOutputStream);
            } catch (FileNotFoundException e) {
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
        });
    }

    public interface OnSaveListener {
        void onSave(String path, FileOutputStream fileOutputStream);
    }
}
