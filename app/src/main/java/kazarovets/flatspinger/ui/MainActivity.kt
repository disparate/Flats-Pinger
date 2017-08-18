package kazarovets.flatspinger.ui

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.ViewSwitcher
import kazarovets.flatspinger.R

class MainActivity : AppCompatActivity() {

    companion object {
        val POSITION_LIST = 0
        val POSITION_FILTER = 1
    }

    private var viewSwitcher: ViewSwitcher? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                viewSwitcher?.displayedChild = POSITION_LIST
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_filter -> {
                viewSwitcher?.displayedChild = POSITION_FILTER
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
    }

}
