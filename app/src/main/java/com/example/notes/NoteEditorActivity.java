package com.example.notes;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashSet;

public class NoteEditorActivity extends AppCompatActivity {
    int noteId;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Edit Note");

        editText = findViewById(R.id.editText);

        Intent intent = getIntent();

        noteId = intent.getIntExtra("noteId", -1);
        if (noteId != -1) {
            editText.setText(MainActivity.notes.get(noteId));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notes_edit_option_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        if (noteId == -1) {
            menu.findItem(R.id.delete).setEnabled(false);
        }
        return true;
    }

    public void deleteNote(){
        MainActivity.notes.remove(noteId);
        MainActivity.arrayAdapter.notifyDataSetChanged();
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
        HashSet<String> set = new HashSet(MainActivity.notes);
        sharedPreferences.edit().putStringSet("notes", set).apply();
        Toast.makeText(NoteEditorActivity.this, "Note deleted.", Toast.LENGTH_LONG).show();
        finish();
    }

    public boolean isEmpty() {
        if (editText.getText().toString().trim().length() == 0) {
            Toast.makeText(NoteEditorActivity.this, "Nothing to save, nothing saved.", Toast.LENGTH_LONG).show();
            return true;
        } else {
            return false;
        }
    }

    public void save() {
        if (noteId == -1) {
            MainActivity.notes.add("");
            noteId = MainActivity.notes.size() - 1;
            MainActivity.arrayAdapter.notifyDataSetChanged();
        }
        MainActivity.notes.set(noteId, editText.getText().toString());
        MainActivity.arrayAdapter.notifyDataSetChanged();
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
        HashSet<String> set = new HashSet(MainActivity.notes);
        sharedPreferences.edit().putStringSet("notes", set).apply();
        Toast.makeText(NoteEditorActivity.this, "Changes were saved.", Toast.LENGTH_LONG).show();
        this.finish();
    }

    public void createDialog(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("You want to exit without saving changes?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                })
                .setNegativeButton("No, save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        save();
                         finish();
                    }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.save:
                if (!isEmpty()) {
                    save();
                }
                return true;

            case android.R.id.home:
                if (!isEmpty()) {
                    createDialog(editText);
                }
                else {
                    finish();
                }
                return true;

            case R.id.delete:
                deleteNote();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (!isEmpty()) {
            createDialog(editText);
        }
        else {
            finish();
        }
    }
}
