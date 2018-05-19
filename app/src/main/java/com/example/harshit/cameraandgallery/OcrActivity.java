package com.example.harshit.cameraandgallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.w3c.dom.Text;

import java.io.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Harshit on 15/3/18.
 */

public class OcrActivity extends Activity {
    private static final String LOG_TAG = "OcrActivity";
//    private RecyclerView rvGallery;

    public static void start(Context context) {
        Intent i = new Intent(context, OcrActivity.class);
        context.startActivity(i);
    }
    Bitmap image;

    private ImageView imageView;
//    Bitmap image = (Bitmap) i.getParcelableExtra("BitmapImage");


    private TessBaseAPI mTess;
    String datapath = "";

    Object language;
    String lang="ben";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocr_layout);
        if(getIntent().hasExtra("byteArray")) {
            ImageView previewThumbnail = new ImageView(this);
            image = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("byteArray"),0,getIntent().getByteArrayExtra("byteArray").length);
            previewThumbnail.setImageBitmap(image);
          }

    //    byte[] byteArray = getIntent().getByteArrayExtra("image");
    //    image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(image);
        //get the spinner from the xml.
        Spinner dropdown = findViewById(R.id.spinner1);
        //create a list of items for the spinner.
        String[] items = new String[]{"Choose Language", "Bengali", "English", "Gujarati", "Hindi", "Kannada", "Tamil"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                language = parent.getItemAtPosition(pos);
                TextView L = (TextView) findViewById(R.id.spinLang);

                //for deciding tessdata file
                if(language.toString().equals("Bengali"))
                    lang="ben";
                if(language.toString().equals("English"))
                    lang="eng";
                if(language.toString().equals("Gujarati"))
                    lang="guj";
                if(language.toString().equals("Hindi"))
                    lang="hin";
                if(language.toString().equals("Kannada"))
                    lang="kan";
                if(language.toString().equals("Tamil"))
                    lang="tam";
                L.setText("You selected " + language);

                //initialize Tesseract API
                datapath = getFilesDir()+ "/tesseract/";
                mTess = new TessBaseAPI();

                checkFile(new File(datapath + "tessdata/"));

                mTess.init(datapath, lang, TessBaseAPI.OEM_TESSERACT_ONLY);

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public void processImage(View view){
        String OCRresult = null;
        mTess.setImage(image);
        OCRresult = mTess.getUTF8Text();
        EditText OCRTextView = (EditText)findViewById(R.id.OCRTextView);
        OCRTextView.setText(OCRresult);
    }

    private void checkFile(File dir) {
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles();
        }
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/"+lang+".traineddata";
            File datafile = new File(datafilepath);

            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    private void copyFiles() {
        try {
            String filepath = datapath + "/tessdata/"+lang+".traineddata";
            AssetManager assetManager = getAssets();

            InputStream instream = assetManager.open("tessdata/"+lang+".traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }


            outstream.flush();
            outstream.close();
            instream.close();

            File file = new File(filepath);
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
