package com.example.notesbee;

import androidx.appcompat.app.AppCompatActivity;
import jp.wasabeef.richeditor.RichEditor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notesbee.ui.NoteList;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;

public class AddNotesActivity extends AppCompatActivity {
    private EditText title;
    private RichEditor notesContent;
    //private String dateTime;
    //private Calendar calendar;
    //private SimpleDateFormat simpleDateFormat;
    private Note note; // Local note used to create alarms and build the save/load with
    private static WeakReference<AddNotesActivity> weakActivity;
    private boolean recognitionStarted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        title=findViewById(R.id.notesTitle);
        ImageButton savenotes= findViewById(R.id.save_note_btn);
        savenotes.setOnClickListener(view -> addDataToDatabase());
        weakActivity = new WeakReference<>(this);

        // Pull note from the database
        try {
            note = NoteList.getInstance().getNote(getIntent().getIntExtra("noteIndex", NoteList.NOTELIST_NEW_NOTE));
        } catch (Exception e) {
            System.out.println(e);
        }

        // Save data to the database on clicking the save button
        findViewById(R.id.save_note_btn).setOnClickListener(view -> addDataToDatabase());

        // Set a reminder for the note
        findViewById(R.id.reminder_btn).setOnClickListener(this::setAlarm);

        // On clicked starts voice-to-text
        findViewById(R.id.vtt_btn).setOnClickListener(this::startVoiceToText);

        //Or if long pressed start voice-to_text
        //findViewById(R.id.vtt_btn).setOnLongClickListener(view -> startVoiceToText(view));


        notesContent= findViewById(R.id.notesContent);

        findViewById(R.id.action_undo).setOnClickListener(view -> notesContent.undo());

        findViewById(R.id.action_redo).setOnClickListener(view -> notesContent.redo());

        findViewById(R.id.action_bold).setOnClickListener(view -> notesContent.setBold());

        findViewById(R.id.action_italic).setOnClickListener(view -> notesContent.setItalic());

        findViewById(R.id.action_strikethrough).setOnClickListener(view -> notesContent.setStrikeThrough());

        findViewById(R.id.action_underline).setOnClickListener(view -> notesContent.setUnderline());

        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            private Boolean isChanged=true;
            @Override
            public void onClick(View view) {
                notesContent.setTextColor(isChanged ? Color.BLACK: Color.RED);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_insert_bullets).setOnClickListener(v -> notesContent.setBullets());

        findViewById(R.id.action_insert_numbers).setOnClickListener(v -> notesContent.setNumbers());

        findViewById(R.id.action_insert_checkbox).setOnClickListener(v -> notesContent.insertTodo());


    }


    /**
     * Flushes all currently stored text and such to the note
     */
    private void flushNote() {
        note.title = title.getText().toString();
        note.memo = notesContent.getHtml();
    }

    /**
     * The function that is called when the "Alarm" button is pressed.
     * It will launch a dialogue to select a time and schedule an alarm for that time.
     */
    public void setAlarm(View view) {
        // Must update note contents before setting alarm so the contents are visible to the future notification
        flushNote();
        note.createAlarm(view.getContext());
    }

    /**
     * Adds the message currently written to the database, called whenever something would
     * cause this message screen to close.
     */
    private void addDataToDatabase(){

        // We will save all data in a note class then serialize it
//        note.title = title.getText().toString();
//        String serial = note.serialize();
//        Boolean alarm = false;
//        Date currentDateTime = Calendar.getInstance().getTime();

//        String titleTXT = title.getText().toString();
//        String contentTXT = notesContent.getHtml();
//        String dateTXT = currentDateTime.toString();
//        String alarmTXT = alarm.toString();


//        DBHelper DH= new DBHelper(AddNotesActivity.this);
//
//        Boolean check_insert_data = DH.insert_user_data(titleTXT, contentTXT, dateTXT, alarmTXT);
//
//        if(check_insert_data)
//            Toast.makeText(AddNotesActivity.this, "New Entry Inserted", Toast.LENGTH_SHORT).show();
//        else
//            Toast.makeText(AddNotesActivity.this, "New Entry Not Inserted", Toast.LENGTH_SHORT).show();

        flushNote();
        try {
            NoteList.getInstance().flush(getString(R.string.database_file));
        } catch (Exception e) {
            System.out.println(e);
        }
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    /**
     * Begins voice recognition, this is called when the microphone icon is pressed.
     * @param view
     */
    public void startVoiceToText(View view) {
        Intent intent = new Intent(this, VoiceRecognition.class);
        if (recognitionStarted == false) {
            recognitionStarted = true;
            startActivity(intent);
        } else {
            recognitionStarted = false;
            VoiceRecognition.getInstanceActivity().onDestroy();
        }
    }

    /**
     * Helper method to return a reference to the activity
     * @return weakActivity
     */
    public static AddNotesActivity getInstanceActivity() { return weakActivity.get(); }

    /**
     * Helper method to update voice-to-text caption in notes from a different class
     * @param text the new text to update the caption with
     */
    public void setVoiceCaptionText(String text) {
                TextView textView = (TextView)findViewById(R.id.caption_text);
                textView.setText(text);
    }

}