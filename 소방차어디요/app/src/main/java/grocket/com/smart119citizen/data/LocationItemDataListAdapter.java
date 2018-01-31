
package grocket.com.smart119citizen.data;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import grocket.com.smart119citizen.MobilizeLocActivity;
import grocket.com.smart119citizen.R;

public class LocationItemDataListAdapter extends RecyclerView.Adapter<LocationItemDataListAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<LocationItemData> mDataList;

    public LocationItemDataListAdapter(Context mContext, ArrayList<LocationItemData> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.list_item_location, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        LocationItemData item = mDataList.get(position);

//        final String primaryKey = item.getPrimarykey();
        final int commandType = item.getCommandType();
        final String accidentNo = item.getAccidentNo();
        final String accidentAddr = item.getAccidentAddress();
        final String accidentAddrDetail = item.getDetailAddress();
        final String accidentContent = item.getAccidentContent();
        final String reporterTel = item.getReporterTelephone();
        final String regDate = item.getRegDate();

        // Bind data
        if(commandType==0) {
            // 화재출동
            Picasso.with(mContext)
                    .load(R.drawable.ico_list_fire)
                    .into(holder.mImgStatus);
            holder.mTxtAccidentContent.setTextColor(Color.parseColor("#ff6147"));
        }else if(commandType==1) {
            // 구조출동
            Picasso.with(mContext)
                    .load(R.drawable.ico_list_rescue)
                    .into(holder.mImgStatus);
            holder.mTxtAccidentContent.setTextColor(Color.parseColor("#ffc64d"));
        }else if(commandType==2) {
            // 구급출동
            Picasso.with(mContext)
                    .load(R.drawable.ico_list_emergency)
                    .into(holder.mImgStatus);
            holder.mTxtAccidentContent.setTextColor(Color.parseColor("#5ae49e"));
        }

        final String addr = accidentAddr + " " + accidentAddrDetail;
        holder.mTxtAccidentContent.setText(accidentContent);
        holder.mTxtAccidentAddr.setText(addr);
        holder.mTxtOrderDateTime.setText(regDate);

        holder.mLayBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(mContext, MobilizeLocActivity.class);
                i.putExtra("AccidentNo", accidentNo);
                i.putExtra("AccidentAddr", accidentAddr);
                i.putExtra("AccidentAddrDetail", accidentAddrDetail);
                i.putExtra("AccidentContent", accidentContent);
                i.putExtra("ReporterTel", reporterTel);
                i.putExtra("RegDate", regDate);
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    protected class ViewHolder extends RecyclerView.ViewHolder {
        View mLayBackground;
        ImageView mImgStatus;
        TextView mTxtAccidentContent;
        TextView mTxtAccidentAddr;
        TextView mTxtOrderDateTime;

        public ViewHolder(View viewItem) {
            super(viewItem);

            mLayBackground = viewItem.findViewById(R.id.layBackground);
            mImgStatus = (ImageView) viewItem.findViewById(R.id.imgStatus);
            mTxtAccidentContent = (TextView) viewItem.findViewById(R.id.txtAccidentContent);
            mTxtAccidentAddr = (TextView) viewItem.findViewById(R.id.txtAccidentAddress);
            mTxtOrderDateTime = (TextView) viewItem.findViewById(R.id.txtOrderDateTime);
        }
    }
}
