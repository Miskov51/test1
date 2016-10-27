package kmisiuk.qr_bileter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class sql_main_menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sql_main_menu);
    }

        //// TODO: 2016-10-26  poukładać to w kolejności tak jak jest w menu faktycznie
    public void sqlBack(View v){
        startActivity(new Intent(sql_main_menu.this, MainActivity.class));
    }

    public void sqlManipulator(View v){
        startActivity(new Intent(sql_main_menu.this, SQL_manipulator.class));
    }

    public void CSVmanipulator(View v){
        startActivity(new Intent(sql_main_menu.this, CSVreader.class));
    }

    public void sqlPreview(View v){
        startActivity(new Intent(sql_main_menu.this, TablesPreview.class));
    }

}
