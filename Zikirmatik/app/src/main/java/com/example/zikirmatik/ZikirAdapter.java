package com.example.zikirmatik;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zikirmatik.databinding.RecyclerRowBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ZikirAdapter extends RecyclerView.Adapter<ZikirAdapter.ZikirHolder> {
    ArrayList<Zikir> artArrayList;
    SQLiteDatabase database;
    public ZikirAdapter(ArrayList<Zikir> artArrayList) {
        this.artArrayList = artArrayList;
    }

    @Override
    public ZikirHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerRowBinding binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ZikirHolder(binding);
    }

    @Override
    public void onBindViewHolder(ZikirHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.recyclerTextViewZikirAdi.setText(artArrayList.get(position).name);
        holder.binding.recyclerTextViewZikirAdet.setText(String.valueOf(artArrayList.get(position).adet));
        holder.binding.recyclerTextViewZikirDurak.setText(String.valueOf(artArrayList.get(position).durakNo));
        holder.binding.recyclerTextViewKalanZikirSayisi.setText(String.valueOf(artArrayList.get(position).durakNo-artArrayList.get(position).adet));
        holder.binding.recyclerBtnSil.setTag(artArrayList.get(position).id);
        holder.binding.recyclerBtnGuncelle.setTag(artArrayList.get(position).id);
        holder.binding.recyclerBtnSil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               database = holder.itemView.getContext().openOrCreateDatabase("zikirmatik", MODE_PRIVATE, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( holder.itemView.getContext());
                alertDialogBuilder.setTitle("Zikir Silme İşlemi");
                alertDialogBuilder
                        .setMessage("Zikir silinsin mi?")
                        .setCancelable(false)
                        .setIcon(R.drawable.baseline_delete_forever_24)
                        .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Integer id=Integer.parseInt(view.getTag().toString());
                                try {
                                    String SORGU="DELETE FROM zikirler WHERE id=?";
                                    SQLiteStatement durumlar=database.compileStatement(SORGU);
                                    durumlar.bindLong(1,id);
                                    durumlar.execute();
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                                removeItem(position);
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
        });
        holder.binding.recyclerBtnGuncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setTitle("Zikir devam etme !")
                        .setMessage("Zikre ekleme yapmak istiyor musunuz?")
                        .setIcon(R.drawable.add_task_24px_black)
                        .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                               Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                                Zikir gonderilen_zikir = null;
                                try {
                                    database =holder.itemView.getContext().openOrCreateDatabase("zikirmatik", MODE_PRIVATE, null);
                                    String query = "SELECT * FROM zikirler WHERE id =" + holder.binding.recyclerBtnGuncelle.getTag().toString();
                                    Cursor cursor=database.rawQuery(query,null);
                                    int idIx=cursor.getColumnIndex("id");
                                    int adetIx=cursor.getColumnIndex("zikirAdet");
                                    int nameIx=cursor.getColumnIndex("zikirAd");
                                    int durakNoIx=cursor.getColumnIndex("durakNo");
                                    while (cursor.moveToNext()){
                                        String zikir_adi=cursor.getString(nameIx);
                                        int zikir_adet=cursor.getInt(adetIx);
                                        int Id =cursor.getInt(idIx);
                                        Integer durakNo=cursor.getInt(durakNoIx);
                                       gonderilen_zikir = new Zikir(Id,zikir_adi,zikir_adet,durakNo);
                                    }
                                }
                                catch(Exception e){
                                    e.printStackTrace();
                                }
                               intent.putExtra("gonderilen_zikir",gonderilen_zikir);
                               holder.itemView.getContext().startActivity(intent);
                            }
                        })
                        .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return artArrayList.size();
    }

    public class ZikirHolder extends RecyclerView.ViewHolder {
        private RecyclerRowBinding binding;
        public ZikirHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void removeItem(int position) {
        artArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, artArrayList.size());
    }

}