package com.dr.vlad.memento.ocr;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dr.vlad.memento.R;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.util.ArrayList;

public class LearnActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = LearnActivity.class.getSimpleName();
    public static final int LEARNING_PHOTO_REQUEST = 1;
    protected static Boolean libraryLoaded = false;

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Cannot load OpenCv");
        } else {
            Log.d(TAG, "OpenCv loaded");
            libraryLoaded = true;
        }
    }

    private LinearLayout infoLayout;
    private RelativeLayout imageLayout;
    private Button takePhotoButton;
    private ImageButton prevSymbolButton;
    private ImageButton nextSymbolButton;
    private ImageView imageView;
    private EditText inputText;
    private ProgressBar progressBar;
    private String imagePath;

    private ArrayList<Symbol> mSymbols;
    private int index = 0;
    private int symbolsArraySize = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!libraryLoaded) {
            Toast.makeText(this, "Failed to load OpenCv", Toast.LENGTH_SHORT).show();
            finish();
        }
        setContentView(R.layout.activity_learn);

        Toolbar toolbar = (Toolbar) findViewById(R.id.learn_activity_toolbar);
        toolbar.setTitle(getResources().getString(R.string.title_learn));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        infoLayout = (LinearLayout) findViewById(R.id.learn_activity_info_ll);
        imageLayout = (RelativeLayout) findViewById(R.id.learn_activity_image_ll);
        progressBar = (ProgressBar) findViewById(R.id.learn_progress_bar);
        imageView = (ImageView) findViewById(R.id.learn_image_view);
        inputText = (EditText) findViewById(R.id.input_symbol_text);
        inputText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
        takePhotoButton = (Button) findViewById(R.id.learn_activity_take_photo_button);
        prevSymbolButton = (ImageButton) findViewById(R.id.prev_symbol_button);
        nextSymbolButton = (ImageButton) findViewById(R.id.next_symbol_button);
        takePhotoButton.setOnClickListener(this);
        prevSymbolButton.setOnClickListener(this);
        nextSymbolButton.setOnClickListener(this);
    }

    private void takePhotoTmp() {
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        imagePath = storageDirectory.getPath() + "/mmnt-o" + "/MMNTO_12072017_090503_885752426.jpg";
        loadImage();
    }

    private void loadImage() {
        SymbolsGeneratorTask generatorTask = new SymbolsGeneratorTask();
        generatorTask.setImagePath(imagePath)
                .setListener(new OnSymbolsGeneratedListener() {
                    @Override
                    public void onSymbolsGenerated(ArrayList<Symbol> symbols) {
                        mSymbols = symbols;
                        symbolsArraySize = mSymbols.size();
                        showKeyboard(true);
                        showSymbol(index);
                        updateButtons();
                    }
                })
                .execute();
    }

    private void showKeyboard(boolean show) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (show) {
            inputText.requestFocus();
            inputMethodManager.showSoftInput(inputText, InputMethodManager.SHOW_IMPLICIT);
        } else {
            inputMethodManager.hideSoftInputFromWindow(inputText.getWindowToken(), 0);
        }
    }

    private void showSymbol(int index) {
        Symbol symbol = mSymbols.get(index);
        imageView.setImageBitmap(symbol.toBitmap());

        if (symbolHasData()) {
            inputText.setText(symbol.getCharacter());
        } else {
            clearInput();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.learn_activity_take_photo_button:
//                takePhoto();
                takePhotoTmp();
                break;
            case R.id.prev_symbol_button:
                prevSymbol();
                break;

            case R.id.next_symbol_button:
                nextSymbol();
                break;
        }
    }


    private void nextSymbol() {
        updateCurrentSymbolData();

        if (!symbolHasData()) {
            Toast.makeText(this, "Please insert symbol", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isLastSymbol()) {
            finishTraining();
            Toast.makeText(this, "Training done...", Toast.LENGTH_SHORT).show();
        }

        showNextSymbol();
        updateButtons();


    }

    private void prevSymbol() {
        showPrevSymbol();
    }

    private void showNextSymbol() {
        int tmpIndex = index + 1;
        if (!isIndexValid(tmpIndex)) {
            return;
        }

        showSymbol(++index);

    }

    private void showPrevSymbol() {
        int tmpIndex = index - 1;
        if (!isIndexValid(tmpIndex)) {
            return;
        }

        showSymbol(--index);
    }

    private void updateButtons() {
        if (isLastSymbol()) {
            nextSymbolButton.setImageResource(R.drawable.ic_action_done);
        } else {
            nextSymbolButton.setImageResource(R.drawable.ic_action_next);
        }

        if (isFirstIndex()) {
            prevSymbolButton.setClickable(false);
        } else {
            prevSymbolButton.setClickable(true);
        }

    }

    private boolean isIndexValid(int validIndex) {
        return (validIndex > -1 && validIndex < symbolsArraySize);
    }

    private boolean symbolHasData() {
        if (null == mSymbols.get(index).getIntSymbol()) {
            return false;
        }
        return true;
    }

    private boolean updateCurrentSymbolData() {
        final String input = getInput();
        if (input.isEmpty()) {
            return false;
        }
        final int inputChar = (int) input.charAt(0);
        setSymbolData(inputChar, index);
        return true;
    }

    private void setSymbolData(int data, int index) {
        mSymbols.get(index).setIntSymbol(data);
    }

    private String getInput() {
        return inputText.getText().toString();
    }

    private void clearInput() {
        inputText.setText("");
    }

    private boolean isLastSymbol() {
        return symbolsArraySize - 1 == index;
    }

    private boolean isFirstIndex() {
        return index == 0;
    }

    private void finishTraining() {
       /*
       @TODO: 1. create classifier
       @TODO: 2. delete image file
       */

        Classifier classifier = new Classifier();
        classifier.setSymbols(mSymbols);

    }


    public interface OnSymbolsGeneratedListener {
        void onSymbolsGenerated(ArrayList<Symbol> symbols);
    }

    protected class SymbolsGeneratorTask extends AsyncTask<String, Void, Integer> {

        private String imagePath;
        private ArrayList<Symbol> mSymbols;
        private OnSymbolsGeneratedListener listener;

        public SymbolsGeneratorTask() {
        }

        public SymbolsGeneratorTask setImagePath(String path) {
            this.imagePath = path;
            return this;
        }

        public SymbolsGeneratorTask setListener(OnSymbolsGeneratedListener listener) {
            this.listener = listener;
            return this;
        }

        @Override
        protected Integer doInBackground(String... params) {
            Bitmap bmp = BitmapFactory.decodeFile(imagePath);
            ImageUtils imageUtils = new ImageUtils(bmp);
            mSymbols = imageUtils.getSymbols();

            return mSymbols.size();
        }

        @Override
        protected void onPreExecute() {
            infoLayout.setVisibility(View.GONE);
            imageLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            listener.onSymbolsGenerated(mSymbols);
            progressBar.setVisibility(View.GONE);
            imageLayout.setVisibility(View.VISIBLE);
        }
    }


}
