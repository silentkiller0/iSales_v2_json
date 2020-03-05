package com.iSales.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iSales.R;
import com.iSales.database.AppDatabase;
import com.iSales.database.entry.EventsEntry;
import com.iSales.interfaces.FindAgendaEventsListener;
import com.iSales.pages.calendar.AgendaEventDetails;
import com.iSales.pages.calendar.CalendarActivity;
import com.iSales.pages.calendar.Events;
import com.iSales.remote.ApiUtils;
import com.iSales.remote.model.AgendaEventSuccess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgendaEventAdapter extends RecyclerView.Adapter<AgendaEventAdapter.MyViewHolder> {
    private String TAG = AgendaEventAdapter.class.getSimpleName();
    private Context mContext;
    private AppDatabase mDB;
    private List<EventsEntry> eventList;
    private ProgressDialog mProgressDialog;
    private FindAgendaEventsListener mFindAgendaEventsListener;

    public AgendaEventAdapter (Context context, List<EventsEntry> eventsArrayList, FindAgendaEventsListener mFindAgendaEventsListener){
        this.mContext = context;
        this.eventList = eventsArrayList;
        this.mDB = AppDatabase.getInstance(mContext);
        this.mProgressDialog = new ProgressDialog(this.mContext);
        this.mFindAgendaEventsListener = mFindAgendaEventsListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        LinearLayout eventLayout;
        TextView DateTxt, Event, Time;
        ImageButton delete_btn;

        public MyViewHolder(View itemView) {
            super(itemView);

            eventLayout = itemView.findViewById(R.id.custome_event_layout);
            DateTxt = itemView.findViewById(R.id.custome_event_eventdate);
            Event = itemView.findViewById(R.id.custome_event_eventname);
            Time = itemView.findViewById(R.id.custome_event_eventtime);
            delete_btn = itemView.findViewById(R.id.custome_event_delete_btn);
        }
    }

    @NonNull
    @Override
    public AgendaEventAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_events_row_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AgendaEventAdapter.MyViewHolder holder, final int position) {
        final EventsEntry events = eventList.get(position);
        holder.DateTxt.setText(events.getDATE());
        holder.Event.setText(events.getLABEL());
        holder.Time.setText(events.getTIME());

        //all on click events
        holder.eventLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AgendaEventDetails.class);
                intent.putExtra("eventObj", events);
                mContext.startActivity(intent);
            }
        });

        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteEvent(events, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    private void deleteEvent(final EventsEntry events, final int position) {
        showProgressDialog(true, "Supression", "Supprimer l'évènement: "+events.getLABEL());

        //delete fom server
        Call<AgendaEventSuccess> call = ApiUtils.getISalesService(mContext).deleteEventById(events.getId());
        call.enqueue(new Callback<AgendaEventSuccess>() {
            @Override
            public void onResponse(Call<AgendaEventSuccess> call, Response<AgendaEventSuccess> response) {
                if (response.isSuccessful()){
                    //delete local
                    mDB.eventsDao().deleteEvent(events.getId());
                    mDB.agendaEventsDao().deleteEvent(events.getId());

                    eventList.remove(position);
                    notifyDataSetChanged();

                    mFindAgendaEventsListener.onDeleteAgendasTaskComplete();

                    Toast.makeText(mContext, "Evènement "+events.getLABEL()+" supprimé!", Toast.LENGTH_SHORT).show();
                    showProgressDialog(false, "Supression", "Supprimer l'évènement: "+events.getLABEL());

                    Log.e(TAG, " Event id: "+events.getLocalId()+"\nCode: "+response.body().getCode()+" message: "+response.body().getMessage());
                }else{
                    try {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.service_indisponible), Toast.LENGTH_SHORT).show();
                        showProgressDialog(false, "Supression", "Supprimer l'évènement: "+events.getLABEL());
                        Log.e(TAG, "deleteButton(): err: message=" + response.message() + " | code=" + response.code() + " | code=" + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e(TAG, "deleteButton(): message=" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AgendaEventSuccess> call, Throwable t) {
                showProgressDialog(false, "Supression", "Supprimer l'évènement: "+events.getLABEL());
                Toast.makeText(mContext, mContext.getResources().getString(R.string.service_indisponible), Toast.LENGTH_LONG).show();
                Log.e(TAG, "deleteButton(): message=" + t.getMessage());
            }
        });
    }

    private void showProgressDialog(boolean show, String title, String message) {

        if (show) {
            if (title != null) mProgressDialog.setTitle(title);
            if (message != null) mProgressDialog.setMessage(message);

            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.circular_progress_view));
            mProgressDialog.show();
        } else {
            if (mProgressDialog != null){
                mProgressDialog.dismiss();
            }
        }
    }
}
