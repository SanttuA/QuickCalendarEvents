package com.apps.santtualatalo.quickcalendarevents;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,
        OnEditTextReadyListener {

    //variables to keep track of event creation progress
    private boolean dateHandled = false;
    private boolean timeHandled = false;
    private boolean messageHandled = false;

    //buttons for triggering user input for a calendar event
    private Button dateButton;
    private Button timeButton;
    private Button messageButton;

    private Button readyButton; //pushes the event to google calendar app

    //variables for calendar event creation
    private int year;
    private int month;
    private int dayOfMonth;
    private int hour;
    private int minutes;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateButton = (Button) findViewById(R.id.dateButton);
        dateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowDatePicker();
            }
        });

        timeButton = (Button) findViewById(R.id.timeButton);
        timeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowTimePicker();
            }
        });

        messageButton = (Button) findViewById(R.id.messageButton);
        messageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowEditTextAlert();
            }
        });

        readyButton = (Button) findViewById(R.id.readyButton);
        readyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SendEventToCalendar();
            }
        });

        //start by showing the date picker for an event
        ShowDatePicker();
    }

    //pops up a date picker for user
    private void ShowDatePicker()
    {
        DialogFragment datePickerFragment = new DatePickerFragment();
        //datePickerFragment.setRetainInstance(true);
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }

    //pops up a time picker for user
    private void ShowTimePicker()
    {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    //pops up a message edit popup for user
    private void ShowEditTextAlert()
    {
        DialogFragment newFragment = new EditTextFragment();
        Bundle args = new Bundle();
        args.putString("messageKey", message);
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "editTextWriter");

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        //store date variables for google calendar event creation
        this.year = year;
        this.month = month;
        this.dayOfMonth = day;

        //use calendar class with given date to create locale specific date format
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        //String formatted= DateFormat.getLongDateFormat(this).format(calendar.getTime());
        dateButton.setText(DateFormat.getLongDateFormat(this).format(calendar.getTime()));
        dateHandled = true; //date is handled now, don't ask for it anymore

        //if time isn't handled yet, move to it next
        if(!timeHandled)
        {
            ShowTimePicker();
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //store date variables for google calendar event creation
        this.hour = hourOfDay;
        this.minutes = minute;

        //use calendar class with given time to create locale specific time format
        Calendar calendar = Calendar.getInstance();
        calendar.getTime();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        //String formatted = DateFormat.getTimeFormat(this).format(calendar.getTime());
        timeButton.setText(DateFormat.getTimeFormat(this).format(calendar.getTime()));
        timeHandled = true; //time is handled now, don't ask for it anymore

        //if message hasn't been handled yet, move to it next
        if(!messageHandled)
        {
            ShowEditTextAlert();
        }
    }

    @Override
    public void onEditTextReady(String message) {
        //user wrote a message for the event
        //could check for empty message
        this.message = message;
        messageHandled = true;  //message is handled now, don't ask for it anymore
        messageButton.setText(message);
    }

    @Override
    public void onEditTextCancel() {
        //user canceled writing a message for the event
    }

    //Sends the event with previously defined values to google calendar and closes this app
    private void SendEventToCalendar()
    {
        //check if any of the event parts are missing and inform the user accordingly
        if(!dateHandled)
            SingleToast.show(this, getString(R.string.select_date_first), Toast.LENGTH_LONG);
        else if(!timeHandled)
            SingleToast.show(this, getString(R.string.select_time_first), Toast.LENGTH_LONG);
        else if(!messageHandled)
            SingleToast.show(this, getString(R.string.write_message_first), Toast.LENGTH_LONG);
        else
        {
            //create the calendar event and send it with intent
            Calendar beginTime = Calendar.getInstance();
            beginTime.set(year, month, dayOfMonth, hour, minutes);
            Calendar endTime = Calendar.getInstance();
            endTime.set(year, month, dayOfMonth, hour, minutes);
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                    .putExtra(CalendarContract.Events.TITLE, message);
            startActivity(intent);

            //close this session/app
            finish();
        }
    }

}
