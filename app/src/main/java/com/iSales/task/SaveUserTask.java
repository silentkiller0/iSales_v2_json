package com.iSales.task;

import android.nfc.Tag;
import android.os.AsyncTask;
import android.content.Context;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.iSales.database.AppDatabase;
import com.iSales.database.entry.DebugItemEntry;
import com.iSales.database.entry.TokenEntry;
import com.iSales.database.entry.UserEntry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class SaveUserTask {
    private String TAG = SaveUserTask.class.getSimpleName();
    private Context mContext;
    private final File DirectoryLocal = Environment.getExternalStorageDirectory();
    private final String DirectoryName = "iSales_BackUp";
    private final String FileNameBackUp = "BackUp.txt";
    private final String BackUpHeader = "BACKUP;ISALES";
    private final String FileSplit = ";";
    private AppDatabase mDb;

    public SaveUserTask(Context mContext){
        this.mContext = mContext;
        this.mDb = AppDatabase.getInstance(this.mContext);

        mDb.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(mContext, (System.currentTimeMillis()/1000), "Ticket", SaveUserTask.class.getSimpleName(), "SaveUserTask()", "Called.", ""));
    }

    public void SetRestoreBackUpData(String task){
        mDb.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(mContext, (System.currentTimeMillis()/1000), "Ticket", SaveUserTask.class.getSimpleName(), "SetRestoreBackUpData()", "SetRestoreBackUpData( "+task+" ) | Called.", ""));

        if (task.equals("SET") || task.equals("Set") || task.equals("set")){
            Log.e(TAG, " SetRestoreBackUpData( SET )");
            generateSaveData(mContext, FileNameBackUp, backUpData());

        } else if (task.equals("RESTORE") || task.equals("Restore") || task.equals("restore")){
            Log.e(TAG, " SetRestoreBackUpData( RESTORE )");
            restoreFileData(mContext);
        }
    }

    private void generateSaveData(Context context, String FileName, String Body) {
        mDb.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(mContext, (System.currentTimeMillis()/1000), "Ticket", SaveUserTask.class.getSimpleName(), "generateSaveData()", "Called.", ""));

        try {
            File root = new File(DirectoryLocal, DirectoryName);
            Log.e(TAG, " root path: "+root.getAbsolutePath());

            if (!root.exists()) {
                if (root.mkdirs()) {
                    Log.e(TAG, " Directory root: " + root.exists());
                }else{
                    Log.e(TAG, " Directory root not created: " + root.exists());
                    mDb.debugMessageDao().insertDebugMessage(
                            new DebugItemEntry(mContext, (System.currentTimeMillis()/1000), "Ticket", SaveUserTask.class.getSimpleName(), "generateSaveData()", "Directory root not created: " + root.exists(), ""));
                }
            }
            if (root.exists() && !FileName.isEmpty() && Body != null) {
                File file = new File(root+File.separator+FileName);
                FileWriter writer = new FileWriter(file);
                writer.append(Body);
                writer.flush();
                writer.close();
            }else{
                Log.e(TAG, " generateSaveData(): File doesn't exist, ");
                mDb.debugMessageDao().insertDebugMessage(
                        new DebugItemEntry(mContext, (System.currentTimeMillis()/1000), "Ticket", SaveUserTask.class.getSimpleName(), "generateSaveData()", "File doesn't exist", ""));
            }

        } catch (IOException e) {
            e.printStackTrace();
            mDb.debugMessageDao().insertDebugMessage(
                    new DebugItemEntry(mContext, (System.currentTimeMillis()/1000), "Ticket", SaveUserTask.class.getSimpleName(), "generateSaveData()", "***** IOException *****\nMessage: "+e.getMessage(), ""+e.getStackTrace()));
        }
    }

    private String backUpData(){
        AppDatabase db = AppDatabase.getInstance(mContext);
        String result = null;
        mDb.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(mContext, (System.currentTimeMillis()/1000), "Ticket", SaveUserTask.class.getSimpleName(), "backUpData()", "Called.", ""));

        if(db.userDao().getUser().size() != 0 && db.tokenDao().getAllToken().size() != 0) {
            UserEntry userEntry = db.userDao().getUser().get(0);
            TokenEntry tokenEntry = db.tokenDao().getAllToken().get(0);

            if (userEntry != null && tokenEntry != null) {

                result = BackUpHeader + FileSplit + "\n";

                if (userEntry.getId() != null) {
                    if (userEntry.getId().equals("")) {
                        result += userEntry.getId().toString().replace("", "@") + FileSplit;
                    } else {
                        result += userEntry.getId().toString() + FileSplit;
                    }
                } else {
                    result += userEntry.getId() + FileSplit;
                }
                if (userEntry.getStatut() != null) {
                    if (userEntry.getStatut().isEmpty()) {
                        result += userEntry.getStatut().replace("", "@") + FileSplit;
                    } else {
                        result += userEntry.getStatut() + FileSplit;
                    }
                } else {
                    result += userEntry.getStatut() + FileSplit;
                }
                if (userEntry.getEmployee() != null) {
                    if (userEntry.getEmployee().isEmpty()) {
                        result += userEntry.getEmployee().replace("", "@") + FileSplit;
                    } else {
                        result += userEntry.getEmployee() + FileSplit;
                    }
                } else {
                    result += userEntry.getEmployee() + FileSplit;
                }
                if (userEntry.getGender() != null) {
                    if (userEntry.getGender().isEmpty()) {
                        result += userEntry.getGender().replace("", "@") + FileSplit;
                    } else {
                        result += userEntry.getGender() + FileSplit;
                    }
                } else {
                    result += userEntry.getGender() + FileSplit;
                }
                if (userEntry.getBirth() != null) {
                    if (userEntry.getBirth().isEmpty()) {
                        result += userEntry.getBirth().replace("", "@") + FileSplit;
                    } else {
                        result += userEntry.getBirth() + FileSplit;
                    }
                } else {
                    result += userEntry.getBirth() + FileSplit;
                }
                if (userEntry.getFirstname() != null) {
                    if (userEntry.getFirstname().isEmpty()) {
                        result += userEntry.getFirstname().replace("", "@") + FileSplit;
                    }
                } else {
                    result += userEntry.getFirstname() + FileSplit;
                }
                if (userEntry.getLastname() != null) {
                    if (userEntry.getLastname().isEmpty()) {
                        result += userEntry.getLastname().replace("", "@") + FileSplit;
                    } else {
                        result += userEntry.getLastname() + FileSplit;
                    }
                } else {
                    result += userEntry.getLastname() + FileSplit;
                }
                if (userEntry.getName() != null) {
                    if (userEntry.getName().isEmpty()) {
                        result += userEntry.getName().replace("", "@") + FileSplit;
                    } else {
                        result += userEntry.getName() + FileSplit;
                    }
                } else {
                    result += userEntry.getName() + FileSplit;
                }
                if (userEntry.getCountry() != null) {
                    if (userEntry.getCountry().isEmpty()) {
                        result += userEntry.getCountry().replace("", "@") + FileSplit;
                    } else {
                        result += userEntry.getCountry() + FileSplit;
                    }
                } else {
                    result += userEntry.getCountry() + FileSplit;
                }
                if (userEntry.getDateemployment() != null) {
                    if (userEntry.getDateemployment().isEmpty()) {
                        result += userEntry.getDateemployment().replace("", "@") + FileSplit;
                    } else {
                        result += userEntry.getDateemployment() + FileSplit;
                    }
                } else {
                    result += userEntry.getDateemployment() + FileSplit;
                }
                if (userEntry.getPhoto() != null) {
                    if (userEntry.getPhoto().isEmpty()) {
                        result += userEntry.getPhoto().replace("", "@") + FileSplit;
                    } else {
                        result += userEntry.getPhoto() + FileSplit;
                    }
                } else {
                    result += userEntry.getPhoto() + FileSplit;
                }
                if (userEntry.getDatelastlogin() != null) {
                    if (userEntry.getDatelastlogin().isEmpty()) {
                        result += userEntry.getDatelastlogin().replace("", "@") + FileSplit;
                    } else {
                        result += userEntry.getDatelastlogin() + FileSplit;
                    }
                } else {
                    result += userEntry.getDatelastlogin() + FileSplit;
                }
                if (userEntry.getDatec() != null) {
                    if (userEntry.getDatec().isEmpty()) {
                        result += userEntry.getDatec().replace("", "@") + FileSplit;
                    } else {
                        result += userEntry.getDatec() + FileSplit;
                    }
                } else {
                    result += userEntry.getDatec() + FileSplit;
                }
                if (userEntry.getDatem() != null) {
                    if (userEntry.getDatem().isEmpty()) {
                        result += userEntry.getDatem().replace("", "@") + FileSplit;
                    } else {
                        result += userEntry.getDatem() + FileSplit;
                    }
                } else {
                    result += userEntry.getDatem() + FileSplit;
                }
                if (userEntry.getAdmin() != null) {
                    if (userEntry.getAdmin().isEmpty()) {
                        result += userEntry.getAdmin().replace("", "@") + FileSplit;
                    } else {
                        result += userEntry.getAdmin() + FileSplit;
                    }
                } else {
                    result += userEntry.getAdmin() + FileSplit;
                }
                if (userEntry.getLogin() != null) {
                    if (userEntry.getLogin().isEmpty()) {
                        result += userEntry.getLogin().replace("", "@") + FileSplit;
                    } else {
                        result += userEntry.getLogin() + FileSplit;
                    }
                } else {
                    result += userEntry.getLogin() + FileSplit;
                }
                if (userEntry.getTown() != null) {
                    if (userEntry.getTown().isEmpty()) {
                        result += userEntry.getTown().replace("", "@") + FileSplit;
                    } else {
                        result += userEntry.getTown() + FileSplit;
                    }
                } else {
                    result += userEntry.getTown() + FileSplit;
                }
                if (userEntry.getAddress() != null) {
                    if (userEntry.getAddress().isEmpty()) {
                        result += userEntry.getAddress().replace("", "@") + FileSplit + "|";
                    } else {
                        result += userEntry.getAddress() + FileSplit + "|";
                    }
                } else {
                    result += userEntry.getAddress() + FileSplit + "|";
                }

                result += tokenEntry.getId() + FileSplit + tokenEntry.getToken() + FileSplit + tokenEntry.getMessage() + FileSplit + "|UPDATED=0;";

                return result;
            }
        }

        mDb.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(mContext, (System.currentTimeMillis()/1000), "Ticket", SaveUserTask.class.getSimpleName(), "backUpData()", ((result != null)? "The backUpDate return is null":"The backUpDate return is not null"), ""));
        return result;
    }

    private void restoreFileData(Context context){
        String line = "";
        mDb.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(mContext, (System.currentTimeMillis()/1000), "Ticket", SaveUserTask.class.getSimpleName(), "restoreFileData()", "Called.", ""));

        try {
            File file = new File(DirectoryLocal, DirectoryName+File.separator+FileNameBackUp);
            if (file.exists()) {

                FileReader fileReader = new FileReader(file);
                BufferedReader br = new BufferedReader(fileReader);

                ArrayList<String> lines = new ArrayList<>();

                while ((line = br.readLine()) != null) {
                    line = br.readLine();
                    lines.add(line);
                    Log.e(TAG, " Save File Line : "+line);
                }
                fileReader.close();
                br.close();

                for (int i=0; i<lines.size(); i++){
                    Log.e(TAG, "Lines "+i+": "+lines.get(i));
                }

                String[] vales = lines.get(0).split("\\|");
                Log.e(TAG, "vales[0]: "+vales[0]);
                Log.e(TAG, "vales[1]: "+vales[1]);
                Log.e(TAG, "vales[2]: "+vales[2]);

                String[] userValues = vales[0].split(FileSplit);
                String[] tokenValues = vales[1].split(FileSplit);
                String check = vales[2].split(FileSplit)[0];

                if (check.equals("UPDATED=0")) {
                    AppDatabase db = AppDatabase.getInstance(mContext);
                    UserEntry user = new UserEntry();
                    TokenEntry token = new TokenEntry();

                    String test = "";
                    Log.e(TAG, "userValues.length: "+userValues.length);
                    Log.e(TAG, "tokenValues.length: "+tokenValues.length);

                    for(int x=0; x<userValues.length; x++){
                        test += userValues[x]+";\n";
                    }
                    Log.e(TAG, "Test: "+test);

                    //Mapping the user data
                    user.setId(Long.valueOf(userValues[0].replace("@","")));
                    user.setStatut(userValues[1].replace("@",""));
                    user.setEmployee(userValues[2].replace("@",""));
                    user.setGender(userValues[3].replace("@",""));
                    user.setBirth(userValues[4].replace("@",""));
                    user.setEmail(userValues[5].replace("@",""));
                    user.setFirstname(userValues[6].replace("@",""));
                    user.setLastname(userValues[7].replace("@",""));
                    user.setName(userValues[8].replace("@",""));
                    user.setCountry(userValues[9].replace("@",""));
                    user.setDateemployment(userValues[10].replace("@",""));
                    user.setPhoto(userValues[11].replace("@",""));
                    user.setDatelastlogin(userValues[12].replace("@",""));
                    user.setDatec(userValues[13].replace("@",""));
                    user.setDatem(userValues[14].replace("@",""));
                    user.setAdmin(userValues[15].replace("@",""));
                    user.setLogin(userValues[16].replace("@",""));
                    //user.setTown(userValues[17].replace("@",""));

                    //Mapping the token data
                    token.setId(Long.valueOf(tokenValues[0]));
                    token.setToken(tokenValues[1]);
                    token.setMessage(tokenValues[2]);

                    //delete de previous just in case
                    db.userDao().deleteAllUser();
                    db.tokenDao().deleteAllToken();

                    //insert into the database
                    db.userDao().insertUser(user);
                    db.tokenDao().insertToken(token);

                    //
                    try {
                        File root = new File(DirectoryLocal, DirectoryName);
                        Log.e(TAG, " root path: "+root.getAbsolutePath());

                        if (file.exists ()) file.delete();
                        if (root.exists()) {
                            check = "UPDATED=1";

                            File mFile = new File(root, FileNameBackUp);
                            if (file.exists ()) {   mFile.delete(); }

                            mFile = new File(root, FileNameBackUp);
                            FileWriter writer = new FileWriter(mFile);
                            writer.write("BACKUP;ISALES;\n"+vales[0]+"|"+vales[1]+"|"+check);
                            writer.flush();
                            writer.close();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        mDb.debugMessageDao().insertDebugMessage(
                                new DebugItemEntry(mContext, (System.currentTimeMillis()/1000), "Ticket", SaveUserTask.class.getSimpleName(), "restoreFileData()", "***** IOException *****\nMessage: "+e.getMessage(), ""+e.getStackTrace()));
                    }

                }else if(check.equals("UPDATED=1")){
                    Log.e(TAG, "UPDATED=1");
                    mDb.debugMessageDao().insertDebugMessage(
                            new DebugItemEntry(mContext, (System.currentTimeMillis()/1000), "Ticket", SaveUserTask.class.getSimpleName(), "restoreFileData()", "The restore already backed up", ""));
                }

            }else{
                Log.e(TAG, "Restoring Data after update, File = ' "+file.getPath()+" ' does not exist. Or its the first time.");
                mDb.debugMessageDao().insertDebugMessage(
                        new DebugItemEntry(mContext, (System.currentTimeMillis()/1000), "Ticket", SaveUserTask.class.getSimpleName(), "restoreFileData()", "Restoring Data after update, File = ' "+file.getPath()+" ' does not exist. Or its the first time.", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
            mDb.debugMessageDao().insertDebugMessage(
                    new DebugItemEntry(mContext, (System.currentTimeMillis()/1000), "Ticket", SaveUserTask.class.getSimpleName(), "restoreFileData()", "***** IOException *****\nMessage: "+e.getMessage(), ""+e.getStackTrace()));
        }
    }
}
