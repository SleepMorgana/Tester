package com.example.toto;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toto.sessions.Session;
import com.example.toto.sessions.Status;
import com.example.toto.users.User;
import com.example.toto.users.UserManager;
import com.example.toto.utils.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.timessquare.CalendarPickerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestSessionActivity extends AppCompatActivity {
    private User tutor;
    private Context mContext=this;
    public static final String mTutorFlag = "selectedItem";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);
        //Enable the Up button
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar(); // Get a support ActionBar corresponding to this toolbar
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
        }

        //Selected tutor user, probably through intent element
        Intent intent = getIntent();
        tutor = intent.getParcelableExtra(mTutorFlag);

        //Render the tutor's identity (i.e. username in this activity)
        TextView text_view = findViewById(R.id.username_profile_id);
        text_view.setText(tutor.getUsername());



        //Dates ListView
        ListView listView = (ListView) findViewById(R.id.session_date_listview);
        final ArrayAdapter<String> dateAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,new ArrayList<String>());
        listView.setAdapter(dateAdapter);

        //spinner elements
        Spinner spinner = (Spinner) findViewById(R.id.subject_spinner);
        final List<String> availableSubjects = tutor.getSubjectNames();
        //Session fields
        final String[] selectedSubject = {""};
        final Map<String,String> selectedDates = new HashMap<>();//key = date; value = date+time

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,availableSubjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSubject[0] = availableSubjects.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //date view
        final CalendarPickerView calendarView = (CalendarPickerView ) findViewById(R.id.calendar_view);
        //getting current
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        Date today = new Date();

        calendarView.init(today,nextYear.getTime())
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE);
        calendarView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(final Date date) {
                //create dialog
                final EditText editText = new EditText(mContext);
                editText.setHint("HH:mm");
                Util.makeInputDialog("Time", "Select a time:", "Confirm", "Cancel", mContext,
                        editText,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //OK
                                String timeString = (editText.getText()!=null)? editText.getText().toString().toLowerCase().trim() : "";
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                Date time = null;
                                try {
                                    time = sdf.parse(timeString);//adding seconds
                                } catch (ParseException e) {
                                    Util.printToast(getApplicationContext(),"Invalid time", Toast.LENGTH_SHORT);
                                    return;
                                }

                                //create timestamp
                                Calendar dayCalendar = GregorianCalendar.getInstance();
                                dayCalendar.setTime(date);

                                Calendar timeCalendar = GregorianCalendar.getInstance();
                                timeCalendar.setTime(time);   // assigns calendar to given date

                                Calendar finalDay = GregorianCalendar.getInstance(); // creates a new calendar instance
                                finalDay.set(dayCalendar.get(Calendar.YEAR),
                                        dayCalendar.get(Calendar.MONTH),dayCalendar.get(Calendar.DAY_OF_MONTH),
                                        timeCalendar.get(Calendar.HOUR),timeCalendar.get(Calendar.MINUTE));   // assigns calendar to given date

                                selectedDates.put(dayCalendar.getTime().toString(),finalDay.getTimeInMillis()+"");
                                dateAdapter.add(finalDay.getTime().toString());
                                dialog.dismiss();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //CANCEL
                                dialog.dismiss();
                            }
                        }).show();
            }

            @Override
            public void onDateUnselected(Date date) {
                selectedDates.remove(date.toString());
                int flag = 0;
                for (int i=0; i<dateAdapter.getCount(); i++){
                    if (dateAdapter.getItem(i).startsWith(date.toString())){
                        flag=i;
                        break;
                    }
                }
                dateAdapter.remove(dateAdapter.getItem(flag));
            }
        });

        //Time box
        //final EditText timebox = (EditText) findViewById(R.id.time_edit_text);

        Button button = (Button) findViewById(R.id.request_session_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create session instance

                if (selectedSubject[0].equals("")){
                    Util.printToast(getApplicationContext(),"Please select subject", Toast.LENGTH_SHORT);
                    return;
                }
                Session session = new Session(selectedSubject[0],UserManager.getUserInstance().getUser().getId(),
                        tutor.getId(), Status.PENDING);

                if (selectedDates.size()==0){
                    Util.printToast(getApplicationContext(),"Invalid day", Toast.LENGTH_SHORT);
                    return;
                }
                //Adding timestamps to session
                for (Map.Entry<String,String> day : selectedDates.entrySet()){
                    session.addDate(day.getValue());
                }

                //save Session
                UserManager.createSession(session, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Util.printToast(mContext,"A new session was scheduled!", Toast.LENGTH_SHORT);
                        finish();
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Util.printToast(mContext,"There were issues scheduling the session", Toast.LENGTH_SHORT);
                    }
                });
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish(); // close this activity and return to preview activity (if there is any)
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
