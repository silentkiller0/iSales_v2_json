package com.iSales.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.iSales.database.AppDatabase;
import com.iSales.database.entry.DebugItemEntry;
import com.iSales.interfaces.FindProductsListener;
import com.iSales.remote.ApiUtils;
import com.iSales.remote.rest.FindProductsREST;
import com.iSales.remote.model.Product;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by netserve on 29/08/2018.
 */

public class FindProductsTask extends AsyncTask<Void, Void, FindProductsREST> {
    private static final String TAG = com.iSales.task.FindProductsTask.class.getSimpleName();

    private FindProductsListener task;
    private String sortfield;
    private String sortorder;
    private long limit;
    private long page;
    private long category;
    private int mode = 1;
    private Context context;
    private AppDatabase mDb;

    /**
     *
     * @param context
     * @param taskComplete
     * @param sortfield
     * @param sortorder
     * @param limit
     * @param page envoyer une valeur négative pour renvoyer la liste sans pagination
     * @param category
     */
    public FindProductsTask(Context context, FindProductsListener taskComplete, String sortfield, String sortorder, long limit, long page, long category) {
        this.task = taskComplete;
        this.sortfield = sortfield;
        this.sortorder = sortorder;
        this.limit = limit;
        this.page = page;
        this.category = category;
        this.context = context;
        this.mDb = AppDatabase.getInstance(this.context);

        mDb.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindProductsTask.class.getSimpleName(), "FindProductsTask()", "Called.", ""));
    }

    @Override
    protected FindProductsREST doInBackground(Void... voids) {
//        filtre les produits qui sont en vente
        String sqlfilters = "tosell=1";
//        Requete de connexion de l'internaute sur le serveur
        Call<ArrayList<Product>> call = null;
        if (page < 0) {
            call = ApiUtils.getISalesService(context).findProductsByCategorie(sqlfilters, sortfield, this.sortorder, this.limit, this.category, this.mode);
        } else {
            call = ApiUtils.getISalesService(context).findProducts(sqlfilters, sortfield, this.sortorder, this.limit, this.page, this.mode);
        }
        mDb.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindProductsTask.class.getSimpleName(), "FindProductsTask()", "Url : "+call.request().url(), ""));

        try {
            Response<ArrayList<Product>> response = call.execute();
            if (response.isSuccessful()) {
                ArrayList<Product> productArrayList = response.body();
                ArrayList<Product> products = new ArrayList<>();
//                Log.e(TAG, "doInBackground: products=" + productArrayList.size());

                return new FindProductsREST(productArrayList, category);
            } else {
                String error = null;
                FindProductsREST findProductsREST = new FindProductsREST();
                findProductsREST.setProducts(null);
                findProductsREST.setErrorCode(response.code());
                try {
                    error = response.errorBody().string();
//                    JSONObject jsonObjectError = new JSONObject(error);
//                    String errorCode = jsonObjectError.getString("errorCode");
//                    String errorDetails = jsonObjectError.getString("errorDetails");
                    Log.e(TAG, "doInBackground: onResponse err: " + error + " code=" + response.code());
                    findProductsREST.setErrorBody(error);

                    mDb.debugMessageDao().insertDebugMessage(
                            new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindProductsTask.class.getSimpleName(), "FindProductsTask()", "doInBackground: onResponse err: " + error + " code=" + response.code(), ""));
                } catch (IOException e) {
                    e.printStackTrace();
                    mDb.debugMessageDao().insertDebugMessage(
                            new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindProductsTask.class.getSimpleName(), "FindProductsTask()", "***** IOException *****\nMessage: "+e.getMessage(), ""+e.getStackTrace()));
                }

                return findProductsREST;
            }
        } catch (IOException e) {
            e.printStackTrace();
            mDb.debugMessageDao().insertDebugMessage(
                    new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindProductsTask.class.getSimpleName(), "FindProductsTask()", "***** IOException *****\nMessage: "+e.getMessage(), ""+e.getStackTrace()));
        }

        return null;
    }

    @Override
    protected void onPostExecute(FindProductsREST findProductsREST) {
//        super.onPostExecute(findProductsREST);
        try {
            task.onFindProductsCompleted(findProductsREST);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
