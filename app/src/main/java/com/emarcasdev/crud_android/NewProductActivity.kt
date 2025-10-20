package com.emarcasdev.crud_android

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NewProductActivity : AppCompatActivity() {

    // Referencias del layout de crear producto
    private lateinit var editName: EditText;
    private lateinit var editCategory: EditText;
    private lateinit var editStock: EditText;
    private lateinit var buttonSave: Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        // Obtenemos las referencias a las vistas por el id
        editName = findViewById(R.id.editName);
        editCategory = findViewById(R.id.editCategory);
        editStock = findViewById(R.id.editStock);
        buttonSave = findViewById(R.id.buttonSave);

        // Al guardar enviamos el nuevo producto al MainActivity
        buttonSave.setOnClickListener {
            val name = editName.text.toString();
            val category = editCategory.text.toString();
            val stock = editStock.text.toString().toIntOrNull() ?: 0;

            // Validar los campos obligatorios
            if (name.isEmpty()) {
                Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
                return@setOnClickListener;
            }
            if (category.isEmpty()) {
                Toast.makeText(this, "La categoria es obligatoria", Toast.LENGTH_SHORT).show();
                return@setOnClickListener;
            }

            // Enviar los campos del producto
            val data = intent.apply {
                putExtra("name", name);
                putExtra("category", category);
                putExtra("stock", stock);
            }
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }
}