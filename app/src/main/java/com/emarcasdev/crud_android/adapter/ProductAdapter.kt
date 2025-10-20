package com.emarcasdev.crud_android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.emarcasdev.crud_android.api.model.Product
import com.emarcasdev.crud_android.R
import com.google.android.material.button.MaterialButton

class ProductAdapter(
    private val onEdit: (Product) -> Unit,
    private val onDelete: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    // Lista de productos
    private val products = mutableListOf<Product>();

    // Remplazamos los datos de la lista y notificamos para que se muestren
    fun updateList(list: List<Product>) {
        products.clear();
        products.addAll(list);
        notifyDataSetChanged();
    }

    // Guardamos las referencias de las vistas de la tarjeta
    class ProductViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textName: TextView = view.findViewById(R.id.textName);
        val textCategory: TextView = view.findViewById(R.id.textCategory);
        val textStock: TextView = view.findViewById(R.id.textStock);
        val buttonEdit: MaterialButton = view.findViewById(R.id.buttonEdit);
        val buttonDelete: MaterialButton = view.findViewById(R.id.buttonDelete);
    }

    // Creamos la tarjeta
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_product, parent, false);
        return ProductViewHolder(view);
    }

    // Asignamos los datos y las funciones correspondientes a su tarjeta correspondiente
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position];

        holder.textName.text = product.name;
        holder.textCategory.text = product.category;
        holder.textStock.text = product.stock.toString();
        holder.buttonEdit.setOnClickListener { onEdit(product) };
        holder.buttonDelete.setOnClickListener { onDelete(product) };
    }

    // Total de tarjetas que debe haber
    override fun getItemCount(): Int = products.size;
}