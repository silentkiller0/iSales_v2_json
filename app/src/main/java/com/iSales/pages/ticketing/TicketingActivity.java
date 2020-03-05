package com.iSales.pages.ticketing;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.iSales.R;
import com.iSales.database.AppDatabase;
import com.iSales.database.entry.DebugItemEntry;
import com.iSales.interfaces.SendindMailListener;
import com.iSales.model.ISalesIncidentTable;
import com.iSales.pages.calendar.CalendarActivity;
import com.iSales.task.SendTicketMail;
import com.iSales.utility.ISalesUtility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.iSales.utility.ISalesUtility.makeSureFileWasCreatedThenMakeAvailable;

public class TicketingActivity extends AppCompatActivity implements SendindMailListener {
    private final String TAG = TicketingActivity.class.getSimpleName();
    private EditText ticketing_name, ticketing_email, ticketing_body;
    private Spinner ticketing_subject_sp;
    private Button ticketing_save, ticketing_preview, ticketing_cancel;
    private AppDatabase db;
    private ProgressDialog mProgressDialog;

    private String ticketing_subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticketing);

        //Prevent the keyboard from displaying on activity start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        db = AppDatabase.getInstance(this);

        ticketing_name = findViewById(R.id.ticketingActivity_ticket_name_et);
        ticketing_subject_sp = findViewById(R.id.ticketingActivity_ticket_subjet_sp);
        ticketing_email = findViewById(R.id.ticketingActivity_ticket_email_et);
        ticketing_body = findViewById(R.id.ticketingActivity_ticket_body_et);
        ticketing_cancel = findViewById(R.id.ticketingActivity_ticket_cancel_btn);
        ticketing_preview = findViewById(R.id.ticketingActivity_ticket_preview_btn);
        ticketing_save = findViewById(R.id.ticketingActivity_ticket_send_btn);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getIncidentNameList());
        ticketing_subject_sp.setAdapter(adapter);
        ticketing_subject_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getSelectedItem().toString().equals("Veuillez selectionne un sujet du ticket...") || parent.getSelectedItem().toString().equals("")){
                    ticketing_subject = null;
                }else{
                    ticketing_subject = parent.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ticketing_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ticketing_name.setText("");
                ticketing_email.setText("");
                ticketing_body.setText("");
                onBackPressed();
            }
        });

        ticketing_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreview(ticketing_name.getText().toString(), ticketing_subject, ticketing_email.getText().toString(), ticketing_body.getText().toString());
            }
        });

        ticketing_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog(true, null, "Préparer du ticket en cours....");
                prepareTicket(ticketing_name.getText().toString(), ticketing_subject, ticketing_email.getText().toString(), ticketing_body.getText().toString());
            }
        });
    }

    private ArrayList<String> getIncidentNameList(){
        ArrayList<String> list = new ArrayList<>();
        list.add("Veuillez selectionne un sujet du ticket....");

        for (int i=0; i<ISalesUtility.getiSalesIncidentList().size(); i++){
            list.add(ISalesUtility.getiSalesIncidentList().get(i).getName());
        }
        return list;
    }

    private ISalesIncidentTable getISalesIncidentByName(String name){
        ISalesIncidentTable incident = null;

        if (name != null && !name.isEmpty()){
            incident = new ISalesIncidentTable();

            for (int i=0; i<ISalesUtility.getiSalesIncidentList().size(); i++){
                if (ISalesUtility.getiSalesIncidentList().get(i).getName().equals(name)){
                    incident = ISalesUtility.getiSalesIncidentList().get(i);
                    return incident;
                }
            }
        }else{
            incident = null;
        }
        return incident;
    }

    private void showPreview(String ticketName, String subjetTicket, String ticketEmail, String ticketBody){
        //get ref Ticket from date
        Date date = new Date((System.currentTimeMillis()/1000)*1000);
        String companyName = db.serverDao().getActiveServer(true).getRaison_sociale();
        String refTicket = "ST_"+companyName.replace(' ','_')+"_" + date.getTime();

        ISalesIncidentTable iSalesIncident = getISalesIncidentByName(subjetTicket);

        final Dialog mDialog = new Dialog(this);
        mDialog.setContentView(R.layout.dialog_ticket_preview_layout);
        mDialog.setTitle("Aperçu du Ticket");

        ImageButton close_ticketPreview = mDialog.findViewById(R.id.dialog_ticket_preview_close_btn);
        TextView text_ticketPreview = mDialog.findViewById(R.id.dialog_ticket_preview_text_tv);

        SendTicketMail mSendTicketMail = new SendTicketMail(this, TicketingActivity.this, "Manuel-Ticket", new ISalesUtility().generateAttchmentFile(this), ticketEmail, ticketName, ticketBody, refTicket, iSalesIncident);

        text_ticketPreview.setText(mSendTicketMail.setMessage());
        close_ticketPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        mDialog.show();
    }

    private void prepareTicket(String ticketName, String subjetTicket, String ticketEmail, String ticketBody){
        boolean error = false;
        boolean error_ticketBody = false;

        if (ticketName.isEmpty()){
            ticketing_name.setFocusable(true);
            ticketing_name.setError("Veuillez renseigner le nom du Ticket!");
            error = true;
        }
        if(subjetTicket == null || subjetTicket.isEmpty()){
            TextView spinner_erreur = (TextView) ticketing_subject_sp.getSelectedView();
            spinner_erreur.setError("Veuillez selectionne un sujet du ticket !");
            spinner_erreur.setTextColor(Color.RED);
            spinner_erreur.setText("Veuillez selectionne un sujet du ticket !");
            ticketing_subject_sp.setFocusable(true);
            error = true;
        }
        if (ticketEmail.isEmpty()){
            ticketing_email.setError("Veuillez renseigner votre email!");
            ticketing_email.setFocusable(true);
            error = true;
        }
        if (ticketBody.isEmpty()){
            ticketing_body.setError("Expliquer en détail l'anomalie!");
            ticketing_body.setFocusable(true);
            error = true;
            error_ticketBody = true;
        }
        if(!error_ticketBody && ticketBody.length() < 15){
            ticketing_body.setError("Veuillez expliquer votre situation avec plus de 15 charactères!");
            ticketing_body.setFocusable(true);
            error = true;
        }
        if (error){
            return;
        }

        //get ref Ticket from date
        Date date = new Date((System.currentTimeMillis()/1000)*1000);
        String companyName = db.serverDao().getActiveServer(true).getRaison_sociale();
        String refTicket = "ST_"+companyName.replace(' ','_')+"_" + date.getTime();

        ISalesIncidentTable iSalesIncident = getISalesIncidentByName(subjetTicket);

        //get isales log file to send in email attachment
        SendTicketMail mSendTicketMail = new SendTicketMail(this, TicketingActivity.this, "Manuel-Ticket", new ISalesUtility().generateAttchmentFile(this), ticketEmail, ticketName, ticketBody, refTicket, iSalesIncident);
        mSendTicketMail.execute();
    }
    
    @Override
    public void onMailSend(boolean internetCheck, String ticketDelay) {
        if (internetCheck){
            if (ticketDelay != null){
                showProgressDialog(false, null, null);
                ticketing_name.setText("");
                ticketing_subject_sp.setSelection(0);
                ticketing_email.setText("");
                ticketing_body.setText("");
                Toast.makeText(this, "Le Ticket est envoyé avec success!\n"+ticketDelay, Toast.LENGTH_SHORT).show();
            }else{
                showProgressDialog(false, null, null);
                Toast.makeText(this, getString(R.string.service_indisponible_email), Toast.LENGTH_SHORT).show();
            }
        }else{
            showProgressDialog(false, null, null);
            Toast.makeText(this, getString(R.string.service_indisponible), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows the progress UI and hides.
     */
    private void showProgressDialog(boolean show, String title, String message) {

        if (show) {
            mProgressDialog = new ProgressDialog(this);
            if (title != null) mProgressDialog.setTitle(title);
            if (message != null) mProgressDialog.setMessage(message);

            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.circular_progress_view));
            mProgressDialog.show();
        } else {
            if (mProgressDialog != null) mProgressDialog.dismiss();
        }
    }
}
