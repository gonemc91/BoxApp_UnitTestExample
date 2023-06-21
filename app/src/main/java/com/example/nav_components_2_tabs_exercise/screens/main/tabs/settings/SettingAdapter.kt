package com.example.nav_components_2_tabs_exercise.screens.main.tabs.settings

import android.location.GnssAntennaInfo.Listener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.nav_components_2_tabs_exercise.R
import com.example.nav_components_2_tabs_exercise.model.boxes.entities.Box

class SettingAdapter(
    private val listener: Listener
) : RecyclerView.Adapter<SettingAdapter.Holder>(), View.OnClickListener {

    private var settings: List<BoxSetting> = emptyList()

    override fun onClick(v: View?) {
        val checkBox = v as CheckBox
        val box = v.tag as Box
        if (checkBox.isChecked){
            listener.enableBox(box)
        }else{
            listener.disableBox(box)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflate = LayoutInflater.from(parent.context)
        val checkBox = inflate.inflate(R.layout.item_setting, parent, false) as CheckBox
        checkBox.setOnClickListener(this)
        return Holder(checkBox)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val setting = settings[position]
        val context = holder.itemView.context
        holder.checkBox.tag = setting.box


        if(holder.checkBox.isChecked != setting.enabled){
            holder.checkBox.isChecked = setting.enabled
        }

        val colorName = setting.box.colorName
        holder.checkBox.text = context.getString(R.string.enable_checkbox, colorName)
    }

    override fun getItemCount(): Int = settings.size

    fun renderSettings(settings: List<BoxSetting>){
        val diffResult = DiffUtil.calculateDiff(BoxSettingDiffCallback(this.settings, settings))
        this.settings = settings
        diffResult.dispatchUpdatesTo(this)
    }


    class Holder(val checkBox: CheckBox) : RecyclerView.ViewHolder(checkBox)

    interface Listener {
        fun enableBox(box: Box)
        fun disableBox(box: Box)
    }
}