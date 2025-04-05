package com.example.notescapture.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notescapture.R;
import com.example.notescapture.models.Note;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private List<Note> notes;
    private OnNoteClickListener listener;

    public NotesAdapter() {
        this.notes = new ArrayList<>();
    }

    public void setOnNoteClickListener(OnNoteClickListener listener) {
        this.listener = listener;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.titleTextView.setText(note.getTitle());
        holder.contentTextView.setText(note.getContent());
        
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNoteClick(note);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView titleTextView;
        TextView contentTextView;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
        }
    }

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
    }
}
