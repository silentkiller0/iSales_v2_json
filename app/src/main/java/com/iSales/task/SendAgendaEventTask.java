package com.iSales.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.iSales.R;
import com.iSales.database.AppDatabase;
import com.iSales.database.entry.DebugItemEntry;
import com.iSales.interfaces.FindAgendaEventsListener;
import com.iSales.pages.calendar.CalendarActivity;
import com.iSales.remote.ApiUtils;
import com.iSales.remote.model.AgendaEvents;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendAgendaEventTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = SendAgendaEventTask.class.getSimpleName();
    private AgendaEvents agendaEvents;
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private FindAgendaEventsListener task;
    private AppDatabase mDB;

    public SendAgendaEventTask(ProgressDialog progressDialog, AgendaEvents agendaEvents, Context context, FindAgendaEventsListener taskComplete){
        this.mProgressDialog = progressDialog;
        this.agendaEvents = agendaEvents;
        this.mContext = context;
        this.task = taskComplete;
        mDB = AppDatabase.getInstance(this.mContext);

        mDB.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(mContext, (System.currentTimeMillis()/1000), "Ticket", SendAgendaEventTask.class.getSimpleName(), "SendAgendaEventTask()", "Called.", ""));
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //Save the event to the server
        Call<Long> call = ApiUtils.getISalesService(mContext).createEvent(agendaEvents);
        Log.e(TAG, " Url: "+call.request().url());
        mDB.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(mContext, (System.currentTimeMillis()/1000), "Ticket", SendAgendaEventTask.class.getSimpleName(), "SendAgendaEventTask() => doInBackground()", "Url: "+call.request().url(), ""));

        call.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if(response.isSuccessful()){
                    final Long responseAgendaId = response.body();
                    Log.e(TAG, "onResponse: saveAgendaEvent id= " + responseAgendaId);

                    Toast.makeText(mContext, "L'évenement enregisté!!!", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();

                }else {
                    Toast.makeText(mContext, "L'évenement non enregisté!\n"+
                            mContext.getResources().getString(R.string.service_indisponible), Toast.LENGTH_SHORT).show();
                    try {
                        Log.e(TAG, "doInBackground: onResponse err: message=" + response.message() + " | code=" + response.code() + " | code=" + response.errorBody().string());
                        mDB.debugMessageDao().insertDebugMessage(
                                new DebugItemEntry(mContext, (System.currentTimeMillis()/1000), "Ticket", SendAgendaEventTask.class.getSimpleName(), "doInBackground() => doInBackground()", "doInBackground: onResponse err: message=" + response.message() + " | code=" + response.code() + " | code=" + response.errorBody().string(), ""));

                    } catch (IOException e) {
                        Log.e(TAG, "doInBackgroundonResponse: message=" + e.getMessage());
                        mDB.debugMessageDao().insertDebugMessage(
                                new DebugItemEntry(mContext, (System.currentTimeMillis()/1000), "Ticket", SendAgendaEventTask.class.getSimpleName(), "doInBackground() => doInBackground()", "doInBackgroundonResponse: message = " + e.getMessage(), ""+e.getStackTrace()));
                    }
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Toast.makeText(mContext, "L'évenement non enregisté!\n"+
                        mContext.getResources().getString(R.string.service_indisponible), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: "+call.request().url().encodedQuery());
                Log.e(TAG, "onFailure: "+t.getMessage());

                mProgressDialog.dismiss();
                return;
            }
        });
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.e(TAG, "onPostExecute: ");
        task.onSendAgendaEventsTaskComplete();
    }
}
