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
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.github.chrisbanes.photoview.PhotoViewAttacher
import com.viewpagerindicator.CirclePageIndicator
import com.viewpagerindicator.PageIndicator
import kazarovets.flatspinger.R

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class ImagesActivity : AppCompatActivity() {

    companion object {
        val KEY_GALLERY = "gallery"

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

    private var viewPager: ViewPager? = null
    private var pageIndicator: PageIndicator? = null
    private var gallery: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_images)
        viewPager = findViewById(R.id.view_pager)
        pageIndicator = findViewById<CirclePageIndicator>(R.id.page_indicator)

        val extras = intent.extras
        gallery = extras.getStringArrayList(KEY_GALLERY)
        viewPager?.adapter = GalleryPhotoPagerAdapter(gallery)
        pageIndicator?.setViewPager(viewPager)
    }


    class GalleryPhotoPagerAdapter(private val mGallery: ArrayList<String>)
        : PagerAdapter(), ViewPager.PageTransformer {
        private var mAttacher: PhotoViewAttacher? = null

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val inflater = LayoutInflater.from(container.context)
            val view = inflater.inflate(R.layout.item_gallery_photo, container, false)
            val image = view.findViewById<PhotoView>(R.id.item_gallery_photo_image)
            mAttacher = PhotoViewAttacher(image)
            Glide.with(image.getContext()).load(mGallery[position]).into(image)
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