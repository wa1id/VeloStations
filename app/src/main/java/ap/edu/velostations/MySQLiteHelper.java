package ap.edu.velostations;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "velostations.db";
    private static final String TABLE_STATIONS = "stations";
    private static final int DATABASE_VERSION = 5;

    public MySQLiteHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STATIONS_TABLE = "CREATE TABLE " + TABLE_STATIONS + "(_id INTEGER PRIMARY KEY, objectId TEXT)";
        db.execSQL(CREATE_STATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATIONS);
        onCreate(db);
    }

    public void addStation(int id, String stationNaam) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("station", stationNaam);

        db.insert(TABLE_STATIONS, null, values);
        db.close();
    }
}
