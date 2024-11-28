package com.example.taskmanager;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

public class TaskAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Task> taskList;
    private TaskDatabaseHelper dbHelper;

    public TaskAdapter(Context context, ArrayList<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
        this.dbHelper = new TaskDatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Object getItem(int position) {
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        }

        Task task = taskList.get(position);

        TextView taskTitle = convertView.findViewById(R.id.taskTitle);
        CheckBox taskCompleted = convertView.findViewById(R.id.taskCompleted);
        Button deleteTaskButton = convertView.findViewById(R.id.deleteTaskButton);
        Button editTaskButton = convertView.findViewById(R.id.editTaskButton);

        taskTitle.setText(task.getTitle());
        taskCompleted.setChecked(task.isCompleted());

        // Toggle task completion
        taskCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            dbHelper.updateTask(task);
        });

        // Delete task
        deleteTaskButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dbHelper.deleteTask(task.getId());
                        taskList.remove(position);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // Edit task
        editTaskButton.setOnClickListener(v -> showEditTaskDialog(task, position));

        return convertView;
    }

    private void showEditTaskDialog(Task task, int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_edit_task);

        EditText editTaskTitle = dialog.findViewById(R.id.editDialogTaskTitle);
        Button saveButton = dialog.findViewById(R.id.dialogEditSaveButton);
        Button cancelButton = dialog.findViewById(R.id.dialogEditCancelButton);

        // Pre-fill the task title
        editTaskTitle.setText(task.getTitle());

        // Save changes
        saveButton.setOnClickListener(v -> {
            String updatedTitle = editTaskTitle.getText().toString().trim();
            if (!updatedTitle.isEmpty()) {
                task.setTitle(updatedTitle);
                dbHelper.updateTask(task);
                taskList.set(position, task);
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        // Cancel editing
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
