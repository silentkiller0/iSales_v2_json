package com.iSales.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.iSales.database.AppDatabase;
import com.iSales.database.entry.DebugItemEntry;
import com.iSales.interfaces.FindThirdpartieListener;
import com.iSales.remote.ApiUtils;
import com.iSales.remote.model.Thirdpartie;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by netserve on 09/10/2018.
 */

public class FindThirdpartieByIdTask extends AsyncTask<Void, Void, com.iSales.remote.model.Thirdpartie> {
    private static final String TAG = FindThirdpartieTask.class.getSimpleName();

    private com.iSales.interfaces.FindThirdpartieListener task;
    private long thirdpartieId;
    private Context context;
    private AppDatabase mDb;

    public FindThirdpartieByIdTask(Context context, FindThirdpartieListener taskComplete, long thirdpartieId) {
        this.task = taskComplete;
        this.thirdpartieId = thirdpartieId;
        this.context = context;
        this.mDb = AppDatabase.getInstance(this.context);

        mDb.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindThirdpartieByIdTask.class.getSimpleName(), "FindThirdpartieByIdTask()", "Called.", ""));
    }

    @Override
    protected com.iSales.remote.model.Thirdpartie doInBackground(Void... voids) {
//        Requete de connexion de l'internaute sur le serveur
        Call<com.iSales.remote.model.Thirdpartie> call = ApiUtils.getISalesService(context).findThirdpartieById(this.thirdpartieId);
        mDb.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindThirdpartieByIdTask.class.getSimpleName(), "FindThirdpartieByIdTask() => doInBackground()", "Url : "+call.request().url(), ""));
        try {
            Response<com.iSales.remote.model.Thirdpartie> response = call.execute();
            if (response.isSuccessful()) {
                com.iSales.remote.model.Thirdpartie thirdpartie = response.body();
                Log.e(TAG, "doInBackground: firstName=" + thirdpartie.getFirstname());

                return thirdpartie;
            } else {
                String error = null;
                try {
                    error = response.errorBody().string();
//                    JSONObject jsonObjectError = new JSONObject(error);
//                    String errorCode = jsonObjectError.getString("errorCode");
//                    String errorDetails = jsonObjectError.getString("errorDetails");
                    Log.e(TAG, "doInBackground: onResponse err: " + error + " code=" + response.code());

                    mDb.debugMessageDao().insertDebugMessage(
                            new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindThirdpartieByIdTask.class.getSimpleName(), "FindThirdpartieByIdTask()  => doInBackground()", "doInBackground: onResponse err: " + error + " code=" + response.code(), ""));
                } catch (IOException e) {
                    e.printStackTrace();
                    mDb.debugMessageDao().insertDebugMessage(
                            new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindThirdpartieByIdTask.class.getSimpleName(), "FindThirdpartieByIdTask()  => doInBackground()", "Called.", ""));
                }

                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            mDb.debugMessageDao().insertDebugMessage(
                    new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindThirdpartieByIdTask.class.getSimpleName(), "FindThirdpartieByIdTask()  => doInBackground()", "Called.", ""));
        }

        return null;
    }

    @Override
    protected void onPostExecute(Thirdpartie thirdpartie) {
//        super.onPostExecute(findProductsREST);
        task.onFindThirdpartieByIdCompleted(thirdpartie);
    }
}
