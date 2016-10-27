package kmisiuk.qr_bileter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;


import com.google.zxing.Result;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;



import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by kmisiuk on 2016-10-15.
 */

public class MainActivity extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //otwieranie widoku od razu przy starcie

    }

    public void startQRscan(View v) {
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    public void sqlSettings(View v) {
        startActivity(new Intent(MainActivity.this, sql_main_menu.class));

    }

    public void sqlManipulator(View v) {
        startActivity(new Intent(MainActivity.this, SQL_manipulator.class));

    }

    public void QuitApp(View v) {
        //EditText mEdit;
        //mEdit   = (EditText)findViewById(R.id.editText);
        //dodajWpis(Long.parseLong(mEdit.getText().toString()));
        //this.finishAffinity();   //to gówno działa tylko dla api 15
        ActivityCompat.finishAffinity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
            mScannerView.stopCamera(); //zatrzymanie kamery, jest zakomentowane bo psuje przełącznie activity TODO spróbować dodać TRY
        }catch (Exception e){
            Log.e("Unknown fuck",e.toString());
        }

    }

    @Override
    public void handleResult(Result result) {
        Log.w("handleResult", result.getText());
        aktywujKod(Long.parseLong(result.getText()));

        mScannerView.stopCamera();
        setContentView(R.layout.activity_main);

        //powrót do skanowania
        //mScannerView.resumeCameraPreview(this); //zakomentowane aby skan wykonał się raz
    }


    public void dodajWpis(Long QRcode){
        DBAdapter myDB; //tworzenie zmiennej do trzymania instancji
        myDB = new DBAdapter(this); //tworzenie instancji, this jest wymagane żeby odnosiło się do "tego frameworka" ale nie wiem dlaczego
        myDB.open();
        long NewID = myDB.insertRow(QRcode);
        myDB.close();
    }

    // dodaje date dla odpowiedniego numeru QR lub wywala alert jeżeli data już jest lub nr biletu nie istnieje
    public void aktywujKod(Long QRcode){
        DBAdapter myDB; //tworzenie zmiennej do trzymania instancji
        myDB = new DBAdapter(this); //tworzenie instancji,  this jest wymagane żeby odnosiło się do tego frameworka ale nie wiem dlaczego
        myDB.open();
        DateFormat df = new SimpleDateFormat(" d.MM.yyyy, HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());


        AlertDialog.Builder bulider; //deklaracja dla popupu

       if (myDB.findQR(QRcode)>0){

           if (myDB.checkAktivation(QRcode).length()>0){
               bulider = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStary));
               bulider.setTitle("Bilet był już użyty! ");
               bulider.setMessage("Data: "+myDB.checkAktivation(QRcode));

           }
           else{
               myDB.updateRow(QRcode,date);  // TODO: 2016-10-18 Odkomentować to po zrobieniu testów aby aktywowało daty biletów
               bulider = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogAktywacja));
               bulider.setTitle("Aktywowano bilet: ");
               bulider.setMessage(QRcode.toString());
           }
        }
        else {
           bulider = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogBrak));
           bulider.setTitle("Brak takiego numeru biletu!");
       }

       AlertDialog alertDialog = bulider.create();
       alertDialog.show();

        myDB.close();

    }
}
