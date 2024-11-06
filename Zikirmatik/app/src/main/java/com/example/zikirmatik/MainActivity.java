package com.example.zikirmatik;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.zikirmatik.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    boolean sesAcik=true;
    private Vibrator vibrator;
    private boolean isVibrating = true;
    Integer zikirSayisi=0;
    Integer onbinler=0,binler=0, yuzler=0,onlar=0,birler=0,zikirSayisiEski,temaSiraNo=0,zikirEkleId=0;
    View rootView;
    List<Gorsel> temaList = new ArrayList<Gorsel>();
    SharedPreferences sharedPreferences;
    private MediaPlayer mediaPlayer;
    private MediaPlayer mediaPlayer2;
    SQLiteDatabase database;
    Zikir gelen_zikir=null;
    Zikir degisecek_zikir=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mediaPlayer = MediaPlayer.create(this, R.raw.mouse_click);
        mediaPlayer2 = MediaPlayer.create(this, R.raw.bildirim);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        rootView = findViewById(R.id.main);
        Gorsel ekran1 = new Gorsel(0,R.drawable.ekran1);
        Gorsel ekran2 = new Gorsel(1,R.drawable.ekran2);
        Gorsel ekran3 = new Gorsel(2,R.drawable.ekran3);
        Gorsel ekran4 = new Gorsel(3,R.drawable.ekran4);
        Gorsel ekran5 = new Gorsel(4,R.drawable.ekran5);
        Gorsel ekran6 = new Gorsel(5,R.drawable.ekran6);
        temaList.add(ekran1);
        temaList.add(ekran2);
        temaList.add(ekran3);
        temaList.add(ekran4);
        temaList.add(ekran5);
        temaList.add(ekran6);
        binding.imageView.setImageResource(temaList.get(0).foto);
        sharedPreferences=this.getPreferences(Context.MODE_PRIVATE);
        int sharedPreferencesZikirSayisi =sharedPreferences.getInt("tutulanZikirSayisi",0);
        int sharedPreferencesTema =sharedPreferences.getInt("tutulanTema",-1);
        database = this.openOrCreateDatabase("zikirmatik", MODE_PRIVATE, null);
        String TABLO="CREATE TABLE IF NOT EXISTS zikirler(id INTEGER PRIMARY KEY, zikirAdet INTEGER,zikirAd VARCHAR,durakNo INTEGER)";
        database.execSQL(TABLO);
        if (sharedPreferencesZikirSayisi!=0){
            zikirSayisi=sharedPreferencesZikirSayisi;
            zikirYazdir();
        }
        if (sharedPreferencesTema!=-1){
            temaSiraNo=sharedPreferencesTema;
            binding.imageView.setImageResource(temaList.get(temaSiraNo).foto);
        }
        mediaPlayer2.start();
        mediaPlayer2.pause();
        setSupportActionBar(toolbar);
        setTitle("SHN Zikirmatik");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.house_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Zikirmatik yeniden başladı", Toast.LENGTH_SHORT).show();
                recreate();
            }
        });
        binding.btnGeri.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_undo_24,0,0,0);
        binding.btnSifirla.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_autorenew_24,0,0,0);
        Intent intent = getIntent();
        gelen_zikir = (Zikir) getIntent().getSerializableExtra("gonderilen_zikir");

        if (gelen_zikir!=null){
            binding.imgBtnZikirEkle.setImageResource(R.drawable.add_task_24px);
            zikirSayisi=gelen_zikir.adet;
            zikirYazdir();
            intent.removeExtra("gonderilen_zikir");

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
                .setIcon(R.drawable.exit_to_app_24px2)
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

    public void zikirYazdir(){
        zikirSayisiEski=zikirSayisi;
        onbinler=zikirSayisi/10000;
        zikirSayisi=zikirSayisi-(onbinler*10000);
        binler=zikirSayisi/1000;
        zikirSayisi=zikirSayisi-(binler*1000);
        yuzler=zikirSayisi/100;
        zikirSayisi=zikirSayisi-(yuzler*100);
        onlar=zikirSayisi/10;
        zikirSayisi=zikirSayisi-(onlar*10);
        birler=zikirSayisi;
        zikirSayisi=zikirSayisiEski;
        //birler
        if (birler==0){
            binding.imageViewBirler.setImageResource(R.drawable.sifir);
        } else if (birler==1) {
            binding.imageViewBirler.setImageResource(R.drawable.bir);
        } else if (birler==2) {
            binding.imageViewBirler.setImageResource(R.drawable.iki);
        } else if (birler==3) {
            binding.imageViewBirler.setImageResource(R.drawable.uc);
        }else if (birler==4) {
            binding.imageViewBirler.setImageResource(R.drawable.dort);
        }else if (birler==5) {
            binding.imageViewBirler.setImageResource(R.drawable.bes);
        }else if (birler==6) {
            binding.imageViewBirler.setImageResource(R.drawable.alti);
        }else if (birler==7) {
            binding.imageViewBirler.setImageResource(R.drawable.yedi);
        }else if (birler==8) {
            binding.imageViewBirler.setImageResource(R.drawable.sekiz);
        }else if (birler==9) {
            binding.imageViewBirler.setImageResource(R.drawable.dokuz);
        }
        //onlar
        if (onlar==0){
            binding.imageViewOnlar.setImageResource(R.drawable.sifir);
        } else if (onlar==1) {
            binding.imageViewOnlar.setImageResource(R.drawable.bir);
        } else if (onlar==2) {
            binding.imageViewOnlar.setImageResource(R.drawable.iki);
        } else if (onlar==3) {
            binding.imageViewOnlar.setImageResource(R.drawable.uc);
        }else if (onlar==4) {
            binding.imageViewOnlar.setImageResource(R.drawable.dort);
        }else if (onlar==5) {
            binding.imageViewOnlar.setImageResource(R.drawable.bes);
        }else if (onlar==6) {
            binding.imageViewOnlar.setImageResource(R.drawable.alti);
        }else if (onlar==7) {
            binding.imageViewOnlar.setImageResource(R.drawable.yedi);
        }else if (onlar==8) {
            binding.imageViewOnlar.setImageResource(R.drawable.sekiz);
        }else if (onlar==9) {
            binding.imageViewOnlar.setImageResource(R.drawable.dokuz);
        }
        //Yüzler
        if (yuzler==0){
            binding.imageViewYuzler.setImageResource(R.drawable.sifir);
        } else if (yuzler==1) {
            binding.imageViewYuzler.setImageResource(R.drawable.bir);
        } else if (yuzler==2) {
            binding.imageViewYuzler.setImageResource(R.drawable.iki);
        } else if (yuzler==3) {
            binding.imageViewYuzler.setImageResource(R.drawable.uc);
        }else if (yuzler==4) {
            binding.imageViewYuzler.setImageResource(R.drawable.dort);
        }else if (yuzler==5) {
            binding.imageViewYuzler.setImageResource(R.drawable.bes);
        }else if (yuzler==6) {
            binding.imageViewYuzler.setImageResource(R.drawable.alti);
        }else if (yuzler==7) {
            binding.imageViewYuzler.setImageResource(R.drawable.yedi);
        }else if (yuzler==8) {
            binding.imageViewYuzler.setImageResource(R.drawable.sekiz);
        }else if (yuzler==9) {
            binding.imageViewYuzler.setImageResource(R.drawable.dokuz);
        }
        //binler
        if (binler==0){
            binding.imageViewBinler.setImageResource(R.drawable.sifir);
        } else if (binler==1) {
            binding.imageViewBinler.setImageResource(R.drawable.bir);
        } else if (binler==2) {
            binding.imageViewBinler.setImageResource(R.drawable.iki);
        } else if (binler==3) {
            binding.imageViewBinler.setImageResource(R.drawable.uc);
        }else if (binler==4) {
            binding.imageViewBinler.setImageResource(R.drawable.dort);
        }else if (binler==5) {
            binding.imageViewBinler.setImageResource(R.drawable.bes);
        }else if (binler==6) {
            binding.imageViewBinler.setImageResource(R.drawable.alti);
        }else if (binler==7) {
            binding.imageViewBinler.setImageResource(R.drawable.yedi);
        }else if (binler==8) {
            binding.imageViewBinler.setImageResource(R.drawable.sekiz);
        }else if (binler==9) {
            binding.imageViewBinler.setImageResource(R.drawable.dokuz);
        }
        //Onbinler
        if (onbinler==0){
            binding.imageViewOnbinler.setImageResource(R.drawable.sifir);
        } else if (onbinler==1) {
            binding.imageViewOnbinler.setImageResource(R.drawable.bir);
        } else if (onbinler==2) {
            binding.imageViewOnbinler.setImageResource(R.drawable.iki);
        } else if (onbinler==3) {
            binding.imageViewOnbinler.setImageResource(R.drawable.uc);
        }else if (onbinler==4) {
            binding.imageViewOnbinler.setImageResource(R.drawable.dort);
        }else if (onbinler==5) {
            binding.imageViewOnbinler.setImageResource(R.drawable.bes);
        }else if (onbinler==6) {
            binding.imageViewOnbinler.setImageResource(R.drawable.alti);
        }else if (onbinler==7) {
            binding.imageViewOnbinler.setImageResource(R.drawable.yedi);
        }else if (onbinler==8) {
            binding.imageViewOnbinler.setImageResource(R.drawable.sekiz);
        }else if (onbinler==9) {
            binding.imageViewOnbinler.setImageResource(R.drawable.dokuz);
        }
    }

    public void btnTemaDegistir(View view){
        if (temaSiraNo>=5)
        {
            temaSiraNo=-1;
        }
        temaSiraNo++;
        binding.imageView.setImageResource(temaList.get(temaSiraNo).foto);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("tutulanTema", temaSiraNo);
        editor.apply();
        //Snackbar snackbar = Snackbar.make(view, "Tema değişti..", Snackbar.LENGTH_SHORT);
        //snackbar.show();
    }

    public void btnArti(View view) {
        if (sesAcik)
        {
            mediaPlayer.start();
        }
        else{
            mediaPlayer.pause();
        }
        if (isVibrating) {
            vibrator.vibrate(60);
        }
        else {
            vibrator.cancel();
        }
        if (zikirSayisi>=99999){
            zikirSayisi=0;
        }
        zikirSayisi++;
        zikirYazdir();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("tutulanZikirSayisi", zikirSayisi);
        editor.apply();

        if (zikirSayisi==33 || zikirSayisi==66 || zikirSayisi==99){
            vibrator.vibrate(400);
        }
        if (gelen_zikir!=null){
            if (zikirSayisi.equals(gelen_zikir.durakNo))
            {
                vibrator.vibrate(500);
                Snackbar snackbar = Snackbar.make(view, "Hedef zikir sayısına ulaşıldı..!", Snackbar.LENGTH_SHORT);
                snackbar.setAction("Tamam", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();

            }
        }

    }

    public void zikirSayisiGeri(View view) {
        if (zikirSayisi>0){
            zikirSayisi=zikirSayisi-2;
            btnArti(view);
        }
    }

    //zikir ekleme ve Güncelleme
    @SuppressLint("SetTextI18n")
    public void showFormDialog(View view) {
        Zikir degisecek_zikir=null;
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_form, null);
        dialogView.setBackgroundColor(Color.parseColor("#ffffff"));
        EditText zikirSayisiEditText = dialogView.findViewById(R.id.zikirSayisiEditText);
        EditText zikirAdiEditText = dialogView.findViewById(R.id.zikirAdiEditText);
        EditText zikirDurakNoEditText = dialogView.findViewById(R.id.zikirDurakNoEditText);
        zikirSayisiEditText.setText(zikirSayisi.toString());
        if (gelen_zikir==null)
        {
            //Yeni Zikir Ekleme
            new AlertDialog.Builder(this)
                    .setTitle("Yeni Zikir Ekleme")
                    .setIcon(R.drawable.baseline_assignment_add_24)
                    .setView(dialogView)
                    .setPositiveButton("Kaydet", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String zikirAdi = zikirAdiEditText.getText().toString();
                            Integer zikirSayi= 0, durakNo=0;
                            if (zikirAdi.isEmpty() || zikirSayisiEditText.getText().toString().isEmpty() || zikirDurakNoEditText.getText().toString().isEmpty())  {
                                Toast.makeText(MainActivity.this, "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                zikirSayi= Integer.parseInt(zikirSayisiEditText.getText().toString());
                                durakNo= Integer.parseInt(zikirDurakNoEditText.getText().toString());
                                if(durakNo>zikirSayi)
                                {
                                    String SORGU="INSERT INTO zikirler(zikirAdet,zikirAd,durakNo) VALUES(?,?,?)";
                                    SQLiteStatement durumlar=database.compileStatement(SORGU);
                                    durumlar.bindLong(1,zikirSayi);
                                    durumlar.bindString(2,zikirAdi);
                                    durumlar.bindLong(3,durakNo);
                                    durumlar.execute();
                                    // Toast.makeText(MainActivity.this, "Zikir kaydedildi", Toast.LENGTH_SHORT).show();
                                    Snackbar snackbar = Snackbar.make(view, "Zikir kaydedildi..", Snackbar.LENGTH_SHORT);
                                    snackbar.setAction("Tamam", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            snackbar.dismiss();
                                        }
                                    });
                                    snackbar.show();
                                }
                                else{
                                    Snackbar snackbar = Snackbar.make(view, "Hedef sayısı zikir sayısından büyük olmalıdır.", Snackbar.LENGTH_SHORT);
                                    snackbar.setAction("Tamam", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            snackbar.dismiss();
                                        }
                                    });
                                    snackbar.show();
                                }

                            }
                        }
                    })
                    .setNegativeButton("İptal", null)
                    .show();
        }
        else{
            //Zikir Güncelleme
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Zikir Değiştirme");
            builder.setIcon(R.drawable.add_task_24px_black);
            builder.setView(dialogView);
            builder.setPositiveButton("Kaydet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String zikirAdi = zikirAdiEditText.getText().toString();
                    Integer zikirSayi = 0, durakNo1 = 0, id;

                    if (zikirAdi.isEmpty() || zikirSayisiEditText.getText().toString().isEmpty() || zikirDurakNoEditText.getText().toString().isEmpty()) {
                        Toast.makeText(MainActivity.this, "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
                    } else {
                        zikirSayi = Integer.parseInt(zikirSayisiEditText.getText().toString());
                        durakNo1 = Integer.parseInt(zikirDurakNoEditText.getText().toString());
                        zikirAdi = zikirAdiEditText.getText().toString();
                        if(durakNo1>zikirSayi)
                        {
                            try {

                                String SORGU = "UPDATE zikirler SET zikirAdet=?,zikirAd=?,durakNo=? WHERE id=?";
                                SQLiteStatement durumlar = database.compileStatement(SORGU);
                                durumlar.bindLong(1, zikirSayi);
                                durumlar.bindString(2, zikirAdi);
                                durumlar.bindLong(3,durakNo1);
                                durumlar.bindString(4, gelen_zikir.id.toString());
                                durumlar.execute();
                                Toast.makeText(MainActivity.this, "Zikir güncellendi", Toast.LENGTH_SHORT).show();
                                //Snackbar snackbar = Snackbar.make(view, "Mobilhanem.com Snackbar", Snackbar.LENGTH_LONG);
                                // snackbar.show();
                                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                                startActivity(intent);
                                finishAffinity();


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            Snackbar snackbar = Snackbar.make(view, "Hedef sayısı zikir sayısından büyük olmalıdır.", Snackbar.LENGTH_SHORT);
                            snackbar.setAction("Tamam", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snackbar.dismiss();
                                }
                            });
                            snackbar.show();
                        }

                    }
                }
            });
            builder.setNegativeButton("İptal", null);
            builder.show();
            try {
                database =MainActivity.this.openOrCreateDatabase("zikirmatik", MODE_PRIVATE, null);
                String query = "SELECT * FROM zikirler WHERE id =" + gelen_zikir.id.toString();
                Cursor cursor=database.rawQuery(query,null);
                int idIx=cursor.getColumnIndex("id");
                int adetIx=cursor.getColumnIndex("zikirAdet");
                int nameIx=cursor.getColumnIndex("zikirAd");
                int durakNoIx=cursor.getColumnIndex("durakNo");
                while (cursor.moveToNext()){
                    String zikir_adi=cursor.getString(nameIx);
                    int zikir_adet=cursor.getInt(adetIx);
                    int id=cursor.getInt(idIx);
                    Integer durakNo=cursor.getInt(durakNoIx);
                    degisecek_zikir =new Zikir(id,zikir_adi,zikir_adet,durakNo);
                }
                zikirAdiEditText.setText(degisecek_zikir.name);
                zikirDurakNoEditText.setText(degisecek_zikir.durakNo.toString());
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    public void sesAcKapa(View view) {

        if (sesAcik){
            sesAcik=false;
            binding.imgBtnSes.setImageResource(R.drawable.baseline_volume_off_24);
            mediaPlayer2.pause();
        }
        else{
            sesAcik=true;
            binding.imgBtnSes.setImageResource(R.drawable.baseline_volume_up_24);
            mediaPlayer2.start();
        }

    }

    public void btnTitresimAcKapa(View view) {
        if (!isVibrating) {
            isVibrating = true;
            binding.imgBtnTitresim.setImageResource(R.drawable.baseline_vibration_24);
            vibrator.vibrate(500);
        } else {
            isVibrating = false;
            binding.imgBtnTitresim.setImageResource(R.drawable.vibrate_off);
        }
    }

    public void zikirSayisiSifirla(View view) {
        if (zikirSayisi!=0){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Zikir Sıfırlama İşlemi");
            alertDialogBuilder
                    .setMessage("Zikir sayısı sıfırlansın mı?")
                    .setCancelable(false)
                    .setIcon(R.drawable.restart_alt_24px)
                    .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            zikirSayisi = -1;
                            btnArti(view);
                        }
                    })
                    .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    public void btnZikirListele(View view){
        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
        startActivity(intent);
        finishAffinity();
    }
}