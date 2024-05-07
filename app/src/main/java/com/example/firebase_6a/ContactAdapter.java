package com.example.firebase_6a;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ContactAdapter extends FirebaseRecyclerAdapter<Contact, ContactAdapter.ViewHolder> {

    Context context;
    public ContactAdapter(Context context, @NonNull FirebaseRecyclerOptions<Contact> options) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Contact contact) {
        viewHolder.tvName.setText(contact.getName());

        String key = getRef(i).getKey();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Contacts");

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder addContact = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context)
                        .inflate(R.layout.add_contact_form, null, false);
                EditText etName = view.findViewById(R.id.etName);
                EditText etPhone = view.findViewById(R.id.etPhone);
                addContact.setTitle("UPDATE CONTACT");
                addContact.setView(view);
                etName.setText(contact.getName());
                etPhone.setText(contact.getPhone());
                addContact.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        HashMap<Object, Object> data = new HashMap<>();
                        data.put("name", etName.getText().toString().trim());
                        data.put("phone", etPhone.getText().toString());

                        assert key != null;
                        reference
                                .child(key)
                                .setValue(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Successfully updated", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                addContact.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        assert key != null;
                        reference.child(key)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                addContact.show();


                return false;
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_contact_item_design, parent, false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}
