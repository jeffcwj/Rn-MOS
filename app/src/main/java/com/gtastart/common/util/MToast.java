package com.gtastart.common.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.valvesoftware.source.R;


public class MToast {

    private static Toast currentToast;

    // 目标sdk安卓11及以上，无法使用咯

    public static void show(Context context, String msg){
//        show(context, msg, false);
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Deprecated
    public static void show(Context context, String msg, boolean isLong){
        // 如果当前有正在显示的 Toast，则先取消它
        if (currentToast != null) {
            currentToast.cancel();
        }
        // 加载Toast布局
        View toastRoot = LayoutInflater.from(context).inflate(R.layout.view_toast, null);
        // 初始化布局控件
        TextView textView = toastRoot.findViewById(R.id.txt_toast);
        // 为控件设置属性
        textView.setText(msg);
        // Toast的初始化
        Toast toastStart = new Toast(context);
        // 设置持续时间
        toastStart.setDuration(isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        // 获取屏幕高度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        // Toast的Y坐标是屏幕高度的2/3，不会出现不适配的问题
        toastStart.setGravity(Gravity.TOP, 0, height / 3*2);
//        toastStart.setGravity(Gravity.CENTER,0,0);
        toastStart.setDuration(isLong?Toast.LENGTH_LONG:Toast.LENGTH_SHORT);
        toastStart.setView(toastRoot);
        // 设置当前正在显示的 Toast 为新创建的 Toast
        currentToast = toastStart;

        // 使用 Handler 在主线程显示 Toast，以确保在 UI 线程中操作
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                toastStart.show();
            }
        });
    }
}
