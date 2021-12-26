package com.example.musicure;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder> {

    public List<String> fileNameList;
    public List<String> fileDoneList;

    public UploadListAdapter(List<String> fileNameList, List<String> fileDoneList){
        this.fileDoneList=fileDoneList;
        this.fileNameList=fileNameList;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String fileName=fileNameList.get(position);
        holder.fileNameView.setText(fileName);

        String fileDone=fileDoneList.get(position);

        if(fileDone.equals("uploading"))
        {
            holder.fileDoneView.setImageResource(R.drawable.upload_icon);
        }else
        {
            holder.fileDoneView.setImageResource(R.drawable.doneupload);
        }

    }

    @Override
    public int getItemCount() {
        return fileNameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        View mView;
        public TextView fileNameView;
        public ImageView fileDoneView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView=itemView;

            fileNameView=(TextView) mView.findViewById(R.id.filename);
            fileDoneView=(ImageView) mView.findViewById(R.id.imageView4);



        }
    }
}
