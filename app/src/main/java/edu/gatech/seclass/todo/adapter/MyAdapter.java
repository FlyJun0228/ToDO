package edu.gatech.seclass.todo.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;




import java.util.List;

import edu.gatech.seclass.todo.R;
import edu.gatech.seclass.todo.model.InfoModel;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<InfoModel> dataModels;

    public MyAdapter(List<InfoModel> dataModels) {
        this.dataModels = dataModels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data, parent, false);
        MyViewHolder  holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final InfoModel dataModel = dataModels.get(position);
        holder.tv_1.setText(dataModel.getNum());
        holder.tv_2.setText(dataModel.getProvince());
        holder.tv_3.setText(dataModel.getCity());
    }

    @Override
    public int getItemCount() {
        return dataModels.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_1,tv_2,tv_3;
        public MyViewHolder(View view) {
            super(view);
            tv_1 = view.findViewById(R.id.tv_1);
            tv_2 = view.findViewById(R.id.tv_2);
            tv_3 = view.findViewById(R.id.tv_3);
        }
    }
}
