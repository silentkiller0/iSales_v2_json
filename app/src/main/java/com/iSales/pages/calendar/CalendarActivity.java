package com.iSales.pages.calendar;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.iSales.R;
import com.iSales.adapter.AgendaAdapter;
import com.iSales.adapter.AgendaEventAdapter;
import com.iSales.database.AppDatabase;
import com.iSales.database.entry.ClientEntry;
import com.iSales.database.entry.DebugItemEntry;
import com.iSales.database.entry.EventsEntry;
import com.iSales.interfaces.FindAgendaEventsListener;
import com.iSales.interfaces.ItemClickListenerAgenda;
import com.iSales.remote.ConnectionManager;
import com.iSales.remote.model.AgendaEvents;
import com.iSales.remote.model.AgendaUserassigned;
import com.iSales.remote.rest.AgendaEventsREST;
import com.iSales.task.FindAgendaEventTask;
import com.iSales.task.SendAgendaEventTask;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CalendarActivity extends AppCompatActivity implements FindAgendaEventsListener {
    private String TAG = CalendarActivity.class.getSimpleName();


    private ImageButton previousBtn,nextBtn;
    private TextView currentDate;
    private ImageButton synchro_ib;
    private RecyclerView recyclerView;
    private static final int MAX_CALENDAR_DAYS = 42;
    private Calendar calendar = Calendar.getInstance(Locale.FRENCH);

    private ArrayList<Date> dates = new ArrayList<>();
    private ArrayList<EventsEntry> eventsList = new ArrayList<>();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.FRENCH);
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.FRENCH);
    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.FRENCH);
    private SimpleDateFormat eventDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.FRENCH);

    private AgendaAdapter mAgendaAdapter;
    private RecyclerView.LayoutManager mLayoutManager_rv;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private Dialog mDialog;

    //    task de recuperation des clients
    private FindAgendaEventTask mFindAgendaEventTask = null;

    private ProgressDialog mProgressDialog;
    private AppDatabase mDB;

    private int mLimit = 50;
    private int mPageEvent = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);

        mDB = AppDatabase.getInstance(getApplicationContext());
        mDB.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", CalendarActivity.class.getSimpleName(), "onCreate()", "Called.", ""));

        initLayout();
        initCalendar();

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.MONTH, -1);
                initCalendar();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(Calendar.MONTH, 1);
                initCalendar();
            }
        });

        synchro_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete local db data
                mDB.debugMessageDao().insertDebugMessage(
                        new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", CalendarActivity.class.getSimpleName(), "onCreate()", "synchro_ib::onClick() before deleting local db data, size: " + mDB.eventsDao().getAllEvents().size(), ""));

                mDB.eventsDao().deleteAllEvent();
                getEventsFromServer();
            }
        });

    }

    private void initLayout(){
        //debug message
        mDB.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", CalendarActivity.class.getSimpleName(), "initLayout()", "Called.", ""));

        previousBtn = findViewById(R.id.calendar_previousMontBtn);
        nextBtn = findViewById(R.id.calendar_activity_nextMontBtn);
        currentDate = findViewById(R.id.calendar_activity_currentDate);
        synchro_ib = (ImageButton) findViewById(R.id.calendar_activity_synchro);

        recyclerView = findViewById(R.id.calendar_activity_recycler_view);
        mLayoutManager_rv = new GridLayoutManager(getApplicationContext(), 7);
        recyclerView.setLayoutManager(mLayoutManager_rv);
    }

    private void initCalendar(){
        mDB.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", CalendarActivity.class.getSimpleName(), "initCalendar()", "Called.", ""));

        String currentDate = dateFormat.format(calendar.getTime());
        this.currentDate.setText(currentDate);

        dates.clear();
        Calendar monthCalendar = (Calendar) calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);

        int FirsDayOfTheMonth = monthCalendar.get(Calendar.DAY_OF_WEEK)-1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -FirsDayOfTheMonth);

        collectEventsPerMonth(monthFormat.format(calendar.getTime()), yearFormat.format(calendar.getTime()));

        while (dates.size() < MAX_CALENDAR_DAYS){
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        mAgendaAdapter = new AgendaAdapter(getApplicationContext(), dates, calendar, eventsList);
        recyclerView.setAdapter(mAgendaAdapter);

        mAgendaAdapter.setOnItemClickListener(new ItemClickListenerAgenda() {
            @Override
            public void OnItemClickAgendaEventAdd(int position) {
                //debug message
                mDB.debugMessageDao().insertDebugMessage(
                        new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", CalendarActivity.class.getSimpleName(), "OnItemClickAgendaEventAdd()", "Called.", ""));

                mDialog = new Dialog(CalendarActivity.this);
                mDialog.setContentView(R.layout.dialog_add_new_event_layout);
                mDialog.setTitle("Créer un événement");
                InitAddAgendaDialog(mDialog, position);
                mDialog.show();
            }

            @Override
            public void OnItemLongClickAgendaEvent(int position) {
                mDB.debugMessageDao().insertDebugMessage(
                        new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", CalendarActivity.class.getSimpleName(), "OnItemLongClickAgendaEvent()", "Called.", ""));

                String date = eventDateFormat.format(dates.get(position));
                Log.e(TAG, " OnItemLongClickAgendaEvent()::OnLongClickEvent || Date: "+date);

                mDialog = new Dialog(CalendarActivity.this);
                mDialog.setContentView(R.layout.custom_show_events_layout);
//                mDialog.setTitle("Créer un événement");

                RecyclerView recyclerViewEvents = mDialog.findViewById(R.id.custom_show_events_layout_recycle_view);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mDialog.getContext());
                recyclerViewEvents.setLayoutManager(layoutManager);
                recyclerViewEvents.setHasFixedSize(true);

                AgendaEventAdapter mAgendaEventAdapter = new AgendaEventAdapter(mDialog.getContext(), collectEventsByDate(date), CalendarActivity.this); //
                recyclerViewEvents.setAdapter(mAgendaEventAdapter);

                InitViewAgendaDialog(mDialog, position);
                mDialog.show();
            }
        });
    }


    private void saveEvent(String label, String location, String percentage, String fullDayEvent, String disponibility,
                           String time, String date, String month, String year, Long startEvent, Long endEvent, String concernTier, String description) {
        String myLog = " SaveEvent() label: "+label+" location: "+location+" percentage: "+percentage+" fullDayEvent: "+fullDayEvent +
                " time: "+time+" date: "+date+" month: "+month+" year: "+year+" startEvent: "+startEvent+" endEvent: "+endEvent+" concernTier socId: "+concernTier+" description: "+description;

        mDB.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", CalendarActivity.class.getSimpleName(), "saveEvent()", myLog, ""));


        final ProgressDialog progressDialog = new ProgressDialog(CalendarActivity.this);
        progressDialog.setMessage("Enregistrement de l'évenement en cours...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.circular_progress_view));
        progressDialog.show();

        if (!ConnectionManager.isPhoneConnected(getApplication())){
            Toast.makeText(this, getString(R.string.erreur_connexion), Toast.LENGTH_LONG).show();
            mDB.debugMessageDao().insertDebugMessage(
                    new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", CalendarActivity.class.getSimpleName(), "saveEvent()", getString(R.string.erreur_connexion), ""));
            return;
        }

        Log.e(TAG, myLog);

        EventsEntry newEvent = new EventsEntry(label, location, percentage, fullDayEvent, disponibility, time, date, month, year, startEvent, endEvent, concernTier, description);
        newEvent.setREF(String.format("PROV-%s", startEvent));
        mDB.eventsDao().insertNewEvent(newEvent);

        /**
         * Now to save to the server side now
         * save event on the server side....
         **/
        Calendar calendar = Calendar.getInstance(Locale.FRENCH);
        AgendaEvents mAgendaEvents = new AgendaEvents();

        mAgendaEvents.setTable_rowid("id");
        mAgendaEvents.setType_code("AC_OTH");
        mAgendaEvents.setType("Other (automatically inserted events)");
        mAgendaEvents.setCode("AC_OTH");
        mAgendaEvents.setLabel(newEvent.getLABEL());
        mAgendaEvents.setDatec(calendar.getTimeInMillis());
        mAgendaEvents.setDatem(calendar.getTimeInMillis());
        mAgendaEvents.setUsermodid("");

        Log.e(TAG, " getSTART_EVENT:: "+(Long) (newEvent.getSTART_EVENT()/1000));
        mAgendaEvents.setDatep( (newEvent.getSTART_EVENT()/1000) );

        Log.e(TAG, " getEND_EVENT:: "+(Long) (newEvent.getEND_EVENT()/1000));
        mAgendaEvents.setDatef( (newEvent.getEND_EVENT()/1000) );

        mAgendaEvents.setDurationp("-1");
        mAgendaEvents.setFulldayevent("1");
        mAgendaEvents.setPercentage(newEvent.getPERCENTAGE());
        mAgendaEvents.setLocation(newEvent.getLIEU());
        mAgendaEvents.setTransparency(newEvent.getTRANSPARENCY());
        mAgendaEvents.setPriority("0");

        Log.e(TAG, "User id: "+mDB.userDao().getUser().get(0).getId());
        AgendaUserassigned[] userassigned = new AgendaUserassigned[1];
        userassigned[0] = new AgendaUserassigned(mDB.userDao().getUser().get(0).getId()+"", "0");
        mAgendaEvents.setUserassigned(userassigned[0]);

        mAgendaEvents.setUserownerid(mDB.userDao().getUser().get(0).getId()+"");
        mAgendaEvents.setSocid(newEvent.getTIER());
        mAgendaEvents.setNote(newEvent.getDESCRIPTION());

         //Creating Object of ObjectMapper define in Jakson Api
        ObjectMapper Obj = new ObjectMapper();
        try {
             //get object as a json string
            String jsonStr = Obj.writeValueAsString(mAgendaEvents);

            mDB.debugMessageDao().insertDebugMessage(
                    new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", CalendarActivity.class.getSimpleName(), "saveEvent()", "Converting AgendaEvents Object to Json Object string.\n\n"+jsonStr, ""));

            Log.e(TAG, "Save JSON:\n\n"+jsonStr);
        }
        catch (IOException e) {
            e.printStackTrace();
            mDB.debugMessageDao().insertDebugMessage(
                    new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", CalendarActivity.class.getSimpleName(), "saveEvent()", "***** IOException *****\n"+e.getMessage(), ""+e.getStackTrace()));
        }


        SendAgendaEventTask sendAgendaEventTask = new SendAgendaEventTask(progressDialog, mAgendaEvents, CalendarActivity.this, CalendarActivity.this);
        sendAgendaEventTask.execute();

    }

    private List<EventsEntry> collectEventsByDate(String Date){
        mDB.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", CalendarActivity.class.getSimpleName(), "collectEventsByDate()", "Called. collectEventsByDate("+Date+")", ""));

        Log.e(TAG, " start collectEventsByDate()");
        List<EventsEntry> listEvents = mDB.eventsDao().getEventsByDate(Date);
        ArrayList<Events> arrayList = new ArrayList<>();

        for (int i=0; i<listEvents.size(); i++){
//            new DebugMe(CalendarActivity.this, CalendarActivity.this, "WL",
//                    TAG+" collectEventsByDate() => called.\n" +
//                            "List size: "+i+"/"+listEvents.size()+"\n" +
//                            "Event: "+listEvents.get(i).getLABEL()+"\n" +
//                            "Time: "+listEvents.get(i).getTIME()+"\n" +
//                            "Date: "+listEvents.get(i).getDATE()+"\n" +
//                            "Month: "+listEvents.get(i).getMONTH()+"\n" +
//                            "Year: "+listEvents.get(i).getYEAR()+"\n\n").execute();

            Long id = listEvents.get(i).getId();
            String label = listEvents.get(i).getLABEL();
            String location = listEvents.get(i).getLIEU();
            String percentage = listEvents.get(i).getPERCENTAGE();
            String fullDayEvent = listEvents.get(i).getFULLDAYEVENT();
            String disponibility = listEvents.get(i).getTRANSPARENCY();
            String time = listEvents.get(i).getTIME();
            String date = listEvents.get(i).getDATE();
            String month = listEvents.get(i).getMONTH();
            String year = listEvents.get(i).getYEAR();
            Long startEvent = listEvents.get(i).getSTART_EVENT();
            Long endEvent = listEvents.get(i).getEND_EVENT();
            String description = listEvents.get(i).getDESCRIPTION();

            arrayList.add(new Events(id, label, location, percentage, fullDayEvent, disponibility, time, date, month, year, startEvent, endEvent, description));
        }
        return listEvents;
    }

    private void collectEventsPerMonth(String Month, String Year){
        mDB.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(getApplicationContext(),
                        (System.currentTimeMillis()/1000),
                        "Ticket",
                        CalendarActivity.class.getSimpleName(),
                        "collectEventsPerMonth()",
                        "Called. collectEventsPerMonth( "+Month+", "+Year+" ). About to clear event list size: "+eventsList.size(),
                        ""));

        Log.e(TAG, " collectEventsPerMonth( "+Month+", "+Year+" )");
        Log.e(TAG, " clearing event list size: "+eventsList.size());
        eventsList.clear();

        List<EventsEntry> listEvents = mDB.eventsDao().getEventsByMonth(Month, Year);
        eventsList.addAll(listEvents);
        Log.e(TAG," eventList size: "+eventsList.size());
    }

    private void InitAddAgendaDialog(final Dialog dialog, final int position){
        mDB.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", CalendarActivity.class.getSimpleName(), "InitAddAgendaDialog()", "Called.", ""));

        final EditText libelle_et = dialog.findViewById(R.id.dialog_add_new_event_libelle);
        final EditText lieu_et = dialog.findViewById(R.id.dialog_add_new_event_lieu);
        final Switch journe_st = dialog.findViewById(R.id.dialog_add_new_event_journee_st);
        final Switch disponible_st = dialog.findViewById(R.id.dialog_add_new_event_disponible_st);

        final TextView dateStart = dialog.findViewById(R.id.dialog_add_new_event_display_starteventday_tv);
        final TextView timeStart = dialog.findViewById(R.id.dialog_add_new_event_display_starteventtime_tv);

        final TextView dateEnd = dialog.findViewById(R.id.dialog_add_new_event_display_endeventday_tv);
        final TextView timeEnd = dialog.findViewById(R.id.dialog_add_new_event_display_endeventtime_tv);

        final EditText description_et = dialog.findViewById(R.id.dialog_add_new_event_description_et);
        final Button add_btn = dialog.findViewById(R.id.dialog_add_new_event_addevent_btn);
        Button cancel_btn = dialog.findViewById(R.id.dialog_add_new_event_cancelevent_btn);

        Spinner clientSpinner = (Spinner) dialog.findViewById(R.id.dialog_add_new_event_concernclient_sp);
        ArrayAdapter<String> clientSpinnerAdapter = new ArrayAdapter<String>(CalendarActivity.this, android.R.layout.simple_spinner_item, getAllClients());
        clientSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clientSpinner.setAdapter(clientSpinnerAdapter);

        final Calendar combinedCalStart = Calendar.getInstance(Locale.FRENCH);
        final Calendar combinedCalEnd = Calendar.getInstance(Locale.FRENCH);
        Calendar calendar = Calendar.getInstance(Locale.FRENCH);
        final int yearStart = calendar.get(Calendar.YEAR);
        final int monthStart = calendar.get(calendar.MONTH);
        final int dayStart = calendar.get(calendar.DAY_OF_MONTH);
        final int hoursStart = calendar.get(calendar.HOUR_OF_DAY);
        final int minutesStart = calendar.get(calendar.MINUTE);

        final int yearEnd = calendar.get(Calendar.YEAR);
        final int monthEnd = calendar.get(calendar.MONTH);
        final int dayEnd = calendar.get(calendar.DAY_OF_MONTH);
        final int hoursEnd = calendar.get(calendar.HOUR_OF_DAY);
        final int minutesEnd = calendar.get(calendar.MINUTE);

        final int[] startDate = new int[5];
        final int[] startEnd = new int[5];
        final String[] concernTier = new String[1];

        //Prevent the keyboard from displaying on activity start
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        clientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!adapterView.getSelectedItem().toString().equals("Veuillez selection Tiers concerné - Optionnelle")){
                    concernTier[0] = getSelectedClient(adapterView.getSelectedItem().toString()).getId().toString();
                    Toast.makeText(CalendarActivity.this, "Tiers concerné sélectionné: "+getSelectedClient(adapterView.getSelectedItem().toString()), Toast.LENGTH_SHORT).show();
                }else{
                    concernTier[0] = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //init date && time
        dateStart.setText(new SimpleDateFormat("EEEE, dd MMMM").format(dates.get(position)));
        timeStart.setText(new SimpleDateFormat("K:mm a").format(dates.get(position)));

        //set the date of the event
        dateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog mDatePickerDialog = new DatePickerDialog(dateStart.getContext(), R.style.Theme_AppCompat_DayNight_Dialog,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                Calendar c = Calendar.getInstance(Locale.FRENCH);
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, month);
                                c.set(Calendar.DAY_OF_MONTH, day);
                                startDate[0] = year;
                                startDate[1] = month;
                                startDate[2] = day;

                                Log.e(TAG, " JL DatePickerDialog:: date=> "+ DateFormat.getDateInstance().format(c.getTime())+"\n" +
                                        "year: "+year+" || month: "+(month+1)+" || day: "+day);

                                SimpleDateFormat hformate = new SimpleDateFormat("EEEE, dd MMMM", Locale.FRENCH);
                                String event_date = hformate.format(c.getTime());

                                dateStart.setText(event_date);
                            }
                        },yearStart, monthStart, dayStart);
                mDatePickerDialog.show();
            }
        });

        //set the time of the event
        timeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(add_btn.getContext(), R.style.Theme_AppCompat_DayNight_Dialog,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
                                Calendar c = Calendar.getInstance(Locale.FRENCH);
                                c.set(Calendar.HOUR_OF_DAY, hour);
                                c.set(Calendar.MINUTE, minutes);
                                c.setTimeZone(TimeZone.getDefault());
                                startDate[3] = hour;
                                startDate[4] = minutes;

                                SimpleDateFormat hformate = new SimpleDateFormat("K:mm a", Locale.FRENCH);
                                String event_Time = hformate.format(c.getTime());

                                timeStart.setText(event_Time);
                            }
                        }, hoursStart, minutesStart,false);
                timePickerDialog.show();
            }
        });

        //set the end date and time of the event
        dateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog mDatePickerDialog = new DatePickerDialog(dateEnd.getContext(), R.style.Theme_AppCompat_DayNight_Dialog,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                Calendar c = Calendar.getInstance(Locale.FRENCH);
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, month);
                                c.set(Calendar.DAY_OF_MONTH, day);
                                startEnd[0] = year;
                                startEnd[1] = month;
                                startEnd[2] = day;

                                SimpleDateFormat hformate = new SimpleDateFormat("EEEE, dd MMMM", Locale.FRENCH);
                                String event_date = hformate.format(c.getTime());

                                dateEnd.setText(event_date);
                            }
                        },yearEnd, monthEnd, dayEnd);
                mDatePickerDialog.show();
            }
        });

        timeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(add_btn.getContext(), R.style.Theme_AppCompat_DayNight_Dialog,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.HOUR_OF_DAY, hour);
                                c.set(Calendar.MINUTE, minutes);
                                c.setTimeZone(TimeZone.getDefault());
                                startEnd[3] = hour;
                                startEnd[4] = minutes;


                                SimpleDateFormat hformate = new SimpleDateFormat("K:mm a", Locale.FRENCH);
                                String event_Time = hformate.format(c.getTime());

                                timeEnd.setText(event_Time);
                            }
                        }, hoursEnd, minutesEnd,false);
                timePickerDialog.show();
            }
        });

        final String date = eventDateFormat.format(dates.get(position));
        final String month = monthFormat.format(dates.get(position));
        final String year = yearFormat.format(dates.get(position));

        //set event end calendar
        journe_st.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.e(TAG, "CompoundButton: "+b);
                SimpleDateFormat sdf_day = new SimpleDateFormat("EEEE, dd MMMM", Locale.FRENCH);
                SimpleDateFormat sdf_time = new SimpleDateFormat("K:mm a", Locale.FRENCH);
                if (!b) {
                    combinedCalStart.setTime(dates.get(position));

                    dateStart.setText(sdf_day.format(combinedCalStart.getTime()));
                    timeStart.setText(sdf_time.format(combinedCalStart.getTime()));
                    dateEnd.setText("XXX xx XXX");
                    timeEnd.setText("X:X XX");
                }else{
                    combinedCalStart.setTime(dates.get(position));

                    //to check once user add the event
                    startEnd[0] = combinedCalStart.get(Calendar.MONTH);
                    startEnd[1] = combinedCalStart.get(Calendar.MONTH);
                    startEnd[2] = combinedCalStart.get(Calendar.DAY_OF_MONTH);
                    startEnd[3] = 23;
                    startEnd[4] = 59;

                    combinedCalStart.set(combinedCalStart.get(Calendar.YEAR), combinedCalStart.get(Calendar.MONTH), combinedCalStart.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                    combinedCalEnd.set(combinedCalStart.get(Calendar.YEAR), combinedCalStart.get(Calendar.MONTH), combinedCalStart.get(Calendar.DAY_OF_MONTH), 23, 59, 59);

                    dateStart.setText(sdf_day.format(combinedCalStart.getTime()));
                    timeStart.setText(sdf_time.format(combinedCalStart.getTime()));
                    dateEnd.setText(sdf_day.format(combinedCalEnd.getTime()));
                    timeEnd.setText(sdf_time.format(combinedCalEnd.getTime()));
                }
            }
        });

        //save the Event in database
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean cancel = false;
                View focusView = null;
                if (libelle_et.getText().toString().isEmpty()){
                    libelle_et.setError("Veuillez remplir le nom de l'événement!");
                    focusView = libelle_et;
                    cancel = true;
                }
                if ((startEnd[0] + startEnd[1] + startEnd[2]) == 0){
                    dateEnd.setError("Veuillez saisir une date de fin de l'événement!");
                    focusView = dateEnd;
                    cancel = true;
                }
                if ((startEnd[3] + startEnd[4]) == 0){
                    timeEnd.setError("Veuillez saisir une heure de fin de l'événement!");
                    focusView = timeEnd;
                    cancel = true;
                }

                //set event start calendar
                if ((startDate[0] + startDate[1] + startDate[2] + startDate[3] + startDate[4]) == 0){
                    combinedCalStart.setTime(dates.get(position));
                }else{
                    combinedCalStart.set(startDate[0], startDate[1], startDate[2], startDate[3], startDate[4]);
                }

                //check if  startEventCalendar > endEventCalendar
                if (combinedCalStart.getTime().getTime() > combinedCalEnd.getTime().getTime()){
                    Log.e(TAG , " combinedCalStart= "+combinedCalStart.getTime().getTime()+" || combinedCalEnd= "+combinedCalEnd.getTime().getTime());
                    dateEnd.setError("Veuillez saisir une date de fin de l'événement!");
                    dateEnd.requestFocus();
                    timeEnd.setError("Veuillez saisir une heure de fin de l'événement!");
                    timeEnd.requestFocus();
                    cancel = true;
                    Toast.makeText(CalendarActivity.this, "Veuillez saisir correctement la durée de l'événement!", Toast.LENGTH_LONG).show();
                }

                if (cancel && (focusView != null)){
                    // form field with an error.
                    focusView.requestFocus();
                    return;
                }

                //save the event
                saveEvent(libelle_et.getText().toString(), lieu_et.getText().toString(), "-1", Boolean.toString(journe_st.isChecked()),
                        Boolean.toString(disponible_st.isChecked()), timeStart.getText().toString(), date, month, year,
                        combinedCalStart.getTimeInMillis(), combinedCalEnd.getTimeInMillis(), concernTier[0], description_et.getText().toString());
                initCalendar();
                dialog.dismiss();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initCalendar();
                dialog.dismiss();
            }
        });

    }

    private List<String> getAllClients(){
        mDB.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", CalendarActivity.class.getSimpleName(), "getAllClients()", "Called.", ""));

        List<String> theList = new ArrayList<>();
        List<ClientEntry> list = mDB.clientDao().getAllClient();

        theList.add("Veuillez selection Tiers concerné - Optionnelle");
        for (int i=0; i<list.size(); i++){
            theList.add(list.get(i).getName());
        }
        return theList;
    }

    private ClientEntry getSelectedClient(String clientName){
        mDB.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", CalendarActivity.class.getSimpleName(), "getSelectedClient()", "Called. getSelectedClient( "+clientName+" )", ""));

        ClientEntry clientEntry = null;
        List<ClientEntry> clientList = mDB.clientDao().getAllClient();
        for (int i=0; i<clientList.size(); i++){
            if (clientList.get(i).getName().equals(clientName)){
                clientEntry = clientList.get(i);
                break;
            }
        }
        return clientEntry;
    }

    private void InitViewAgendaDialog(final Dialog dialog, int position) {
        Button close_btn;

        close_btn = dialog.findViewById(R.id.custom_show_events_layout_close_btn);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void getEventsFromServer(){
        mDB.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", CalendarActivity.class.getSimpleName(), "getEventsFromServer()", "Called.", ""));

        if (!ConnectionManager.isPhoneConnected(getApplication())){
            Toast.makeText(this, getString(R.string.erreur_connexion), Toast.LENGTH_LONG).show();
            mDB.debugMessageDao().insertDebugMessage(
                    new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", CalendarActivity.class.getSimpleName(), "getEventsFromServer()", getString(R.string.erreur_connexion), ""));
            //progressDialog.dismiss();
            return;
        }

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Objects.requireNonNull(CalendarActivity.this).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            Objects.requireNonNull(CalendarActivity.this).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        //affichage du loader dialog
        //showProgressDialog(true, null, "Synchronisation des évènements en cours...");

        if (mFindAgendaEventTask == null) {
            mFindAgendaEventTask = new FindAgendaEventTask(this, CalendarActivity.this, "datec", "asc", mLimit, mPageEvent);
            mFindAgendaEventTask.execute();
        }
    }

    @Override
    public void onFindAgendaEventsTaskComplete(AgendaEventsREST mAgendaEventsREST) {
        mFindAgendaEventTask = null;
        mDB.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", CalendarActivity.class.getSimpleName(), "onFindAgendaEventsTaskComplete()", "Called.", ""));

        //Si la recupération echoue, on renvoi un message d'erreur
        if (mAgendaEventsREST == null) {
            //Fermeture du loader
            //showProgressDialog(false, null, null);
            mDB.eventsDao().deleteAllEvent();
            Toast.makeText(this, getString(R.string.service_indisponible), Toast.LENGTH_LONG).show();
            mDB.debugMessageDao().insertDebugMessage(
                    new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", CalendarActivity.class.getSimpleName(), "onFindAgendaEventsTaskComplete()", getString(R.string.service_indisponible), ""));
            return;
        }
        if (mAgendaEventsREST.getAgendaEvents() == null) {
            Objects.requireNonNull(this).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            //Fermeture du loader
            //showProgressDialog(false, null, null);
            //reinitialisation du nombre de page
            mPageEvent = 0;

            Log.e(TAG, " onFindAgendaEventsTaskComplete:: DB table size: "+mDB.eventsDao().getAllEvents().size());
            mDB.debugMessageDao().insertDebugMessage(
                    new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", CalendarActivity.class.getSimpleName(), "onFindAgendaEventsTaskComplete()", "Calendar event saved!", ""));

            Toast.makeText(this, "Evènements synchronizé !", Toast.LENGTH_LONG).show();

            //update the calendar
            collectEventsPerMonth(monthFormat.format(calendar.getTime()), yearFormat.format(calendar.getTime()));
            mAgendaAdapter.notifyDataSetChanged();
            return;
        }

        for (AgendaEvents eventItem : mAgendaEventsREST.getAgendaEvents()) {

            EventsEntry eventsEntry = new EventsEntry();
            eventsEntry.setId(eventItem.getId());
            eventsEntry.setREF(eventItem.getRef());
            eventsEntry.setLABEL(eventItem.getLabel());
            eventsEntry.setLIEU(eventItem.getLocation());
            eventsEntry.setPERCENTAGE(eventItem.getPercentage());
            eventsEntry.setFULLDAYEVENT(eventItem.getFulldayevent());
            eventsEntry.setTRANSPARENCY(eventItem.getTransparency());

            /***********************************For AdminDebugging***********************************/
            mDB.debugMessageDao().insertDebugMessage(
                    new DebugItemEntry(getApplicationContext(),
                            (System.currentTimeMillis()/1000),
                            "Ticket", CalendarActivity.class.getSimpleName(),
                            "onFindAgendaEventsTaskComplete()",
                            "AgendaEvents id: "+eventItem.getId()+"\n" +
                                    "AgendaEvents datec: "+(eventItem.getDatec()*1000)+"\n" +
                                    "AgendaEvents datep: "+(eventItem.getDatep()*1000)+"\n" +
                                    "EventsEntry Event Time: "+new SimpleDateFormat("K:mm a", Locale.FRENCH).format(new Date( (eventItem.getDatep()*1000) ))+"\n" +
                                    "EventsEntry Event Date: "+eventDateFormat.format(new Date( (eventItem.getDatep()*1000) ))+"\n" +
                                    "EventsEntry Event month: "+monthFormat.format(new Date( (eventItem.getDatep()*1000) ))+"\n" +
                                    "EventsEntry Event Year: "+yearFormat.format(new Date( (eventItem.getDatep()*1000) )),
                            ""));

            eventsEntry.setTIME(new SimpleDateFormat("K:mm a", Locale.FRENCH).format(new Date( (eventItem.getDatep()*1000) )));
            eventsEntry.setDATE(eventDateFormat.format(new Date( (eventItem.getDatep()*1000) )));
            eventsEntry.setMONTH(monthFormat.format(new Date( (eventItem.getDatep()*1000) )));
            eventsEntry.setYEAR(yearFormat.format(new Date( (eventItem.getDatep()*1000) )));

            eventsEntry.setSTART_EVENT(eventItem.getDatep()*1000);
            eventsEntry.setEND_EVENT(eventItem.getDatef()*1000);
            eventsEntry.setTIER(eventItem.getSocid());
            eventsEntry.setDESCRIPTION(eventItem.getNote());

            mDB.eventsDao().insertNewEvent(eventsEntry);
        }

        mPageEvent++;
        getEventsFromServer();
    }

    @Override
    public void onSendAgendaEventsTaskComplete() {
        mDB.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", CalendarActivity.class.getSimpleName(), "onSendAgendaEventsTaskComplete()", "Called.", ""));

        mDB.eventsDao().deleteAllEvent();
        getEventsFromServer();
    }

    @Override
    public void onDeleteAgendasTaskComplete() {
        mDB.debugMessageDao().insertDebugMessage(
                new DebugItemEntry(getApplicationContext(), (System.currentTimeMillis()/1000), "Ticket", CalendarActivity.class.getSimpleName(), "onDeleteAgendasTaskComplete()", "Called.", ""));
        initCalendar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if(item.getItemId() == R.id.action_fragclient_sync){
            if (!ConnectionManager.isPhoneConnected(this)) {
                Toast.makeText(this, getString(R.string.erreur_connexion), Toast.LENGTH_LONG).show();
                return true;
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
