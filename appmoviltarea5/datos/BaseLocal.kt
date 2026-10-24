package com.example.appmoviltarea5.datos

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class UsuarioFavorito(
    val id: Int,
    val nombre: String,
    val correo: String,
    val telefono: String?,
    val sitioWeb: String?,
    val empresa: String
)

fun Usuario.aFavorito() = UsuarioFavorito(id, nombre, correo, telefono, sitioWeb, empresa.nombre)

fun UsuarioFavorito.aUsuario() = Usuario(id, nombre, correo, telefono, sitioWeb, Empresa(empresa))

class BaseFavoritos(contexto: Context) :
    SQLiteOpenHelper(contexto.applicationContext, "directorio.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""CREATE TABLE favoritos (
            id INTEGER PRIMARY KEY,
            nombre TEXT NOT NULL,
            correo TEXT NOT NULL,
            telefono TEXT,
            sitio_web TEXT,
            nombre_empresa TEXT NOT NULL
        )""")
    }

    override fun onUpgrade(db: SQLiteDatabase, versionAnterior: Int, versionNueva: Int) {
        db.execSQL("DROP TABLE IF EXISTS favoritos")
        onCreate(db)
    }

    fun obtenerTodos(): List<UsuarioFavorito> {
        val lista = mutableListOf<UsuarioFavorito>()
        readableDatabase.rawQuery(
            "SELECT id, nombre, correo, telefono, sitio_web, nombre_empresa " + "FROM favoritos ORDER BY nombre",
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                lista += UsuarioFavorito(
                    cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), cursor.getString(5)
                )
            }
        }
        return lista
    }

    fun insertar(favorito: UsuarioFavorito) {
        val valores = ContentValues().apply {
            put("id", favorito.id)
            put("nombre", favorito.nombre)
            put("correo", favorito.correo)
            put("telefono", favorito.telefono)
            put("sitio_web", favorito.sitioWeb)
            put("nombre_empresa", favorito.empresa)
        }
        writableDatabase.insertWithOnConflict(
            "favoritos", null, valores, SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun eliminar(id: Int) {
        writableDatabase.delete("favoritos", "id = ?", arrayOf(id.toString()))
    }
}
