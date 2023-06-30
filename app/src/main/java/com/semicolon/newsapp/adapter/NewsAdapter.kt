package com.semicolon.newsapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.semicolon.newsapp.MainActivity
import com.semicolon.newsapp.databinding.RowNewsBinding
import com.semicolon.newsapp.helper.HelperClass
import com.semicolon.newsapp.model.NewsItem
import com.squareup.picasso.Picasso
import java.util.Locale

class NewsAdapter():RecyclerView.Adapter<NewsAdapter.Holder>(),Filterable {

    lateinit var context: Context
    lateinit var list: ArrayList<NewsItem>
    lateinit var list2: ArrayList<NewsItem>
    fun setData(context: Context,list: ArrayList<NewsItem>){
        this.context = context
        this.list = list
        this.list2 = list
    }

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

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val oReturn = FilterResults()
                val results2 = ArrayList<NewsItem>()

                if (list2.size > 0) {
                    for (g in list2) {
                        if (g.judul.toString().lowercase(Locale.ROOT).contains(charSequence.toString())){
                            results2.add(g)
                        }
                    }
                }
                oReturn.values = results2
                return oReturn
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                list = filterResults.values as ArrayList<NewsItem>
                notifyDataSetChanged()
            }
        }
    }
}