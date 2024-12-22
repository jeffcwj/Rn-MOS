package me.nillerusr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gtastart.common.util.MToast;
import com.valvesoftware.source.R;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/* loaded from: classes.dex */
public class DirchActivity extends Activity implements View.OnTouchListener {
    static LinearLayout body;
    public static String cur_dir;
    public static final int sdk = Integer.valueOf(Build.VERSION.SDK).intValue();
    public SharedPreferences mPref;

    private static final String TAG = "DirchActivity";

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) { // 处理列表项点击
        if (event.getAction() == 1) {
            TextView btn = (TextView) v.findViewById(R.id.dirname);
            if (cur_dir == null) {
                ListDirectory("" + ((Object) btn.getText()));
                return false;
            }
            ListDirectory(cur_dir + "/" + ((Object) btn.getText()));
            return false;
        }
        return false;
    }

    public void ListDirectory(String path) {
        TextView header = (TextView) findViewById(R.id.header_txt);
        File myDirectory = new File(path);
        File[] directories = myDirectory.listFiles(new FileFilter() { // from class: me.nillerusr.DirchActivity.1
            @Override // java.io.FileFilter
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        if (directories != null && directories.length > 1) {
            Arrays.sort(directories, new Comparator<File>() { // from class: me.nillerusr.DirchActivity.2
                @Override // java.util.Comparator
                public int compare(File object1, File object2) {
                    return object1.getName().toUpperCase().compareTo(object2.getName().toUpperCase());
                }
            });
        }
        LayoutInflater ltInflater = getLayoutInflater();
        if (directories != null) {
            try {
                cur_dir = myDirectory.getCanonicalPath();
                header.setText(cur_dir);
            } catch (IOException e) {
            }
            body.removeAllViews();
            // 添加回到上级的item
            View view = ltInflater.inflate(R.layout.directory, (ViewGroup) body, false);
            TextView txt = (TextView) view.findViewById(R.id.dirname);
            txt.setText("..");
            body.addView(view);
            view.setOnTouchListener(this);

            // 添加子文件夹item
            for (File dir : directories) {
                View view2 = ltInflater.inflate(R.layout.directory, (ViewGroup) body, false);
                TextView txt2 = (TextView) view2.findViewById(R.id.dirname);
                txt2.setText(dir.getName());
                body.addView(view2);
                view2.setOnTouchListener(this);
            }
        }
    }

    public List<String> getExtStoragePaths() {
        List<String> list = new ArrayList<>();
        File[] fileList = new File("/storage/").listFiles();
        if (fileList != null) {
            for (File file : fileList) {
                if (!file.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath()) && file.isDirectory() && file.canRead()) {
                    list.add(file.getAbsolutePath());
                }
            }
        }
        return list;
    }

    @SuppressLint("MissingInflatedId")
    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mPref = getSharedPreferences("mod", Context.MODE_MULTI_PROCESS);
        requestWindowFeature(1);
        if (sdk >= 21) {
            super.setTheme(android.R.style.Theme_Material);
        } else {
            super.setTheme(android.R.style.Theme);
        }
        setContentView(R.layout.activity_directory_choice);
        cur_dir = null;
        body = (LinearLayout) findViewById(R.id.bodych);
        TextView header = (TextView) findViewById(R.id.header_txt);
        header.setText("");
        Button button = (Button) findViewById(R.id.button_choice);
        button.setOnClickListener(new View.OnClickListener() { // from class: me.nillerusr.DirchActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (DirchActivity.cur_dir != null) {
                    if (LauncherActivity.GamePath != null) {
                        LauncherActivity.GamePath.setText(DirchActivity.cur_dir + "/");
                    }
                    SharedPreferences.Editor editor = DirchActivity.this.mPref.edit();
                    editor.putString("gamepath", DirchActivity.cur_dir + "/");
                    editor.commit();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("result", "OK");
                    setResult(Activity.RESULT_OK, resultIntent);
                    DirchActivity.this.finish();
                }
            }
        });

        // 创建文件夹
        Button buttonCreateFolder = findViewById(R.id.button_create_folder);
        buttonCreateFolder.setOnClickListener(v -> {
            EditText et = new EditText(this);
            et.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
            et.setHint("请输入文件夹名称");
            new AlertDialog.Builder(DirchActivity.this) // 还用不了androidx的dialog，没崩住
                    .setTitle("新建文件夹")
                    .setView(et)
                    .setPositiveButton("创建", (dialog, b) -> {
                        Log.d("", "create folder: " + et.getText().toString());
                        String folderName = et.getText().toString();
                        if (folderName.isEmpty()) {
                            MToast.show(this, "请不要为空");
                            return;
                        }
                        File file = new File(cur_dir, folderName);
                        if (file.exists()) {
                            MToast.show(this, "文件夹已存在");
                            return;
                        }
                        if (file.mkdir()) {
                            ListDirectory(cur_dir + "/" + folderName); // 进入文件夹
                        } else {
                            MToast.show(this, "创建失败");
                        }
                    })
                    .show();
        });

        LauncherActivity.changeButtonsStyle((ViewGroup) getWindow().getDecorView());
        List<String> l = getExtStoragePaths();
        if (l == null || l.isEmpty()) {
            ListDirectory(LauncherActivity.getDefaultDir());
            return;
        }
        LayoutInflater ltInflater = getLayoutInflater();
        View view = ltInflater.inflate(R.layout.directory, (ViewGroup) body, false);
        TextView txt = (TextView) view.findViewById(R.id.dirname);
        txt.setText(LauncherActivity.getDefaultDir());
        body.addView(view);
        view.setOnTouchListener(this);
        for (String dir : l) {
            View view2 = ltInflater.inflate(R.layout.directory, (ViewGroup) body, false);
            TextView txt2 = (TextView) view2.findViewById(R.id.dirname);
            txt2.setText(dir);
            body.addView(view2);
            view2.setOnTouchListener(this);
        }
    }
}
