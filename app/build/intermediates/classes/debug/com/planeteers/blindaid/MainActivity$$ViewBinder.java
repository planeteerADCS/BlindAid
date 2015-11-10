// Generated code from Butter Knife. Do not modify!
package com.planeteers.blindaid;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MainActivity$$ViewBinder<T extends com.planeteers.blindaid.MainActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492947, "field 'cameraFeedButton' and method 'onCameraButtonClicked'");
    target.cameraFeedButton = finder.castView(view, 2131492947, "field 'cameraFeedButton'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onCameraButtonClicked(p0);
        }
      });
    view = finder.findRequiredView(source, 2131492948, "field 'faceDetectButton' and method 'onFaceDetectButtonClicked'");
    target.faceDetectButton = finder.castView(view, 2131492948, "field 'faceDetectButton'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onFaceDetectButtonClicked(p0);
        }
      });
    view = finder.findRequiredView(source, 2131492949, "field 'mPreviewImage'");
    target.mPreviewImage = finder.castView(view, 2131492949, "field 'mPreviewImage'");
  }

  @Override public void unbind(T target) {
    target.cameraFeedButton = null;
    target.faceDetectButton = null;
    target.mPreviewImage = null;
  }
}
