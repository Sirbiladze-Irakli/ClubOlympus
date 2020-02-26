package myapp.ru.olympusclub.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static myapp.ru.olympusclub.data.ClubOlympusContract.*;
import static myapp.ru.olympusclub.data.ClubOlympusContract.MemberEntry;

public class OlympusDataBaseHelper extends SQLiteOpenHelper {

    public OlympusDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_MEMBERS = "CREATE TABLE " + MemberEntry.TABLE_NAME + "(" +
                MemberEntry._ID + " INTEGER PRIMARY KEY," +
                MemberEntry.COLUMN_FIRST_NAME + " TEXT," +
                MemberEntry.COLUMN_LAST_NAME + " TEXT," +
                MemberEntry.COLUMN_GENDER + " INTEGER NOT NULL," +
                MemberEntry.COLUMN_SPORT + " TEXT" + ")";
        db.execSQL(CREATE_TABLE_MEMBERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }
}
