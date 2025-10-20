package com.emarcasdev.crud_android

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EditProductActivity : AppCompatActivity() {

    // Referencias del layout de editar producto
    private lateinit var editName: EditText;
    private lateinit var editCategory: EditText;
    private lateinit var editStock: EditText;
    private lateinit var buttonSave: Button;

    // Guardamos el ID para devolverlo y saber que producto actualizar
    private var id: String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        // Obtenemos las referencias a las vistas por el id
        editName = findViewById(R.id.editName);
        editCategory = findViewById(R.id.editCategory);
        editStock = findViewById(R.id.editStock);
        buttonSave = findViewById(R.id.buttonSave);

        // Cargar los datos recuperados del MainActivity
        id = intent.getStringExtra("id") ?: "";
        editName.setText(intent.getStringExtra("name") ?: "");
        editCategory.setText(intent.getStringExtra("category") ?: "");
        editStock.setText((intent.getIntExtra("stock", 0)).toString());

        // Al guardar enviamos los cambios al MainActivity
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

            // Moadal para confirmar los cambios
            MaterialAlertDialogBuilder(this).setTitle("Confirmar cambios")
                .setMessage(
                    "¿Quieres guardar los nuevos valores del producto?\n\n" +
                    "- Nombre: $name\n" +
                    "- Categoría: $category\n" +
                    "- Cantidad: $stock"
                ).setPositiveButton("Guardar") { _, _ ->
                    // Devolver el id y los campos actualizados
                    val data = intent.apply {
                        putExtra("id", id);
                        putExtra("name", name);
                        putExtra("category", category);
                        putExtra("stock", stock);
                    }
                    setResult(Activity.RESULT_OK, data);
                    finish();
            }
                    .setNegativeButton("Cancelar", null).show();
        }
    }
}