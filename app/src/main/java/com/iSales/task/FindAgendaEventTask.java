package com.iSales.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.iSales.R;
import com.iSales.database.AppDatabase;
import com.iSales.database.entry.DebugItemEntry;
import com.iSales.database.entry.UserEntry;
import com.iSales.interfaces.FindAgendaEventsListener;
import com.iSales.pages.home.fragment.ProfilFragment;
import com.iSales.remote.ApiUtils;
import com.iSales.remote.model.AgendaEvents;
import com.iSales.remote.rest.AgendaEventsREST;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class FindAgendaEventTask extends AsyncTask<Void, Void, AgendaEventsREST> {
    private static final String TAG = FindAgendaEventTask.class.getSimpleName();
    private Context context;

    private FindAgendaEventsListener task;
    private String sortfield;
    private String sortorder;
    private long limit;
    private long page;
    private long thirdparty_ids;

    private AppDatabase mDb;
    private UserEntry userEntry;
    private ProgressDialog mProgressDialog;

    public FindAgendaEventTask(Context context, FindAgendaEventsListener taskComplete, String sortfield, String sortorder, long limit, long page) {
        this.task = taskComplete;
        this.sortfield = sortfield;
        this.sortorder = sortorder;
        this.limit = limit;
        this.page = page;
        this.context = context;
        this.mProgressDialog = new ProgressDialog(context);
        mDb = AppDatabase.getInstance(context.getApplicationContext());
        userEntry = mDb.userDao().getUser().get(0);

        mDb.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindAgendaEventTask.class.getSimpleName(), "FindAgendaEventTask()", "Called.", ""));
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showProgressDialog(true, "Agenda", "Synchronisation des évènements en cours...");
    }

    @Override
    protected AgendaEventsREST doInBackground(Void... voids) {
        Log.e(TAG, "doInBackground: ");

        String sqlfilters = "fk_user_author="+userEntry.getId();

        //Requete de connexion de l'internaute sur le serveur
        Call<ArrayList<AgendaEvents>> call = ApiUtils.getISalesService(context).getAllEvents(sqlfilters, sortfield, sortorder, limit, page);
        Log.e(TAG, " Url= "+call.request().url());
        mDb.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindAgendaEventTask.class.getSimpleName(), "FindOrderLinesTask() => doInBackground()", "Url= "+call.request().url(), ""));

        try {
            Response<ArrayList<AgendaEvents>> response = call.execute();
            Log.e(TAG, " Response: "+response.body());
            if (response.isSuccessful()) {
                ArrayList<AgendaEvents> agendaEventsArrayList = response.body();
                Log.e(TAG, "doInBackground: page="+page+" eventArrayList=" + agendaEventsArrayList.size());

                return new AgendaEventsREST(agendaEventsArrayList);
            } else {
                Log.e(TAG, "doInBackground: !isSuccessful");
                String error = null;
                AgendaEventsREST mAgendaEventsREST = new AgendaEventsREST();
                mAgendaEventsREST.sendAgendaEvents(null);
                mAgendaEventsREST.setErrorCode(response.code());
                try {
                    error = response.errorBody().string();
//                    JSONObject jsonObjectError = new JSONObject(error);
//                    String errorCode = jsonObjectError.getString("errorCode");
//                    String errorDetails = jsonObjectError.getString("errorDetails");
                    Log.e(TAG, "doInBackground: onResponse err: " + error + " code=" + response.code());
                    mAgendaEventsREST.setErrorBody(error);

                    mDb.debugMessageDao().insertDebugMessage(
                            new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindAgendaEventTask.class.getSimpleName(), "FindOrderLinesTask() => doInBackground()", "doInBackground: onResponse err: " + error + " code=" + response.code(), ""));
                } catch (IOException e) {
                    e.printStackTrace();
                    mDb.debugMessageDao().insertDebugMessage(
                            new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindAgendaEventTask.class.getSimpleName(), "FindOrderLinesTask() => doInBackground()", "***** IOException *****\nMessage: "+e.getMessage(), ""+e.getStackTrace()));
                }

                return mAgendaEventsREST;
            }
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: IOException");
            e.printStackTrace();
            mDb.debugMessageDao().insertDebugMessage(
                    new DebugItemEntry(context, (System.currentTimeMillis()/1000), "Ticket", FindAgendaEventTask.class.getSimpleName(), "FindOrderLinesTask() => doInBackground()", "***** IOException *****\nMessage: "+e.getMessage(), ""+e.getStackTrace()));
        }

        return null;
    }

    @Override
    protected void onPostExecute(AgendaEventsREST mAgendaEventsREST) {
        Log.e(TAG, "onPostExecute: ");
//        super.onPostExecute(findProductsREST);
        task.onFindAgendaEventsTaskComplete(mAgendaEventsREST);

        showProgressDialog(false, null, null);
    }

    /**
     * Shows the progress UI and hides.
     */
    private void showProgressDialog(boolean show, String title, String message) {

        if (show) {
            if (title != null) mProgressDialog.setTitle(title);
            if (message != null) mProgressDialog.setMessage(message);

            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setProgressDrawable(this.context.getResources().getDrawable(R.drawable.circular_progress_view));
            mProgressDialog.show();
        } else {
            if (mProgressDialog != null){
                mProgressDialog.dismiss();
            }
        }
    }
}