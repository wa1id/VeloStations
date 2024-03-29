package ap.edu.velostations;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {

    private String jsonString;
    List stationNaam = new ArrayList<>();
    GeoPoint g;
    MySQLiteHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = new MySQLiteHelper(this);

        InputStream is = getResources().openRawResource(R.raw.velostation);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        jsonString = writer.toString();
        initList();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, stationNaam);
        setListAdapter(adapter);

        doesDatabaseExist(this, "velostations.db");

    }

    private static boolean doesDatabaseExist(Context context, String dbName) {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);

        try {
            JSONArray array = new JSONArray(jsonString);
            JSONObject obj = array.getJSONObject(position);
            g = new GeoPoint(obj.getDouble("point_lat"), obj.getDouble("point_lng"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent mapIntent = new Intent(this, MapActivity.class);
        mapIntent.putExtra("lat", g.getLatitude());
        mapIntent.putExtra("lng", g.getLongitude());
        startActivity(mapIntent);
    }

    private void initList(){

        try {

            JSONArray array = new JSONArray(jsonString);


            for(int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String naam = obj.optString("naam");
                stationNaam.add(i, naam);
                helper.addStation(i, naam); //naar sqlite
            }


        } catch (Throwable t) {
            Log.e("mytag", "Could not parse malformed JSON: \"" + jsonString + "\"");
        }
    }
}
