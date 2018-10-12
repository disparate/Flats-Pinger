package kazarovets.flatspinger.flats

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.clans.fab.FloatingActionButton
import kazarovets.flatspinger.R
import kazarovets.flatspinger.flats.adapter.FlatViewState
import kazarovets.flatspinger.flats.adapter.FlatsRecyclerAdapter
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.utils.extensions.getAppComponent
import kazarovets.flatspinger.viewmodel.FlatInfosViewModel
import kazarovets.flatspinger.viewmodel.FlatInfosViewModelFactory
import kazarovets.flatspinger.views.ContentViewState
import kotlinx.android.synthetic.main.fragment_flats_list.*
import javax.inject.Inject


class FlatsListFragment : Fragment() {

    @Inject
    lateinit var flatsFactory: FlatInfosViewModelFactory

    private var adapter = FlatsRecyclerAdapter(
            onFavoriteChangedListener = { flat, isFav ->
                flatsViewModel.updateIsFavorite(flat, isFav)
            },

            onRemoveClickListener = { flatsViewModel.setHiddenFlat(it) },

            onClickListener = {
                startActivity(FlatDetailsActivity.getCallingIntent(context!!, it))
            })

    private var currentMode: MODE = MODE.LIST

    private var flatsMapFragment: FlatsMapFragment? = null


    private lateinit var flatsViewModel: FlatInfosViewModel

    private val filterClickListener = View.OnClickListener { v ->
        val mode = v?.tag as? MODE
        mode?.let {
            currentMode = it
            flatsViewModel.flatsMode = it
            fillFloatingMenu()
            flatsListMapSwitcher.displayedChild = getDisplayedPagePos(it)
        }
    }

    override fun onAttach(context: Context?) {
        context!!.getAppComponent().inject(this)
        super.onAttach(context)

        flatsViewModel = ViewModelProviders.of(this, flatsFactory).get(FlatInfosViewModel::class.java)
        flatsViewModel.init()
        flatsViewModel.flatsMode = currentMode
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_flats_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        flatsListSwipeRefresh.setOnRefreshListener {
            flatsViewModel.refresh()
        }

        flatsListRecycler.layoutManager = LinearLayoutManager(context)
        (flatsListRecycler.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false

        flatsListRecycler.adapter = adapter

        fillFloatingMenu()

        flatsListSwipeRefresh.isRefreshing = true

        flatsViewModel.getFlats().observe(this, Observer {
            onFlatsReceived(it)
        })

        observeLoadingState()

        flatsMapFragment = FlatsMapFragment()
        childFragmentManager
                .beginTransaction()
                .replace(R.id.flatsListsMapContainer, flatsMapFragment)
                .commit()

        flatsViewModel.setShowSeen(showVisibleFlatsSwitch.isChecked)
        showVisibleFlatsSwitch.setOnCheckedChangeListener { _, checked ->
            flatsViewModel.setShowSeen(checked)
        }
    }

    private fun observeLoadingState() {
        flatsViewModel.loadingStateData.stateData.observe(this, Observer<ContentViewState> {
            when (it) {
                ContentViewState.CONTENT -> flatsContentViewFlipper.showContent()
                ContentViewState.LOADING -> flatsContentViewFlipper.showLoader()
                ContentViewState.ERROR -> flatsContentViewFlipper.showError()
                ContentViewState.NO_DATA -> flatsContentViewFlipper.showNoContent()
                null -> flatsContentViewFlipper.showLoader()
            }
        })
        flatsViewModel.loadingStateData.isRefreshing.observe(this, Observer {
            flatsListSwipeRefresh.isRefreshing = it == true
        })
    }


    private fun onFlatsReceived(flats: List<FlatViewState>?) {
        adapter.submitList(flats)
        flatsMapFragment?.setFlats(flats?.map { it.flat } ?: emptyList())
    }

    private fun fillFloatingMenu() {
        flatsListFabMenu?.removeAllMenuButtons()

        flatsListFabMenu.menuIconView?.setImageResource(getFabImage(currentMode))
        flatsListFabMenu.close(true)
        for (mode in MODE.values()) {
            if (mode == currentMode) {
                continue
            }

            val fab = FloatingActionButton(context)
            fab.setImageResource(getFabImage(mode))
            fab.setColorNormalResId(R.color.colorAccent)
            fab.setColorPressedResId(R.color.colorAccent)
            fab.tag = mode
            fab.labelText = getString(getFabText(mode))
            fab.setOnClickListener(filterClickListener)
            flatsListFabMenu.addMenuButton(fab)
        }
    }

    private fun getDisplayedPagePos(mode: MODE): Int = when (mode) {
        MODE.LIST, MODE.FAVORITES -> VIEW_FLIPPER_LIST_POS
        MODE.MAP -> VIEW_FLIPPER_MAP_POS
    }

    private fun getFabImage(mode: MODE): Int = when (mode) {
        MODE.LIST -> R.drawable.ic_format_list_bulleted_24dp
        MODE.MAP -> R.drawable.ic_map_24dp
        MODE.FAVORITES -> R.drawable.ic_filter_favorites_white_24dp
    }

    private fun getFabText(mode: MODE): Int = when (mode) {
        MODE.LIST -> R.string.fab_list
        MODE.MAP -> R.string.fab_map
        MODE.FAVORITES -> R.string.fab_favorites
    }


    enum class MODE(val statuses: List<FlatStatus>) {
        LIST(listOf(FlatStatus.REGULAR, FlatStatus.FAVORITE)),
        MAP(listOf(FlatStatus.REGULAR, FlatStatus.FAVORITE)),
        FAVORITES(listOf(FlatStatus.FAVORITE))
    }

    companion object {
        const val VIEW_FLIPPER_LIST_POS = 0
        const val VIEW_FLIPPER_MAP_POS = 1
    }
}