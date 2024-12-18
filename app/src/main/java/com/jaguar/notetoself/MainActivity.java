package com.jaguar.notetoself;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jaguar.notetoself.adapters.NotesRecyclerAdapter;
import com.jaguar.notetoself.dialogs.NewNoteDialog;
import com.jaguar.notetoself.json.NotesJSONSerializer;
import com.jaguar.notetoself.note.Note;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    List<Note> notes;
    private NotesRecyclerAdapter noteAdapter;
    private NotesJSONSerializer serializer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FloatingActionButton fab = findViewById(R.id.floatingAdd);
        fab.setOnClickListener(view -> {
            DialogFragment dialog = new NewNoteDialog();
            dialog.show(getSupportFragmentManager(), "");
        });

        findViewById(R.id.settingsButton).setOnClickListener(view -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        serializer = new NotesJSONSerializer("Notes.json", getApplicationContext());
        try {
            notes = serializer.loadNotes();
        } catch (Exception e) {
            notes = new ArrayList<>();
            Toast.makeText(this, "Error loading notes", Toast.LENGTH_SHORT).show();
        }

        RecyclerView recyclerView = findViewById(R.id.NotesList);
        noteAdapter = new NotesRecyclerAdapter(notes, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(noteAdapter);
    }

    public void addNewNote(Note newNote) {
        notes.add(newNote);
        noteAdapter.notifyItemInserted(notes.size()-1);
    }

    public void editNote () {
        noteAdapter.notifyDataSetChanged();
    }

    public void deleteNote (Note note) {
        notes.remove(note);
        noteAdapter.notifyDataSetChanged();
    }

    public void saveNotes() {
        try {
            serializer.saveNotes(notes);
        } catch (Exception e) {
            Toast.makeText(this, "Error saving notes", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveNotes();
    }

}