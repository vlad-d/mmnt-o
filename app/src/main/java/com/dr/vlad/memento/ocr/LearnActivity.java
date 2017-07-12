package com.dr.vlad.memento.ocr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dr.vlad.memento.CameraIntentActivity;
import com.dr.vlad.memento.R;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.MatOfPoint;

import java.util.ArrayList;

public class LearnActivity extends AppCompatActivity {

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
    private LinearLayout imageLayout;
    private Button takePhotoButton;
    private ImageView imageView;
    private EditText inputText;
    private String imagePath;

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
        imageLayout = (LinearLayout) findViewById(R.id.learn_activity_image_ll);
        imageView = (ImageView) findViewById(R.id.learn_image_view);
        inputText = (EditText) findViewById(R.id.input_text);
        takePhotoButton = (Button) findViewById(R.id.learn_activity_take_photo_button);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
    }


    private void takePhoto() {
        Intent takePhotoIntent = new Intent(LearnActivity.this, CameraIntentActivity.class);
        takePhotoIntent.putExtra(CameraIntentActivity.ACTIVITY_KEY, CameraIntentActivity.CODE_ACTIVITY_LEARN);
        startActivityForResult(takePhotoIntent, LEARNING_PHOTO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LEARNING_PHOTO_REQUEST) {
            if (resultCode == RESULT_OK) {
                data.getData();
                imagePath = data.getStringExtra(CameraIntentActivity.BUNDLE_KEY);
                loadImage();
            }
        }
    }

    private void loadImage() {
        imageView.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bmp = BitmapFactory.decodeFile(imagePath);
                ImageUtils imageUtils = new ImageUtils(bmp);
//                ArrayList<MatOfPoint> contours = imageUtils.getContours();
//                imageView.setImageBitmap(imageUtils.getImageFromContour(contours.get(0)));
                infoLayout.setVisibility(View.GONE);
                imageLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        infoLayout.setVisibility(View.VISIBLE);
        imageLayout.setVisibility(View.GONE);
    }
}
