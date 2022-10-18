package com.creatah.yesattendance.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creatah.yesattendance.data.model.Member
import com.creatah.yesattendance.data.model.ResponseModel
import com.creatah.yesattendance.databinding.LayoutSearchItemBinding

class SearchListAdapter(
    private val clickListener: (Member) -> Unit
): RecyclerView.Adapter<SearchListAdapter.SearchListViewHolder>() {

    private val memberList = ArrayList<Member>()

    class SearchListViewHolder(private val binding: LayoutSearchItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(member: Member, clickListener: (Member) -> Unit) {
            binding.tvSearch.text = member.memberName
            binding.clSearch.setOnClickListener {
                clickListener(member)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchListViewHolder {
        val binding = LayoutSearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchListViewHolder, position: Int) {
        holder.bind(memberList[position], clickListener)
    }

    fun setMemberList(data: List<Member>) {
        memberList.clear()
        memberList.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return memberList.size
    }
}