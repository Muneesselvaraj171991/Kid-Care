package com.child.parent.kidcare.views.applist;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.HapticFeedbackConstants;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import android.view.View;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.child.parent.kidcare.MyApplication;
import com.child.parent.kidcare.R;
import com.child.parent.kidcare.db.Packages;
import com.child.parent.kidcare.utils.EasyFlipView;
import com.google.android.material.imageview.ShapeableImageView;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder>{

    private int mLastPosition ;
    private List<Packages> packagesList;
    private ClickListener mListener;

    public AppsAdapter(List<Packages> appInformations, ClickListener listener){
        this.packagesList = appInformations;
        mListener = listener;

    }

    public  class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView iconApp;
        public TextView lblLockStatus,lblFlip,lblFlipInfo;
        EasyFlipView flipView;
        public TextView lblAppName,lblAppStatus;
        public CardView cardViewFlip;
        public CardView frontView;


        public ViewHolder (View view){
            super(view);
            iconApp = view.findViewById(R.id.iconApp);
            flipView = view.findViewById(R.id.flipView);
            lblLockStatus = view.findViewById(R.id.tvlock);
            lblFlip  = view.findViewById(R.id.tvflip);
            lblAppName =  view.findViewById(R.id.applist_lblappname);
            lblAppStatus = view.findViewById(R.id.applist_lblstatus);
            lblFlipInfo = view.findViewById(R.id.tvflip_info);
            cardViewFlip = view.findViewById(R.id.cardviewFlip);
            frontView = view.findViewById(R.id.front_view);
            lblLockStatus.setClickable(false);

            cardViewFlip.setOnClickListener(v -> {
                setFlip();
            });

            lblFlip.setOnClickListener(v -> {
                setFlip();
            });

          frontView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
               lblLockStatus.setVisibility(!packagesList.get(pos).isLocked()? View.VISIBLE : View.GONE);
                mListener.onLongPress(pos);
            });

        }

        private void setFlip() {
            int position = getAdapterPosition();
            Packages lastItem = packagesList.get(mLastPosition);
            if(mLastPosition != getAdapterPosition() && lastItem.isFlipped()) {
                lastItem.setFlipped(false);

            }
            if (packagesList.get(position).isFlipped()) {
                packagesList.get(position).setFlipped(false);
               flipView.setEnabled(true);

            } else {
                flipView.setEnabled(false);
                lblAppName.setText("App : " + packagesList.get(position).getAppName());
                if(packagesList.get(position).isLocked()){
                   lblAppStatus.setText("Status : Lock");
                } else {
                    lblAppStatus.setText("Status : Unlock");

                }
                packagesList.get(position).setFlipped(true);
            }

            flipView.flip();
            mLastPosition = position;
        }

    }



    @Override
    public AppsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position){

        viewHolder.iconApp.setImageDrawable(packagesList.get(position).getIcon());
        viewHolder.lblLockStatus.setVisibility(packagesList.get(position).isLocked()? View.VISIBLE : View.GONE);







    }

    public interface ClickListener {
        public void onLongPress(int position);
    }

    @Override
    public int getItemCount(){
        return packagesList.size();
    }

}
