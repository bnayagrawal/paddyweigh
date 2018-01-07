package xyz.bnayagrawal.paddyweigh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by binay on 1/1/18.
 */

public class RAdapter extends RecyclerView.Adapter<RAdapter.ViewHolder> {

    People currentPeople;
    Context context;

    public RAdapter(Context context, People currentPeople) {
        this.context = context;
        this.currentPeople = currentPeople;
    }

    public void updateCurrentPeople(People currentPeople) {
        this.currentPeople = currentPeople;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.template_paddy_weight, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        String type = (currentPeople.packets.get(i).getType() == Packet.Type.Plastic) ? "P" : "J";
        DecimalFormat decimalFormat = new DecimalFormat("##.#");
        double weight = Float.parseFloat(decimalFormat.format(currentPeople.packets.get(i).getWeight()));
        viewHolder.txtPacket.setText(String.format("%.1f",weight) + type);
    }

    @Override
    public int getItemCount() {
        return currentPeople.packets.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtPacket;

        public ViewHolder(View item) {
            super(item);
            txtPacket = item.findViewById(R.id.txtPacket);
        }
    }
}
