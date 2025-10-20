package com.emarcasdev.crud_android

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emarcasdev.crud_android.adapter.ProductAdapter
import com.emarcasdev.crud_android.api.ApiClient
import com.emarcasdev.crud_android.api.model.Product
import com.emarcasdev.crud_android.api.model.ProductBody
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    // Referencias del layout principal
    private lateinit var recyclerView: RecyclerView;
    private lateinit var textEmpty: TextView;
    private lateinit var floatingActionButton: FloatingActionButton;
    private lateinit var search: EditText;

    // Adapter para mostrar las tarjetas
    private lateinit var adapter: ProductAdapter;

    // Lista de productos local
    private val products = mutableListOf<Product>();

    // Job para cancelar la busqyeda si se sige escribiendo
    private var searchJob: Job? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtenemos las referencias a las vistas por el id
        recyclerView = findViewById(R.id.recyclerProducts);
        textEmpty = findViewById(R.id.textEmpty);
        floatingActionButton = findViewById(R.id.openNewProduct);
        search = findViewById(R.id.inputSearch);

        // Creamos el adaptador para las opciones de editar y eliminar
        adapter = ProductAdapter(
            onEdit = {product ->
                // Vamos a la vista de editar con los datos del producto
                val editProductIntent = Intent(this, EditProductActivity::class.java).apply {
                    putExtra("id", product.id);
                    putExtra("name", product.name);
                    putExtra("category", product.category);
                    putExtra("stock", product.stock);
                }
                openEditProduct.launch(editProductIntent);
            },
            onDelete = {product ->
                // Modal de confirmacion para evitar eliminar por error
                MaterialAlertDialogBuilder(this).setTitle("Eliminar producto")
                    .setMessage(
                        "¿Quieres eliminar este producto?\n\n" +
                        "- Nombre: ${product.name}\n" +
                        "- Categoría: ${product.category}\n" +
                        "- Cantidad: ${product.stock}"
                    )
                    .setPositiveButton("Eliminar") { _, _ ->
                    // Borrar por el id
                    deleteProduct(product.id);
                    }
                        .setNegativeButton("Cancelar", null).show();
            }
        );

        // Configura el recyclerView de manera vertical y le asignamos el adapter para poder mostrar las tarjetas
        recyclerView.layoutManager = LinearLayoutManager(this);
        recyclerView.adapter = adapter;

        // Cargar los productos
        loadProducts(null);

        // Buscar por nombre cuando se escribe en el buscador
        search.addTextChangedListener { text ->
            searchJob?.cancel();
            searchJob = lifecycleScope.launch {
                delay(300);
                val name = text?.toString()?.trim().takeUnless { text -> text.isNullOrEmpty() };
                loadProducts(name);
            }
        }

        // Boton de accion para crear un nuevo producto
        floatingActionButton.setOnClickListener {
            openNewProduct.launch(Intent(this, NewProductActivity::class.java));
        }
    }

    // Actualizar la lista de productos
    private fun updateListProducts() {
        adapter.updateList(products);
        if (products.isEmpty()) {
            textEmpty.visibility = View.VISIBLE;
        } else {
            textEmpty.visibility = View.GONE;
        }
    }

    // Ir a la vista para crear un nuevo producto y recuperamos el resultado
    private val openNewProduct = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        // Cuando haya creado el producto
        if (result.resultCode == RESULT_OK && result.data != null) {
            // Recuparamos los campos
            val name = result.data!!.getStringExtra("name")!!;
            val category = result.data!!.getStringExtra("category")!!;
            val stock = result.data!!.getIntExtra("stock", 0);

            // Creamos el producto
            createProduct(name, category, stock);
        }
    }

    // Ir a la vista para editar un producto y recuperamos el resultado
    private val openEditProduct = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        // Cuando haya actualizado el producto
        if (result.resultCode == RESULT_OK && result.data != null) {
            // Recuparamos el id y los campos
            val id = result.data!!.getStringExtra("id");
            val name = result.data!!.getStringExtra("name")!!;
            val category = result.data!!.getStringExtra("category")!!;
            val stock = result.data!!.getIntExtra("stock", 0);

            // Editamos el producto
            editProduct(id, name, category, stock);
        }
    }

    // Función para cargar todos los productos
    private fun loadProducts(name: String? = null) {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiClient.service.getProducts(name);
                }
                if (response.isSuccessful) {
                    // Recuperamos la lista de productos
                    val productsList = response.body().orEmpty();
                    products.clear();
                    products.addAll(productsList);
                    updateListProducts();
                } else {
                    Toast.makeText(this@MainActivity, "Error, no se pudieron recuperar los productos", Toast.LENGTH_SHORT).show();
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error, no se pudo conectar a la API", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Función para crear un nuevo prodcuto
    private fun createProduct(name: String, category: String, stock: Int) {
        lifecycleScope.launch {
            try {
                val body = ProductBody(name, category, stock);
                var response = withContext(Dispatchers.IO) {
                    ApiClient.service.createProduct(body);
                }
                if (response.isSuccessful) {
                    response.body()?.product?.let { createdProduct ->
                        // Añadimos el nuevo producto al final
                        products.add(createdProduct);
                        updateListProducts();
                        Toast.makeText(this@MainActivity, "Producto creado correctamente", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error, no se pudo crear el producto", Toast.LENGTH_SHORT).show();
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error, no se pudo conectar a la API", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Función para editar un prodcuto
    private fun editProduct(id: String?, name: String, category: String, stock: Int) {
        // Si no tiene id no se puede editar
        if (id.isNullOrEmpty()) {
            Toast.makeText(this, "No tiene ID no se puede editar", Toast.LENGTH_SHORT).show()
            return;
        }
        lifecycleScope.launch {
            try {
                val body = ProductBody(name, category, stock);
                var response = withContext(Dispatchers.IO) {
                    ApiClient.service.editProduct(id, body);
                }
                if (response.isSuccessful) {
                    response.body()?.product?.let { updatedProduct ->
                        // Buscamos en la lista y remplazamos por id
                        val productId = products.indexOfFirst { product -> product.id == id };
                        if (productId != -1) {
                            products[productId] = updatedProduct;
                            updateListProducts();
                            Toast.makeText(this@MainActivity, "Producto actualizado correctamente", Toast.LENGTH_SHORT).show();
                        } else {
                            loadProducts(null);
                        }
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error, no se pudo actualizar el producto", Toast.LENGTH_SHORT).show();
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error, no se pudo conectar a la API", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Función para eliminar un prodcuto
    private fun deleteProduct(id: String?) {
        // Si no tiene id no se puede eliminar
        if (id.isNullOrEmpty()) {
            Toast.makeText(this, "No tiene ID no se puede eliminar", Toast.LENGTH_SHORT).show()
            return;
        }
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiClient.service.deleteProduct(id);
                }
                if (response.isSuccessful) {
                    // Buscamos en la lista local y lo eliminamos
                    products.removeAll { product -> product.id == id };
                    updateListProducts();
                    Toast.makeText(this@MainActivity, "Producto eliminado correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this@MainActivity, "Error, no se pudo eliminar el producto", Toast.LENGTH_SHORT).show();
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Error, no se pudo conectar a la API", Toast.LENGTH_SHORT).show();
            }
        }
    }
}