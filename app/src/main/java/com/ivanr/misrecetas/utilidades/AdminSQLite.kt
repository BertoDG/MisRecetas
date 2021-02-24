package com.ivanr.misrecetas.utilidades

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.util.Log
import com.ivanr.misrecetas.clases.Categoria
import com.ivanr.misrecetas.clases.ImagenesReceta
import com.ivanr.misrecetas.clases.Nota
import com.ivanr.misrecetas.clases.Receta

class AdminSQLite(context: Context?, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {
    val util = Utilidades

    override fun onCreate(db: SQLiteDatabase) {
        //Tablas para version 1, de base de datos.
        db.execSQL("create table recetas(codigo INTEGER PRIMARY KEY autoincrement, \n" +
                " descripcion text,\n" +
                " ingredientes text,\n" +
                " elaboracion text,\n" +
                " foto blob,\n" +
                " url text,\n" +
                " favorito text)")
        db.execSQL("create table recetas_his(receta integer ,\n" +
                " linea integer ,\n" +
                " fecha date,\n" +
                " notas text,\n" +
                " ingredientes text,\n" +
                " elbaoracion text,\n" +
                " foto blob)")
        db.execSQL("create table recetas_img(receta integer,\n" +
                " linea integer,\n" +
                " foto blob, \n"+
                " linea_nota INTEGER)")
        db.execSQL("create table recetas_cat (codigo integer primary key autoincrement, \n" +
                " descripcion text)")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
    fun creaCategoria (p_admin: AdminSQLite, pcl_categoria: Categoria) {
        val bd = p_admin.writableDatabase
        val categoria = ContentValues ()
        categoria.put ("descripcion", pcl_categoria.descripcion)
        bd.insert ("categorias", null, categoria)
        bd.close ()
    }
    fun creaReceta(p_admin: AdminSQLite, p_descripcion: String, p_elaboracion: String, p_url: String, p_foto: Bitmap?, p_favorito: String) {
        val bd = p_admin.writableDatabase
        val receta = ContentValues()
        var v_imagen_ba = util.img_to_array(p_foto)
        receta.put("descripcion", p_descripcion)
        receta.put("elaboracion", p_elaboracion)
        receta.put("url", p_url)
        receta.put("foto", v_imagen_ba)
        receta.put("favorito", p_favorito)
        bd.insert("recetas", null, receta)
        bd.close()
    }
    fun borrarReceta(p_admin: AdminSQLite, p_id_Receta: Int?) {
        val bd = p_admin.writableDatabase
        bd.delete("recetas", "codigo=${p_id_Receta}", null)
        bd.close()
    }
    fun consultar(p_admin: AdminSQLite, p_sql: String): Cursor? {
        val bd = p_admin.writableDatabase
        var fila = bd.rawQuery(p_sql, null)
        return fila
    }
    fun actualizar(p_admin: AdminSQLite, p_tabla: String, p_id_Receta: Int?, p_campos_string: Array<String?> , p_valores_string: Array<String?>) {
        val bd = p_admin.writableDatabase
        val receta = ContentValues()
        var v_Actualizar = "N"
        if (p_campos_string.size > 0) {
            var vCont = 0
            for (campo in p_campos_string) {
                if (campo != null) {
                    receta.put(campo, p_valores_string[vCont])
                    vCont++
                }
            }
            v_Actualizar = "S"
        }

        if (v_Actualizar == "S") {
            bd.update(p_tabla, receta, "codigo=${p_id_Receta}", null)
        }
    }
    fun carga_lista_recetas(fila: Cursor?, p_registros:Int):  ArrayList<Receta> {
        var listaRecetas = ArrayList<Receta>()
        var v_Cont:Int = 0

        listaRecetas.clear()
        if (fila != null) {
            if (fila.moveToFirst()) {
                do {
                    val id = fila.getInt(fila.getColumnIndex("codigo"))
                    val descripcion = fila.getString(fila.getColumnIndex("descripcion"))
                    var elaboracion = fila.getString(fila.getColumnIndex("elaboracion"))
                    var ingredientes = fila.getString(fila.getColumnIndex("ingredientes"))
                    val favorito = fila.getString(fila.getColumnIndex("favorito"))
                    var url = fila.getString(fila.getColumnIndex("url"))
                    var foto = fila.getBlob(fila.getColumnIndex("foto"))

                    if (elaboracion==null){elaboracion=""}
                    if (ingredientes == null) {ingredientes = ""}
                    if (url==null){url=""}

                    var foto_bm = util.array_to_img(foto)
                    val vr_receta = Receta(id, descripcion, ingredientes, elaboracion, url, favorito)
                    vr_receta.put_foto(foto_bm)
                    listaRecetas.add(vr_receta)
                    v_Cont ++
                    if (v_Cont == p_registros) { break }
                } while (fila.moveToNext() )
            }
        }
        return listaRecetas
    }

    fun carga_lista_imagenes (fila: Cursor?):ArrayList<ImagenesReceta>{
        var listaImagenes = ArrayList<ImagenesReceta>()
        listaImagenes.clear()

        if (fila!=null){
            if (fila.moveToFirst()) {
                do {
                    val id_receta = fila.getInt(fila.getColumnIndex("receta"))
                    val id_linea_nota = fila.getInt(fila.getColumnIndex("id_linea_nota"))
                    val id_linea = fila.getInt(fila.getColumnIndex("id_linea"))
                    var foto = fila.getBlob(fila.getColumnIndex("foto"))

                    var foto_bm = util.array_to_img(foto)
                    var v_Imagen_Receta = ImagenesReceta (id_receta, id_linea, id_linea_nota, foto_bm)
                    listaImagenes.add(v_Imagen_Receta)

                } while (fila.moveToNext() )
            }
        }
        return listaImagenes
    }

    fun carga_lista_notas (fila: Cursor?):ArrayList<Nota> {
        var listaNotas = ArrayList <Nota>()
        listaNotas.clear()
        if (fila!=null){
            if (fila.moveToFirst()) {
                do {
                    val id_receta = fila.getInt(fila.getColumnIndex("receta"))
                    val id_linea_nota = fila.getInt(fila.getColumnIndex("linea"))
                    val fecha = fila.getString(fila.getColumnIndex("fecha"))
                    var descripcion = fila.getString(fila.getColumnIndex("notas"))

                    var v_Nota = Nota (id_receta, id_linea_nota, fecha, descripcion)
                    listaNotas.add(v_Nota)

                } while (fila.moveToNext() )
            }
        }
        return listaNotas
    }
}