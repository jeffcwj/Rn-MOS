package me.nillerusr;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import com.valvesoftware.source.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/* loaded from: classes.dex */
public class UpdateSystem extends AsyncTask<String, Integer, String> {
    private static final String app = "srceng-debug.apk";
    private static final String git_url = "https://raw.githubusercontent.com/nillerusr/srceng-deploy";
    String deploy_branch;
    String last_commit;
    Context mContext;

    public UpdateSystem(Context context) {
        this.mContext = context;
        this.deploy_branch = context.getResources().getString(R.string.deploy_branch);
        this.last_commit = context.getResources().getString(R.string.last_commit);
    }

    private static String toString(InputStream inputStream) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();
            while (true) {
                String inputLine = bufferedReader.readLine();
                if (inputLine != null) {
                    stringBuilder.append(inputLine);
                } else {
                    return stringBuilder.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public String doInBackground(String... params) {
        try {
            URL urlObject = new URL("https://raw.githubusercontent.com/nillerusr/srceng-deploy/" + this.deploy_branch + "/version");
            URLConnection urlConnection = urlObject.openConnection();
            return toString(urlConnection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onPostExecute(String result) {
        if (result != null && !result.equals("") && !this.last_commit.equals(result)) {
            Intent notif = new Intent(this.mContext, (Class<?>) UpdateService.class);
            notif.putExtra("update_url", "https://raw.githubusercontent.com/nillerusr/srceng-deploy/" + this.deploy_branch + "/" + app);
            this.mContext.startService(notif);
        }
    }
}
