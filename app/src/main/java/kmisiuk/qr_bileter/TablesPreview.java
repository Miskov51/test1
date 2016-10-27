package kmisiuk.qr_bileter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by kmisiuk on 2016-10-26.
 */

public class TablesPreview extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sql_preview);
    }

    public void listaTablic(View v){
        DBAdapter myDB; //tworzenie zmiennej do trzymania instancji
        myDB = new DBAdapter(this); //tworzenie instancji, this jest wymagane żeby odnosiło się do "tego frameworka" ale nie wiem dlaczego
        myDB.open();
        myDB.listaTablicSQL();
        myDB.close();

    }

    public void tableBack(View v){
        startActivity(new Intent(TablesPreview.this, sql_main_menu.class));
    }
}
