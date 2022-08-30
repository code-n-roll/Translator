package com.romankaranchuk.translator.ui.translator.selectlang

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romankaranchuk.translator.R
import com.romankaranchuk.translator.databinding.ActivitySelectLanguageBinding
import com.romankaranchuk.translator.utils.UIUtils
import dagger.android.AndroidInjection
import kotlinx.coroutines.launch
import javax.inject.Inject

class SelectLanguageActivity : AppCompatActivity() {

    private var adapter: SelectLanguageRecyclerAdapter? = null

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<SelectLanguageViewModel> { viewModelFactory }

    private var _binding: ActivitySelectLanguageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        _binding = ActivitySelectLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecycler()

        bindViewModel()

        lifecycle.addObserver(viewModel)
    }

    override fun onDestroy() {
        super.onDestroy()

        lifecycle.removeObserver(viewModel)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(menuItem)
    }

    private fun bindViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect { viewState ->
                    when (viewState) {
                        is SelectLanguageViewModel.ViewState.ShowLanguageSelected -> {
                            UIUtils.showToast(applicationContext, "selected ${viewState.language}")
                            setResult(RESULT_OK, Intent().apply {
                                putExtra("result", viewState.language.name)
                            })
                            finish()
                        }
                        is SelectLanguageViewModel.ViewState.ShowLanguages -> {
                            adapter?.updateAll(viewState.languages, viewState.selectedId)
                        }
                    }
                }
            }
        }
    }

    private fun setupRecycler() {
        with(binding.recyclerviewSrcLang) {
            layoutManager = LinearLayoutManager(this@SelectLanguageActivity)
            addItemDecoration(DividerItemDecoration(this@SelectLanguageActivity, RecyclerView.VERTICAL))
            this@SelectLanguageActivity.adapter = SelectLanguageRecyclerAdapter(
                itemClickListener = { language ->
                    viewModel.onLanguageItemClick(language)
                }
            )
            adapter = this@SelectLanguageActivity.adapter
        }
    }

    private fun setupToolbar() {
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = if (viewModel.isSource) {
                resources.getString(R.string.title_source_lang)
            } else {
                resources.getString(R.string.title_target_lang)
            }
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
    }
}