package com.ivanr.misrecetas.actividades

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ivanr.misrecetas.MainActivity2
import com.ivanr.misrecetas.R
import com.ivanr.misrecetas.utilidades.AdminSQLite
import com.ivanr.misrecetas.utilidades.Parametros
import com.ivanr.misrecetas.utilidades.Utilidades


class ActividadNuevaReceta : AppCompatActivity() {
    val util = Utilidades
    private val rParam = Parametros
    lateinit var etDescripcion: TextView
    lateinit var etElaboracion:TextView
    lateinit var etUrl:TextView
    var vg_imagen: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nueva_receta)
        etDescripcion = findViewById(R.id.etDescripcion)
        etElaboracion = findViewById(R.id.etIndicaciones)
        etUrl = findViewById(R.id.etUrl)

        findViewById<Button>(R.id.bt_Crear_Receta).setOnClickListener {
            crearReceta()
        }
        findViewById<ImageButton>(R.id.ib_ir_url).setOnClickListener {
            var v_url = etUrl.getText().toString()
            if (v_url != "" ) {
                util.navega_url(this, v_url)
            }
            else {
                util.mensaje(this, "No hay URL para navegar")
            }
        }
        findViewById<Button>(R.id.btSeImagen).setOnClickListener {
            seleccionarImagen()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== RESULT_OK && requestCode == rParam.PHOTO_SELECTED) {
            val selectedImage = data!!.data
            findViewById<ImageView>(R.id.ivImagenReceta).setImageURI(selectedImage)
            vg_imagen = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage)
        }
    }
    fun seleccionarImagen() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, rParam.PHOTO_SELECTED)
    }

    fun crearReceta () {
        val admin = AdminSQLite(this, "recetas", null, rParam.VERSION_BD)
        //    var vg_imagen_bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_imagen_vacio)
        admin.creaReceta(admin, etDescripcion.getText().toString(), etElaboracion.getText().toString(), etUrl.getText().toString(), vg_imagen, "N")
        //Volvemos atras y actualizarmos los datos al crear el MainActivity
        val intent = Intent(this, MainActivity2::class.java)
        startActivity(intent)
    }
}

