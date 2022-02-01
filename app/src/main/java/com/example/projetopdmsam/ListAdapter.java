package com.example.projetopdmsam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.projetopdmsam.Modelos.Caso;

import java.util.List;

public class ListAdapter extends BaseAdapter {
    Context context;
    List<Caso> listaCasos;

    public ListAdapter(Context context, List<Caso> casosArrayList){
        this.context = context;
        this.listaCasos = casosArrayList;
    }

    @Override
    public int getCount() {
        return listaCasos.size();
    }

    @Override
    public Object getItem(int position) {
        return listaCasos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        Caso caso = (Caso) getItem(position);

        TextView txt_TituloItem = convertView.findViewById(R.id.txt_TituloItem);
        TextView txt_DescricaoItem = convertView.findViewById(R.id.txt_DescricaoItem);

        if (caso.getTitulo().length()>39){
            caso.setTitulo(caso.getTitulo().substring(0,38) + "...");
        }
        if(caso.getDescricao().length()>97){
            caso.setDescricao(caso.getDescricao().substring(0,96) + "...");
        }

        txt_TituloItem.setText(caso.getTitulo());
        txt_DescricaoItem.setText(caso.getDescricao());

        return convertView;
    }
}
