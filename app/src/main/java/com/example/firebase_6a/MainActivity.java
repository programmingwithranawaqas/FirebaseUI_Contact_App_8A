package com.example.firebase_6a;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    EditText etName, etPhone;
    DatabaseReference reference;
    FloatingActionButton fabAdd;
    RecyclerView rvContacts;

    ContactAdapter adapter;

    private final String KEY_PARENT = "Contacts";

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

        reference = FirebaseDatabase.getInstance().getReference();
        fabAdd = findViewById(R.id.fabAdd);
        rvContacts = findViewById(R.id.rvContacts);



        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder addContact = new AlertDialog.Builder(MainActivity.this);
                View view = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.add_contact_form, null, false);
                etName = view.findViewById(R.id.etName);
                etPhone = view.findViewById(R.id.etPhone);
                addContact.setTitle("NEW CONTACT");
                addContact.setView(view);
                addContact.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        HashMap<Object, Object> data = new HashMap<>();
                        data.put("name", etName.getText().toString().trim());
                        data.put("phone", etPhone.getText().toString());

                        reference.child(KEY_PARENT)
                                .push()
                                .setValue(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(MainActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                addContact.show();



            }
        });

        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.setHasFixedSize(true);

        FirebaseRecyclerOptions<Contact> options =
                new FirebaseRecyclerOptions.Builder<Contact>()
                        .setQuery(reference.child(KEY_PARENT), Contact.class)
                        .build();

        adapter = new ContactAdapter(this, options);

        rvContacts.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

//    public void loadDataOnReferesh()
//    {
//        reference.child(KEY_PARENT).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String text = "";
//                for(DataSnapshot s: snapshot.getChildren())
//                {
//                    text = text + s.child("name").getValue() + " : "+s.child("phone").getValue()+"\n";
//                }
//                tvResults.setText(text);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

//    public void readRealTime()
//    {
//        reference.child("Contacts").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                String text = "";
//                for(DataSnapshot s: snapshot.getChildren())
//                {
//                    text = text + s.child("name").getValue() + " : "+s.child("phone").getValue()+"\n";
//                }
//                tvResults.setText(text);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
}