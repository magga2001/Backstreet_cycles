package com.example.backstreet_cycles.domain.utils

import android.app.Application
import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.*

object JsonHelper {

    /**
     *
     */
    fun getJsonFromResources(application: Application, resourceId: Int): String {
        return application.resources.openRawResource(resourceId).use { it ->
            it.bufferedReader().use {
                it.readText()
            }
        }
    }

    /**
     *
     */
//    fun writeJsonToResources(application: Application, json: String, resourceId: Int) {
//        val writeFile: String = application.resources.getResourceName(resourceId)
//        File(writeFile).writeText(json)
//    }


     fun writeJsonFile(context: Context, fileName: String, jsonString: String?): Boolean {
        return try {
            val fos: FileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            if (jsonString != null) {
                fos.write(jsonString.toByteArray())
            }
            fos.close()
            true
        } catch (fileNotFound: FileNotFoundException) {
            false
        } catch (ioException: IOException) {
            false
        }
    }

     fun readJsonFile(context: Context, fileName: String): String? {
        return try {
            val fis: FileInputStream = context.openFileInput(fileName)
            val isr = InputStreamReader(fis)
            val bufferedReader = BufferedReader(isr)
            val sb = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            sb.toString()
        } catch (fileNotFound: FileNotFoundException) {
            null
        } catch (ioException: IOException) {
            null
        }
    }

    fun isFilePresent(context: Context, fileName: String): Boolean {
        val path = context.filesDir.absolutePath + "/" + fileName
        val file = File(path)
        return file.exists()
    }

    /**
     *
     */
    fun <T> stringToObject(text: String, type: Class<T>): List<T>? {
        val parameterizedType = Types.newParameterizedType(List::class.java, type)
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val adapter: JsonAdapter<List<T>> = moshi.adapter(parameterizedType)
        return adapter.fromJson(text)
    }

    /**
     *
     */
    fun <T> objectToString(values: List<T>, type: Class<T>): String {
        val parameterizedType = Types.newParameterizedType(List::class.java, type)
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val adapter: JsonAdapter<List<T>> = moshi.adapter(parameterizedType)
        return adapter.toJson(values)
    }
}