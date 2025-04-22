package com.cardoso.shareyourtime.ui.task;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.cardoso.shareyourtime.R;
import com.cardoso.shareyourtime.data.Task;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {
    private final OnTaskClickListener listener;
    private final SimpleDateFormat dateFormat;
    private final SimpleDateFormat timeFormat;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    protected TaskAdapter(OnTaskClickListener listener) {
        super(new DiffUtil.ItemCallback<Task>() {
            @Override
            public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
                return oldItem.getTitle().equals(newItem.getTitle()) &&
                       oldItem.getDateTime().equals(newItem.getDateTime()) &&
                       oldItem.isCompleted() == newItem.isCompleted();
            }
        });
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = getItem(position);
        holder.bind(task, listener, dateFormat, timeFormat);
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView descriptionText;
        private final TextView dateTimeText;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.task_title);
            descriptionText = itemView.findViewById(R.id.task_description);
            dateTimeText = itemView.findViewById(R.id.task_datetime);
        }

        public void bind(Task task, OnTaskClickListener listener,
                        SimpleDateFormat dateFormat, SimpleDateFormat timeFormat) {
            titleText.setText(task.getTitle());
            
            if (task.getDescription() != null && !task.getDescription().isEmpty()) {
                descriptionText.setVisibility(View.VISIBLE);
                descriptionText.setText(task.getDescription());
            } else {
                descriptionText.setVisibility(View.GONE);
            }

            String dateTime = dateFormat.format(task.getDateTime()) + " " +
                            timeFormat.format(task.getDateTime());
            dateTimeText.setText(dateTime);
            
            itemView.setOnClickListener(v -> listener.onTaskClick(task));
        }
    }
} 