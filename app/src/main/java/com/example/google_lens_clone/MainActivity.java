package com.example.google_lens_clone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // creating variables for our
    // image view, text view and two buttons.
    private ImageView img;
    private TextView textview;
    private Button snapBtn;
    private Button detectBtn;

    // variable for our image bitmap.
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // on below line we are initializing our variables.
        img = (ImageView) findViewById(R.id.image);
        textview = (TextView) findViewById(R.id.text);
        snapBtn = (Button) findViewById(R.id.snapbtn);
        detectBtn = (Button) findViewById(R.id.detectbtn);

        // adding on click listener for detect button.
        detectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calling a method to
                // detect a text .
                detectTxt();
            }
        });
        snapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calling a method to capture our image.
                dispatchTakePictureIntent();
            }
        });
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        // in the method we are displaying an intent to capture our image.
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // on below line we are calling a start activity
        // for result method to get the image captured.
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // calling on activity result method.
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // on below line we are getting
            // data from our bundles. .
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");

            // below line is to set the
            // image bitmap to our image.
            img.setImageBitmap(imageBitmap);
        }
    }

    private void detectTxt() {
        // this is a method to detect a text from image.
        // below line is to create variable for firebase
        // vision image and we are getting image from bitmap with rotation 0.
        InputImage image=InputImage.fromBitmap(imageBitmap,0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result=recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(@NonNull Text text) {
                StringBuilder result=new StringBuilder();
                //for loop for detecting the block of text
                for(Text.TextBlock block: text.getTextBlocks()){
                    String blockText=block.getText();
                    Point[] blockCornerPoint=block.getCornerPoints();
                    Rect blockFrame=block.getBoundingBox();
                    //for loop for recognizing line of text within the block
                    for(Text.Line line: block.getLines()){
                        String lineText=line.getText();
                        Point[] lineCornerPoint=block.getCornerPoints();
                        Rect lineRect=block.getBoundingBox();
                        //for loop for recognizing each text elements within the line of text
                        for(Text.Element element: line.getElements()){
                            String elementText=element.getText();
                            result.append(elementText);
                        }
                        textview.setText(blockText);
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failed to detect text from image", Toast.LENGTH_LONG).show();
            }
        });
    }
}