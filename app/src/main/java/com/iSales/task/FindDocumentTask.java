package com.iSales.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.iSales.database.AppDatabase;
import com.iSales.database.entry.DebugItemEntry;
import com.iSales.interfaces.FindDocumentListener;
import com.iSales.remote.ApiUtils;
import com.iSales.remote.model.DolPhoto;
import com.iSales.remote.rest.FindDolPhotoREST;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by netserve on 10/10/2018.
 */

public class FindDocumentTask extends AsyncTask<Void, Void, com.iSales.remote.rest.FindDolPhotoREST> {
    private static final String TAG = FindDocumentTask.class.getSimpleName();

    private com.iSales.interfaces.FindDocumentListener task;
    private String modulePart;
    private String originalFile;
    private int productsPosition;

    private Context context;
    private AppDatabase db;

    public FindDocumentTask(Context context, FindDocumentListener taskComplete, String modulePart, String originalFile) {
        this.task = taskComplete;
        this.modulePart = modulePart;
        this.originalFile = originalFile;
        this.context = context;
        this.db = AppDatabase.getInstance(this.context);
        db.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindDocumentTask.class.getSimpleName(), "FindDocumentTask()", "Called.", ""));
    }

    @Override
    protected com.iSales.remote.rest.FindDolPhotoREST doInBackground(Void... voids) {

//        Requete de connexion de l'internaute sur le serveur
        Call<com.iSales.remote.model.DolPhoto> call = ApiUtils.getISalesService(context).findDocument(modulePart, originalFile);
        try {
            Response<com.iSales.remote.model.DolPhoto> response = call.execute();db.debugMessageDao().insertDebugMessage(
                    new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindDocumentTask.class.getSimpleName(), "FindDocumentTask() => doInBackground()", "Called.\nUrl : "+call.request().url(), ""));

            if (response.isSuccessful()) {
                DolPhoto dolPhoto = response.body();
                Log.e(TAG, "doInBackground: dolPhoto | Filename=" + dolPhoto.getFilename() + " content=" + dolPhoto.getContent());
                db.debugMessageDao().insertDebugMessage(
                        new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindDocumentTask.class.getSimpleName(), "FindDocumentTask() => doInBackground()", "Called.", ""));

                return new com.iSales.remote.rest.FindDolPhotoREST(dolPhoto);
            } else {
                String error = null;
                com.iSales.remote.rest.FindDolPhotoREST findDolPhotoREST = new com.iSales.remote.rest.FindDolPhotoREST();
                findDolPhotoREST.setDolPhoto(null);
                findDolPhotoREST.setErrorCode(response.code());
                try {
                    error = response.errorBody().string();
//                    JSONObject jsonObjectError = new JSONObject(error);
//                    String errorCode = jsonObjectError.getString("errorCode");
//                    String errorDetails = jsonObjectError.getString("errorDetails");
//                    Log.e(TAG, "doInBackground: onResponse err: " + error + " code=" + response.code());
                    findDolPhotoREST.setErrorBody(error);

                    db.debugMessageDao().insertDebugMessage(
                            new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindDocumentTask.class.getSimpleName(), "FindDocumentTask() => doInBackground()", "doInBackground: onResponse err: " + error + " code=" + response.code(), ""));

                } catch (IOException e) {
                    e.printStackTrace();
                    db.debugMessageDao().insertDebugMessage(
                            new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindDocumentTask.class.getSimpleName(), "FindDocumentTask() => doInBackground()", "***** IOException *****\nMessage: "+e.getMessage(), ""+e.getStackTrace()));
                }

                return findDolPhotoREST;
            }
        } catch (IOException e) {
            e.printStackTrace();
            db.debugMessageDao().insertDebugMessage(
                    new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindDocumentTask.class.getSimpleName(), "FindDocumentTask() => doInBackground()", "***** IOException ******\nMessage: "+e.getMessage(), ""+e.getStackTrace()));
        }

        return null;
    }

    @Override
    protected void onPostExecute(FindDolPhotoREST findDolPhotoREST) {
//        super.onPostExecute(findDolPhotoREST);
        if (task == null) {
            super.onPostExecute(findDolPhotoREST);
            return;
        }

        task.onFindDocumentComplete(findDolPhotoREST);
    }
}
