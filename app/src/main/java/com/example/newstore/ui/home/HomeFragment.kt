package com.example.newstore.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.newstore.R
import com.example.newstore.adapter.CategoryAdapter
import com.example.newstore.adapter.PosterAdapter
import com.example.newstore.adapter.product.ProductAdapter
import com.example.newstore.adapter.product.ProductState
import com.example.newstore.databinding.HomeFragmentBinding
import com.example.newstore.databinding.ProductItemSearchBinding
import com.example.newstore.interfaces.RecyclerAdapterListener
import com.example.newstore.model.CategoryM
import com.example.newstore.model.NetworkResult
import com.example.newstore.model.PosterM
import com.example.newstore.model.ProductM
import com.example.newstore.ui.MainActivity
import com.example.newstore.ui.details.ProductDetailsActivity
import com.example.newstore.ui.search.SearchActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable


@AndroidEntryPoint
class HomeFragment : Fragment(), RecyclerAdapterListener {//close class// close class


    lateinit var name: String
    private lateinit var binding: HomeFragmentBinding
    lateinit var adapter: ProductAdapter
    lateinit var posteradapter: PosterAdapter
    lateinit var cateadapter: CategoryAdapter


    var posterMS = ArrayList<PosterM>()
    var cateMs = ArrayList<CategoryM>()
    var lProduct: MutableList<ProductM> = ArrayList()
    var sorted: MutableList<ProductM> = ArrayList()


    companion object {
        fun newInstance() = HomeFragment()
    }

    private val homeVM: HomeVM by viewModels()
    var error: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)
        binding.presenter = this

        homeVM.listProducts()

        observe()

//calling function
        providePosterList()
        createRecyclerPoster()
        provideCateList()
        createRecyclerCate()

        return binding.root


    }// close onCreateView

    fun search(view: View) {
        binding.expandableLayoutHrz.collapse()
        intent(lProduct)
    }

    private fun observe() {
        homeVM.homeLiveDataP.observe(viewLifecycleOwner) {

            when (it) {
                is NetworkResult.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE

                }
                is NetworkResult.Success -> {
                    lProduct = it.data
                    createRecyclerProduct(lProduct)
                    binding.progressBar.visibility = View.GONE


                }
                is NetworkResult.Failure -> {
                    error = it.errorMessage
                    binding.progressBar.visibility = View.GONE
                    binding.error.visibility = View.VISIBLE

                }
                else -> {}
            }

        }
    }

    fun filterCate(l: MutableList<ProductM>, nameClick: String) {
        l.forEach { it ->
            if (nameClick == "All Product") {
                createRecyclerProduct(l)
            } else {
                var v = l.filter { it.category.name == nameClick }
                sorted = v as MutableList<ProductM>
                createRecyclerProduct(sorted)
            }
        }
    }

    private fun createRecyclerProduct(productList: MutableList<ProductM>) {

        adapter = ProductAdapter(requireContext(), productList, ProductState.Item, this)
        binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProducts.adapter = adapter


    }

    private fun createRecyclerPoster() {
        posteradapter = PosterAdapter(requireContext(), posterMS)
        binding.rvPoster.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvPoster);
        binding.rvPoster.adapter = posteradapter


    }


    private fun createRecyclerCate() {
        cateadapter = CategoryAdapter(requireContext(), ProductState.CategoryName, cateMs, this)
        binding.rvCategory.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategory.adapter = cateadapter
    }

    private fun providePosterList() {
        posterMS.add(
            PosterM(
                "https://dkstatics-public.digikala.com/digikala-adservice-banners/3961d8e88ae3abd59959b1de8115323de3256629_1664953207.jpg?x-oss-process=image/quality,q_95"
            )
        )



        posterMS.add(
            PosterM(
                "https://dkstatics-public.digikala.com/digikala-adservice-banners/bbee21ad67f54251cf69358eafd637869ef35c4e_1664952872.gif?x-oss-process=image"
            )
        )
        posterMS.add(
            PosterM(
                "https://mashadkala.com/images/thumbs/0188735_1.jpeg"
            )
        )

        posterMS.add(
            PosterM(
                "https://mashadkala.com/images/thumbs/0188739_3.3.jpeg"
            )
        )


    }

    private fun provideCateList() {
        cateMs.add(
            CategoryM(
                0,
                "All Product",
                " "
            )
        )
        cateMs.add(
            CategoryM(
                1,
                "Clothes",
                ""
            )
        )
        cateMs.add(
            CategoryM(
                2,
                "Electronics",
                ""
            )
        )
        cateMs.add(
            CategoryM(
                3,
                "Furniture",
                ""
            )
        )
        cateMs.add(
            CategoryM(
                4,
                "Shoes",
                ""
            )
        )

        cateMs.add(
            CategoryM(
                5,
                "Others",
                ""
            )
        )


    }

    override fun update(productModel: ProductM) {
        (activity as MainActivity).selectCart(productModel)
        (activity as MainActivity).selectFav(productModel)

    }

    override fun type(name: String) {
        filterCate(lProduct, name)
    }

    fun intent(myList: MutableList<ProductM>) {

        val intent = Intent(context, SearchActivity::class.java)
        intent.putExtra("myList", myList as Serializable)
        startActivity(intent)


    }

}


