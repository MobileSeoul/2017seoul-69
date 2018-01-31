
package grocket.com.smart119citizen.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import grocket.com.smart119citizen.R;

public class ContactItemDataListAdapter extends RecyclerView.Adapter<ContactItemDataListAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<ContactItemData> mDataList;

    public ContactItemDataListAdapter(Context mContext, ArrayList<ContactItemData> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.list_item_contact, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ContactItemData item = mDataList.get(position);
        final String name = item.getName();
        final String positionTxt = item.getPosition();
        holder.txtName.setText(name);
        holder.txtPosition.setText(positionTxt);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    protected class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        TextView txtPosition;

        public ViewHolder(View viewItem) {
            super(viewItem);
            txtName = (TextView) viewItem.findViewById(R.id.txtName);
            txtPosition = (TextView) viewItem.findViewById(R.id.txtPosition);
        }
    }
}
