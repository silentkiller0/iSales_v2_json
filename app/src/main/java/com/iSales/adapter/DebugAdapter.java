package com.iSales.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iSales.R;
import com.iSales.database.entry.DebugItemEntry;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DebugAdapter extends RecyclerView.Adapter<com.iSales.adapter.DebugAdapter.DebugsViewHolder> {
    private String TAG = DebugAdapter.class.getSimpleName();

    private Context mContext;
    private List<DebugItemEntry> debugList;

    public DebugAdapter(Context mContext, List<DebugItemEntry> debugList){
        this.mContext = mContext;
        this.debugList = debugList;
    }

    public class DebugsViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_dateTime, tv_mask, tv_className, tv_methodName, tv_message, tv_straceStack;

        public DebugsViewHolder(View itemView) {
            super(itemView);

            tv_dateTime = (TextView) itemView.findViewById(R.id.list_item_debug_date_time);
            tv_mask = (TextView) itemView.findViewById(R.id.list_item_debug_mask);
            tv_className = (TextView) itemView.findViewById(R.id.list_item_debug_className);
            tv_methodName = (TextView) itemView.findViewById(R.id.list_item_debug_methodName);
            tv_message = (TextView) itemView.findViewById(R.id.list_item_debug_message);
            tv_straceStack = (TextView) itemView.findViewById(R.id.list_item_debug_straceStack);
        }
    }


    @NonNull
    @Override
    public DebugsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_item_debug, parent, false);
        return new DebugsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DebugsViewHolder holder, int position) {
        //long millisecond = Long.parseLong(debugList.get(position).getDatetimeLong());
        String dateString = DateFormat.format("dd-MM-yyyy @ hh:mm:ss", new Date(debugList.get(position).getDatetimeLong()*1000)).toString();


        holder.tv_dateTime.setText(dateString);
        holder.tv_mask.setText(debugList.get(position).getMask());
        holder.tv_className.setText("Class : "+debugList.get(position).getClassName());
        holder.tv_methodName.setText("Method : "+debugList.get(position).getMethodName());
        holder.tv_message.setText("Message : "+debugList.get(position).getMessage());
        holder.tv_straceStack.setText("StraceStack : "+debugList.get(position).getStackTrace());
    }

    @Override
    public int getItemCount() {
        return debugList.size();
    }

}
