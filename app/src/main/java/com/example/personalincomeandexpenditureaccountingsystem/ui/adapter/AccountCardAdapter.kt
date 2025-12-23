package com.example.personalincomeandexpenditureaccountingsystem.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.personalincomeandexpenditureaccountingsystem.data.entity.Account
import com.example.personalincomeandexpenditureaccountingsystem.databinding.ItemAccountBinding

/**
 * 账本卡片列表 Adapter
 */
class AccountCardAdapter(
    private val onItemClick: (Account) -> Unit = {},
    private val onEditClick: (Account) -> Unit = {},
    private val onDeleteClick: (Account) -> Unit = {}
) : ListAdapter<Account, AccountCardAdapter.AccountViewHolder>(AccountDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val binding = ItemAccountBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AccountViewHolder(binding, onItemClick, onEditClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AccountViewHolder(
        private val binding: ItemAccountBinding,
        private val onItemClick: (Account) -> Unit,
        private val onEditClick: (Account) -> Unit,
        private val onDeleteClick: (Account) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(account: Account) {
            binding.apply {
                tvAccountName.text = account.name
                tvBalance.text = String.format("¥%.2f", account.balance)
                tvCreatedDate.text = "创建于 ${java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.CHINA).format(java.util.Date())}"

                // 设置点击监听
                root.setOnClickListener { onItemClick(account) }
                btnViewDetails.setOnClickListener { onItemClick(account) }
                btnEdit.setOnClickListener { onEditClick(account) }
                btnDelete.setOnClickListener { onDeleteClick(account) }
            }
        }
    }
}

class AccountDiffCallback : DiffUtil.ItemCallback<Account>() {
    override fun areItemsTheSame(oldItem: Account, newItem: Account): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Account, newItem: Account): Boolean {
        return oldItem == newItem
    }
}
