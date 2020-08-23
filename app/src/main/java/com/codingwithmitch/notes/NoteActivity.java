package com.codingwithmitch.notes;

import android.graphics.Color;
import android.opengl.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codingwithmitch.notes.models.Note;
import com.codingwithmitch.notes.persistence.NoteRepository;
import com.codingwithmitch.notes.util.Utility;

public class NoteActivity extends AppCompatActivity implements
        View.OnTouchListener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        View.OnClickListener,
        TextWatcher
{

    private static final String TAG = "NoteActivity";
    private static final int EDIT_MODE_ENABLED = 1;
    private static final int EDIT_MODE_DISABLED = 0;

    // UI components
    private EditText mLinedEditText;
    private EditText mEditTitle;
    private TextView mViewTitle;
    private TextView mItems;
    private RelativeLayout mCheckContainer, mBackArrowContainer;
    private ImageButton mCheck, mBackArrow;
    private EditText mMinQuantity;
    private EditText mStock;
    private EditText mChangeStock;
    private LinearLayout mButtons;


    // vars
    private boolean mIsNewNote;
    private Note mNoteInitial;
    private GestureDetector mGestureDetector;
    private int mMode;
    private NoteRepository mNoteRepository;
    private Note mNoteFinal;
    private Button mbtnSell;
    private Button mbtnBuy;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        mLinedEditText = findViewById(R.id.note_text);
        mEditTitle = findViewById(R.id.note_edit_title);
        mViewTitle = findViewById(R.id.note_text_title);
        mCheck = findViewById(R.id.toolbar_check);
        mBackArrow = findViewById(R.id.toolbar_back_arrow);
        mCheckContainer = findViewById(R.id.check_container);
        mBackArrowContainer = findViewById(R.id.back_arrow_container);
        mbtnSell=findViewById(R.id.btnsell);
        mbtnBuy=findViewById(R.id.btnbuy);
        mMinQuantity=findViewById(R.id.minquantity);
        mStock=findViewById(R.id.stock);
        mChangeStock=findViewById(R.id.quantity);
        mItems=findViewById(R.id.items);
        mButtons=findViewById(R.id.interact);

        mStock.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                mItems.setText(mStock.getText().toString());
            }
        });

        mNoteRepository = new NoteRepository(this);

        setListeners();

        if(getIncomingIntent()){
            setNewNoteProperties();
            enableEditMode();
        }
        else {
            setNoteProperties();
            disableContentInteraction();
        }
    }

    private void saveChanges(){
        if(mIsNewNote){
            saveNewNote();
        }else{
            updateNote();
        }
    }

    public void updateNote() {
        mNoteRepository.updateNoteTask(mNoteFinal);
    }

    public void saveNewNote() {
        mNoteRepository.insertNoteTask(mNoteFinal);
    }

    private void setListeners(){
        mGestureDetector = new GestureDetector(this, this);

        mLinedEditText.setOnTouchListener(this);
        mStock.setOnTouchListener(this);
        mMinQuantity.setOnTouchListener(this);

        mCheck.setOnClickListener(this);
        mViewTitle.setOnClickListener(this);
        mBackArrow.setOnClickListener(this);
        mEditTitle.addTextChangedListener(this);
        mbtnBuy.setOnClickListener(this);
        mbtnSell.setOnClickListener(this);
    }

    private boolean getIncomingIntent(){
        if(getIntent().hasExtra("selected_note")){
            mNoteInitial = getIntent().getParcelableExtra("selected_note");

            mNoteFinal = new Note();
            mNoteFinal.setTitle(mNoteInitial.getTitle());
            mNoteFinal.setContent(mNoteInitial.getContent());
            mNoteFinal.setTimestamp(mNoteInitial.getTimestamp());
            mNoteFinal.setId(mNoteInitial.getId());
            mNoteFinal.setQuantity(mNoteInitial.getQuantity());
            mNoteFinal.setMinimum(mNoteInitial.getMinimum());

            mMode = EDIT_MODE_ENABLED;
            mIsNewNote = false;
            return false;
        }
        mMode = EDIT_MODE_ENABLED;
        mIsNewNote = true;
        return true;
    }

    private void disableContentInteraction(){
        mLinedEditText.setKeyListener(null);
        mLinedEditText.setFocusable(false);
        mLinedEditText.setFocusableInTouchMode(false);
        mLinedEditText.setCursorVisible(false);
        mLinedEditText.clearFocus();

        mStock.setKeyListener(null);
        mStock.setFocusable(false);
        mStock.setFocusableInTouchMode(false);
        mStock.setCursorVisible(false);
        mStock.clearFocus();

        mMinQuantity.setKeyListener(null);
        mMinQuantity.setFocusable(false);
        mMinQuantity.setFocusableInTouchMode(false);
        mMinQuantity.setCursorVisible(false);
        mMinQuantity.clearFocus();

    }

    private void enableContentInteraction(){
        mLinedEditText.setKeyListener(new EditText(this).getKeyListener());
        mLinedEditText.setFocusable(true);
        mLinedEditText.setFocusableInTouchMode(true);
        mLinedEditText.setCursorVisible(true);


       mStock.setKeyListener(new EditText(this).getKeyListener());
        mStock.setFocusable(true);
        mStock.setFocusableInTouchMode(true);
        mStock.setCursorVisible(true);

        mMinQuantity.setKeyListener(new EditText(this).getKeyListener());
        mMinQuantity.setFocusable(true);
        mMinQuantity.setFocusableInTouchMode(true);
        mMinQuantity.setCursorVisible(true);

    }

    private void enableEditMode(){
        mBackArrowContainer.setVisibility(View.GONE);
        mCheckContainer.setVisibility(View.VISIBLE);

        mItems.setVisibility(View.GONE);
        mButtons.setVisibility(View.GONE);

        mViewTitle.setVisibility(View.GONE);
        mEditTitle.setVisibility(View.VISIBLE);

        mMode = EDIT_MODE_ENABLED;

        enableContentInteraction();
    }

    private void disableEditMode(){
        Log.d(TAG, "disableEditMode: called.");
        mBackArrowContainer.setVisibility(View.VISIBLE);
        mCheckContainer.setVisibility(View.GONE);

        mItems.setVisibility(View.VISIBLE);
        mButtons.setVisibility(View.VISIBLE);

        mViewTitle.setVisibility(View.VISIBLE);
        mEditTitle.setVisibility(View.GONE);

        mMode = EDIT_MODE_DISABLED;

        disableContentInteraction();

        // Check if they typed anything into the note. Don't want to save an empty note.
        String temp = mLinedEditText.getText().toString();
        temp = temp.replace("\n", "");
        temp = temp.replace(" ", "");
        if(temp.length() > 0){
            mNoteFinal.setTitle(mEditTitle.getText().toString());
            mNoteFinal.setContent(mLinedEditText.getText().toString());
            String timestamp = Utility.getCurrentTimeStamp();
            mNoteFinal.setTimestamp(timestamp);
            mNoteFinal.setMinimum(Integer.parseInt(mMinQuantity.getText().toString()));
            mNoteFinal.setQuantity(Integer.parseInt(mStock.getText().toString()));

            Log.d(TAG, "disableEditMode: initial: " + mNoteInitial.toString());
            Log.d(TAG, "disableEditMode: final: " + mNoteFinal.toString());

            // If the note was altered, save it.
            if(!(mNoteFinal.getQuantity()==mNoteInitial.getQuantity())
                    || !(mNoteFinal.getMinimum()==mNoteInitial.getMinimum())
                    ||!mNoteFinal.getTitle().equals(mNoteInitial.getQuantity())
                    ||!mNoteFinal.getContent().equals(mNoteInitial.getContent())){
                Log.d(TAG, "disableEditMode: called?");
                saveChanges();
            }
        }
    }

    private void setNewNoteProperties(){
        mViewTitle.setText("Item Type");
        mEditTitle.setText("Item Type");

        mNoteFinal = new Note();
        mNoteInitial = new Note();
        mNoteInitial.setTitle("Item Type");
    }

    private void setNoteProperties(){
        mViewTitle.setText(mNoteInitial.getTitle());
        mEditTitle.setText(mNoteInitial.getTitle());
        mLinedEditText.setText(mNoteInitial.getContent());
        mStock.setText(String.valueOf(mNoteInitial.getQuantity()));
        mMinQuantity.setText(String.valueOf(mNoteInitial.getMinimum()));
        mChangeStock.setText("1");
        mItems.setText(mStock.getText().toString());

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return mGestureDetector.onTouchEvent(motionEvent);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        Log.d(TAG, "onDoubleTap: double tapped.");
        enableEditMode();
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.toolbar_back_arrow:{
                if(!mIsNewNote)
                    disableEditMode();
                finish();
                break;
            }
            case R.id.toolbar_check:{
                disableEditMode();
                break;
            }
            case R.id.note_text_title:{
                enableEditMode();
                mEditTitle.requestFocus();
                mEditTitle.setSelection(mEditTitle.length());
                break;
            }
            case R.id.btnbuy:{
                mItems.setTextColor(Color.GREEN);
                int current= Integer.parseInt(mStock.getText().toString());
                int newst= Integer.parseInt(mChangeStock.getText().toString());

                mStock.setText(String.valueOf((current+newst)));
                mItems.setText(mStock.getText().toString());
                break;
            }
            case R.id.btnsell:{
                int current= Integer.parseInt(mStock.getText().toString());
                int newst= Integer.parseInt(mChangeStock.getText().toString());
                mItems.setTextColor(getColor(R.color.colorPrimary));
                if(current>newst){
                mStock.setText(String.valueOf((current-newst)));
                mItems.setText(mStock.getText().toString());
                }
                else {
                    mStock.setText("0");
                mItems.setText(String.valueOf(current));
                }
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    @Override
    public void onBackPressed() {
        if(mMode == EDIT_MODE_ENABLED){
            onClick(mCheck);
        }
        else{
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
        if(mMode == EDIT_MODE_ENABLED){
            enableEditMode();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        mViewTitle.setText(charSequence.toString());



    }

    @Override
    public void afterTextChanged(Editable editable) {


    }
}



















