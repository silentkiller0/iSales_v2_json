package com.iSales.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.iSales.database.AppDatabase;
import com.iSales.database.entry.DebugItemEntry;
import com.iSales.interfaces.InsertThirdpartieListener;
import com.iSales.remote.ApiUtils;
import com.iSales.remote.model.Document;
import com.iSales.remote.model.Thirdpartie;
import com.iSales.remote.rest.InsertThirdpartieREST;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class InsertThirdpartieTask extends AsyncTask<Void, Void, InsertThirdpartieREST> {
    private static final String TAG = com.iSales.task.InsertThirdpartieTask.class.getSimpleName();

    private InsertThirdpartieListener task;
    private Thirdpartie thirdpartie;
    private Document document;
    private Context context;
    private AppDatabase mDb;

    public InsertThirdpartieTask(Context context, InsertThirdpartieListener taskComplete, Thirdpartie thirdpartie, Document logo) {
        this.task = taskComplete;
        this.thirdpartie = thirdpartie;
        this.document = logo;
        this.context = context;
        this.mDb = AppDatabase.getInstance(context);

        mDb.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", InsertThirdpartieTask.class.getSimpleName(), "InsertThirdpartieTask()", "Called.", ""));
    }

    @Override
    protected InsertThirdpartieREST doInBackground(Void... voids) {
//        Requete de connexion de l'internaute sur le serveur
        if (document != null) {

            Call<String> callUploadDoc = ApiUtils.getISalesService(context).uploadDocument(document);
            mDb.debugMessageDao().insertDebugMessage(
                    new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", InsertThirdpartieTask.class.getSimpleName(), "InsertThirdpartieTask() => doInBackground()", "Upload TirdPartie Logo document\nUrl : "+callUploadDoc.request().url(), ""));

            try {
                Response<String> responseDoc = callUploadDoc.execute();

                if (responseDoc.isSuccessful()) {
                    String responseLogoClientBody = responseDoc.body();
                    Log.e(TAG, "onResponse: responseLogoClient=" + responseLogoClientBody);

                    thirdpartie.setLogo(document.getFilename());
                    Call<Long> callSaveThirdpartie = ApiUtils.getISalesService(context).saveThirdpartie(thirdpartie);
                    mDb.debugMessageDao().insertDebugMessage(
                            new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", InsertThirdpartieTask.class.getSimpleName(), "InsertThirdpartieTask() => doInBackground()", "Upload TirdPartie\nUrl : "+callSaveThirdpartie.request().url(), ""));

                    Response<Long> responseThirdpartie = callSaveThirdpartie.execute();
                    if (responseThirdpartie.isSuccessful()) {
                        Long responseThirdpartieBody = responseThirdpartie.body();
                        mDb.clientDao().updateIdClient(responseThirdpartieBody, thirdpartie.getName(), thirdpartie.getEmail());
                        Log.e(TAG, "onResponse: responseThirdpartieBody=" + responseThirdpartieBody);
                        InsertThirdpartieREST rest = new InsertThirdpartieREST();
                        rest.setThirdpartie_id(responseThirdpartieBody);

                        return rest;

                    } else {
                        String error = null;
                        InsertThirdpartieREST rest = new InsertThirdpartieREST();
                        rest.setThirdpartie_id(null);
                        try {
                            error = responseThirdpartie.errorBody().string();
                            Log.e(TAG, "doInBackground:ThirdPartie onResponse err: " + error + " code=" + responseDoc.code());
                            rest.setErrorBody(error);

                            mDb.debugMessageDao().insertDebugMessage(
                                    new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", InsertThirdpartieTask.class.getSimpleName(), "InsertThirdpartieTask() => doInBackground()", "doInBackground:ThirdPartie onResponse err: " + error + " code=" + responseDoc.code(), ""));
                        } catch (IOException e) {
                            e.printStackTrace();
                            mDb.debugMessageDao().insertDebugMessage(
                                    new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", InsertThirdpartieTask.class.getSimpleName(), "InsertThirdpartieTask() => doInBackground()", "***** IOException *****\nMessage: "+e.getMessage(), ""+e.getStackTrace()));
                        }

                        return rest;
                    }
                } else {
                    String error = null;
                    InsertThirdpartieREST rest = new InsertThirdpartieREST();
                    rest.setThirdpartie_id(null);
                    try {
                        error = responseDoc.errorBody().string();
                        Log.e(TAG, "doInBackground:Document onResponse err: " + error + " code=" + responseDoc.code());
                        rest.setErrorBody(error);

                        mDb.debugMessageDao().insertDebugMessage(
                                new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", InsertThirdpartieTask.class.getSimpleName(), "InsertThirdpartieTask() => doInBackground()", "doInBackground:Document onResponse err: " + error + " code=" + responseDoc.code(), ""));
                    } catch (IOException e) {
                        e.printStackTrace();
                        mDb.debugMessageDao().insertDebugMessage(
                                new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", InsertThirdpartieTask.class.getSimpleName(), "InsertThirdpartieTask() => doInBackground()", "***** IOException *****\nMessage: "+e.getMessage(), ""+e.getStackTrace()));
                    }

                    return rest;
                }
            } catch (IOException e) {
                e.printStackTrace();
                mDb.debugMessageDao().insertDebugMessage(
                        new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", InsertThirdpartieTask.class.getSimpleName(), "InsertThirdpartieTask()", "***** IOException *****\nMessage: "+e.getMessage(), ""+e.getStackTrace()));
            }
        } else {

            Call<Long> callSaveThirdpartie = ApiUtils.getISalesService(context).saveThirdpartie(thirdpartie);
            mDb.debugMessageDao().insertDebugMessage(
                    new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", InsertThirdpartieTask.class.getSimpleName(), "InsertThirdpartieTask()", "Upload TirdPartie\nUrl : "+callSaveThirdpartie.request().url(), ""));
            try {
                Response<Long> responseThirdpartie = callSaveThirdpartie.execute();
                if (responseThirdpartie.isSuccessful()) {
                    Long responseThirdpartieBody = responseThirdpartie.body();
                    mDb.clientDao().updateIdClient(responseThirdpartieBody, thirdpartie.getName(), thirdpartie.getEmail());
                    Log.e(TAG, "onResponse: responseThirdpartieBody=" + responseThirdpartieBody);
                    InsertThirdpartieREST rest = new InsertThirdpartieREST();
                    rest.setThirdpartie_id(responseThirdpartieBody);

                    return rest;

                } else {
                    String error = null;
                    InsertThirdpartieREST rest = new InsertThirdpartieREST();
                    rest.setThirdpartie_id(null);
                    try {
                        error = responseThirdpartie.errorBody().string();
                        Log.e(TAG, "doInBackground:ThirdPartie onResponse err: " + error + " code=" + responseThirdpartie.code());
                        rest.setErrorBody(error);

                        mDb.debugMessageDao().insertDebugMessage(
                                new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", InsertThirdpartieTask.class.getSimpleName(), "InsertThirdpartieTask()", "doInBackground:ThirdPartie onResponse err: " + error + " code=" + responseThirdpartie.code(), ""));
                    } catch (IOException e) {
                        e.printStackTrace();
                        mDb.debugMessageDao().insertDebugMessage(
                                new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", InsertThirdpartieTask.class.getSimpleName(), "InsertThirdpartieTask()", "***** IOException *****\nMessage: "+e.getMessage(), ""+e.getStackTrace()));
                    }

                    return rest;
                }
            } catch (IOException e) {
                e.printStackTrace();
                mDb.debugMessageDao().insertDebugMessage(
                        new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", InsertThirdpartieTask.class.getSimpleName(), "InsertThirdpartieTask()", "***** IOException *****\nMessage: "+e.getMessage(), ""+e.getStackTrace()));
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(InsertThirdpartieREST insertThirdpartieREST) {
//        super.onPostExecute(findProductsREST);
        if (task != null) {
            task.onInsertThirdpartieCompleted(insertThirdpartieREST);
        }
    }
}
