/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.amnesica.kryptey.inputmethod.latin;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.amnesica.kryptey.inputmethod.R;
import com.amnesica.kryptey.inputmethod.keyboard.MainKeyboardView;
import com.amnesica.kryptey.inputmethod.latin.e2ee.E2EEStripView;

public final class InputView extends FrameLayout {
  private MainKeyboardView mMainKeyboardView;
  private KeyboardTopPaddingForwarder mKeyboardTopPaddingForwarder;

  public InputView(final Context context, final AttributeSet attrs) {
    super(context, attrs, 0);
  }

  @Override
  protected void onFinishInflate() {
    final E2EEStripView e2eeStripView = findViewById(R.id.e2ee_strip_view);
    mMainKeyboardView = findViewById(R.id.keyboard_view);
    mKeyboardTopPaddingForwarder = new KeyboardTopPaddingForwarder(
        mMainKeyboardView, e2eeStripView);

    super.onFinishInflate();
  }

  public void setKeyboardTopPadding(final int keyboardTopPadding) {
    mKeyboardTopPaddingForwarder.setKeyboardTopPadding(keyboardTopPadding);
  }

  /**
   * This class forwards {@link android.view.MotionEvent}s happened in the top padding of
   * {@link MainKeyboardView} to {@link com.amnesica.kryptey.inputmethod.latin.e2ee.E2EEStripView}.
   */
  private static class KeyboardTopPaddingForwarder
      extends MotionEventForwarder<MainKeyboardView, E2EEStripView> {
    private int mKeyboardTopPadding;

    public KeyboardTopPaddingForwarder(final MainKeyboardView mainKeyboardView,
                                       final E2EEStripView e2eeStripView) {
      super(mainKeyboardView, e2eeStripView);
    }

    public void setKeyboardTopPadding(final int keyboardTopPadding) {
      mKeyboardTopPadding = keyboardTopPadding;
    }

    private boolean isInKeyboardTopPadding(final int y) {
      return y < mEventSendingRect.top + mKeyboardTopPadding;
    }

    @Override
    protected boolean needsToForward(final int x, final int y) {
      // Forwarding an event only when {@link MainKeyboardView} is visible.
      // Because the visibility of {@link MainKeyboardView} is controlled by its parent
      // view in {@link KeyboardSwitcher#setMainKeyboardFrame()}, we should check the
      // visibility of the parent view.
      final View mainKeyboardFrame = (View) mSenderView.getParent();
      return mainKeyboardFrame.getVisibility() == View.VISIBLE && isInKeyboardTopPadding(y);
    }

    @Override
    protected int translateY(final int y) {
      final int translatedY = super.translateY(y);
      if (isInKeyboardTopPadding(y)) {
        // The forwarded event should have coordinates that are inside of the target.
        return Math.min(translatedY, mEventReceivingRect.height() - 1);
      }
      return translatedY;
    }
  }

  /**
   * This class forwards series of {@link android.view.MotionEvent}s from <code>SenderView</code> to
   * <code>ReceiverView</code>.
   *
   * @param <SenderView>   a {@link View} that may send a {@link android.view.MotionEvent} to <ReceiverView>.
   * @param <ReceiverView> a {@link View} that receives forwarded {@link android.view.MotionEvent} from
   *                       <SenderView>.
   */
  private static abstract class
  MotionEventForwarder<SenderView extends View, ReceiverView extends View> {
    protected final SenderView mSenderView;
    protected final ReceiverView mReceiverView;

    protected final Rect mEventSendingRect = new Rect();
    protected final Rect mEventReceivingRect = new Rect();

    public MotionEventForwarder(final SenderView senderView, final ReceiverView receiverView) {
      mSenderView = senderView;
      mReceiverView = receiverView;
    }

    // Return true if a touch event of global coordinate x, y needs to be forwarded.
    protected abstract boolean needsToForward(final int x, final int y);

    // Translate global x-coordinate to <code>ReceiverView</code> local coordinate.
    protected int translateX(final int x) {
      return x - mEventReceivingRect.left;
    }

    // Translate global y-coordinate to <code>ReceiverView</code> local coordinate.
    protected int translateY(final int y) {
      return y - mEventReceivingRect.top;
    }

    /**
     * Callback when a {@link android.view.MotionEvent} is forwarded.
     *
     * @param me the motion event to be forwarded.
     */
    protected void onForwardingEvent(final MotionEvent me) {
    }

    // Returns true if a {@link MotionEvent} is needed to be forwarded to
    // <code>ReceiverView</code>. Otherwise returns false.
    public boolean onInterceptTouchEvent(final int x, final int y, final MotionEvent me) {
      // Forwards a {link MotionEvent} only if both <code>SenderView</code> and
      // <code>ReceiverView</code> are visible.
      if (mSenderView.getVisibility() != View.VISIBLE ||
          mReceiverView.getVisibility() != View.VISIBLE) {
        return false;
      }
      mSenderView.getGlobalVisibleRect(mEventSendingRect);
      if (!mEventSendingRect.contains(x, y)) {
        return false;
      }

      if (me.getActionMasked() == MotionEvent.ACTION_DOWN) {
        // If the down event happens in the forwarding area, successive
        // {@link MotionEvent}s should be forwarded to <code>ReceiverView</code>.
        return needsToForward(x, y);
      }

      return false;
    }

    // Returns true if a {@link MotionEvent} is forwarded to <code>ReceiverView</code>.
    // Otherwise returns false.
    public boolean onTouchEvent(final int x, final int y, final MotionEvent me) {
      mReceiverView.getGlobalVisibleRect(mEventReceivingRect);
      // Translate global coordinates to <code>ReceiverView</code> local coordinates.
      me.setLocation(translateX(x), translateY(y));
      mReceiverView.dispatchTouchEvent(me);
      onForwardingEvent(me);
      return true;
    }
  }
}
