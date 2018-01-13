package kazarovets.flatspinger.fragments

import android.annotation.TargetApi
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.clans.fab.FloatingActionButton
import kazarovets.flatspinger.R
import kazarovets.flatspinger.activity.FlatDetailsActivity
import kazarovets.flatspinger.db.FlatsDatabase
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatInfo
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.ui.FlatsRecyclerAdapter
import kazarovets.flatspinger.utils.PreferenceUtils
import kazarovets.flatspinger.utils.getAppComponent
import kazarovets.flatspinger.viewmodel.FlatInfosViewModel
import kazarovets.flatspinger.viewmodel.FlatInfosViewModelFactory
import kotlinx.android.synthetic.main.fragment_flats_list.*
import javax.inject.Inject


class FlatsListFragment : Fragment() {

    companion object {
        val VIEW_FLIPPER_LIST_POS = 0
        val VIEW_FLIPPER_MAP_POS = 1
    }

    @Inject
    lateinit var flatsFactory: FlatInfosViewModelFactory

    private var adapter: FlatsRecyclerAdapter? = null
    private var paint: Paint = Paint()
    private var currentMode: MODE = MODE.LIST

    private var flats: MutableList<Flat> = ArrayList()

    private var flatsMapFragment: FlatsMapFragment? = null


    private lateinit var flatsViewModel: FlatInfosViewModel

    private val filterClickListener = View.OnClickListener { v ->
        val mode = v?.tag as MODE
        currentMode = mode
        flatsViewModel.flatsMode = mode
        fillFloatingMenu()
        updateAdapterData()
        flatsListMapSwitcher.displayedChild = getDisplayedPagePos(mode)
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
            flatsViewModel.loadFlats()
        }

        flatsListRecycler.layoutManager = LinearLayoutManager(context)
        adapter = FlatsRecyclerAdapter(ArrayList())
        adapter?.onClickListener = object : FlatsRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(item: Flat) {
                val intent = FlatDetailsActivity.getCallingIntent(context!!, item)
                startActivity(intent)
            }
        }
        flatsListRecycler.adapter = adapter
        initSwipe()

        fillFloatingMenu()

        flatsListSwipeRefresh.isRefreshing = true

        flatsViewModel.getFlats().observe(this, Observer<List<FlatInfo>> {
            onFlatsReceived(it)
        })

        flatsViewModel.getIsLoading().observe(this, Observer<Boolean> {
            flatsListSwipeRefresh.isRefreshing = it ?: flatsListSwipeRefresh.isRefreshing ?: false
        })

        flatsMapFragment = FlatsMapFragment()
        childFragmentManager
                .beginTransaction()
                .replace(R.id.flatsListsMapContainer, flatsMapFragment)
                .commit()
    }


    private fun onFlatsReceived(flats: List<Flat>?) {
        val list = ArrayList<Flat>()
        if (flats != null) {
            list.addAll(flats)
        }
        this.flats = list
        updateAdapterData()

        flatsMapFragment?.setFlats(getFilteredFlats())
    }

    private fun initSwipe() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun isItemViewSwipeEnabled(): Boolean = true

            override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
                val dragFlags = 0
                val swipeFlags = ItemTouchHelper.LEFT
                return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
            }


            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                val flat = adapter?.getItem(position)
                if (flat != null) {
                    if (direction == ItemTouchHelper.LEFT) {
                        adapter?.removeItem(position)
                        FlatsDatabase.getInstance(context!!).setHiddenFlat(flat.getId(), flat.getProvider())
//                    } else {
//                        adapter?.removeItem(position)
//                        FlatsDatabase.getInstance(context).setRegularFlat(flat.getId(), flat.getProvider())
                    }
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                     dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                val icon: Bitmap
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    val itemView = viewHolder.itemView
                    val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                    val width = height / 3

                    if (dX > 0) {
//                        paint.setColor(ContextCompat.getColor(context, R.color.colorFlatSeen))
//                        val background = RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
//                        c.drawRect(background, paint)
//                        val vectorDrawable = context.getDrawable(R.drawable.ic_seen_white) as VectorDrawable
//                        icon = getBitmap(vectorDrawable)
//                        val icon_dest = RectF(itemView.left.toFloat() - width / 2 + dX / 2, itemView.top.toFloat() + width,
//                                itemView.left.toFloat() + width / 2 + dX / 2, itemView.bottom.toFloat() - width)
//                        c.drawBitmap(icon, null, icon_dest, paint)
                    } else {
                        paint.setColor(ContextCompat.getColor(context!!, R.color.colorFlatDelete))
                        val background = RectF(itemView.right.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                        c.drawRect(background, paint)
                        val vectorDrawable = context?.getDrawable(R.drawable.ic_delete_white) as VectorDrawable
                        icon = getBitmap(vectorDrawable)
                        val icon_dest = RectF(itemView.right.toFloat() - width / 2 + dX / 2, itemView.top.toFloat() + width,
                                itemView.right.toFloat() + width / 2 + dX / 2, itemView.bottom.toFloat() - width)
                        c.drawBitmap(icon, null, icon_dest, paint)
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(flatsListRecycler)
    }

    private fun getFilteredFlats(): List<Flat> {
        return flats.filter {
            if (context == null) {
                return flats
            }

            val db = FlatsDatabase.getInstance(context!!)
            val status = db.getFlatStatus(it.getId(), it.getProvider())
            val seen = db.isSeenFlat(it.getId(), it.getProvider())
            val showSeen = PreferenceUtils.showSeenFlats

            //todo: seen flats
            currentMode.statuses.contains(status) && (showSeen || !seen)
        }
    }

    private fun updateAdapterData() {
        adapter?.setData(getFilteredFlats())
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getBitmap(vectorDrawable: VectorDrawable): Bitmap {
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return bitmap
    }

    enum class MODE(val statuses: List<FlatStatus>) {
        LIST(listOf(FlatStatus.REGULAR, FlatStatus.FAVORITE)),
        MAP(listOf(FlatStatus.REGULAR, FlatStatus.FAVORITE)),
        FAVORITES(listOf(FlatStatus.FAVORITE))
    }
}