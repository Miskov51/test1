package kmisiuk.qr_bileter;

/**
 * Created by kmisiuk on 2016-10-20.
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;


public class CSVreader extends AppCompatActivity {
    InputStream plikCSV;

    String[] liniaCSV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csv);
        plikCSV = getResources().openRawResource(R.raw.sample);


        String karta = Environment.getExternalStorageState(); //pobiera status karty
            if (Environment.MEDIA_MOUNTED.equals(karta)) {  //do sprawdzenia czy karta sd jest dostepna

                File folderCSV = new File("/sdcard/PlikiCSV");

                if (!folderCSV.exists()) {
                    folderCSV.mkdirs();
                    Log.d("udane", "zrobiono folder");
                } else
                    Log.d("error", "dir. already exists");

            }else
                Log.d("error", "Brak karty SD");

        //te dwa niżej powodują symulacje naciśnięcia przycisku odświerzania listy plików csf
        Button btn = (Button)findViewById(R.id.buttonRefreshCSVlist);
        btn.performClick();


    }

    public void csvBack(View v){
        startActivity(new Intent(CSVreader.this, sql_main_menu.class));
    }

    public void listaPlikow(View v){
        ListView list ;
        ArrayAdapter<String> adapter ;

            String path = Environment.getExternalStorageDirectory().toString()+"/PlikiCSV";
            File directory = new File(path);
            File[] files = directory.listFiles();

            ArrayList<String> liczbyT = new ArrayList<String>();
            if (files==null) {
                String karta = Environment.getExternalStorageState(); //pobiera aktualny status karty
                if (Environment.MEDIA_MOUNTED.equals(karta)) { //do sprawdzenia czy karta sd jest dostepna
                    liczbyT.add("Brak plików w folderze");   //todo zmienić to na zmienną do podmiany języka
                }
                    else
                        liczbyT.add("Brak karty pamięci");
            }else{
                for (int i = 0; i < files.length; i++) {
                    liczbyT.add(files[i].getName().toString()); //dodaje do listy samą nazwę pliku
                }

            }


        list = (ListView) findViewById(R.id.listaPlikow);

        adapter = new ArrayAdapter<String>(this, R.layout.row, liczbyT);

        list.setAdapter(adapter);
    }

    public void ladowanieCSV(View v){ //todo połączyć tą procedurę z listą plików aby ładował wybrany plik
    BufferedReader reader = new BufferedReader(new InputStreamReader(plikCSV));
        try {
            String csvLine;
            ArrayList<Long> listaWpisow = new ArrayList<Long>();
            int i=0;
            DBAdapter myDB; //tworzenie zmiennej do trzymania instancji
            myDB = new DBAdapter(this); //tworzenie instancji, this jest wymagane żeby odnosiło się do "tego frameworka" ale nie wiem dlaczego
            myDB.open();

            while ((csvLine = reader.readLine()) != null) { //dopuki jest coś w pliku

                liniaCSV=csvLine.split(";");
                listaWpisow.add(Long.parseLong(liniaCSV[1]));
            }
            try{

                myDB.insertALLrows(listaWpisow);

            }catch (Exception e){
                Log.e("Unknown fuck",e.toString());
            }
            myDB.close();
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }



    }
}
