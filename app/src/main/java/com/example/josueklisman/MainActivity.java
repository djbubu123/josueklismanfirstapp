package com.example.josueklisman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.josueklisman.Adapters.ListViewProductAdapter;
import com.example.josueklisman.Models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Product> listProducts = new ArrayList<>();
    ArrayAdapter<Product> arrayAdapterProduct;
    ListViewProductAdapter listViewProductAdapter;
    LinearLayout linearLayoutEdit;
    ListView listViewProducts;

    EditText inputName, inputPrice;
    Button btnCancel;

    Product productSelected;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputName = findViewById(R.id.inputName);
        inputPrice = findViewById(R.id.inputPrice);
        btnCancel = findViewById(R.id.btnCancel);

        listViewProducts = findViewById(R.id.listViewProducts);
        linearLayoutEdit = findViewById(R.id.linearLayoutEdit);
        linearLayoutEdit.setVisibility(View.GONE);

        listViewProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                productSelected = (Product) adapterView.getItemAtPosition(i);
                inputName.setText(productSelected.getName());
                inputPrice.setText(productSelected.getPrice().toString());

                linearLayoutEdit.setVisibility(View.VISIBLE);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayoutEdit.setVisibility(View.GONE);
                productSelected = null;
            }
        });

        initFirestore();
        listProducts();
    }

    private void initFirestore(){
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
    }

    private void listProducts(){
        db.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            listProducts.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("success", document.getId() + " => " + document.getData());
                                Product p = new Product();
                                p.setIdproduct(document.getId());
                                p.setName(document.getData().get("name").toString());
                                p.setPrice(Double.parseDouble(document.getData().get("price").toString()));
                                listProducts.add(p);
                            }
                            listViewProductAdapter = new ListViewProductAdapter(MainActivity.this, listProducts);


                            /*arrayAdapterProduct = new ArrayAdapter<Product>(
                                    MainActivity.this,
                                    android.R.layout.simple_list_item_1,
                                    listProducts
                            );*/
                            listViewProducts.setAdapter(listViewProductAdapter);
                        } else {
                            Log.w("Error", "Error getting products.", task.getException());
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.crud_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        String name = inputName.getText().toString();
        String prePrice = (inputPrice.getText().toString().isEmpty())?"0":inputPrice.getText().toString();
        Double price = Double.parseDouble(prePrice);

        switch (item.getItemId()){
            case R.id.menu_create:
                create();
                break;
            case R.id.menu_save:
                if(productSelected != null){
                    if (validateEditInputs()){
                        Product p = new Product();
                        p.setIdproduct(productSelected.getIdproduct());
                        p.setName(name);
                        p.setPrice(price);
                        db.collection("products")
                                .document(p.getIdproduct())
                                .set(p.getMapWithoutId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        listProducts();
                                        Toast.makeText(
                                                MainActivity.this,
                                                "Editado Correctamente",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        listProducts();
                                        Toast.makeText(
                                                MainActivity.this,
                                                "Error",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                    linearLayoutEdit.setVisibility(View.GONE);
                    productSelected = null;
                }
                break;
            case R.id.menu_delete:
                if(productSelected != null){
                    if (validateEditInputs()){
                        Product p = new Product();
                        p.setIdproduct(productSelected.getIdproduct());
                        p.setName(name);
                        p.setPrice(price);
                        db.collection("products")
                                .document(p.getIdproduct())
                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        listProducts();
                                        Toast.makeText(
                                                MainActivity.this,
                                                "Eliminado Correctamente",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        listProducts();
                                        Toast.makeText(
                                                MainActivity.this,
                                                "Error",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                    linearLayoutEdit.setVisibility(View.GONE);
                    productSelected = null;
                }
                break;
            case R.id.menu_sync:
                listProducts();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void create(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(
                MainActivity.this
        );
        View mView = getLayoutInflater().inflate(R.layout.create, null);
        Button btnCreate = (Button) mView.findViewById(R.id.btnCreate);
        final EditText mInputName = (EditText) mView.findViewById(R.id.inputName);
        final EditText mInputPrice = (EditText) mView.findViewById(R.id.inputPrice);

        mBuilder.setView(mView);

        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mInputName.getText().toString();
                String price = mInputPrice.getText().toString();
                if (name.isEmpty()){
                    showError(mInputName, "el nombre no puede estar vacio");
                } else if (name.length()<3){
                    showError(mInputName, "minimo 3 letras en el nombre");
                } else if (price.isEmpty()){
                    showError(mInputPrice, "el precio no puede estar vacio");
                } else {
                    Product p = new Product();
                    p.setName(name);
                    p.setPrice(Double.parseDouble(price));
                    db.collection("products")
                            .add(p.getMapWithoutId())
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(
                                            MainActivity.this,
                                            "Registrado Correctamente",
                                            Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("error", "Error adding document", e);
                                    Toast.makeText(MainActivity.this,"Error", Toast.LENGTH_LONG);
                                }
                            });
                    listProducts();
                    dialog.hide();
                }
            }
        });
        linearLayoutEdit.setVisibility(View.GONE);
        productSelected = null;
    }


    public void showError(EditText input, String s){
        input.requestFocus();
        input.setError(s);
    }

    public boolean validateEditInputs(){
        String name = inputName.getText().toString();
        String price = inputPrice.getText().toString();
        if (name.isEmpty()){
            showError(inputName, "el nombre no puede estar vacio");
            return false;
        } else if (name.length()<3){
            showError(inputName, "minimo 3 letras en el nombre");
            return false;
        } else if (price.isEmpty()){
            showError(inputPrice, "el precio no puede estar vacio");
            return false;
        } else{
            return true;
        }
    }
}