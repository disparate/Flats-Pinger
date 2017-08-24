package kazarovets.flatspinger.activity

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.ViewSwitcher
import kazarovets.flatspinger.R

class MainActivity : AppCompatActivity() {

    companion object {
        val POSITION_LIST = 0
        val POSITION_FILTER = 1

        val CURRENT_POSITION = "curr_pos"
    }

    private var viewSwitcher: ViewSwitcher? = null
    private var currPosition = POSITION_LIST

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                currPosition = POSITION_LIST
                viewSwitcher?.displayedChild = currPosition
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_filter -> {
                currPosition = POSITION_FILTER
                viewSwitcher?.displayedChild = currPosition
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewSwitcher = findViewById(R.id.view_switcher)

        val navigation = findViewById<BottomNavigationView>(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        currPosition = savedInstanceState?.getInt(CURRENT_POSITION) ?: currPosition
        viewSwitcher?.displayedChild = currPosition
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(CURRENT_POSITION, currPosition)
    }

}