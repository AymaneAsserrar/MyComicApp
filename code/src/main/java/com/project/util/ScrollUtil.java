package com.project.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class ScrollUtil {
    public static final int PAGE_SIZE = 12;
    private static final int SCROLL_THRESHOLD = 50;

    public interface LoadMoreCallback {
        void onLoadMore(int offset);
    }

    public static JScrollPane createInfiniteScrollPane(JPanel contentPanel, LoadMoreCallback callback) {
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            private int lastOffset = 0;
            
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
                int extent = scrollBar.getModel().getExtent();
                int maximum = scrollBar.getModel().getMaximum();
                int value = e.getValue();
                
                if (maximum - (value + extent) <= SCROLL_THRESHOLD) {
                    int currentOffset = contentPanel.getComponentCount();
                    if (currentOffset > lastOffset) {
                        lastOffset = currentOffset;
                        callback.onLoadMore(currentOffset);
                    }
                }
            }
        });
        
        return scrollPane;
    }
}
