package kazarovets.flatspinger.fragments

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import kazarovets.flatspinger.R
import kazarovets.flatspinger.activity.FlatDetailsActivity
import kazarovets.flatspinger.api.ApiManager
import kazarovets.flatspinger.db.FlatsDatabase
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.ui.FlatsRecyclerAdapter
import kazarovets.flatspinger.utils.FlatsFilterMatcher
import kazarovets.flatspinger.utils.PreferenceUtils


class FlatsListFragment : Fragment() {


    protected var disposable: Disposable? = null

    private var recyclerView: RecyclerView? = null
    private var adapter: FlatsRecyclerAdapter? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var paint: Paint = Paint()
    private var currentMode: MODE = MODE.MODE_NOT_SEEN

    private var floatingActionMenu: FloatingActionMenu? = null
    private var flats: MutableList<Flat> = ArrayList()

    private val filterClickListener = View.OnClickListener { v ->
        val mode = v?.tag as MODE
        currentMode = mode
        fillFloatingMenu()
        updateAdapterData()
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_flats_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout = view?.findViewById(R.id.swipe_refresh)
        swipeRefreshLayout?.setOnRefreshListener { loadData() }

        recyclerView = view?.findViewById(R.id.recycler)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        adapter = FlatsRecyclerAdapter(ArrayList())
        adapter?.onClickListener = object : FlatsRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(item: Flat) {
                val intent = FlatDetailsActivity.getCallingIntent(context, item)
                startActivity(intent)
            }
        }
        recyclerView?.adapter = adapter
        initSwipe()

        floatingActionMenu = view?.findViewById(R.id.fab_menu)
        fillFloatingMenu()

        loadData()
    }

    private fun loadData() {
        val minCost = PreferenceUtils.minCost
        val maxCost = PreferenceUtils.maxCost
        val allowAgency = PreferenceUtils.allowAgency
        swipeRefreshLayout?.isRefreshing = true
        disposable = ApiManager.iNeedAFlatApi
                .getFlats(minCost?.toDouble(), maxCost?.toDouble(), allowAgency)
                .mergeWith(ApiManager.onlinerApi.getLatestFlats(minCost, maxCost, !allowAgency))
                .toObservable()
                .flatMap { Observable.fromIterable(it) }
                .filter { FlatsFilterMatcher.matches(PreferenceUtils.flatFilter, it) }
                .toSortedList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<Flat>>() {
                    override fun onError(e: Throwable) {
                        Log.e("FlatsListFragment", "Error receiving flats", e)
                        onFlatsReceived(null)
                        swipeRefreshLayout?.isRefreshing = false
                    }

                    override fun onSuccess(flats: List<Flat>) {
                        onFlatsReceived(flats)
                        swipeRefreshLayout?.isRefreshing = false
                    }

                })
    }

    private fun onFlatsReceived(flats: List<Flat>?) {
        val list = ArrayList<Flat>()
        if (flats != null) list.addAll(flats)
        this.flats = list
        updateAdapterData()
    }


    private fun initSwipe() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun isItemViewSwipeEnabled(): Boolean {
                return currentMode == MODE.MODE_ALL || currentMode == MODE.MODE_NOT_SEEN || currentMode == MODE.MODE_FAVORITES
            }

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
                        FlatsDatabase.getInstance(context).setHiddenFlat(flat.getId(), flat.getProvider())
//                    } else {
//                        adapter?.removeItem(position)
//                        FlatsDatabase.getInstance(context).setSeenFlat(flat.getId(), flat.getProvider())
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
                        paint.setColor(ContextCompat.getColor(context, R.color.colorFlatDelete))
                        val background = RectF(itemView.right.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                        c.drawRect(background, paint)
                        val vectorDrawable = context.getDrawable(R.drawable.ic_delete_white) as VectorDrawable
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
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun updateAdapterData() {
        adapter?.setData(flats.filter {
            val status = FlatsDatabase.getInstance(context).getFlatStatus(it.getId(), it.getProvider())
            currentMode.statuses.contains(status)
        })
    }

    private fun fillFloatingMenu() {
        floatingActionMenu?.removeAllMenuButtons()

        floatingActionMenu?.menuIconView?.setImageResource(getFabImage(currentMode))
        floatingActionMenu?.close(true)
        for (mode in MODE.values()) {
            if (mode == currentMode) {
                continue
            }

            val fab = FloatingActionButton(context)
            fab.setImageResource(getFabImage(mode))
            fab.tag = mode
            fab.labelText = getString(getFabText(mode))
            fab.setOnClickListener(filterClickListener)
            floatingActionMenu?.addMenuButton(fab)
        }
    }

    private fun getFabImage(mode: MODE): Int = when (mode) {
        MODE.MODE_ALL -> R.drawable.ic_filter_all_24dp
        MODE.MODE_NOT_SEEN -> R.drawable.ic_filter_new_white_24dp
        MODE.MODE_SEEN -> R.drawable.ic_filter_seen_white_24dp
        MODE.MODE_DELETED -> R.drawable.ic_filter_delete_white_24dp
        MODE.MODE_FAVORITES -> R.drawable.ic_filter_favorites_white_24dp
    }

    private fun getFabText(mode: MODE): Int = when (mode) {
        MODE.MODE_ALL -> R.string.fab_all
        MODE.MODE_NOT_SEEN -> R.string.fab_not_seen
        MODE.MODE_SEEN -> R.string.fab_seen
        MODE.MODE_FAVORITES -> R.string.fab_favorites
        MODE.MODE_DELETED -> R.string.fab_deleted
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


    override fun onDestroyView() {
        if (disposable != null) {
            disposable?.dispose()
            disposable = null
        }

        super.onDestroyView()
    }

    enum class MODE(val statuses: List<FlatStatus>) {
        MODE_ALL(arrayListOf(FlatStatus.FAVORITE, FlatStatus.NOT_SEEN, FlatStatus.SEEN)),
        MODE_NOT_SEEN(arrayListOf(FlatStatus.NOT_SEEN)),
        MODE_SEEN(arrayListOf(FlatStatus.FAVORITE, FlatStatus.SEEN)),
        MODE_FAVORITES(arrayListOf(FlatStatus.FAVORITE)),
        MODE_DELETED(arrayListOf(FlatStatus.HIDDEN))
    }
}