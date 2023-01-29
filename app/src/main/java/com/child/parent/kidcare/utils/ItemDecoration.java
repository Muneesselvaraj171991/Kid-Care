package com.child.parent.kidcare.utils;


import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ItemDecoration extends RecyclerView.ItemDecoration {
        private static final int GENERAL_TAB_SPACING_8_PX = dpToPx(8);

        // Convert dp/px
        private static int dpToPx(int dp) {
            return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
        }


        @Override
        public void getItemOffsets(
                @NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();

            GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) view.getLayoutParams();
            int spanIndex = params.getSpanIndex();

            int position = parent.getChildAdapterPosition(view);
            int spanSize = gridLayoutManager.getSpanSizeLookup().getSpanSize(position);
            int totalSpanSize = gridLayoutManager.getSpanCount();
            int noOfColumns = spanSize != 0 ? totalSpanSize / spanSize : 0;

            if (noOfColumns > 1) {
                int childCount = state.getItemCount();

                outRect.top = GENERAL_TAB_SPACING_8_PX;

                outRect.left = spanIndex != 0 ? GENERAL_TAB_SPACING_8_PX / 2 : GENERAL_TAB_SPACING_8_PX;

                outRect.right = spanIndex == spanSize ? GENERAL_TAB_SPACING_8_PX : 0;

                if (position >= childCount - 2) {
                    if ((childCount % 2 == 0) || (position == childCount - 1)) {
                        outRect.bottom = GENERAL_TAB_SPACING_8_PX;
                    }
                }
            }
        }
    }

