package com.iSales.pages.welcome;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.iSales.database.AppDatabase;
import com.iSales.database.entry.DebugItemEntry;
import com.iSales.database.entry.DebugSettingsEntry;
import com.iSales.database.entry.SettingsEntry;
import com.iSales.pages.home.fragment.ClientsRadioFragment;
import com.iSales.pages.login.LoginActivity;
import com.iSales.R;
import com.iSales.remote.ConnectionManager;
import com.iSales.task.SaveUserTask;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = WelcomeActivity.class.getSimpleName();

    /**
     * By JL --- Check Google PlayStore App Version
     */
    private String currentVersion, latestVersion;
    private Dialog dialog;
    private AppDatabase db;
    private TextView Error_Message;

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 100;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private final Runnable mGotoLoginRunnable = new Runnable() {
        @Override
        public void run() {

//            Toast.makeText(WelcomeActivity.this, "Going to Login page", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        mContentView = findViewById(R.id.fullscreen_content);
        Error_Message = findViewById(R.id.Error_Message);

        db = AppDatabase.getInstance(this);
        db.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(this, (System.currentTimeMillis()/1000), "Ticket", WelcomeActivity.class.getSimpleName(), "onCreate()", "Called.", ""));

        // Check version on playStore
        getCurrentVersion();

        //Clean debug logs every day
        checkDebugLogs();

        //init debug settings
        initDebugSettings();

        //set default settings
        initSettings();

//        checkExternalMedia();
//        writeToSDFile();
    }

    private void initSettings() {
        if (AppDatabase.getInstance(getApplicationContext()).settingsDao().getAllSettings().size() == 0) {
            AppDatabase.getInstance(getApplicationContext()).settingsDao().insertSettings(new SettingsEntry(1, true, false));
        }
    }

    private void initDebugSettings() {
        AppDatabase.getInstance(getApplicationContext()).debugSettingsDao().deleteDebugSettings();
        AppDatabase.getInstance(getApplicationContext()).debugSettingsDao().insertDebugSettings(new DebugSettingsEntry(1, 0));

        Log.e(TAG, " initDebugSettings(): checkDebug = " + AppDatabase.getInstance(getApplicationContext()).debugSettingsDao().getAllDebugSettings().get(0).getCheckDebug());
    }

