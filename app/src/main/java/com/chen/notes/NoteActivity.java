package com.chen.notes;


import android.app.Activity;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chen.notes.models.Note;
import com.chen.notes.persistence.NoteRepository;
import com.chen.notes.util.Utility;

public class NoteActivity extends AppCompatActivity
        implements View.OnTouchListener, GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener, View.OnClickListener, TextWatcher {

    private static final String TAG = "NoteActivity";
    private static final int Edit_MODE_ENABLED = 1;
    private static final int Edit_Mode_DISABLED = 0;

    //ui components
    private LinedEditText mLinedEditTest;
    private EditText mEditTitle;
    private TextView mViewTitle;
    private RelativeLayout mCheckContainer, mBackArrowContainer;
    private ImageButton mCheck, mBackArrow;

    //vars
    private boolean mIsNewNote;
    private Note mInitialNote;
    private GestureDetector mGestureDetector;
    private int mMode;
    private NoteRepository mNoteRepository;
    private Note mFinalNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

       mLinedEditTest = findViewById(R.id.note_text);
       mEditTitle = findViewById(R.id.note_edit_title);
       mViewTitle = findViewById(R.id.note_text_title);
       mCheckContainer=findViewById(R.id.check_container);
       mBackArrowContainer=findViewById(R.id.back_arrow_container);
       mCheck=findViewById(R.id.toolbar_check);
       mBackArrow=findViewById(R.id.toolbar_back_arrow);

       mNoteRepository = new NoteRepository(this);
       if(getIncomingIntent()){
           //this is a new note---Edit mode
           setNewNoteProperties();
           enableEditMode();
       }else{
           //this not a new note -- view mode
           setNoteProperties();
           disableContentInteraction();

       }
       setListeners();

    }

   private void saveChanges(){
        if(mIsNewNote){
            saveNewNote();
        }else{
            updateNote();
        }
    }
    public void saveNewNote(){
        mNoteRepository.insertNoteTask(mFinalNote);
    }

    private void updateNote(){
        mNoteRepository.updateNoteTask(mFinalNote);
    }

    //helper method

    private void setListeners(){
        mLinedEditTest.setOnTouchListener(this);
        mGestureDetector= new GestureDetector(this, this);
        mViewTitle.setOnClickListener(this);
        mCheck.setOnClickListener(this);
        mBackArrow.setOnClickListener(this);
        mEditTitle.addTextChangedListener(this);
    }



    private boolean getIncomingIntent(){
        if(getIntent().hasExtra("selected_note")){
            mInitialNote = getIntent().getParcelableExtra("selected_note");
            mFinalNote = new Note();
            mFinalNote.setTitle(mInitialNote.getTitle());
            mFinalNote.setContent(mInitialNote.getContent());
            mFinalNote.setTimestamp(mInitialNote.getTimestamp());
            mFinalNote.setId(mInitialNote.getId());
            Log.d(TAG, "getIncomingIntent:  " + mInitialNote.toString());

            mMode = Edit_Mode_DISABLED;
            mIsNewNote =false;
            return false;
        }
        mMode = Edit_MODE_ENABLED;
        mIsNewNote = true;
        return true;
    }

    private void disableContentInteraction(){
        mLinedEditTest.setKeyListener(null);
        mLinedEditTest.setFocusable(false);
        mLinedEditTest.setFocusableInTouchMode(false);
        mLinedEditTest.setCursorVisible(false);
        mLinedEditTest.clearFocus();

    }
    private void enableContentInteraction(){
        mLinedEditTest.setKeyListener(new EditText(this).getKeyListener());
        mLinedEditTest.setFocusable(true);
        mLinedEditTest.setFocusableInTouchMode(true);
        mLinedEditTest.setCursorVisible(true);
        mLinedEditTest.requestFocus();
    }

 



    //taggle editMode
    private void enableEditMode(){
        mBackArrowContainer.setVisibility(View.GONE);
        mCheckContainer.setVisibility(View.VISIBLE);

        mViewTitle.setVisibility(View.GONE);
        mEditTitle.setVisibility(View.VISIBLE);

        mMode = Edit_MODE_ENABLED;
        enableContentInteraction();
    }

    private void disableEditMode(){
        mBackArrowContainer.setVisibility(View.VISIBLE);
        mCheckContainer.setVisibility(View.GONE);

        mViewTitle.setVisibility(View.VISIBLE);
        mEditTitle.setVisibility(View.GONE);

        mMode = Edit_Mode_DISABLED;
        disableContentInteraction();
        String temp = mLinedEditTest.getText().toString();
        temp = temp.replace("\n", "");
        temp = temp.replace(" ", "");
        if(temp.length() > 0){
            mFinalNote.setTitle(mEditTitle.getText().toString());
            mFinalNote.setContent(mLinedEditTest.getText().toString());
            String timestamp = Utility.getCurrentTimeStamp();
            mFinalNote.setTimestamp(timestamp);

            if(!mFinalNote.getContent().equals(mInitialNote.getContent())
            || !mFinalNote.getTitle().equals(mInitialNote.getTitle())){
                Log.d(TAG, "disableEditMode:  called ");
                saveChanges();
            }
        }


    }

    private void hideSoftKeyBoard(){
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    

    private void setNewNoteProperties(){
        mViewTitle.setText("Note title");
        mEditTitle.setText("Note title");

        mInitialNote = new Note();
        mFinalNote = new Note();
        mInitialNote.setTitle("Note title");
        mInitialNote.setTitle("Note title");
    }


    private void setNoteProperties(){
        mViewTitle.setText(mInitialNote.getTitle());
        mEditTitle.setText(mInitialNote.getTitle());
        mLinedEditTest.setText(mInitialNote.getContent());
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d(TAG, "onDoubleTap:  double tapped");
        enableEditMode();
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.toolbar_check:{
                hideSoftKeyBoard();
                disableEditMode();
                break;
            }

            case R.id.note_text_title:{
                enableEditMode();
                mEditTitle.requestFocus();
                mEditTitle.setSelection(mEditTitle.length());
                break;
            }

            case R.id.toolbar_back_arrow:{
                finish();
                break;
            }

        }

    }

    @Override
    public void onBackPressed() {
        if(mMode == Edit_MODE_ENABLED){
            onClick(mCheck);
        }else{
            super.onBackPressed();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mode", mMode);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMode = savedInstanceState.getInt("mode");
        if(mMode == Edit_MODE_ENABLED){
            enableEditMode();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mViewTitle.setText(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
