package com.example.personalincomeandexpenditureaccountingsystem.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.personalincomeandexpenditureaccountingsystem.R
import com.example.personalincomeandexpenditureaccountingsystem.data.entity.Record
import com.example.personalincomeandexpenditureaccountingsystem.databinding.ItemRecordBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * 收支记录列表 Adapter
 * 使用 ListAdapter 和 DiffUtil 优化列表性能
 */
class RecordAdapter(
    private val onItemClick: (Record) -> Unit = {},
    private val onItemLongClick: (Record) -> Unit = {},
    private val onDeleteClick: (Record) -> Unit = {}
) : ListAdapter<RecordGroup, RecyclerView.ViewHolder>(RecordDiffCallback()) {

    companion object {
        private const val TYPE_DATE_HEADER = 0
        private const val TYPE_RECORD_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is RecordGroup.DateHeader -> TYPE_DATE_HEADER
            is RecordGroup.RecordItem -> TYPE_RECORD_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_DATE_HEADER -> {
                val binding = ItemRecordBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                DateHeaderViewHolder(binding)
            }
            TYPE_RECORD_ITEM -> {
                val binding = ItemRecordBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                RecordViewHolder(binding, onItemClick, onItemLongClick, onDeleteClick)
            }
            else -> throw IllegalArgumentException("Invalid view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is RecordGroup.DateHeader -> (holder as DateHeaderViewHolder).bind(item.date)
            is RecordGroup.RecordItem -> (holder as RecordViewHolder).bind(item.record)
        }
    }

    /**
     * 日期组头 ViewHolder
     */
    class DateHeaderViewHolder(private val binding: ItemRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(date: String) {
            binding.tvDateHeader.text = date
            binding.tvDateHeader.visibility = android.view.View.VISIBLE
            binding.itemRecordContainer.visibility = android.view.View.GONE
        }
    }

    /**
     * 记录项 ViewHolder
     */
    class RecordViewHolder(
        private val binding: ItemRecordBinding,
        private val onItemClick: (Record) -> Unit,
        private val onItemLongClick: (Record) -> Unit,
        private val onDeleteClick: (Record) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var currentRecord: Record

        init {
            binding.root.setOnClickListener {
                onItemClick(currentRecord)
            }
            binding.root.setOnLongClickListener {
                onItemLongClick(currentRecord)
                true
            }
            binding.btnDelete.setOnClickListener {
                onDeleteClick(currentRecord)
            }
        }

        fun bind(record: Record) {
            currentRecord = record

            binding.apply {
                // 标题
                tvTitle.text = record.title

                // 金额
                val amountText = if (record.type == "income") {
                    "+¥${String.format("%.2f", record.amount)}"
                } else {
                    "-¥${String.format("%.2f", record.amount)}"
                }
                tvAmount.text = amountText

                // 金额颜色：收入绿色，支出红色
                val amountColor = if (record.type == "income") {
                    ContextCompat.getColor(itemView.context, R.color.income_green)
                } else {
                    ContextCompat.getColor(itemView.context, R.color.expense_red)
                }
                tvAmount.setTextColor(amountColor)

                // 分类
                tvCategory.text = record.category

                // 日期
                val dateFormat = SimpleDateFormat("HH:mm", Locale.CHINA)
                val timeStr = dateFormat.format(Date(record.date))
                tvTime.text = timeStr

                // 备注
                tvNote.text = record.note
                tvNote.visibility = if (record.note.isNotEmpty()) {
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }

                // 显示记录容器
                itemRecordContainer.visibility = android.view.View.VISIBLE
                tvDateHeader.visibility = android.view.View.GONE
            }
        }
    }
}

/**
 * 记录分组数据结构
 */
sealed class RecordGroup {
    data class DateHeader(val date: String) : RecordGroup()
    data class RecordItem(val record: Record) : RecordGroup()
}

/**
 * DiffUtil 回调，用于计算列表项的差异
 */
class RecordDiffCallback : DiffUtil.ItemCallback<RecordGroup>() {
    override fun areItemsTheSame(oldItem: RecordGroup, newItem: RecordGroup): Boolean {
        return when {
            oldItem is RecordGroup.DateHeader && newItem is RecordGroup.DateHeader ->
                oldItem.date == newItem.date
            oldItem is RecordGroup.RecordItem && newItem is RecordGroup.RecordItem ->
                oldItem.record.id == newItem.record.id
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: RecordGroup, newItem: RecordGroup): Boolean {
        return oldItem == newItem
    }
}

/**
 * 将记录列表按日期分组
 */
fun List<Record>.groupByDate(): List<RecordGroup> {
    val grouped = mutableListOf<RecordGroup>()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd EEEE", Locale.CHINA)
    var lastDate: String? = null

    this.forEach { record ->
        val currentDate = dateFormat.format(Date(record.date))

        // 如果日期改变，添加日期组头
        if (lastDate != currentDate) {
            grouped.add(RecordGroup.DateHeader(currentDate))
            lastDate = currentDate
        }

        // 添加记录项
        grouped.add(RecordGroup.RecordItem(record))
    }

    return grouped
}
