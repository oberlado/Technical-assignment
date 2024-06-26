package com.example.technical_assignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var offersRecyclerView: RecyclerView
    private lateinit var offersAdapter: OffersAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottom_navigation)
        offersRecyclerView = findViewById(R.id.offers_recycler_view)

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            val fragment: Fragment = PlaceholderFragment()
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
            true
        }

        offersAdapter = OffersAdapter(listOf())
        offersRecyclerView.adapter = offersAdapter

        val repository = MainRepository(RetrofitClient.instance)
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        viewModel.getOffers()
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
            1 -> R.drawable.offers_drawable_2
            2 -> R.drawable.offers_drawable_3
            3 -> R.drawable.offers_drawable_4
            4 -> R.drawable.offers_drawable_5
            5 -> R.drawable.offers_drawable_6
            else -> null
        }

        if (imageResource != null) {
            holder.largeImage.setImageResource(imageResource)
            holder.largeImage.visibility = View.VISIBLE
            holder.loadingSpinner.visibility = View.GONE
        } else {
            holder.largeImage.visibility = View.GONE
            holder.loadingSpinner.visibility = View.VISIBLE
        }
    }

    override fun getItemCount() = offers.size

    fun updateOffers(newOffers: List<Offer>) {
        this.offers = newOffers
        notifyDataSetChanged()
    }
}
