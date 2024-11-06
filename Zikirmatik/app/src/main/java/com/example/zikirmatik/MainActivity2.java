package com.example.zikirmatik;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.zikirmatik.databinding.ActivityMain2Binding;
import com.example.zikirmatik.databinding.ActivityMainBinding;

import java.security.PublicKey;
import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {
    private ActivityMain2Binding binding;
    ArrayList<Zikir> zikirArrayListe;
    SQLiteDatabase database;
    ZikirAdapter zikirAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        zikirArrayListe = new ArrayList<Zikir>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        zikirAdapter = new ZikirAdapter(zikirArrayListe);
        binding.recyclerView.setAdapter(zikirAdapter);
        getDataZikir();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Zikirlerim");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.house_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        database=this.openOrCreateDatabase("zikirler",MODE_PRIVATE,null);
    }

    private void getDataZikir(){
        try {
            database = this.openOrCreateDatabase("zikirmatik", MODE_PRIVATE, null);
            Cursor cursor=database.rawQuery("Select * from zikirler ORDER BY id DESC",null);
            int idIx=cursor.getColumnIndex("id");
            int adetIx=cursor.getColumnIndex("zikirAdet");
            int nameIx=cursor.getColumnIndex("zikirAd");
            int durakNoIx=cursor.getColumnIndex("durakNo");
            while (cursor.moveToNext()){
                String zikir_adi=cursor.getString(nameIx);
                int zikir_adet=cursor.getInt(adetIx);
                int id=cursor.getInt(idIx);
                int durakNo=cursor.getInt(durakNoIx);
                Zikir zikir=new Zikir(id,zikir_adi,zikir_adet,durakNo);
                zikirArrayListe.add(zikir);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Çıkış Yapma İşlemi")
                .setMessage("Çıkmak istediğinizden emin misiniz?.")
                .setIcon(R.drawable.exit_to_app_24px)
                .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .show();
        return super.onOptionsItemSelected(item);
    }

    public void anasayfaGit(View view){
        Intent intent = new Intent(MainActivity2.this, MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

}