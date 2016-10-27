package kmisiuk.qr_bileter;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class SQL_manipulator extends AppCompatActivity {

    DBAdapter myDB; //tworzenie zmiennej do trzymania instancji
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sql_manipulator);
        openDB();
    }

    //otwieranie bazy danych
    private void openDB() {
        myDB = new DBAdapter(this); //tworzenie instancji, this jest wymagane żeby odnosiło się do tego frameworka ale nie wiem dlaczego
        myDB.open();

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        closeDB();
    }

    //zamykanie bazy danych
    private void closeDB() {
        myDB.close();
    }

    //wyswietalnie tekstu (info dla usera o wykonanej akcji)
    private void displayText(String message){
        TextView textView = (TextView) findViewById(R.id.textDisplay);
        textView.setText(message);

    }

    //dodaje pojedynczy wpis, ta metoda będzie zmieniona na ładującą dane z csv do bazy
    public void onclick_DodajWpis(View v){
        displayText("Dodano wpis");
        //long NewID = myDB.insertRow(1234);
        ArrayList <Long> testLista = new ArrayList<Long>();
        testLista.add(Long.valueOf(1111));
        testLista.add(Long.valueOf(2222));
        testLista.add(Long.valueOf(3333));
        myDB.insertALLrows(testLista);
    }

    //czyszczenie bazy
    public void onClick_WyczyscWszystko(View v){
        displayText("Wyczyszczono baze");
        myDB.deleteAll();
    }

    //wyświetla całą zawartość z bazy ładując uprzednio wszystkie wpisy do kursora
    public void onClick_WyswietlWpisy(View v){
        Cursor kursor = myDB.getAllRows();
        WyswietlWpisyBazy(kursor);
    }

    //wyświetla wpisy dla całej zawartości kursora bazy
    private void WyswietlWpisyBazy(Cursor kursor) {
        String tresc = "---dane z bazy ---\n";

        //przestawianie kursora na poczatek (o ile są dane bo inaczej nie będzie się dało stąd if)
        if(kursor.moveToFirst()){
            do {
                int id = kursor.getInt(0);
                String name = kursor.getString(1);
                String StudentNumber = kursor.getString(2);

                //składanie odczytanych danych do kupy
                tresc += "ID=" + id + "   QR=" + name + "   Aktywacja=" + StudentNumber +"\n";
            }while(kursor.moveToNext()); //wyjdzie z pętli gdy kursor nie ma możliwości przejścia dalej
        }
        kursor.close(); //podobno trzeba zamykać kursor po użyciu inaczej jest "resource leak"
        displayText(tresc);
    }


    //odpowiada za powrót do menu głównego
    public void manipulator_back(View v){
        startActivity(new Intent(SQL_manipulator.this, sql_main_menu.class));
    }
}
