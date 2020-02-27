package myapp.ru.olympusclub.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.lang.reflect.Member;

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
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        String firstName = values.getAsString(MemberEntry.COLUMN_FIRST_NAME);
        if (firstName.equals("")) {
            throw new IllegalArgumentException("You have to input first name");
        }
        String lastName = values.getAsString(MemberEntry.COLUMN_LAST_NAME);
        if (lastName.equals("")) {
            throw new IllegalArgumentException("You have to input last name");
        }
        Integer gender = values.getAsInteger(MemberEntry.COLUMN_GENDER);
        if (gender == null || !(gender == MemberEntry.UNKNOWN || gender == MemberEntry.MALE
                || gender == MemberEntry.FEMALE)) {
            throw new IllegalArgumentException("You have to input gender");
        }
        String sport = values.getAsString(MemberEntry.COLUMN_SPORT);
        if (sport.equals("")) {
            throw new IllegalArgumentException("You have to input sport");
        }

        int match = uriMatcher.match(uri);
        switch (match) {
            case MEMBERS:
                long id = db.insert(MemberEntry.TABLE_NAME, null, values);
                if (id == -1) {
                    Log.i("insertMethod" , "Insertion of data in the table failed for " + uri);
                    return null;
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            default:
                throw new IllegalArgumentException("Can't query incorrect URI " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        if (values.containsKey(MemberEntry.COLUMN_FIRST_NAME)) {
            String firstName = values.getAsString(MemberEntry.COLUMN_FIRST_NAME);
            if (firstName == null) {
                throw new IllegalArgumentException("You have to input first name");
            }
        }
        if (values.containsKey(MemberEntry.COLUMN_LAST_NAME)) {
            String lastName = values.getAsString(MemberEntry.COLUMN_LAST_NAME);
            if (lastName == null) {
                throw new IllegalArgumentException("You have to input last name");
            }
        }
        if (values.containsKey(MemberEntry.COLUMN_GENDER)) {
            Integer gender = values.getAsInteger(MemberEntry.COLUMN_GENDER);
            if (gender == null || !(gender == MemberEntry.UNKNOWN || gender == MemberEntry.MALE
                    || gender == MemberEntry.FEMALE)) {
                throw new IllegalArgumentException("You have to input gender");
            }
        }
        if (values.containsKey(MemberEntry.COLUMN_SPORT)) {
            String sport = values.getAsString(MemberEntry.COLUMN_SPORT);
            if (sport == null) {
                throw new IllegalArgumentException("You have to input sport");
            }
        }

        int match = uriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MEMBERS:
                rowsUpdated = db.update(MemberEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MEMBER_ID:
                selection = MemberEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = db.update(MemberEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Can't update URI " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case MEMBERS:
                rowsDeleted = db.delete(MemberEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MEMBER_ID:
                selection = MemberEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(MemberEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Can't delete this URI " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
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
