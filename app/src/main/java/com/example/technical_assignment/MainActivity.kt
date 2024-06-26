package com.example.technical_assignment

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
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
                .replace(R.id.fragment_container, lastFragment!!)
                .commit()
        }

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.navigation_tickets -> lastFragment ?: PlaceholderFragment()
                else -> PlaceholderFragment()
            }

            if (fragment !is PlaceholderFragment) {
                lastFragment = fragment
            }

            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
            true
        }
    }
}


class OffersAdapter(private var offers: List<Offer>) : RecyclerView.Adapter<OffersAdapter.OfferViewHolder>() {

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
        holder.price.text = "от ${NumberFormat.getNumberInstance(Locale("ru", "RU")).format(offer.price.value)}"

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        offersRecyclerView = view.findViewById(R.id.offers_recycler_view)

        offersAdapter = OffersAdapter(listOf())
        offersRecyclerView.adapter = offersAdapter

        val repository = MainRepository(RetrofitClient.instance)
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        viewModel.getOffers()

        viewModel.offers.observe(viewLifecycleOwner, Observer { offers ->
            offersAdapter.updateOffers(offers)
        })

        // Вставьте этот код здесь
        val editText1 = view.findViewById<EditText>(R.id.editText1)
        val editText2 = view.findViewById<EditText>(R.id.editText2)

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

}

class PlaceholderFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_placeholder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button: Button = view.findViewById(R.id.button) // Замените на ID вашей кнопки
        button.setOnClickListener {
            val lastFragment = (activity as MainActivity).lastFragment
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, lastFragment ?: MainFragment())
                ?.commit()

            // Обновите активный элемент в BottomNavigationView
            if (lastFragment is MainFragment) {
                (activity as MainActivity).bottomNavigation.setSelectedItemId(R.id.navigation_tickets)
            } else {
                // Установите ID другого элемента, если у вас есть другие фрагменты
            }
        }
    }
}