//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//
//        // Trigger the initial hide() shortly after the activity has been
//        // created, to briefly hint to the user that UI controls
//        // are available.
//        delayedHide(100);
//    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check version on playStore
        getCurrentVersion();
        checkDebugLogs();

    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    /**
     * Schedules a call to go to LoginActivity page
     */
    private void delayedLogged(int delayMillis) {
        mHideHandler.removeCallbacks(mGotoLoginRunnable);
        mHideHandler.postDelayed(mGotoLoginRunnable, delayMillis);
    }

    private void checkExternalMedia() {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
    }

    /**
     * Method to write ascii text characters to file on SD card. Note that you must add a
     * WRITE_EXTERNAL_STORAGE permission to the manifest file or this method will throw
     * a FileNotFound Exception because you won't have write permission.
     */

    private File writeToSDFile() {

        // Find the root of the external storage.
        // See http://developer.android.com/guide/topics/data/data-  storage.html#filesExternal

        File root = Environment.getExternalStorageDirectory();

        // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder

        File dir = new File(root.getAbsolutePath() + "/iSales");
        dir.mkdirs();
        File file = new File(dir, "logcat.txt");
        return file;

//        try {
//            FileOutputStream f = new FileOutputStream(file);
//            PrintWriter pw = new PrintWriter(f);
//            pw.println("Hi , How are you");
//            pw.println("Hello");
//            pw.flush();
//            pw.close();
//            f.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            Log.i(TAG, "******* File not found. Did you" +
//                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        tv.append("\n\nFile written to "+file);
    }

    public void checkDebugLogs(){
        if (db == null){
            db = AppDatabase.getInstance(this);
        }
        Log.e(TAG, "checkDebugLogs() log size before => " + db.debugMessageDao().getAllDebugMessages().size());
        long time = (System.currentTimeMillis()/1000) - 86400000;
        db.debugMessageDao().deleteAllDebugMessagesOver24Hrs(time);
        Log.e(TAG, "checkDebugLogs() log size after => "+db.debugMessageDao().getAllDebugMessages().size());
    }

    private void getCurrentVersion() {
        db.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(this, (System.currentTimeMillis()/1000), "Ticket", WelcomeActivity.class.getSimpleName(), "getCurrentVersion()", "Called.", ""));

        if (!ConnectionManager.isPhoneConnected(this)) {
            Log.e(TAG, " getCurrentVersion() : No Internet no Update");
            db.debugMessageDao().insertDebugMessage(
                    new DebugItemEntry(this, (System.currentTimeMillis()/1000), "Ticket", WelcomeActivity.class.getSimpleName(), "getCurrentVersion()", "No Internet no Update.", ""));

            delayedHide(100);
            delayedLogged(3000);
            return;
        }

        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo = pm.getPackageInfo(this.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        currentVersion = pInfo.versionName;

        new GetLatestVersion().execute();

    }

    private class GetLatestVersion extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                //It retrieves the latest version by scraping the content of current version from play store at runtime
                Document doc = Jsoup.connect("https://play.google.com/store/apps/details?id=com.iSales").get();

                for (int i = 0; i < doc.getElementsByClass("htlgb").size(); i++) {
                    Log.e(TAG, "DOC: index = " + i + " :: " + ((doc.getElementsByClass("htlgb").get(i).text() == null) ? "Null" : doc.getElementsByClass("htlgb").get(i).text()));
                }
                latestVersion = doc.getElementsByClass("htlgb").get(6).text();
                Log.e(TAG, "latestVersion: " + latestVersion);

            } catch (Exception e) {
                e.printStackTrace();

            }

            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            Log.e(TAG, "latestVersion: " + latestVersion);
            Log.e(TAG, "currentVersion: " + currentVersion);
            db.debugMessageDao().insertDebugMessage(
                    new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", WelcomeActivity.class.getSimpleName() +" => GetLatestVersion", "onPostExecute()", "currentVersion: " + currentVersion + " || latestVersion: " + latestVersion, ""));

            Log.e(TAG, "Check version desable: latestVersion == currentVersion");
            latestVersion = currentVersion;

            if (latestVersion != null) {
                if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                    if (!isFinishing()) { //This would help to prevent Error : BinderProxy@45d459c0 is not valid; is your activity running? error
                        showUpdateDialog();
                    }
                } else {
                    // if version is correct then proceed
                    db.debugMessageDao().insertDebugMessage(
                            new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", WelcomeActivity.class.getSimpleName() +" => GetLatestVersion", "onPostExecute()", "The version is correct!", ""));

                    delayedHide(100);
                    delayedLogged(3000);
                }
//                delayedHide(100);
//                delayedLogged(3000);
            } else {
                //background.start();
                CountDownTimer countDownTimer = new CountDownTimer(10 * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        Error_Message.setText("Impossible de trouver la version sur le PlayStore\n" +
                                "Résultat : " + ((latestVersion == null) ? "latestVersion == NULL" : latestVersion) + "\n" +
                                "Veuillez vérifier votre connexion internet !\n\n" +
                                "iSales va s'ouvrir dans "+(millisUntilFinished / 1000)+" seconds");
                    }

                    @Override
                    public void onFinish() {
                        delayedHide(100);
                        delayedLogged(1000);
                    }
                };

                db.debugMessageDao().insertDebugMessage(
                        new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", WelcomeActivity.class.getSimpleName() +" => GetLatestVersion", "onPostExecute()", "Impossible de trouver la version sur le PlayStore\n" +
                                "Résultat : " + ((latestVersion == null) ? "latestVersion == NULL" : latestVersion) + "\n" +
                                "Veuillez vérifier votre connexion internet !", ""));

                countDownTimer.start();
                Toast.makeText(WelcomeActivity.this, "latestVersion == NULL", Toast.LENGTH_SHORT).show();

                super.onPostExecute(jsonObject);
            }
        }
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e){  e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    private void showUpdateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("iSales Mise à Jour");
        builder.setTitle("Une nouvelle mise à jour est disponible");
        builder.setPositiveButton("Mettre à jour", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.debugMessageDao().insertDebugMessage(
                        new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", WelcomeActivity.class.getSimpleName(), "showUpdateDialog()", "Show update window.", ""));

                //Save user login && token info in a backup file
                new SaveUserTask(getApplicationContext()).SetRestoreBackUpData("SET");

                //Clean app cache
                deleteCache(WelcomeActivity.this);

                //Open the browser and open the url
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                        ("https://play.google.com/store/apps/details?id=com.iSales")));
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //background.start();
                //kill the app
                System.exit(0);
            }
        });

        builder.setCancelable(false);
        dialog = builder.show();
    }

}
