package com.app.guardian.common

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val includeEdge: Boolean
) :
    ItemDecoration() {
    companion object {
        fun getItemOffsets(gridSpacingItemDecoration: GridSpacingItemDecoration, outRect: Rect, view: View?, parent: RecyclerView, state: RecyclerView.State?)
        {
            val position = parent.getChildAdapterPosition(view!!) // item position
            val column = position % gridSpacingItemDecoration.spanCount // item column
            if (gridSpacingItemDecoration.includeEdge) {
                outRect.left =
                    gridSpacingItemDecoration.spacing - column * gridSpacingItemDecoration.spacing / gridSpacingItemDecoration.spanCount // spacing - column * ((1f / spanCount) * spacing)
                outRect.right =
                    (column + 1) * gridSpacingItemDecoration.spacing / gridSpacingItemDecoration.spanCount // (column + 1) * ((1f / spanCount) * spacing)
                if (position < gridSpacingItemDecoration.spanCount) { // top edge
                    outRect.top = gridSpacingItemDecoration.spacing
                }
                outRect.bottom = gridSpacingItemDecoration.spacing // item bottom
            } else {
                outRect.left = column * gridSpacingItemDecoration.spacing / gridSpacingItemDecoration.spanCount // column * ((1f / spanCount) * spacing)
                outRect.right =
                    gridSpacingItemDecoration.spacing - (column + 1) * gridSpacingItemDecoration.spacing / gridSpacingItemDecoration.spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= gridSpacingItemDecoration.spanCount) {
                    outRect.top = gridSpacingItemDecoration.spacing // item top
                }
            }
        }
    }

}