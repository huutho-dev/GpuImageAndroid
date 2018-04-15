package android.sticky.com.ui

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.sticky.com.R
import android.sticky.com.lib.Config
import android.sticky.com.lib.StickerGroup
import android.sticky.com.lib.event.BottomRightIconEvent
import android.sticky.com.lib.event.TopLeftIconEvent
import android.sticky.com.lib.event.flip.FlipHorizontallyEvent
import android.sticky.com.lib.event.parent.OnStickerOperationListener
import android.sticky.com.lib.stickeritem.DrawableSticker
import android.sticky.com.lib.stickeritem.IconMenuOption
import android.sticky.com.lib.stickeritem.Sticker
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.sample.huutho.utils.App
import com.sample.huutho.utils.getBitmapFromTempCache
import com.sample.huutho.utils.saveBitmapToTempCache
import org.jetbrains.anko.dip
import org.jetbrains.anko.forEachChild
import java.io.File
import java.io.InputStreamReader
import java.util.*

/**
 * Created by FRAMGIA\nguyen.huu.tho on 13/04/2018.
 */
class IconStickyFragment : Fragment(), OnStickerOperationListener, IconStickyAdapter.IOnStickyEventListener {


    companion object {

        const val SHARE_PREF_KEY_HISTORY = "SHARE_PREF_KEY_HISTORY"

        fun newInstance() = IconStickyFragment()
    }


    private lateinit var mTitle: TextView
    private lateinit var mImageView: ImageView
    private lateinit var mStickerGroup: StickerGroup
    private lateinit var mContainerCategory: LinearLayout
    private lateinit var mRecyclerView: RecyclerView

    private val mStickyEntity = Gson().fromJson(InputStreamReader(App.getInstance().assets.open("stickers/package.json")), StickyEntity::class.java)
    private val mStickyAdapter = IconStickyAdapter(this)

    /**********************************************************************************************/

    private val start = {}

    private val success = { bmp: Bitmap ->
        mImageView.setImageBitmap(bmp)
        mStickerGroup.layout(mImageView.left, mImageView.top, mImageView.right, mImageView.bottom)
        mStickerGroup.invalidate()
    }

    private val failure = { Toast.makeText(context, "failure", Toast.LENGTH_SHORT).show() }

    /**********************************************************************************************/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_icon_sticky, container, false)
        mTitle = view.findViewById(R.id.control_bar_title)
        mImageView = view.findViewById(R.id.image_view)
        mStickerGroup = view.findViewById(R.id.sticker_group)
        mContainerCategory = view.findViewById(R.id.container_category)
        mRecyclerView = view.findViewById(R.id.recycler_view)

        view.findViewById<ImageView>(R.id.control_bar_cancel).setOnClickListener { onCancel() }
        view.findViewById<ImageView>(R.id.control_bar_done).setOnClickListener { onSave() }
        context?.getBitmapFromTempCache(start, success, failure)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initStickerGroup()

        mRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mRecyclerView.adapter = mStickyAdapter

        mStickyEntity.categories.forEachIndexed { index, category ->
            val categoryView = TextView(view.context)
            with(categoryView) {
                setPadding(dip(16), dip(8), dip(16), dip(8))
                text = category.name

                setOnClickListener {
                    mContainerCategory.forEachChild { (it as TextView).setTextColor(resources.getColor(R.color.colorTextDarkSecondary)) }
                    categoryView.setTextColor(resources.getColor(R.color.colorSelected))
                    mStickyAdapter.mStickies = mStickyEntity.categories[index].items
                    mStickyAdapter.notifyDataSetChanged()
                }
            }
            mContainerCategory.addView(categoryView)
        }
        if (mContainerCategory.childCount > 0){
            mContainerCategory.getChildAt(0).performClick()
        }
    }


    override fun onStickySelected(view: View, sticky: ItemsBean, position: Int) {
        addSticker("stickers/${sticky.main}")
    }


    private fun onSave() {
        val saveSuccess = { _: File -> activity?.supportFragmentManager?.popBackStackImmediate().run {} }

        val saveFailure = {
            activity?.supportFragmentManager?.popBackStackImmediate()
            Toast.makeText(context, "Save failur", Toast.LENGTH_SHORT).show()
        }

        context?.saveBitmapToTempCache(mStickerGroup.createBitmap(), saveSuccess, saveFailure)
    }

    private fun onCancel() {
        Toast.makeText(context, "onCancel", Toast.LENGTH_SHORT).show()
        activity?.supportFragmentManager?.popBackStack()
    }

    /************************************** INNER FUNCTION ****************************************/

    private fun initStickerGroup() {

        context?.let {
            val deleteIcon = IconMenuOption(ContextCompat.getDrawable(it, R.drawable.sticker_ic_close_white_18dp), Config.Gravity.LEFT_TOP)
            deleteIcon.iconEventListener = TopLeftIconEvent()

            val zoomIcon = IconMenuOption(ContextCompat.getDrawable(it, R.drawable.sticker_ic_scale_white_18dp), Config.Gravity.RIGHT_BOTOM)
            zoomIcon.iconEventListener = BottomRightIconEvent()

            val flipIcon = IconMenuOption(ContextCompat.getDrawable(it, R.drawable.sticker_ic_flip_white_18dp), Config.Gravity.RIGHT_TOP)
            flipIcon.iconEventListener = FlipHorizontallyEvent()


            mStickerGroup.optionIcons = Arrays.asList(deleteIcon, zoomIcon, flipIcon)
            mStickerGroup.setBackgroundColor(Color.WHITE)
            mStickerGroup.isLocked = false
            mStickerGroup.isConstrained = true
            mStickerGroup.onStickerOperationListener = this
        }
    }


    /**
     * Replace the current sticker with another sticker
     * @param sticker The sticker will be replaced
     */
    fun testReplace(sticker: Sticker) {
        if (mStickerGroup.replace(sticker)) {
            Toast.makeText(this.context!!, "Replace Sticker successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this.context!!, "Replace Sticker failed!", Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * Lock selected sticker
     * Hide all option menu
     */
    fun lockSticker() {
        mStickerGroup.isLocked = !mStickerGroup.isLocked
    }


    /**
     * Remove current selected sticker in ViewGroup
     */
    fun removeCurrentSticker() {
        if (mStickerGroup.removeCurrentSticker()) {
            Toast.makeText(this.context!!, "Remove current Sticker successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this.context!!, "Remove current Sticker failed!", Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * Remove all sticker in ViewGroup
     */
    private fun removeAllSticker() {
        mStickerGroup.removeAllStickers()
    }


    /**
     * Add Sticker to ViewGroup
     * @param pathAsset path of image_sticker in assets folder
     */
    private fun addSticker(pathAsset: String) {
        val d = Drawable.createFromStream(this.context!!.assets.open(pathAsset), null)
        mStickerGroup.addSticker(DrawableSticker(d))


    }


    /**************************************** Sticker Callback ************************************/

    override fun onStickerAdded(sticker: Sticker) {
    }

    override fun onStickerClicked(sticker: Sticker) {
    }

    override fun onStickerDeleted(sticker: Sticker) {
    }

    override fun onStickerDragFinished(sticker: Sticker) {
    }

    override fun onStickerTouchedDown(sticker: Sticker) {
    }

    override fun onStickerZoomFinished(sticker: Sticker) {
    }

    override fun onStickerFlipped(sticker: Sticker) {
    }

    override fun onStickerDoubleTapped(sticker: Sticker) {
    }


}