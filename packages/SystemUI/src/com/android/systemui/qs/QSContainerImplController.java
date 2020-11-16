/*
 * Copyright (C) 2019 The Android Open Source Project
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

package com.android.systemui.qs;

import com.android.systemui.R;

import javax.inject.Inject;

public class QSContainerImplController {
    private final QSContainerImpl mView;
    private final QSFooterImplController mQSFooterImplController;

    private QSContainerImplController(QSContainerImpl view,
            QSFooterImplController.Builder qsFooterImplControllerBuilder ) {
        mView = view;
        mQSFooterImplController = qsFooterImplControllerBuilder
                .setQSFooterImpl(mView.findViewById(R.id.qs_footer)).build();
    }

    public void setListening(boolean listening) {
        mQSFooterImplController.setListening(listening);
    }

    public static class Builder {
        private final QSFooterImplController.Builder mQSFooterImplControllerBuilder;
        private QSContainerImpl mView;

        @Inject
        public Builder(
                QSFooterImplController.Builder qsFooterImplControllerBuilder) {
            mQSFooterImplControllerBuilder = qsFooterImplControllerBuilder;
        }

        public Builder setQSContainerImpl(QSContainerImpl view) {
            mView = view;
            return this;
        }

        public QSContainerImplController build() {
            return new QSContainerImplController(mView, mQSFooterImplControllerBuilder);
        }
    }
}
