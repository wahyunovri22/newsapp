package com.semicolon.newsapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.semicolon.newsapp.MainActivity
import com.semicolon.newsapp.databinding.RowNewsBinding
import com.semicolon.newsapp.helper.HelperClass
import com.semicolon.newsapp.model.NewsItem
import com.squareup.picasso.Picasso

class NewsAdapter(var context: Context, var list:ArrayList<NewsItem>):RecyclerView.Adapter<NewsAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = RowNewsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

   inner class Holder(private val binding:RowNewsBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(data:NewsItem){
            binding.tvTitle.text = data.judul
            binding.tvUsername.text = data.userinput
            binding.tvDate.text = HelperClass().convertDate(data.tanggal?:"")
            Picasso.get().load(data.cover).into(binding.imgCover)

            binding.root.setOnClickListener {
                (context as MainActivity).goToDetail(data)
            }
        }
    }
}