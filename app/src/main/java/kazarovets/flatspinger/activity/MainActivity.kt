package kazarovets.flatspinger.activity

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ViewFlipper
import kazarovets.flatspinger.R


class MainActivity : AppCompatActivity() {

    companion object {
        val POSITION_LIST = 0
        val POSITION_FILTER = 1
        val POSITION_SETTINGS = 2

        val CURRENT_POSITION = "curr_pos"
    }

    private var viewFlipper: ViewFlipper? = null
    private var currPosition = POSITION_LIST

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                currPosition = POSITION_LIST
                viewFlipper?.displayedChild = currPosition
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_filter -> {
                currPosition = POSITION_FILTER
                viewFlipper?.displayedChild = currPosition
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                currPosition = POSITION_SETTINGS
                viewFlipper?.displayedChild = currPosition
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        viewFlipper = findViewById(R.id.view_flipper)

        val navigation = findViewById<BottomNavigationView>(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        currPosition = savedInstanceState?.getInt(CURRENT_POSITION) ?: currPosition
        viewFlipper?.displayedChild = currPosition

    }


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(CURRENT_POSITION, currPosition)
    }

}
