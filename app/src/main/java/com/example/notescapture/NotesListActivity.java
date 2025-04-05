package com.example.notescapture;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notescapture.adapters.NotesAdapter;
import com.example.notescapture.models.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class NotesListActivity extends AppCompatActivity implements NotesAdapter.OnNoteClickListener {
    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private List<Note> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Saved Notes");
        }

        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        notes = new ArrayList<>();
        adapter = new NotesAdapter();
        adapter.setOnNoteClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotesActivity.class);
            startActivity(intent);
        });

        loadNotes();
    }

    private void loadNotes() {
        // For now, adding sample notes
        notes.clear();
        notes.add(new Note("Sample Note 1", "This is a sample note content."));
        notes.add(new Note("Sample Note 2", "Another sample note content."));
        adapter.setNotes(notes);
    }

    @Override
    public void onNoteClick(Note note) {
        if (note != null) {
            Intent intent = new Intent(this, NotesActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes(); // Refresh notes when returning to this activity
    }
}
