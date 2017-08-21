package kazarovets.flatspinger.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import kazarovets.flatspinger.R
import kazarovets.flatspinger.api.ApiManager
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.OnlinerFlat
import kazarovets.flatspinger.ui.FlatsRecyclerAdapter
import kazarovets.flatspinger.utils.FlatsFilterMatcher
import kazarovets.flatspinger.utils.PreferenceUtils
import java.util.*


class FlatsListFragment : Fragment() {


    protected var disposable: Disposable? = null

    private var recyclerView: RecyclerView? = null
    private var adapter: FlatsRecyclerAdapter? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_flats_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout = view?.findViewById(R.id.swipe_refresh)
        swipeRefreshLayout?.setOnRefreshListener { loadData() }

        recyclerView = view?.findViewById(R.id.recycler)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        adapter = FlatsRecyclerAdapter(ArrayList())
        adapter?.onClickListener = object : FlatsRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(item: Flat) {
                if (!TextUtils.isEmpty(item.getOriginalUrl())) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.getOriginalUrl()))
                    startActivity(intent)
                }
            }
        }
        recyclerView?.adapter = adapter

        loadData()
    }

    private fun loadData() {
        val minCost = PreferenceUtils.minCost
        val maxCost = PreferenceUtils.maxCost
        swipeRefreshLayout?.isRefreshing = true
        disposable = ApiManager.iNeedAFlatApi
                .getFlats(minCost?.toDouble(), maxCost?.toDouble())
                .mergeWith(ApiManager.onlinerApi.getLatestFlats(minCost, maxCost))
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

                    override fun onSuccess(standings: List<Flat>) {
                        onFlatsReceived(standings)
                        swipeRefreshLayout?.isRefreshing = false
                    }

                })
    }

    private fun onFlatsReceived(standings: List<Flat>?) {
        adapter?.setData(standings)
    }

    override fun onDestroyView() {
        if (disposable != null) {
            disposable?.dispose()
            disposable = null
        }

        super.onDestroyView()
    }
}