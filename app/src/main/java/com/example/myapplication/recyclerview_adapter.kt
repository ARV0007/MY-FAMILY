package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class recyclerview_adapter(private var listmemebers: List<membermodel>) :
    RecyclerView.Adapter<recyclerview_adapter.ViewHolder>() {
    class ViewHolder(private val item:View):RecyclerView.ViewHolder(item){
        val imageuser=item.findViewById<ImageView>(R.id.img_user)
        val names =item.findViewById<TextView>(R.id.name)
        val address = item.findViewById<TextView>(R.id.address)
        val battery = item.findViewById<TextView>(R.id.battery_percent)
        val distance = item.findViewById<TextView>(R.id.distance_value)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): recyclerview_adapter.ViewHolder {
       val inflator=LayoutInflater.from(parent.context)
        val item=inflator.inflate(R.layout.item_member,parent,false)
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: recyclerview_adapter.ViewHolder, position: Int) {
       val item=listmemebers[position]
        holder.names.text=item.name
        holder.address.text = item.address
        holder.battery.text = item.battery
        holder.distance.text = item.distance

    }

    override fun getItemCount(): Int {
       return listmemebers.size
    }
}