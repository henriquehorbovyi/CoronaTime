package io.henrikhorbovyi.coronamap.ui

import io.henrikhorbovyi.coronamap.R

class ProgressDialogFragment : Dialog() {

    override var layout: Int = R.layout.fragment_progress_dialog

    companion object {
        var TAG: String = ProgressDialogFragment::class.java.simpleName
    }
}