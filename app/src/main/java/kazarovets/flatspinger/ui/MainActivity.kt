package kazarovets.flatspinger.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import kazarovets.flatspinger.R
import kazarovets.flatspinger.api.ApiManager
import kazarovets.flatspinger.model.Flat

class MainActivity : AppCompatActivity() {


    protected var disposable: Disposable? = null

    private var mRecyclerView: RecyclerView? = null
    private var adapter: FlatsRecyclerAdapter? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRecyclerView = findViewById(R.id.recycler)
        mRecyclerView?.layoutManager = LinearLayoutManager(this)
        mRecyclerView?.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        adapter = FlatsRecyclerAdapter(ArrayList())
        adapter?.onClickListener = object : FlatsRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(item: Flat) {
                if (!TextUtils.isEmpty(item.getOriginalUrl())) {
                    intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.getOriginalUrl()))
                    startActivity(intent)
                }
            }
        }
        mRecyclerView?.adapter = adapter

        disposable = ApiManager.onlinerApi.getLatestFlats()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<Flat>>() {
                    override fun onError(e: Throwable) {
                        onFlatsReceived(null)
                    }

                    override fun onSuccess(standings: List<Flat>) {
                        onFlatsReceived(standings)
                    }

                })

        val navigation = findViewById<BottomNavigationView>(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun onFlatsReceived(standings: List<Flat>?) {
        adapter?.setData(standings)
    }

    override fun onDestroy() {
        if (disposable != null) {
            disposable?.dispose()
            disposable = null
        }
        super.onDestroy()
    }

}
