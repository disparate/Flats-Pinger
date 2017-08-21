package kazarovets.flatspinger.model

import kazarovets.flatspinger.R


enum class Provider (val nameRes: Int, val drawableRes: Int) {
    ONLINER(R.string.onliner_provider_name, R.drawable.onliner_logo_height_24dp),
    I_NEED_A_FLAT(R.string.i_need_a_flat_provider_name, R.drawable.i_need_a_flat_logo_height_24dp)
}