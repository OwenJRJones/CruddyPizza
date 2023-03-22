package com.example.cruddypizza_assign3;

import android.view.LayoutInflater;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    //Interface
    private final RecyclerViewInterface recyclerViewInterface;

    //ArrayList to hold oder objects
    private final ArrayList<Order> orderList;
    private final String[] toppings;
    private final String[] sizes;
    //Lang variable
    private String lang;

    //Constructor
    public OrderAdapter(ArrayList<Order> orderList, String[] toppings, String[] sizes, String lang, RecyclerViewInterface recyclerViewInterface) {
        this.orderList = orderList;
        this.toppings = toppings;
        this.sizes = sizes;
        this.lang = lang;
        this.recyclerViewInterface = recyclerViewInterface;
    }//End constructor

    //ViewHolder
    public class MyViewHolder extends RecyclerView.ViewHolder{
        //Controls
        TextView tvOrderName, tvSizeValue, tvT1Value, tvT2Value, tvT3Value, tvDate;
        TextView tvSizeRV, tvToppings;

        public MyViewHolder(final View view, RecyclerViewInterface recyclerViewInterface){
            super(view);
            //Hookup controls
            tvOrderName = view.findViewById(R.id.tvOrderName);
            tvSizeRV = view.findViewById(R.id.tvSizeRV);
            tvSizeValue = view.findViewById(R.id.tvSizeValue);
            tvToppings = view.findViewById(R.id.tvToppings);
            tvT1Value = view.findViewById(R.id.tvT1Value);
            tvT2Value = view.findViewById(R.id.tvT2Value);
            tvT3Value = view.findViewById(R.id.tvT3Value);
            tvDate = view.findViewById(R.id.tvDate);

            //Set onClick listener for recycler view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null) {
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });//End onClick
        }
    }//End MyViewHolder

    @NonNull
    @Override
    public OrderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflater
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        //Return view
        return new MyViewHolder(itemView, recyclerViewInterface);
    }//End onCreateViewHolder

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.MyViewHolder holder, int position) {
        //Create variables
        String name = orderList.get(position).getName();
        int size = orderList.get(position).getSize();
        int topp1 = orderList.get(position).getTopp1();
        int topp2 = orderList.get(position).getTopp2();
        int topp3 = orderList.get(position).getTopp3();
        String date = orderList.get(position).getDate();

        //Set text controls
        if(lang.equals("EN")){
            holder.tvSizeRV.setText(R.string.tvSize);
            holder.tvToppings.setText(R.string.tvToppings);
        }
        else {
            holder.tvSizeRV.setText(R.string.tvSize2);
            holder.tvToppings.setText(R.string.tvToppings2);
        }
        holder.tvOrderName.setText(name);
        holder.tvSizeValue.setText(sizes[size]);
        holder.tvDate.setText(date);
        holder.tvT1Value.setText(toppings[topp1]);
        holder.tvT2Value.setText(toppings[topp2]);
        holder.tvT3Value.setText(toppings[topp3]);
    }//End onBindViewHolder

    @Override
    public int getItemCount () {
        //Return orderList array size
        return orderList.size();
    }//End getItemCount
}//End Adapter class
