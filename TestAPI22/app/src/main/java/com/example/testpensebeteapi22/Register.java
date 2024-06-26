package com.example.testpensebeteapi22;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.common.value.qual.PolyValue;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

public class Register extends AppCompatActivity {


    int id = new Random().nextInt(1000);

    DatabaseReference database = FirebaseDatabase.getInstance("https://pense-bete-9293d-default-rtdb.europe-west1.firebasedatabase.app").getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText name = findViewById(R.id.name);
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        EditText confirmationPassword = findViewById(R.id.confirmpassword);
        Button register = findViewById(R.id.register);
        TextView loginNow = findViewById(R.id.login);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // Récupération des valeurs dans les champs

                String nameTxt = name.getText().toString();
                String emailTxt = email.getText().toString();
                String passwordTxt = password.getText().toString();
                String passwordConfirmationTxt = confirmationPassword.getText().toString();
                String idTxt = String.valueOf(id);

                if(nameTxt.isEmpty() || emailTxt.isEmpty() || passwordTxt.isEmpty() || passwordConfirmationTxt.isEmpty()){
                    Toast.makeText(Register.this, "Complétez tous les champs s'il vous plaît", Toast.LENGTH_SHORT).show();
                }
                else if(!passwordTxt.equals(passwordConfirmationTxt)){
                    Toast.makeText(Register.this, "Les passwords ne sont pas les mêmes", Toast.LENGTH_SHORT).show();
                }
                else{
                    String mode = readConfig();
                    String path;
                    if(mode.equals("1")){
                        path =  "aidants";
                    }
                    else{
                        path = "aidés";
                    }
                    System.out.println(path);
                    database.child(path).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // On regarde s'il n'y a pas déjà cet id dans la base de données
                            Toast.makeText(Register.this, "Email déjà inscrit", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    database.child(path).child(idTxt).child("name").setValue(nameTxt);
                    database.child(path).child(idTxt).child("email").setValue(emailTxt);
                    database.child(path).child(idTxt).child("password").setValue(passwordTxt);
                    if(mode.equals("1")){
                        database.child(path).child(idTxt).child("list").setValue(new ArrayList<>());
                    }

                    Toast.makeText(Register.this, "Utilisateur inscrit", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private String readConfig() {
        // Obtenir le chemin du répertoire de stockage interne de l'application
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir("config", Context.MODE_PRIVATE);
        File configFile = new File(directory, "config.properties");

        // Créer un objet Properties
        Properties prop = new Properties();

        try (FileInputStream input = new FileInputStream(configFile)) {
            // Charger les propriétés à partir du fichier
            prop.load(input);

            // Récupérer l'identifiant à partir des propriétés
            return prop.getProperty("mode");
        } catch (IOException io) {
            io.printStackTrace();
            return null;
        }
    }
}