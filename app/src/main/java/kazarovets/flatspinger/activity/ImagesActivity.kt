package kazarovets.flatspinger.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.chrisbanes.photoview.PhotoView
import com.github.chrisbanes.photoview.PhotoViewAttacher
import kazarovets.flatspinger.R
import kazarovets.flatspinger.utils.load
import kotlinx.android.synthetic.main.activity_images.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class ImagesActivity : AppCompatActivity() {

    companion object {
        private const val KEY_GALLERY = "gallery"

        fun getCallingIntent(context: Context, urls: List<String>?): Intent {
            val intent = Intent()
            val arrayList = ArrayList<String>()
            if (urls != null) {
                arrayList.addAll(urls)
            }
            intent.putStringArrayListExtra(KEY_GALLERY, arrayList)
            intent.setClass(context, ImagesActivity::class.java)
            return intent
        }
    }

    private var gallery: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE)

        setContentView(R.layout.activity_images)

        val extras = intent.extras
        gallery = extras.getStringArrayList(KEY_GALLERY)
        imagesViewPager.adapter = GalleryPhotoPagerAdapter(gallery, this)
        imagesPageIndicator.setViewPager(imagesViewPager)
    }


    class GalleryPhotoPagerAdapter(private val mGallery: ArrayList<String>,
                                   private val mActivity: ImagesActivity)
        : PagerAdapter(), ViewPager.PageTransformer {
        private var mAttacher: PhotoViewAttacher? = null

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val inflater = LayoutInflater.from(container.context)
            val view = inflater.inflate(R.layout.item_gallery_photo, container, false)
            val image = view.findViewById<PhotoView>(R.id.item_gallery_photo_image)
            mAttacher = PhotoViewAttacher(image)
            mAttacher?.setOnClickListener {
                mActivity.finish()
            }
            image.load(mGallery[position])
            container.addView(view)
            return view
        }

        override fun getCount(): Int = mGallery.size


        override fun isViewFromObject(view: View, o: Any): Boolean = view === o


        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        //ViewPager.PageTransformer
        override fun transformPage(view: View, position: Float) {
            val parallaxView = view.findViewById<View>(R.id.item_gallery_photo_image)
            if (parallaxView != null) {
                if (position < 0) {
                    parallaxView.translationX = 0f
                } else {
                    val width = parallaxView.width.toFloat()
                    parallaxView.translationX = -(position * width)
                }
            }
        }
    }
}
