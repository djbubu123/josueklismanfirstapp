package com.example.josueklisman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.josueklisman.Adapters.ListViewProductAdapter;
import com.example.josueklisman.Models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

                            arrayAdapterProduct = new ArrayAdapter<Product>(
                                    MainActivity.this,
                                    android.R.layout.simple_list_item_1,
                                    listProducts
                            );
                            listViewProducts.setAdapter(arrayAdapterProduct);
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
}