package pl.example.databasemodule.database.security

import android.content.Context
import androidx.core.content.edit
import com.scottyab.rootbeer.RootBeer
import net.sqlcipher.database.SQLiteDatabase
import kotlin.system.exitProcess

fun isDeviceRooted(context: Context): Boolean {
    val rootBeer = RootBeer(context)
    return rootBeer.isRooted
}

fun loadBase(context: Context){
    SQLiteDatabase.loadLibs(context)
}

fun deleteDatabase(context: Context) {
    context.deleteDatabase("GlucoConnect_mobileBase")
}

fun wipeAppDataAndExit(context: Context) {
    context.deleteDatabase("GlucoConnect_mobileBase")
    context.getSharedPreferences("gluco_db_secure_prefs", Context.MODE_PRIVATE)
        .edit { clear() }

    android.os.Process.killProcess(android.os.Process.myPid())
    exitProcess(0)
}
