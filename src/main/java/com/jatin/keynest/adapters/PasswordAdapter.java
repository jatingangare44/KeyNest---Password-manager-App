package com.jatin.keynest.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.jatin.keynest.R;
import com.jatin.keynest.activities.EditPasswordActivity;
import com.jatin.keynest.database.EncryptedDBHelper;
import com.jatin.keynest.models.PasswordModel;
import com.jatin.keynest.utils.EncryptionUtils;

import java.util.List;

public class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.ViewHolder> {

    private Context context;
    private List<PasswordModel> passwordList;
    private String masterKey;

    public PasswordAdapter(List<PasswordModel> passwordList, String masterKey) {
        this.passwordList = passwordList;
        this.masterKey = masterKey;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, usernameText, passwordText;
        ImageView iconShowHide, iconEdit, iconDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.password_title);
            usernameText = itemView.findViewById(R.id.password_username);
            passwordText = itemView.findViewById(R.id.password_password);
            iconShowHide = itemView.findViewById(R.id.icon_show_hide);
            iconEdit = itemView.findViewById(R.id.icon_edit);
            iconDelete = itemView.findViewById(R.id.icon_delete);
        }
    }

    @Override
    public PasswordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.item_password, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PasswordAdapter.ViewHolder holder, int position) {
        PasswordModel model = passwordList.get(position);

        holder.titleText.setText(model.getTitle());
        holder.usernameText.setText("Username: " + model.getUsername());
        holder.passwordText.setText("••••••••");

        // Toggle Show/Hide Password
        holder.iconShowHide.setOnClickListener(view -> {
            try {
                String decrypted = EncryptionUtils.decrypt(model.getEncryptedPassword(), masterKey);
                if (holder.passwordText.getText().toString().equals("••••••••")) {
                    holder.passwordText.setText("Password: " + decrypted);
                    holder.iconShowHide.setImageResource(R.drawable.ic_eye_off); // Set to hide icon
                } else {
                    holder.passwordText.setText("••••••••");
                    holder.iconShowHide.setImageResource(R.drawable.baseline_remove_red_eye_24); // Set to show icon
                }
            } catch (Exception e) {
                Toast.makeText(view.getContext(), "Invalid key or data", Toast.LENGTH_SHORT).show();
            }
        });

        // Edit Action
        holder.iconEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditPasswordActivity.class);
            intent.putExtra("id", model.getId());
            intent.putExtra("title", model.getTitle());
            intent.putExtra("username", model.getUsername());
            intent.putExtra("encryptedPassword", model.getEncryptedPassword());
            intent.putExtra("key", masterKey);
            context.startActivity(intent);
        });

        // Delete Action
        holder.iconDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Entry")
                    .setMessage("Are you sure you want to delete this password?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        SQLiteDatabase db = new EncryptedDBHelper(context).getWritableDatabase();
                        db.delete("passwords", "id=?", new String[]{String.valueOf(model.getId())});
                        passwordList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, passwordList.size());
                        Toast.makeText(context, "Password deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return passwordList.size();
    }

    public void updateList(List<PasswordModel> newList) {
        this.passwordList = newList;
        notifyDataSetChanged();
    }

}
