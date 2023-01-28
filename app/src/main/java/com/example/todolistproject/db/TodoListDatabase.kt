package com.example.todolistproject.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.todolistproject.R
import com.example.todolistproject.db.entities.Category
import com.example.todolistproject.db.entities.ToDo
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader



@Database(entities = [ToDo::class, Category::class], version = 1)
abstract class TodoListDatabase : RoomDatabase() {
    abstract fun getDao(): TodoListDao


    companion object {
        @Volatile
        private var INSTANCE: TodoListDatabase? = null
        private val LOCK = Any()

        fun invoke(context: Context) = synchronized(LOCK) {
            INSTANCE ?: createDatabase(context).also {
                INSTANCE = it
            }
        }


        private fun createDatabase(context: Context): TodoListDatabase {
            return Room
                .databaseBuilder(
                    context, TodoListDatabase::class.java, "todo.db"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Log.d("InitialCallback", "InitialCallback.onCreate()")
                        CoroutineScope(Dispatchers.IO).launch {
                            fillWithInitialCategories(context)
                        }

                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        Log.d("InitialCallback", "InitialCallback.onOpen()")

                    }

                    private fun loadJSONArray(context: Context): JSONArray{

                        val inputStream = context.resources.openRawResource(R.raw.categories)

                        BufferedReader(inputStream.reader()).use {
                            return JSONArray(it.readText())
                        }
                    }
                    private suspend fun fillWithInitialCategories(context: Context){

                        val dao = invoke(context).getDao()

                        try {
                            val categories = loadJSONArray(context)
                            if (categories != null){
                                for (i in 0 until categories.length()){
                                    val item = categories.getJSONObject(i)
                                    Log.d("InitialCallback", item.getString("title"))

                                    dao.insertCategory(Category(item.getString("title")))
                                }
                            }
                        }

                        catch (e: JSONException) {
                            Log.d("JSONException", "fillWithStartingNotes: $e")
                        }
                    }


                })
                .build()
        }
    }

}