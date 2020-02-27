package myapp.ru.olympusclub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import static myapp.ru.olympusclub.data.ClubOlympusContract.*;

public class AddMemberActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText sportEditText;
    private Spinner genderSpinner;
    private Uri currentMemberUri;
    private int gender = MemberEntry.UNKNOWN;
    private ArrayAdapter spinnerAdapter;
    private static final int EDIT_MEMBER_LOADER = 333;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        Intent intent = getIntent();

        currentMemberUri = intent.getData();

        if (currentMemberUri == null) {
            setTitle("Add a Member");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit The Member");
            getSupportLoaderManager().initLoader(EDIT_MEMBER_LOADER, null, this);
        }

        firstNameEditText = findViewById(R.id.first_name);
        lastNameEditText = findViewById(R.id.last_name);
        sportEditText = findViewById(R.id.sport);
        genderSpinner = findViewById(R.id.gender);


        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(spinnerAdapter);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGender = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selectedGender)) {
                    if (selectedGender.equals("Male")) {
                        gender = MemberEntry.MALE;
                    } else if (selectedGender.equals("Female")) {
                        gender = MemberEntry.FEMALE;
                    } else {
                        gender = MemberEntry.UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gender = MemberEntry.UNKNOWN;
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (currentMemberUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_member);
            menuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_member_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_member:
                saveMember();
                return true;
            case R.id.delete_member:
                showDeleteMemberDialog();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveMember() {
        String firsName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String sport = sportEditText.getText().toString().trim();

        if (TextUtils.isEmpty(firsName)) {
            Toast.makeText(this, "Input the first name", Toast.LENGTH_LONG).show();
            return ;
        } else if (TextUtils.isEmpty(lastName)) {
            Toast.makeText(this, "Input the last name", Toast.LENGTH_LONG).show();
            return ;
        } else if (gender == MemberEntry.UNKNOWN) {
            Toast.makeText(this, "Choose the gender", Toast.LENGTH_LONG).show();
            return ;
        } else if (TextUtils.isEmpty(sport)) {
            Toast.makeText(this, "Input the sport", Toast.LENGTH_LONG).show();
            return ;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(MemberEntry.COLUMN_FIRST_NAME, firsName);
        contentValues.put(MemberEntry.COLUMN_LAST_NAME, lastName);
        contentValues.put(MemberEntry.COLUMN_SPORT, sport);
        contentValues.put(MemberEntry.COLUMN_GENDER, gender);

        if (currentMemberUri == null) {
            Uri uri = getContentResolver().insert(MemberEntry.CONTENT_URI, contentValues);

            if (uri == null) {
                Toast.makeText(this, "Insertion of data in the table failed", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Data saved", Toast.LENGTH_LONG).show();
            }
        } else {
            int rowsChanged = getContentResolver().update(currentMemberUri, contentValues, null, null);

            if (rowsChanged == 0) {
                Toast.makeText(this, "Saved of data in the table failed", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Member updated", Toast.LENGTH_LONG).show();
            }
        }


    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                MemberEntry._ID,
                MemberEntry.COLUMN_FIRST_NAME,
                MemberEntry.COLUMN_LAST_NAME,
                MemberEntry.COLUMN_GENDER,
                MemberEntry.COLUMN_SPORT
        };
        return new CursorLoader(this,
                currentMemberUri,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            int firstNameIndex = data.getColumnIndex(MemberEntry.COLUMN_FIRST_NAME);
            int lastNameIndex = data.getColumnIndex(MemberEntry.COLUMN_LAST_NAME);
            int genderIndex = data.getColumnIndex(MemberEntry.COLUMN_GENDER);
            int sportIndex = data.getColumnIndex(MemberEntry.COLUMN_SPORT);

            String  firstName = data.getString(firstNameIndex);
            String  lastName = data.getString(lastNameIndex);
            int  gender = data.getInt(genderIndex);
            String  sport = data.getString(sportIndex);

            firstNameEditText.setText(firstName);
            lastNameEditText.setText(lastName);
            sportEditText.setText(sport);

            switch (gender) {
                case MemberEntry.MALE:
                    genderSpinner.setSelection(MemberEntry.MALE);
                    break;
                case MemberEntry.FEMALE:
                    genderSpinner.setSelection(MemberEntry.FEMALE);
                    break;
                case MemberEntry.UNKNOWN:
                    genderSpinner.setSelection(MemberEntry.UNKNOWN);
                    break;
            }
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    private void showDeleteMemberDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want delete the member?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMember();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteMember() {
        if (currentMemberUri != null) {
            int rowsDeleted = getContentResolver().delete(currentMemberUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, "Deleting of data from the table failed", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Member is deleted", Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }
}
