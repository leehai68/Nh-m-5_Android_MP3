package hteam.mp3playback;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Vector;

import hteam.model.SubjectMp3;

/**
 * Created by Admin on 3/7/2017.
 */

public class AdapterMp3 extends RecyclerView.Adapter<AdapterMp3.ViewHolder>
{
    Vector<SubjectMp3> data_mp3;
    Context context;
    public AdapterMp3(Vector<SubjectMp3> a,Context b)
    {
        this.data_mp3=a;
        this.context=b;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row,parent,false);
        return new ViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SubjectMp3 m=data_mp3.get(position);
        Picasso.with(context).load(m.url_img).into(holder.imgAnh);
        holder.tvTen.setText(m.Ten);
    }
    public String alo(int poss)
    {
        String s=data_mp3.get(poss).url_cd;
        return s;

    }
    @Override
    public int getItemCount() {
        return data_mp3.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private final Context context1;
        public TextView tvTen;
        public ImageView imgAnh;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTen=(TextView) itemView.findViewById(R.id.txtName);
            imgAnh=(ImageView) itemView.findViewById(R.id.imgHinh);
            context1=itemView.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final  Intent intent;
                    Log.e("HIHI",alo(getAdapterPosition()));
                    intent=new Intent(context1,abc.class);
                    intent.putExtra("KEY",alo(getAdapterPosition()));
                    context1.startActivity(intent);
                }
            });
        }
    }
}
