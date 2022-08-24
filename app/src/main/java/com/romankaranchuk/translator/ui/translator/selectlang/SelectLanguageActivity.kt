package com.romankaranchuk.translator.ui.translator.selectlang

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romankaranchuk.translator.R
import dagger.android.AndroidInjection
import javax.inject.Inject

/**
 * Created by roman on 10.4.17.
 */
class SelectLanguageActivity : AppCompatActivity(R.layout.activity_select_language) {

    private var sourceLanguageRecycler: RecyclerView? = null
    private var adapter: SelectLanguageRecyclerAdapter? = null
    private var isSource = false

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<SelectLanguageViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)

        isSource = intent.getStringExtra("TYPE") == "SOURCE"
        setupToolbar()
        setupRecycler()

        bindViewModel()
        viewModel.loadLanguages(isSource)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(menuItem)
    }

    private fun bindViewModel() {
        viewModel.languagesLiveData.observe(this) {
            adapter?.updateAll(it.first, it.second)
        }
    }

    private fun setupRecycler() {
        sourceLanguageRecycler = findViewById(R.id.recyclerview_src_lang)
        sourceLanguageRecycler?.layoutManager = LinearLayoutManager(this)
        sourceLanguageRecycler?.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))
        adapter = SelectLanguageRecyclerAdapter(
            itemClickListener = { language ->
                viewModel.saveSelectedLanguage(isSource, language)

                com.romankaranchuk.translator.utils.UIUtils.showToast(applicationContext, "selected $language")
                setResult(RESULT_OK, Intent().apply {
                    putExtra("result", language.name)
                })
                finish()
            }
        )
        sourceLanguageRecycler?.adapter = adapter
    }

    private fun setupToolbar() {
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.title = if (isSource) {
                resources.getString(R.string.title_source_lang)
            } else {
                resources.getString(R.string.title_target_lang)
            }
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun inject() {
        AndroidInjection.inject(this)
    }
}