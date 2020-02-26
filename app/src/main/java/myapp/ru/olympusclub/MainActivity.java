package myapp.ru.olympusclub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import myapp.ru.olympusclub.data.ClubOlympusContract.MemberEntry;


public class MainActivity extends AppCompatActivity {

    TextView dataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataTextView = findViewById(R.id.text_view);

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddMemberActivity.class);
                startActivity(intent);
            }
        });

    }

    private void displayData() {
        String[] projection = {
                MemberEntry._ID,
                MemberEntry.COLUMN_FIRST_NAME,
                MemberEntry.COLUMN_LAST_NAME,
                MemberEntry.COLUMN_GENDER,
                MemberEntry.COLUMN_SPORT
        };

        Cursor cursor = getContentResolver().query(
                MemberEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );

        dataTextView.setText("All members\n\n");
        dataTextView.append(
                MemberEntry._ID + " " +
                MemberEntry.COLUMN_FIRST_NAME + " " +
                MemberEntry.COLUMN_LAST_NAME + " " +
                MemberEntry.COLUMN_GENDER + " " +
                MemberEntry.COLUMN_SPORT);

        int idIndex = cursor.getColumnIndex(MemberEntry._ID);
        int firstNameIndex = cursor.getColumnIndex(MemberEntry.COLUMN_FIRST_NAME);
        int lastNameIndex = cursor.getColumnIndex(MemberEntry.COLUMN_LAST_NAME);
        int genderIndex = cursor.getColumnIndex(MemberEntry.COLUMN_GENDER);
        int sportIndex = cursor.getColumnIndex(MemberEntry.COLUMN_SPORT);

        while (cursor.moveToNext()) {
            int currentId = cursor.getInt(idIndex);
            String currentFirstName = cursor.getString(firstNameIndex);
            String currentLastName = cursor.getString(lastNameIndex);
            int currentGender = cursor.getInt(genderIndex);
            String currentSport = cursor.getString(sportIndex);

            dataTextView.append("\n" +
                    currentId + " " +
                    currentFirstName + " " +
                    currentLastName + " " +
                    currentGender + " " +
                    currentSport
                    );
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayData();
    }
}
