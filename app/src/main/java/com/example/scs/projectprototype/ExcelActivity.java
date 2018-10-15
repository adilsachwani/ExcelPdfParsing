package com.example.scs.projectprototype;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ExcelActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> patients;
    ArrayAdapter<String> arrayAdapter;
    String cellData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excel);

        listView = (ListView) findViewById(R.id.excel_list_view);
        patients = new ArrayList<String>();

        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(1000)
                .withHiddenFiles(true)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            parseExcel(filePath);

        }
    }

    private String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
        String value = "";
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    value = ""+cellValue.getBooleanValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    double numericValue = cellValue.getNumberValue();
                    if(HSSFDateUtil.isCellDateFormatted(cell)) {
                        double date = cellValue.getNumberValue();
                        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
                        value = formatter.format(HSSFDateUtil.getJavaDate(date));
                    } else {
                        value = ""+numericValue;
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    value = ""+cellValue.getStringValue();
                    break;
                default:
            }
        } catch (NullPointerException e) {

            Toast.makeText(this, "Null Pointer Exception in getCellAsString", Toast.LENGTH_SHORT).show();
        }
        return value;
    }

    public void parseExcel(String filePath){
        try{
            FileInputStream inputStream = new FileInputStream(filePath);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();
            int colCount;
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

            for(int r=0 ;r < rowCount; r++){
                Row row = sheet.getRow(r);
                colCount = row.getPhysicalNumberOfCells();
                cellData = "";

                for(int c=0; c < colCount; c++){
                    cellData  = cellData + getCellAsString(row,c,formulaEvaluator) + "  ";
                }

                patients.add(cellData);
            }

        }catch (FileNotFoundException e){
            Toast.makeText(this, "File Not Found", Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            Toast.makeText(this, "IO Error reading input stream", Toast.LENGTH_SHORT).show();
        }

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,patients);
        listView.setAdapter(arrayAdapter);
    }

}