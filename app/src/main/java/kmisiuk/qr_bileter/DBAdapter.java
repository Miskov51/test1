package kmisiuk.qr_bileter;

/**
 * Created by kmisiuk on 2016-10-15.
 */

// ------------------------------------ DBADapter.java ---------------------------------------------


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;


// Search for "TODO", and make the appropriate changes.
public class DBAdapter {

    /////////////////////////////////////////////////////////////////////
    //	Deklaracje
    /////////////////////////////////////////////////////////////////////


    // definiowanie nazw dla kolumn
    public static final String KEY_ID = "_id";
    public static final String KEY_QR = "qr_code";
    public static final String KEY_ACT_TIME = "activation_time";

    // na razie zbędne ale moze z tego skorzystam - odwołania po numerach kolumn (0 = KEY_ID, 1=...)
    public static final int COL_KEY_ID = 0;
    public static final int COL_KEY_QR = 1;
    public static final int COL_KEY_ACT_TIME = 2;

    //deklaracja tablicy zawierającej nazwy wszystkich kolumn
    public static final String[] ALL_KEYS = new String[] {KEY_ID, KEY_QR, KEY_ACT_TIME};

    // deklaracja bazy danych oraz jej tablicy.
    public static final String DATABASE_NAME = "MyDb";
    public static final String DATABASE_TABLE = "mainTable";

    // wersjonowanie bazy danych
    public static final int DATABASE_VERSION = 7;


    // Tworzenie tablicy
    private static final String DATABASE_CREATE_SQL =
            "create table " + DATABASE_TABLE
                    + " (" + KEY_ID + " integer primary key autoincrement, "
                    + KEY_QR + " text not null, "
                    + KEY_ACT_TIME + " text default ''"
                    + ");";

    // Context of application who uses us. NI chu chu nie wiem co to TODO:  sprawdzić działanie
    private final Context context;

    private DatabaseHelper myDBHelper; //zmienna do obsługi działań na bazie (kod na końcu pliku w databaseHelper)
    private SQLiteDatabase db;




    /////////////////////////////////////////////////////////////////////
    //	Metody publiczne
    /////////////////////////////////////////////////////////////////////

    public DBAdapter(Context ctx) {   //// TODO: 2016-10-19 sprawdzić co to robi
        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);
    }

    // otwieranie połączenia z bazą
    public DBAdapter open() {
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    // Zamykanie połączenia z bazą
    public void close() {
        myDBHelper.close();
    }

    // Dodawanie nowych wpisów do bazy
    public long insertRow(long qr_code) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_QR, qr_code);

        // Insert it into the database.
        return db.insert(DATABASE_TABLE, null, initialValues);
    }


    //szybki insert wielu wpisów przy użyciu jednej tranzakcji
    public void insertALLrows(ArrayList <Long> listaQR) {
        String sql = "INSERT INTO "+ DATABASE_TABLE +" ("+KEY_QR+") VALUES (?);";
        SQLiteStatement statement = db.compileStatement(sql);
        db.beginTransaction();
        for (int i = 0; i<listaQR.size(); i++) {
            statement.clearBindings();
            statement.bindLong(1, listaQR.get(i));

            statement.execute();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }


    // Skasuj wpis o odpowiednim ID (primary key)
    public boolean deleteRow(long rowId) {
        String where = KEY_ID + "=" + rowId;
        return db.delete(DATABASE_TABLE, where, null) != 0;
    }

    //czyszczenie wszystkich wpisów
    public void deleteAll() {
        db.delete(DATABASE_TABLE, null,null);
        //bajer niżej resetuje autoinkrementowane ID do wartości 0;
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + DATABASE_TABLE + "'");
    }

    // Pobierz wszystkie wpisy
    public Cursor getAllRows() {
        String where = null;
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // pobierz konkretny wpis o odpowiednim ID (primary key)
    public Cursor getRow(long rowId) {   //TODO ta metode prawdopodobnie można wywalić, raczej się nie przyda
        String where = KEY_ID + "=" + rowId;
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }


    //aktualizuj wpis o odpowiednim id (numer biletu z KEY_QR)
    public boolean updateRow(long qr_code,String data) {
        String where = KEY_QR + "=" + qr_code;

        ContentValues newValues = new ContentValues();
        newValues.put(KEY_QR, qr_code);
        newValues.put(KEY_ACT_TIME, data);

        // Insert it into the database.
        return db.update(DATABASE_TABLE, newValues, where, null) != 0;
    }

    //znajdź numer biletu
    //@Return ID dla danego numeru biletu

    public long findQR(long qr_code) {
        String where = KEY_QR + "=" + qr_code;
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            return c.getInt(0);
        }
        else return -1;
    }

    //sprawdza czy bilet posiada jakąś date aktywacji (a konkretnie czy cokolwiek jest wpisane)
    //@Return zwraca wpis z pola daty aktywacji lub gdy jest puste to "Nie odnaleziono"
    public String checkAktivation(long qr_code) {
        String where = KEY_QR + "=" + qr_code;
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            return c.getString(2);
        }
        else return "Nie odnaleziono";
    }

    public void listaTablicSQL(){  //todo przerobić żeby zwracało listę tablic a nie robiło println 
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' and name<>'android_metadata' and name<>'sqlite_sequence'", null); //wycięte te dwie nazwy stałych tabel

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                System.out.println(c.getString(0));
                c.moveToNext();
            }
        }

    }


    /////////////////////////////////////////////////////////////////////
    //	Private Helper Classes:
    /////////////////////////////////////////////////////////////////////

    /**
     * Private class which handles database creation and upgrading.
     * Used to handle low-level database access.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DATABASE_CREATE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            Log.w("DBAdapter", "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

            // Recreate new database:
            onCreate(_db);
        }
    }
}
