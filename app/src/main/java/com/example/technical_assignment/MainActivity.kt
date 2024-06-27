package com.example.technical_assignment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationBarView
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var bottomNavigation: BottomNavigationView
    var lastFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED

        if (savedInstanceState == null) {
            lastFragment = MainFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, lastFragment!!).commit()
        }

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.navigation_tickets -> lastFragment ?: PlaceholderFragment()
                else -> PlaceholderFragment()
            }

            if (fragment !is PlaceholderFragment) {
                lastFragment = fragment
            }

            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                .commit()
            true
        }
    }
}


class OffersAdapter(private var offers: List<Offer>) :
    RecyclerView.Adapter<OffersAdapter.OfferViewHolder>() {

    class OfferViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.offer_title)
        val town: TextView = view.findViewById(R.id.offer_town)
        val price: TextView = view.findViewById(R.id.offer_price)
        val largeImage: ImageView = view.findViewById(R.id.offer_large_image)
        val loadingSpinner: ProgressBar = view.findViewById(R.id.loading_spinner)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.offer_item, parent, false)
        return OfferViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        val offer = offers[position]
        holder.title.text = offer.title
        holder.town.text = offer.town
        holder.price.text =
            "от ${NumberFormat.getNumberInstance(Locale("ru", "RU")).format(offer.price.value)}"

        val imageResource = when (offer.id % 6) {
            1 -> R.drawable.offers_drawable_1
            2 -> R.drawable.offers_drawable_2
            3 -> R.drawable.offers_drawable_3
            4 -> R.drawable.offers_drawable_4
            5 -> R.drawable.offers_drawable_5
            6 -> R.drawable.offers_drawable_6
            else -> null
        }

        if (imageResource != null) {
            holder.largeImage.setImageResource(imageResource)
            holder.largeImage.visibility = View.VISIBLE
            holder.loadingSpinner.visibility = View.GONE
        } else {
            holder.largeImage.visibility = View.INVISIBLE
            holder.loadingSpinner.visibility = View.VISIBLE
        }
    }

    override fun getItemCount() = offers.size

    fun updateOffers(newOffers: List<Offer>) {
        this.offers = newOffers
        notifyDataSetChanged()
    }
}

class MainFragment : Fragment() {
    private lateinit var offersRecyclerView: RecyclerView
    private lateinit var offersAdapter: OffersAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var editText1: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        offersRecyclerView = view.findViewById(R.id.offers_recycler_view)
        editText1 = view.findViewById(R.id.editText1)

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        editText1.setText(sharedPref?.getString("edit_text_1", ""))


        offersAdapter = OffersAdapter(listOf())
        offersRecyclerView.adapter = offersAdapter

        val repository = MainRepository(RetrofitClient.instance)
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        viewModel.getOffers()

        viewModel.offers.observe(viewLifecycleOwner, Observer { offers ->
            offersAdapter.updateOffers(offers)
        })
        val editText1 = view.findViewById<EditText>(R.id.editText1)
        val editText2 = view.findViewById<EditText>(R.id.editText2)

        editText2.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val dialog = ModalDialogFragment()
                dialog.show(childFragmentManager, "ModalDialogFragment")
            }
        }

        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (!Character.UnicodeBlock.of(source[i]).equals(Character.UnicodeBlock.CYRILLIC)) {
                    return@InputFilter ""
                }
            }
            null
        }

        editText1.filters = arrayOf(filter)
        editText2.filters = arrayOf(filter)
    }

    override fun onPause() {
        super.onPause()

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("edit_text_1", editText1.text.toString())
            apply()
        }
    }

}

class PlaceholderFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_placeholder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button: Button = view.findViewById(R.id.button)
        button.setOnClickListener {
            val lastFragment = (activity as MainActivity).lastFragment
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, lastFragment ?: MainFragment())?.commit()

            if (lastFragment is MainFragment) {
                (activity as MainActivity).bottomNavigation.setSelectedItemId(R.id.navigation_tickets)
            } else {
            }
        }
    }
}

class ModalDialogFragment : BottomSheetDialogFragment() {

    interface DialogListener {
        fun onOptionSelected(option: String)
    }

    var listener: DialogListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_fragment, container, false)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogWindowTheme)
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            it.window?.setLayout(width, height)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cardView: CardView = view.findViewById(R.id.dialogCardView)
        val editText1: EditText = view.findViewById(R.id.editText1)
        val editText2: EditText = view.findViewById(R.id.editText2)
        val undoImageButton: ImageButton = view.findViewById(R.id.undoImageButton)
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        editText1.setText(sharedPref?.getString("edit_text_1", ""))

        editText1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                with(sharedPref?.edit()) {
                    this?.putString("edit_text_1", s.toString())
                    this?.apply()
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        undoImageButton.setOnClickListener {
            editText2.text.clear()
        }

        view.findViewById<LinearLayout>(R.id.dialogButton1).setOnClickListener {
            startActivity(Intent(activity, BlankActivity::class.java))
        }

        view.findViewById<LinearLayout>(R.id.dialogButton2).setOnClickListener {
            val cities =
                arrayOf("Москва", "Санкт-Петербург", "Новосибирск", "Екатеринбург", "Казань")
            val randomCity = cities[(cities.indices).random()]
            editText2.setText(randomCity)
        }

        view.findViewById<LinearLayout>(R.id.dialogButton3).setOnClickListener {
            startActivity(Intent(activity, BlankActivity::class.java))
        }

        view.findViewById<LinearLayout>(R.id.dialogButton4).setOnClickListener {
            startActivity(Intent(activity, BlankActivity::class.java))
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DialogListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
