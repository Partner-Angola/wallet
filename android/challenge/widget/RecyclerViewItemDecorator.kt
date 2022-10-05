package com.joeware.android.gpulumera.challenge.widget

import android.graphics.Rect
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils

class VerticalSpaceItemDecoration(
    private val spaceHeight: Int,
    private val hasHeader: Boolean = false,
    private val includeHeader: Boolean = false,
    private val includeLast: Boolean = false
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        val layoutManager = parent.layoutManager
        var itemCount = parent.adapter?.itemCount ?: 0
        itemCount = if (hasHeader) itemCount - 1 else itemCount
        val spaceHeightDP = ConvertUtils.dp2px(spaceHeight.toFloat())

        if (layoutManager is GridLayoutManager) {
            val spanCount = layoutManager.spanCount
            val spanSize = layoutManager.spanSizeLookup.getSpanSize(itemPosition)

            if (spanSize == spanCount) {
                // header cell
                if (includeHeader) {
                    outRect.bottom = spaceHeightDP
                }
            } else {
                // normal cell
                var lastRowCellCount = itemCount % spanCount
                lastRowCellCount = if (lastRowCellCount == 0) spanCount else lastRowCellCount
                val cellPosition = if (hasHeader) itemPosition - 1 else itemPosition
                if (cellPosition < (itemCount - lastRowCellCount)) {
                    outRect.bottom = spaceHeightDP
                } else {
                    if (includeLast) {
                        outRect.bottom = spaceHeightDP
                    }
                }
            }
        } else {
            if (itemPosition == 0 && hasHeader) {
                if (includeHeader) {
                    outRect.bottom = spaceHeightDP
                }
            } else {
                if (includeLast || (itemPosition < (itemCount - 1))) {
                    outRect.bottom = spaceHeightDP
                }
            }
        }
    }
}

class HorizontalSpaceItemDecoration(
    private val spaceWidth: Int,
    private val hasHeader: Boolean = false,
    private val includeHeader: Boolean = false,
    private val includeLast: Boolean = false
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        val layoutManager = parent.layoutManager
        val itemCount = parent.adapter?.itemCount ?: 0
        val spaceWidthDP = ConvertUtils.dp2px(spaceWidth.toFloat())

        if (layoutManager is GridLayoutManager) {
            val spanCount = layoutManager.spanCount
            val spanSize = layoutManager.spanSizeLookup.getSpanSize(itemPosition)

            if (spanSize == spanCount) {
                // header cell
                if (includeHeader) {
                    outRect.right = spaceWidthDP
                }
            } else {
                // normal cell
                val cellPosition = if (hasHeader) itemPosition - 1 else itemPosition
                if (((cellPosition % spanCount) < (spanCount - 1)) || includeLast) {
                    outRect.right = spaceWidthDP
                }
            }
        } else {
            if (itemPosition == 0 && hasHeader) {
                if (includeHeader) {
                    outRect.right = spaceWidthDP
                }
            } else {
                if (includeLast || (itemPosition < (itemCount - 1))) {
                    outRect.right = spaceWidthDP
                }
            }
        }
    }
}