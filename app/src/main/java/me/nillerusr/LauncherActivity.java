package me.nillerusr;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.billflx.csgo.data.ModLocalDataSource;
import com.gtastart.common.base.BaseComposeActivity;
import com.valvesoftware.source.R;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.libsdl.app.SDLActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
/* loaded from: classes.dex */
public class LauncherActivity extends AppCompatActivity {
    static EditText EnvEdit = null;
    public static String PKG_NAME = null;
    static final int REQUEST_PERMISSIONS = 42;
    static CheckBox check_updates;
    static EditText cmdArgs;
    static EditText res_height;
    static EditText res_width;
    static CheckBox useVolumeButtons;
    public SharedPreferences mPref;
    public static boolean can_write = true;
    static EditText GamePath = null;
    public static final int sdk = Integer.valueOf(Build.VERSION.SDK).intValue();

    public void applyPermissions(String[] permissions, int code) {
        List<String> requestPermissions = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions.add(permissions[i]);
                }
            }
        }
        if (!requestPermissions.isEmpty()) {
            String[] requestPermissionsArray = new String[requestPermissions.size()];
            for (int i2 = 0; i2 < requestPermissions.size(); i2++) {
                requestPermissionsArray[i2] = requestPermissions.get(i2);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(requestPermissionsArray, code);
            }
        }
    }

    @Override // android.app.Activity
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS && grantResults[0] == -1) {
            Toast.makeText(this, R.string.srceng_launcher_error_no_permission, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public static String getDefaultDir() {
        File dir = Environment.getExternalStorageDirectory();
        return (dir == null || !dir.exists()) ? "/sdcard/" : dir.getPath();
    }

    public static String getAndroidDataDir() {
        String path = getDefaultDir() + "/Android/data/" + PKG_NAME + "/files";
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return path;
    }

    public static void changeButtonsStyle(ViewGroup parent) {
        if (sdk < 21) {
            for (int i = parent.getChildCount() - 1; i >= 0; i--) {
                try {
                    View child = parent.getChildAt(i);
                    if (child != null) {
                        if (child instanceof ViewGroup) {
                            changeButtonsStyle((ViewGroup) child);
                        } else if (child instanceof Button) {
                            Button b = (Button) child;
                            Drawable bg = b.getBackground();
                            if (bg != null) {
                                bg.setAlpha(96);
                            }
                            b.setTextColor(-1);
                            b.setTextSize(15.0f);
                            b.setTypeface(b.getTypeface(), Typeface.BOLD);
                        } else if (child instanceof EditText) {
                            EditText b2 = (EditText) child;
                            b2.setBackgroundColor(-14211289);
                            b2.setTextColor(-1);
                            b2.setTextSize(15.0f);
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PKG_NAME = getApplication().getPackageName();
//        requestWindowFeature(1);
//        if (sdk >= 21) {
//            super.setTheme(android.R.style.Theme_Material);
//        } else {
//            super.setTheme(android.R.style.Theme);
//        }
        this.mPref = getSharedPreferences("mod", 0);
        setContentView(R.layout.activity_launcher);
        cmdArgs = (EditText) findViewById(R.id.edit_cmdline);
        EnvEdit = (EditText) findViewById(R.id.edit_env);
        GamePath = (EditText) findViewById(R.id.edit_gamepath);
        Button button = (Button) findViewById(R.id.button_launch);
        button.setOnClickListener(new View.OnClickListener() { // from class: me.nillerusr.LauncherActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                LauncherActivity.this.startSource(v);
            }
        });
        Button aboutButton = (Button) findViewById(R.id.button_about);
        aboutButton.setOnClickListener(new View.OnClickListener() { // from class: me.nillerusr.LauncherActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Dialog dialog = new Dialog(LauncherActivity.this);
                dialog.setTitle(R.string.srceng_launcher_about);
                ScrollView scroll = new ScrollView(LauncherActivity.this);
                scroll.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
                scroll.setPadding(5, 5, 5, 5);
                TextView text = new TextView(LauncherActivity.this);
                text.setText(R.string.srceng_launcher_about_text);
                text.setLinksClickable(true);
                text.setTextIsSelectable(true);
                Linkify.addLinks(text, Linkify.WEB_URLS);
                scroll.addView(text);
                dialog.setContentView(scroll);
                dialog.show();
            }
        });
        Button dirButton = (Button) findViewById(R.id.button_gamedir);
        dirButton.setOnClickListener(new View.OnClickListener() { // from class: me.nillerusr.LauncherActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Intent intent = new Intent(LauncherActivity.this, (Class<?>) DirchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                LauncherActivity.this.startActivity(intent);
            }
        });
        getResources().getString(R.string.last_commit);
        cmdArgs.setText(this.mPref.getString("argv", "-console"));
        GamePath.setText(this.mPref.getString("gamepath", getDefaultDir() + "/srceng"));
        EnvEdit.setText(this.mPref.getString("env", "LIBGL_USEVBO=0"));
        changeButtonsStyle((ViewGroup) getWindow().getDecorView());
        if (sdk >= 23) {
            applyPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.RECORD_AUDIO"}, REQUEST_PERMISSIONS);
        }
    }

    public void saveSettings(SharedPreferences.Editor editor) {
        String argv = cmdArgs.getText().toString();
        String gamepath = GamePath.getText().toString();
        String env = EnvEdit.getText().toString();
        editor.putString("argv", argv);
        editor.putString("gamepath", gamepath);
        editor.putString("env", env);
        editor.commit();
    }

    public void startSource(View view) {
        GamePath.getText().toString();
        SharedPreferences.Editor editor = this.mPref.edit();
        saveSettings(editor);
        if (sdk >= 19) {
            editor.putBoolean("immersive_mode", true);
        } else {
            editor.putBoolean("immersive_mode", false);
        }
        editor.commit();
        Intent intent = new Intent(this, (Class<?>) SDLActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void startSource() {
        Intent intent = new Intent(this, (Class<?>) SDLActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override // android.app.Activity
    public void onPause() {
        Log.v("SRCAPK", "onPause");
//        saveSettings(this.mPref.edit());
        super.onPause();
    }
}
