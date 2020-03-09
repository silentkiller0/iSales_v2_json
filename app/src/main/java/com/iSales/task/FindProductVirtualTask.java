package com.iSales.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.iSales.R;
import com.iSales.database.AppDatabase;
import com.iSales.database.entry.DebugItemEntry;
import com.iSales.database.entry.ProduitEntry;
import com.iSales.interfaces.FindProductVirtualListener;
import com.iSales.remote.ApiUtils;
import com.iSales.remote.ConnectionManager;
import com.iSales.remote.model.ProductVirtual;
import com.iSales.remote.rest.FindProductVirtualREST;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class FindProductVirtualTask extends AsyncTask<Void, Void, FindProductVirtualREST> {
    private static final String TAG = com.iSales.task.FindProductVirtualTask.class.getSimpleName();

    private FindProductVirtualListener task;
    private long productId;
    private Context context;
    private AppDatabase mDb;

    public FindProductVirtualTask(Context context, long productId, FindProductVirtualListener taskComplete) {
        this.task = taskComplete;
        this.context = context;
        this.productId = productId;
        this.mDb = AppDatabase.getInstance(this.context);

        mDb.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindProductVirtualTask.class.getSimpleName(), "FindProductVirtualTask()", "Called.", ""));
    }

    @Override
    protected FindProductVirtualREST doInBackground(Void... voids) {
        Log.e(TAG, "doInBackground called! ");

        //Requete de connexion de l'internaute sur le serveur
        Call<ArrayList<ProductVirtual>> call = ApiUtils.getISalesRYImg(context).ryFindProductVirtual(this.productId);
        mDb.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindProductVirtualTask.class.getSimpleName(), "FindProductVirtualTask() => doInBackground()", "Url : "+call.request().url(), ""));

        Log.e(TAG, "URL: "+call.request().url());
        try {
            Response<ArrayList<ProductVirtual>> response = call.execute();
            Log.e(TAG, "JSon: "+toJSON(response.body()));
            if (response.isSuccessful()) {
                ArrayList<ProductVirtual> productVirtualArrayList = response.body();
                Log.e(TAG, "doInBackground: FindProductVirtualREST size =" + productVirtualArrayList.size());

                if(productVirtualArrayList.size() > 0) {
                    FindProductVirtualREST rest = new FindProductVirtualREST();
                    rest.setProductVirtuals(productVirtualArrayList);
                    rest.setProduct_parent_id(this.productId);
                    Log.e(TAG, "doInBackground: FindProductVirtualREST Found Virtual Products");
                    return rest;
                }else{
                    FindProductVirtualREST rest = new FindProductVirtualREST();
                    rest.setProductVirtuals(null);
                    rest.setErrorCode(response.code());
                    rest.setErrorBody("Aucun produit virtuel n'est attaché au produit ref: "+this.productId);
                    Log.e(TAG, "doInBackground: FindProductVirtualREST No Virtual Products");
                    return rest;
                }
            } else {
                Log.e(TAG, "doInBackground: !isSuccessful");
                String error = null;
                FindProductVirtualREST findProductVirtualREST = new FindProductVirtualREST();
                findProductVirtualREST.setProductVirtuals(null);
                findProductVirtualREST.setErrorCode(response.code());
                try {
                    error = response.errorBody().string();
//                    JSONObject jsonObjectError = new JSONObject(error);
//                    String errorCode = jsonObjectError.getString("errorCode");
//                    String errorDetails = jsonObjectError.getString("errorDetails");
                    Log.e(TAG, "doInBackground: onResponse err: " + error + " code=" + response.code());
                    findProductVirtualREST.setErrorBody(error);

                    mDb.debugMessageDao().insertDebugMessage(
                            new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindProductVirtualTask.class.getSimpleName(), "FindProductVirtualTask() => doInBackground()", "doInBackground: onResponse err: " + error + " code=" + response.code(), ""));
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: ********** IOException !isSuccessful **********");
                    Log.e(TAG, "URL: "+call.request().url());
                    Log.e(TAG, "Message: "+e.getMessage());
                    Log.e(TAG, "StackTrace: "+e.getStackTrace());
                    mDb.debugMessageDao().insertDebugMessage(
                            new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindProductVirtualTask.class.getSimpleName(), "FindProductVirtualTask() => doInBackground()", "***** IOException *****\nMessage: "+e.getMessage(), ""+e.getStackTrace()));
                }

                return findProductVirtualREST;
            }
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: ********** IOException **********");
            Log.e(TAG, "URL: "+call.request().url());
            Log.e(TAG, "Message: "+e.getMessage());
            Log.e(TAG, "StackTrace: "+e.getStackTrace());
            mDb.debugMessageDao().insertDebugMessage(
                    new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindProductVirtualTask.class.getSimpleName(), "FindProductVirtualTask() => doInBackground()", "***** IOException *****\nMessage: "+e.getMessage(), ""+e.getStackTrace()));
        }

        return null;
    }

    String toJSON(ArrayList<ProductVirtual> list) {
        Gson gson = new Gson();
        StringBuilder sb = new StringBuilder();
        for(ProductVirtual d : list) {
            sb.append(gson.toJson(d)+"");
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(FindProductVirtualREST findProductVirtualREST) {
        //Log.e(TAG, "onPostExecute: ");
        if (task != null) {
            task.onFindProductVirtualCompleted(findProductVirtualREST);
        }
    }
}
