package com.example.notesbee.ui;

import android.content.Context;

import com.example.notesbee.Note;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Class for loading and saving all notes, all notes belong to this class
 */
public class NoteList {
    // Array of notes
    private ArrayList<Note> notes;
    private Context context;
    private static NoteList instance;

    // Used for getNote, if this is specified a new note is returned
    public static final int NOTELIST_NEW_NOTE = -1;

    /**
     * Loads a note list from a file, returning an empty note list if the file doesn't exist
     * @param db File to load from, saved from flush()
     */
    private NoteList(Context context, String db) {
        notes = new ArrayList<Note>();
        this.context = context;
        StringBuilder fileString = new StringBuilder();

        try {
            // Read file into string
            FileInputStream input = context.openFileInput(db);
            byte[] buffer = new byte[100];
            while (input.read(buffer) != -1) {
                fileString.append(new String(buffer));
                buffer = new byte[100];
            }
            input.close();

            // Separate each line and parse into a note
            for (String s : fileString.toString().split("\n")) {
                notes.add(new Note(s));
            }
        } catch (Exception e) {
            // lmao
        } finally {
            // works on my machine
        }
    }

    /**
     * Initializes the instance, call at the start of the program
     * @param filename File to load from, saved from flush()
     */
    public static void initInstance(Context context, String filename) {
        instance = new NoteList(context, filename);
    }

    /**
    * Grabs the NoteList singleton, throwing if it hasn't been initialized
    */
    public static NoteList getInstance() throws InstantiationException {
        if (instance != null)
            return instance;
        throw new InstantiationException("NoteList was never initialized");
    }

    /**
     * Flushes all saved notes to a database file
     * @param db
     */
    public void flush(String db) {
        // Just dump each serialized note and a newline
        try {
            FileOutputStream out = context.openFileOutput(db, Context.MODE_PRIVATE);
            for (int i = 0; i < notes.size(); i++) {
                out.write(notes.get(i).toString().getBytes());
                if (i != notes.size() - 1)
                    out.write("\n".getBytes());
            }
        } catch (Exception e) {
            // lmao
        } finally {
            // works on my machine
        }
    }

    /**
     * Since notes are guaranteed to preserve order when saving/loading you can use this to swap two notes in the array
     * @param index First note to swap in the range [0, getNoteCount)
     * @param otherIndex Second note to swap with in the range [0, getNoteCount)
     * @throws IndexOutOfBoundsException if either index is out of bounds
     */
    public void swapNotes(int index, int otherIndex) throws IndexOutOfBoundsException {
        Note hold = notes.get(index);
        notes.set(index, notes.get(otherIndex));
        notes.set(otherIndex, hold);
    }

    /**
     * Removes a note
     * @param index Index of the note to remove
     */
    public void remove(int index) throws ArrayIndexOutOfBoundsException {
        notes.remove(index);
    }

    /**
     * Gets a note at a given index
     * @param index Index of the note to get in the range [0, getNoteCount) U NOTELIST_NEW_NOTE
     * @throws ArrayIndexOutOfBoundsException
     */
    public Note getNote(int index) throws ArrayIndexOutOfBoundsException {
        if (index == NOTELIST_NEW_NOTE) {
            Note out = new Note();
            notes.add(out);
            return out;
        } else {
            return notes.get(index);
        }
    }

    /**
     * Returns the number of notes stored in the note list
     */
    public int getNoteCount() {
        return notes.size();
    }
}
