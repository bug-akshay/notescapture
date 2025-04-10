package com.example.notescapture;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notescapture.adapters.NotesAdapter;
import com.example.notescapture.models.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotesListActivity extends AppCompatActivity implements NotesAdapter.OnNoteClickListener {
    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private List<Note> notes;
    private DatabaseReference notesRef;
    private ValueEventListener notesListener;

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

        // Initialize Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            notesRef = FirebaseDatabase.getInstance().getReference("notes").child(currentUser.getUid());
            loadNotes();
        }
    }

    private void loadNotes() {
        if (notesRef == null) return;

        // Remove existing listener if present
        if (notesListener != null) {
            notesRef.removeEventListener(notesListener);
        }

        // Add new listener for real-time updates
        notesListener = notesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notes.clear();
                for (DataSnapshot noteSnapshot : snapshot.getChildren()) {
                    Note note = noteSnapshot.getValue(Note.class);
                    if (note != null) {
                        notes.add(note);
                    }
                }
                adapter.setNotes(notes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NotesListActivity.this, "Error loading notes", Toast.LENGTH_SHORT).show();
            }
        });
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
