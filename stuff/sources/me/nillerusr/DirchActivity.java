package me.nillerusr;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
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

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
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
            View view = ltInflater.inflate(R.layout.directory, (ViewGroup) body, false);
            TextView txt = (TextView) view.findViewById(R.id.dirname);
            txt.setText("..");
            body.addView(view);
            view.setOnTouchListener(this);
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

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mPref = getSharedPreferences("mod", 0);
        requestWindowFeature(1);
        if (sdk >= 21) {
            super.setTheme(android.R.style.Theme.Material);
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
                    DirchActivity.this.finish();
                }
            }
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
