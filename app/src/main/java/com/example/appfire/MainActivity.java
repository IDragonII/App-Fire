package com.example.appfire;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appfire.model.Persona;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private List<Persona> listPerson = new ArrayList<Persona>();
    ArrayAdapter<Persona> arrayAdapterPersona;
    EditText nomP, appP, correoP, paswordP;
    ListView listV_personas;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Persona personaSelected;

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

        nomP=findViewById(R.id.txt_nombrePersona);
        appP=findViewById(R.id.txt_appPersona);
        correoP=findViewById(R.id.txt_correoPersona);
        paswordP=findViewById(R.id.txt_passwordPersona);

        listV_personas=findViewById(R.id.lv_datosPersonas);
        inicializarFirebase();
        listarDatos();

        listV_personas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                personaSelected= (Persona) adapterView.getItemAtPosition(i);
                nomP.setText(personaSelected.getNombre());
                appP.setText(personaSelected.getApellido());
                correoP.setText(personaSelected.getCorreo());
                paswordP.setText(personaSelected.getPassword());
            }
        });
    }

    private void listarDatos() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPerson.clear();
                for (DataSnapshot objSnaptshot : snapshot.getChildren()){
                    Persona p=objSnaptshot.getValue(Persona.class);
                    listPerson.add(p);
                    arrayAdapterPersona=new ArrayAdapter<Persona>(MainActivity.this, android.R.layout.simple_list_item_1,listPerson);
                    listV_personas.setAdapter(arrayAdapterPersona);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase=FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference=firebaseDatabase.getReference();
    }

    public void add(View v) {
        String nombre=nomP.getText().toString();
        String correo=correoP.getText().toString();
        String password=paswordP.getText().toString();
        String app=appP.getText().toString();
        if (nombre.equals("")||app.equals("")||correo.equals("")||password.equals("")){
            validacion();
        }
        else {
            Persona p= new Persona();
            p.setUid(UUID.randomUUID().toString());
            p.setNombre(nombre);
            p.setApellido(app);
            p.setCorreo(correo);
            p.setPassword(password);
            databaseReference.child("Persona").child(p.getUid()).setValue(p);
            Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show();
            limpiarCajas();
        }
    }

    public void save(View v) {
        String nombre=nomP.getText().toString();
        String correo=correoP.getText().toString();
        String password=paswordP.getText().toString();
        String app=appP.getText().toString();
        if (nombre.equals("")||app.equals("")||correo.equals("")||password.equals("")){
            validacion();
        }
        else {
            Persona p = new Persona();
            p.setUid(personaSelected.getUid());
            p.setNombre(nomP.getText().toString().trim());
            p.setApellido(appP.getText().toString().trim());
            p.setCorreo(correoP.getText().toString().trim());
            p.setPassword(paswordP.getText().toString().trim());
            databaseReference.child("Persona").child(p.getUid()).setValue(p);

            Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show();
            limpiarCajas();
        }
    }
    public void delete(View v) {
        Persona p = new Persona();
        p.setUid(personaSelected.getUid());
        databaseReference.child("Persona").child(p.getUid()).removeValue();
        Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show();
        limpiarCajas();
    }

    private void validacion() {
        String nombre=nomP.getText().toString();
        String correo=correoP.getText().toString();
        String password=paswordP.getText().toString();
        String app=appP.getText().toString();
        if (nombre.equals("")){
            nomP.setError("Required");
        } else if (password.equals("")) {
            paswordP.setError("Required");
        }else if (correo.equals("")) {
            correoP.setError("Required");
        }
        else if (app.equals("")) {
            appP.setError("Required");
        }
    }
    private void limpiarCajas() {
        nomP.setText("");
        correoP.setText("");
        paswordP.setText("");
        appP.setText("");
    }
}