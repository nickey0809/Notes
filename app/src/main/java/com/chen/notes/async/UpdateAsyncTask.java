package com.chen.notes.async;

import android.os.AsyncTask;
import android.util.Log;

import com.chen.notes.models.Note;
import com.chen.notes.persistence.NoteDao;

public class UpdateAsyncTask extends AsyncTask<Note, Void, Void> {
    private static final String TAG = "UpdateAsyncTask";
    private NoteDao mNoteDao;

    public UpdateAsyncTask(NoteDao dao){
        mNoteDao = dao;
    }

    @Override
    protected Void doInBackground(Note... notes) {
        Log.d(TAG, "doInBackground:  thread: " + Thread.currentThread().getName());
        mNoteDao.update(notes);
        return null;
    }
}
