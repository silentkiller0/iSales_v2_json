package com.iSales.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.iSales.database.AppDatabase;
import com.iSales.database.entry.DebugItemEntry;
import com.iSales.interfaces.FindCategorieListener;
import com.iSales.remote.ApiUtils;
import com.iSales.remote.model.Categorie;
import com.iSales.remote.rest.FindCategoriesREST;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by netserve on 05/09/2018.
 */

public class FindCategorieTask extends AsyncTask<Void, Void, FindCategoriesREST> {
    private static final String TAG = com.iSales.task.FindCategorieTask.class.getSimpleName();

    private FindCategorieListener task;
    private String sortfield;
    private String sortorder;
    private long limit;
    private long page;
    private String type;

    private Context context;
    private AppDatabase db;

    public FindCategorieTask(Context context, FindCategorieListener taskComplete, String sortfield, String sortorder, long limit, long page, String type) {
        this.task = taskComplete;
        this.sortfield = sortfield;
        this.sortorder = sortorder;
        this.limit = limit;
        this.page = page;
        this.type = type;
        this.context = context;
        this.db = AppDatabase.getInstance(this.context);
        db.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindCategorieTask.class.getSimpleName(), "FindCategorieTask()", "Called.", ""));
    }

    @Override
    protected FindCategoriesREST doInBackground(Void... voids) {
//        Requete de connexion de l'internaute sur le serveur
        Call<ArrayList<Categorie>> call = ApiUtils.getISalesService(context).findCategories(sortfield, this.sortorder, this.limit, this.page, this.type);
        try {
            Response<ArrayList<Categorie>> response = call.execute();
            db.debugMessageDao().insertDebugMessage(
                    new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindCategorieTask.class.getSimpleName(), "FindCategorieTask() => doInBackground()", "Called.\nUrl: "+call.request().url(), ""));

            if (response.isSuccessful()) {
                ArrayList<Categorie> categorieArrayList = response.body();
                Log.e(TAG, "doInBackground: Categorie=" + categorieArrayList.size());
                db.debugMessageDao().insertDebugMessage(
                        new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindCategorieTask.class.getSimpleName(), "FindCategorieTask() => doInBackground()", "Category size : "+categorieArrayList.size(), ""));

                return new FindCategoriesREST(categorieArrayList);
            } else {
                String error = null;
                FindCategoriesREST findCategoriesREST = new FindCategoriesREST();
                findCategoriesREST.setCategories(null);
                findCategoriesREST.setErrorCode(response.code());
                try {
                    error = response.errorBody().string();
//                    JSONObject jsonObjectError = new JSONObject(error);
//                    String errorCode = jsonObjectError.getString("errorCode");
//                    String errorDetails = jsonObjectError.getString("errorDetails");
                    Log.e(TAG, "doInBackground: onResponse err: " + error + " code=" + response.code());
                    findCategoriesREST.setErrorBody(error);

                    db.debugMessageDao().insertDebugMessage(
                            new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindCategorieTask.class.getSimpleName(), "FindCategorieTask() => doInBackground()", "onResponse err: " + error + " code=" + response.code(), ""));

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return findCategoriesREST;
            }
        } catch (IOException e) {
            e.printStackTrace();
            db.debugMessageDao().insertDebugMessage(
                    new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindCategorieTask.class.getSimpleName(), "FindCategorieTask() => doInBackground()", ""+e.getMessage(), ""+e.getStackTrace()));
        }

        return null;
    }

    @Override
    protected void onPostExecute(FindCategoriesREST findCategoriesREST) {
//        super.onPostExecute(findProductsREST);
        task.onFindCategorieCompleted(findCategoriesREST);
    }
}
