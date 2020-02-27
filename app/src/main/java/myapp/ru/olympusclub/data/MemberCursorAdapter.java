package myapp.ru.olympusclub.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import myapp.ru.olympusclub.R;
import myapp.ru.olympusclub.data.ClubOlympusContract.MemberEntry;

public class MemberCursorAdapter extends CursorAdapter {

    public MemberCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.member_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView fn = (TextView) view.findViewById(R.id.first_name_field);
        TextView ln = (TextView) view.findViewById(R.id.last_name_field);
        TextView gen = (TextView) view.findViewById(R.id.gender_field);
        TextView sp = (TextView) view.findViewById(R.id.sport_field);

        String fistName = cursor.getString(cursor.getColumnIndexOrThrow(MemberEntry.COLUMN_FIRST_NAME));
        String lastName = cursor.getString(cursor.getColumnIndexOrThrow(MemberEntry.COLUMN_LAST_NAME));
        String sport = cursor.getString(cursor.getColumnIndexOrThrow(MemberEntry.COLUMN_SPORT));
        String gender = null;
        if (cursor.getInt(cursor.getColumnIndexOrThrow(MemberEntry.COLUMN_GENDER)) == 0) {
            gender = "unknown";
        } else if (cursor.getInt(cursor.getColumnIndexOrThrow(MemberEntry.COLUMN_GENDER)) == 1) {
            gender = "male";
        } else if (cursor.getInt(cursor.getColumnIndexOrThrow(MemberEntry.COLUMN_GENDER)) == 2) {
            gender = "female";
        }

        fn.setText(fistName);
        ln.setText(lastName);
        gen.setText(gender);
        sp.setText(sport);
    }
}
