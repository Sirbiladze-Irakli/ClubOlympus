package myapp.ru.olympusclub.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static myapp.ru.olympusclub.data.ClubOlympusContract.*;

public class OlympusContentProvider extends ContentProvider {
    OlympusDataBaseHelper dataBaseHelper;

    private static final int MEMBERS = 111;
    private static final int MEMBER_ID = 222;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, PATH_MEMBERS, MEMBERS);
        uriMatcher.addURI(AUTHORITY, PATH_MEMBERS + "/#", MEMBER_ID);
    }


    @Override
    public boolean onCreate() {
        dataBaseHelper = new OlympusDataBaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor;

        int match = uriMatcher.match(uri);

        switch (match) {
            case MEMBERS:
                cursor = db.query(MemberEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case MEMBER_ID:
                selection = MemberEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(MemberEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Can't query incorrect URI " + uri);
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);
        switch (match) {
            case MEMBERS:
                long id = db.insert(MemberEntry.TABLE_NAME, null, values);
                if (id == -1) {
                    Log.i("insertMethod" , "Insertion of data in the table failed for " + uri);
                    return null;
                }
                return ContentUris.withAppendedId(uri, id);
            default:
                throw new IllegalArgumentException("Can't query incorrect URI " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);

        switch (match) {
            case MEMBERS:
                return db.update(MemberEntry.TABLE_NAME, values, selection, selectionArgs);
            case MEMBER_ID:
                selection = MemberEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return db.update(MemberEntry.TABLE_NAME, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Can't update URI " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);
        switch (match) {
            case MEMBERS:
                return db.delete(MemberEntry.TABLE_NAME, selection, selectionArgs);
            case MEMBER_ID:
                selection = MemberEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return db.delete(MemberEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Can't delete this URI " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);

        switch (match) {
            case MEMBERS:
                return MemberEntry.MULTIPLE_ITEMS;
            case MEMBER_ID:
                return MemberEntry.SINGLE_ITEMS;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }
}
