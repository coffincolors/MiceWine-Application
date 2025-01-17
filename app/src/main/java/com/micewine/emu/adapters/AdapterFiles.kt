package com.micewine.emu.adapters

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.micewine.emu.R
import com.micewine.emu.activities.MainActivity.Companion.ACTION_SELECT_FILE_MANAGER
import com.micewine.emu.activities.MainActivity.Companion.customRootFSPath
import com.micewine.emu.activities.MainActivity.Companion.fileManagerCwd
import com.micewine.emu.activities.MainActivity.Companion.fileManagerDefaultDir
import com.micewine.emu.activities.MainActivity.Companion.selectedFile
import com.micewine.emu.activities.MainActivity.Companion.usrDir
import com.micewine.emu.core.WineWrapper.extractIcon
import com.micewine.emu.fragments.FloatingFileManagerFragment.Companion.refreshFiles
import java.io.File

class AdapterFiles(private val fileList: List<FileList>, private val context: Context, private val isFloatFilesDialog: Boolean) : RecyclerView.Adapter<AdapterFiles.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_files_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sList = fileList[position]

        if (fileManagerCwd == fileManagerDefaultDir) {
            holder.fileName.text = sList.file.name.uppercase()
        } else {
            holder.fileName.text = sList.file.name
        }

        if (sList.file.isDirectory) {
            holder.icon.setImageResource(R.drawable.ic_folder)
        } else if (sList.file.isFile) {
            if (sList.file.name.endsWith(".exe")) {
                val output = "$usrDir/icons/${sList.file.nameWithoutExtension}-icon.ico"

                extractIcon(sList.file, output)

                if (File(output).exists()) {
                    holder.icon.setImageBitmap(BitmapFactory.decodeFile(output))
                } else {
                    holder.icon.setImageResource(R.drawable.ic_log)
                }
            } else if (sList.file.name.endsWith(".rat")) {
                holder.icon.setImageResource(R.drawable.icon_grayscale)
            } else {
                holder.icon.setImageResource(R.drawable.ic_log)
            }
        }
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        val fileName: TextView = itemView.findViewById(R.id.title_preferences_model)
        val icon: ImageView = itemView.findViewById(R.id.set_img)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition < 0) {
                return
            }

            val settingsModel = fileList[adapterPosition]

            if (isFloatFilesDialog) {
                if (settingsModel.file.name == "..") {
                    fileManagerCwd = File(fileManagerCwd).parent!!

                    refreshFiles()
                } else if (settingsModel.file.isFile) {
                    if (settingsModel.file.name.contains(".rat")) {
                        customRootFSPath = settingsModel.file.path
                    }
                } else if (settingsModel.file.isDirectory) {
                    fileManagerCwd = settingsModel.file.path

                    refreshFiles()
                }
            } else {
                val intent = Intent(ACTION_SELECT_FILE_MANAGER).apply {
                    putExtra("selectedFile", settingsModel.file.path)
                }

                context.sendBroadcast(intent)
            }
        }

        override fun onLongClick(v: View): Boolean {
            val settingsModel = fileList[adapterPosition]

            selectedFile = settingsModel.file.path

            return (fileManagerCwd == fileManagerDefaultDir) || selectedFile == ".."
        }
    }

    class FileList(var file: File)
}