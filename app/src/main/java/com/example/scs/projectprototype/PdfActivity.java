package com.example.scs.projectprototype;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class PdfActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> patients;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        listView = (ListView) findViewById(R.id.pdf_list_view);
        patients = new ArrayList<String>();

        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(1000)
                .withHiddenFiles(true)
                .start();
    }

    public void parsePdf(String filePath){
        try{
            FileInputStream inputStream = new FileInputStream(filePath);
            String parsedText = "";
            PdfReader reader = new PdfReader(inputStream);
            int n = reader.getNumberOfPages();

            for (int i = 0; i < n; i++)
                parsedText = parsedText + PdfTextExtractor.getTextFromPage(reader, i + 1).trim() + "\n";

            String[] data = parsedText.split("\n");

            for(String d : data){
                patients.add(d);
            }

            reader.close();

        }catch (FileNotFoundException e){
            Toast.makeText(this, "File Not Found", Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            Toast.makeText(this, "IO Error reading input stream", Toast.LENGTH_SHORT).show();
        }

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,patients);
        listView.setAdapter(arrayAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            parsePdf(filePath);
        }
    }
}