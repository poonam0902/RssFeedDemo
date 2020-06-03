package com.app.myapplication

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.app.myapplication.databinding.ActivityMainBinding
import com.app.myapplication.databinding.FragmentItemListBinding
import pub.devrel.easypermissions.EasyPermissions
import java.io.*
import java.lang.ref.WeakReference
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection
import kotlin.collections.ArrayList

class RSSFragment : Fragment(), OnItemClickListener<RssItem>, EasyPermissions.PermissionCallbacks{

    // TODO: Customize parameters
    private var columnCount = 1

    private val permission = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    val RSS_FEED_LINK = "https://flipboard.com/topic/moncton.rss";

    var adapter: MyItemRecyclerViewAdapter? = null
    var rssItems = ArrayList<RssItem>()

    var listV : RecyclerView?= null
    var viewpager :ViewPager2?= null
     var link:String?=""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        viewpager = view.findViewById(R.id.viewPager2)
        viewpager!!.orientation  = ViewPager2.ORIENTATION_VERTICAL

       return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = MyItemRecyclerViewAdapter(rssItems, activity, this)

        viewpager!!.adapter=adapter
        adapter!!.setItemClickListener(this)


        val url = URL(RSS_FEED_LINK)
        RssFeedFetcher(this).execute(url)

    }

    fun updateRV(rssItemsL: List<RssItem>) {
        if (rssItemsL != null && !rssItemsL.isEmpty()) {
            rssItems.addAll(rssItemsL)
            adapter?.notifyDataSetChanged()
        }
    }


    class RssFeedFetcher(val context: RSSFragment) : AsyncTask<URL, Void, List<RssItem>>() {
        val reference = WeakReference(context)
        private var stream: InputStream? = null;
        override fun doInBackground(vararg params: URL?): List<RssItem>? {
            val connect = params[0]?.openConnection() as HttpsURLConnection
            connect.readTimeout = 8000
            connect.connectTimeout = 8000
            connect.requestMethod = "GET"
            connect.connect();

            val responseCode: Int = connect.responseCode;
            var rssItems: List<RssItem>? = null
            if (responseCode == 200) {
                stream = connect.inputStream;


                try {
                    val parser = RssParser()
                    rssItems = parser.parse(stream!!)

                } catch (e: IOException) {
                    e.printStackTrace()
                }


            }

            return rssItems

        }

        override fun onPostExecute(result: List<RssItem>?) {
            super.onPostExecute(result)
            if (result != null && !result.isEmpty()) {
                reference.get()?.updateRV(result)
            }

        }

    }



    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: RssItem?)
    }

    override fun onItemClick(view: View?, feed: RssItem, position: Int) {
        val item_id = view?.id
        link = feed.link
        when (item_id) {

            //Toast.makeText(context, "recipe", Toast.LENGTH_SHORT).show()
            R.id.ll_main -> openWebview(feed)
            R.id.shareBtn ->  checkStoragePermission(view.rootView, link!!)
        }

    }
    fun checkStoragePermission(view: View, link: String) {
        if (!EasyPermissions.hasPermissions(context!!, *permission)
        ) { // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "Require Permission", 111, *permission)

        } else {
            Toast.makeText(context, "PERMISSION  GRANTED", Toast.LENGTH_SHORT).show()
            shareImage(saveImageToExternalStorage(getScreenShot(view)!!), link)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.d("Check Permission", "ON PERMISSION Denied CALLED")

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.d("Check Permission", "ON PERMISSION GRANTED CALLED")
        checkStoragePermission(view!!.rootView, link!!)

    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray) {
        Log.d("Check Permission", "ON PERMISSION Result CALLED")

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)

    }

    fun getScreenShot(view: View): Bitmap? {
        val screenView = view.rootView
        screenView.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(screenView.drawingCache)
        screenView.isDrawingCacheEnabled = false
        return bitmap
    }

    fun shareImage( uri: Uri, link:String){
        //val uri = Uri.fromFile(file)
        val intent = Intent()
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.setPackage("com.whatsapp");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, link);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


        try {
            startActivity(Intent.createChooser(intent, "Share News"));
        } catch ( e : ActivityNotFoundException) {
            Toast.makeText(context, "No App Available", Toast.LENGTH_SHORT).show();
        }
    }

    private fun saveImageToExternalStorage(bitmap: Bitmap): Uri {
        // Get the external storage directory path
        val path = Environment.getExternalStorageDirectory().toString()

        // Create a file to save the image
       val file = File(path, "${UUID.randomUUID()}.jpg")

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress the bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the output stream
            stream.flush()

            // Close the output stream
            stream.close()
            Toast.makeText(context, "Image saved", Toast.LENGTH_SHORT).show()
        } catch (e: IOException){ // Catch the exception
            e.printStackTrace()
            Toast.makeText(context, "Error to save image", Toast.LENGTH_SHORT).show()
        }

        // Return the saved image path to uri
        return Uri.parse(file.absolutePath)
    }
    fun openWebview(item:RssItem){
        val intent = Intent(context, WebViewActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, item.link)
        }

            startActivity(intent)

    }


}