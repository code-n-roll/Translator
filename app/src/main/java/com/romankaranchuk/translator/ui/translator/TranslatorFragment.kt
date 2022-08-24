package com.romankaranchuk.translator.ui.translator

import android.Manifest
import android.Manifest.permission
import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.romankaranchuk.translator.R
import com.romankaranchuk.translator.TranslatorApplication
import com.romankaranchuk.translator.common.Constants.*
import com.romankaranchuk.translator.ui.translator.selectlang.SelectLanguageActivity
import com.romankaranchuk.translator.utils.extensions.bind
import com.romankaranchuk.translator.utils.network.ContentResult
import org.xmlpull.v1.XmlPullParserException
import ru.yandex.speechkit.Language
import java.io.IOException
import java.util.ArrayList
import javax.inject.Inject

/**
 * Created by roman on 8.4.17.
 */

class TranslatorFragment @Inject constructor() : Fragment(), TranslatorContract.View {

    companion object {
        const val SRC_LANG_ACTIVITY_REQUEST_CODE = 1
        const val TRG_LANG_ACTIVITY_REQUEST_CODE = 2
        val TAG = TranslatorFragment::class.java.simpleName.toString()
    }

    private val mButtonGetPhotoOrSourceVoice: ImageButton by bind(R.id.get_source_voice)
    private val mButtonGetAudioSpelling: ImageButton by bind(R.id.get_audio_spelling)
    private val mButtonGetTargetVoice: ImageButton by bind(R.id.get_target_voice)
    private val mButtonSetFavorite: ImageButton by bind(R.id.set_favorite)
    private val mButtonShare: ImageButton by bind(R.id.share_translated_word)
    private val mButtonFullscreen: ImageButton by bind(R.id.fullscreen_translated_word)
    private val mClearEditText: ImageButton by bind(R.id.clear_edittext)
    private val mButtonRetry: Button by bind(R.id.button_connection_error_retry)
    private val mGeneralContainer: LinearLayout by bind(R.id.general_container)
    private val mProgressDictionary: ProgressBar by bind(R.id.fragment_translator_progressbar)
    private val mProgressTargetVoice: ProgressBar by bind(R.id.get_target_voice_progress)
    private val mProgressSourceVoice: ProgressBar by bind(R.id.get_source_voice_progress)
    val mTranslateRecyclerView: RecyclerView by bind(R.id.container_dict_defin)
    val mCustomEditText: com.romankaranchuk.translator.ui.view.CustomEditText by bind(R.id.edittext)
    val mTranslatedResult: TextView by bind(R.id.textview_translate_result)
    private val mContainerEditText: RelativeLayout by bind(R.id.container_edittext)
    private val mContainerSuccess: RelativeLayout by bind(R.id.connection_successful_content)
    private val mContainerError: LinearLayout by bind(R.id.connection_error_content)

    private val mCircleFirst: AppCompatImageView by bind(R.id.circle_first)
    private val mCircleSecond: AppCompatImageView by bind(R.id.circle_first)
    private val mCircleThird: AppCompatImageView by bind(R.id.circle_first)
    private val mCircleForth: AppCompatImageView by bind(R.id.circle_first)
    private val translatorToolbar: Toolbar by bind(R.id.translator_toolbar)

    var mButtonSwitchLang: ImageButton? = null
    var mButtonSrcLang: Button? = null
    var mButtonTrgLang: Button? = null

//    private var mView: View? = null
    private var mMainActivityContainer: FrameLayout? = null
    private var mTranslations: MutableList<com.romankaranchuk.translator.data.database.model.Translation> = mutableListOf()
    private var mBottomPadding: Int = 0

    private var mAnimatorSet: AnimatorSet? = null
    private var mAnimatorSecond: Animator? = null
    private var mAnimatorSecondBack: Animator? = null
    private var mAnimatorThird: Animator? = null
    private var mAnimatorThirdBack: Animator? = null
    private var mAnimatorForth: Animator? = null
    private var mAnimatorForthBack: Animator? = null

    private var isRecognizingSourceText: Boolean = false
    private val mGestureDetector: GestureDetector? = null
    private var adapter: TranslatorRecyclerAdapter? = null

//    private lateinit var mNavigation: AHBottomNavigation
    private lateinit var mRepository: com.romankaranchuk.translator.data.database.repository.TranslatorRepositoryImpl
    private lateinit var customEditText: com.romankaranchuk.translator.ui.view.CustomEditText

    @Inject lateinit var textDataStorage: com.romankaranchuk.translator.data.database.storage.TextDataStorage
    @Inject lateinit var translatorRepository: com.romankaranchuk.translator.data.database.repository.TranslatorRepository
    @Inject lateinit var mSettings: SharedPreferences

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<TranslatorViewModel> { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_translator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        findViewsOnActivity()
        findViewsOnActionBar()
        findViewsOnContainerEditText()

        setupRecycler()
        initCustomEditText()
        initEventListenerKeyboardVisibility()
        initListeners()

        hideLoadingDictionary()
        hideLoadingTargetVoice()
        hideLoadingSourceVoice()
        hideRetry()

        bindViewModel()
        viewModel.loadTranslations()

//        val localDataSource = TranslatorLocalRepository.getInstance(context!!)
//        mRepository = TranslatorRepositoryImpl.getInstance(localDataSource)
//
//        UIUtils.changeSoftInputModeWithOrientation(activity!!)
//        setupStartedUI()

        Log.d(TAG, "onViewCreated")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        if (savedInstanceState != null) {
//            restoreVisibility(savedInstanceState, mContainerError, CONT_ERROR_VISIBILITY)
//            restoreVisibility(savedInstanceState, mContainerSuccess, CONT_SUCCESS_VISIBILITY)
//            restoreVisibility(savedInstanceState, mProgressDictionary, PROGRESS_BAR_VISIBILITY)
//        }
        Log.d(TAG, "onActivityCreated")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.apply {
            putString(CONT_ERROR_VISIBILITY, mContainerError.visibility.toString())
            putString(CONT_SUCCESS_VISIBILITY, mContainerSuccess.visibility.toString())
            putString(PROGRESS_BAR_VISIBILITY, mProgressDictionary.visibility.toString())
        }
        Log.d(TAG, "onSaveInstanceState")
    }

    override fun setTextButtonSrcLang(text: String) {
        mButtonSrcLang?.text = text
    }

    override fun getTextButtonSrcLang(): String {
        return mButtonSrcLang!!.text.toString()
    }

    override fun setTextButtonTrgLang(text: String) {
        mButtonTrgLang?.text = text
    }

    override fun getTextButtonTrgLang(): String {
        return mButtonTrgLang!!.text.toString()
    }

    override fun isEmptyTranslatedResultView(): Boolean {
        return mTranslatedResult.text.toString().isEmpty()
    }

    override fun getTextTranslatedResultView(): String {
        return mTranslatedResult.text.toString()
    }

    override fun isEmptyCustomEditText(): Boolean {
        return mCustomEditText.text.toString().isEmpty()
    }

    override fun isRecognizingSourceText(): Boolean {
        return isRecognizingSourceText
    }

    override fun isRecordAudioGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun setTextCustomEditText(text: String) {
        mCustomEditText.setText(text)
    }

    override fun clearCustomEditText() {
        mCustomEditText.text?.clear()
    }

    private fun setupStartedUI() {
        if (isRecordAudioGranted()) {
            mButtonGetAudioSpelling.setImageResource(R.drawable.tool_dark512)
        } else {
            mButtonGetAudioSpelling.setImageResource(R.drawable.tool_light512)
        }

        if (mCustomEditText.text.toString().isEmpty()) {
            mButtonGetPhotoOrSourceVoice.setImageResource(R.drawable.camera_dark512)
        } else {
            mButtonGetPhotoOrSourceVoice.setImageResource(R.drawable.volume_up_indicator_dark512)
        }

        mSettings?.let {
            mTranslatedResult.text = it.getString(TRANSL_RESULT, "")
            if (java.lang.Boolean.parseBoolean(it.getString(IS_FAVORITE, ""))) {
                mButtonSetFavorite.setImageResource(R.drawable.bookmark_black_shape_gold512)
            } else {
                mButtonSetFavorite.setImageResource(R.drawable.bookmark_black_shape_dark512)
            }
            if (it.getString(TRANSL_CONTENT, "")!!.isEmpty()) {
                hideSuccess()
            }
        }
    }

    private fun setRecognizingSourceText(recognizingSourceText: Boolean) {
        isRecognizingSourceText = recognizingSourceText
    }

    //    private View.OnTouchListener mViewOnTouchListener = new View.OnTouchListener() {
    //        @Override
    //        public boolean onTouch(View view, MotionEvent motionEvent) {
    //            mGestureDetector.onTouchEvent(motionEvent);
    //            return false;
    //        }
    //    };

    private fun initListeners() {
        mButtonSrcLang!!.setOnClickListener { this.onSelectLanguageButtonClick(true) }
//        mButtonSwitchLang!!.setOnClickListener { this.clickOnSwitchLangButton() }
        mButtonTrgLang!!.setOnClickListener{ this.onSelectLanguageButtonClick(false) }
//        mGeneralContainer.setOnTouchListener { view, event -> this.clickOnGeneralContainer() }
//        mButtonRetry.setOnClickListener{ this.clickOnRetryButton() }
//        mButtonFullscreen.setOnClickListener{ this.clickOnFullscreenButton() }
//        mClearEditText.setOnClickListener{ this.clickOnClearEditText() }
//        mButtonGetPhotoOrSourceVoice.setOnClickListener{ this.clickOnRecognizePhotoOrVocalizeSourceText() }
//        mButtonGetAudioSpelling.setOnClickListener{ this.clickOnRecognizeSourceText() }
//        mButtonGetTargetVoice.setOnClickListener{ this.clickOnVocalizeTargetText() }
//        mButtonSetFavorite.setOnClickListener{ clickOnSetFavoriteButton() }
//        mButtonShare.setOnClickListener{ this.clickOnShareButton() }
    }

    private fun findViewsOnActivity() {
//        mNavigation = activity!!.findViewById(R.id.navigation)
//        mMainActivityContainer = activity!!.findViewById(R.id.main_activity_container)
    }

    private fun findViewsOnActionBar() {
        mButtonSwitchLang = translatorToolbar.findViewById(R.id.center_actionbar_button)
        mButtonSrcLang = translatorToolbar.findViewById(R.id.left_actionbar_button)
        mButtonTrgLang = translatorToolbar.findViewById(R.id.right_actionbar_button)
        val title = resources.getString(R.string.title_choose_lang)
        mSettings?.let {
            mButtonSrcLang!!.text = it.getString(SRC_LANG, title)
            mButtonTrgLang!!.text = it.getString(TRG_LANG, title)
        }
    }

    private fun findViewsOnContainerEditText() {
        customEditText = mContainerEditText.findViewById(R.id.edittext)
    }

    override fun requestRecordAudioPermissions() {
        requestPermissions(
            arrayOf(Manifest.permission.RECORD_AUDIO),
            RECOGNIZING_REQUEST_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != RECOGNIZING_REQUEST_PERMISSION_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (grantResults.size == 1 && grantResults[0] == PERMISSION_GRANTED) {
            //            mPresenter.recognizeSourceText();
            mButtonGetAudioSpelling.setImageResource(R.drawable.tool_dark512)
        } else {
            com.romankaranchuk.translator.utils.UIUtils.showToast(
                context,
                resources.getString(R.string.record_audio_not_granted)
            )
            mButtonGetAudioSpelling.setImageResource(R.drawable.tool_light512)
        }
    }

    override fun hideKeyboard() {
        val `in` = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        `in`.hideSoftInputFromWindow(requireView().windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun showClear() {
        mClearEditText.visibility = View.VISIBLE
    }

    override fun hideClear() {
        mClearEditText.visibility = View.INVISIBLE
    }

    override fun showLoadingTargetVoice() {
        mProgressTargetVoice.visibility = View.VISIBLE
    }

    override fun hideLoadingTargetVoice() {
        mProgressTargetVoice.visibility = View.INVISIBLE
    }

    override fun showIconTargetVoice() {
        mButtonGetTargetVoice.visibility = View.VISIBLE
    }

    override fun hideIconTargetVoice() {
        mButtonGetTargetVoice.visibility = View.INVISIBLE
    }

    override fun showLoadingSourceVoice() {
        mProgressSourceVoice.visibility = View.VISIBLE
    }

    override fun hideLoadingSourceVoice() {
        mProgressSourceVoice.visibility = View.INVISIBLE
    }

    override fun showIconSourceVoice() {
        mButtonGetPhotoOrSourceVoice.visibility = View.VISIBLE
    }

    override fun hideIconSourceVoice() {
        mButtonGetPhotoOrSourceVoice.visibility = View.INVISIBLE
    }

    override fun activateVoiceRecognizer() {
        hideClear()
        hideKeyboard()
        hideIconSourceVoice()
        showActiveRecognizerInput()
        hideCursorInput()
        setHintRecognizer()
    }

    override fun deactivateVoiceRecognizer() {
        if (!mCustomEditText.text.toString().isEmpty()) {
            showClear()
        } else {
            hideClear()
        }
        showIconSourceVoice()
        showActiveBorderInput()
        showCursorInput()
        setHintOnInput()
        isRecognizingSourceText = false
    }

    override fun showAnimationMicroWaves() {
        mCircleFirst.alpha = 1f

        mAnimatorSecond = AnimatorInflater.loadAnimator(context, R.animator.micro_waves_second)
        mAnimatorSecondBack = AnimatorInflater.loadAnimator(
            context,
            R.animator.micro_waves_second_back
        )

        mAnimatorThird = AnimatorInflater.loadAnimator(context, R.animator.micro_waves_third)
        mAnimatorThirdBack = AnimatorInflater.loadAnimator(
            context,
            R.animator.micro_waves_third_back
        )

        mAnimatorForth = AnimatorInflater.loadAnimator(context, R.animator.micro_waves_forth)
        mAnimatorForthBack = AnimatorInflater.loadAnimator(
            context,
            R.animator.micro_waves_forth_back
        )

        mAnimatorSecond!!.setTarget(mCircleSecond)
        mAnimatorSecondBack!!.setTarget(mCircleSecond)
        mAnimatorThird!!.setTarget(mCircleThird)
        mAnimatorThirdBack!!.setTarget(mCircleThird)
        mAnimatorForth!!.setTarget(mCircleForth)
        mAnimatorForthBack!!.setTarget(mCircleForth)

        mAnimatorSet = AnimatorSet()

        mAnimatorSet?.let {
            it.play(mAnimatorSecond).before(mAnimatorSecondBack)
            it.play(mAnimatorSecondBack).after(
                resources.getInteger(R.integer.dur_second_to_back).toLong()
            )

            it.play(mAnimatorThird).after(mAnimatorSecond)
            it.play(mAnimatorThird).before(mAnimatorThirdBack)
            it.play(mAnimatorThirdBack).after(
                resources.getInteger(R.integer.dur_third_to_back).toLong()
            )

            it.play(mAnimatorForth).after(mAnimatorThird)
            it.play(mAnimatorForth).before(mAnimatorForthBack)
            it.play(mAnimatorForthBack).after(
                resources.getInteger(R.integer.dur_forth_to_back).toLong()
            )

            it.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {

                }

                override fun onAnimationEnd(animator: Animator) {
                    animator.start()
                }

                override fun onAnimationCancel(animator: Animator) {

                }

                override fun onAnimationRepeat(animator: Animator) {

                }
            })

            it.interpolator = LinearInterpolator()
            it.start()
        }
    }

    override fun stopAnimationMicroWaves() {
        mAnimatorSet?.removeAllListeners()

        mAnimatorSet?.end()
        mAnimatorSecond?.end()
        mAnimatorSecondBack?.end()
        mAnimatorThird?.end()
        mAnimatorThirdBack?.end()
        mAnimatorForth?.end()
        mAnimatorForthBack?.end()

        mCircleFirst.alpha = 0f
        mCircleSecond.alpha = 0f
        mCircleThird.alpha = 0f
        mCircleForth.alpha = 0f
    }

    override fun showKeyboard() {
        val `in` = TranslatorApplication.instance.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        `in`.toggleSoftInput(
            InputMethodManager.SHOW_IMPLICIT,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

    fun clickOnButtonSwitchLang() {
        mButtonSwitchLang!!.performClick()
    }

    override fun createPredictedTranslatedItem(): com.romankaranchuk.translator.data.database.model.TranslatedItem {
        val curEditTextContent = mCustomEditText.text.toString().trim { it <= ' ' }
        val srcLangAPI = mSettings!!.getString(CUR_SELECTED_ITEM_SRC_LANG, "")
        val trgLangAPI = mSettings!!.getString(CUR_SELECTED_ITEM_TRG_LANG, "")

        return com.romankaranchuk.translator.data.database.model.TranslatedItem(
            srcLangAPI, trgLangAPI, null, null,
            curEditTextContent, null, null, null
        )
    }

    override fun showLoadingDictionary() {
        mProgressDictionary.visibility = View.VISIBLE
    }

    override fun hideLoadingDictionary() {
        mProgressDictionary.visibility = View.INVISIBLE
    }

    override fun showRetry() {
        mContainerError.visibility = View.VISIBLE
    }

    override fun hideRetry() {
        mContainerError.visibility = View.INVISIBLE
    }

    override fun showSuccess() {
        mContainerSuccess.visibility = View.VISIBLE
    }

    override fun hideSuccess() {
        mContainerSuccess.visibility = View.INVISIBLE
    }

    override fun showActiveInput() {
        showCursorInput()
//        mBottomPadding = UIUtils.hideBottomNavViewGetBottomPadding(
//                activity!!,
//                mMainActivityContainer,
//                mNavigation)
        showActiveBorderInput()
    }

    override fun hideActiveInput() {
        hideCursorInput()
//        UIUtils.showBottomNavViewSetBottomPadding(
//                activity!!,
//                mMainActivityContainer,
//                mNavigation,
//                mBottomPadding)
        if (isRecognizingSourceText) {
            showActiveRecognizerInput()
        } else {
            hideActiveBorderInput()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SRC_LANG_ACTIVITY_REQUEST_CODE -> if (resultCode == AppCompatActivity.RESULT_OK) {
                val result = data!!.getStringExtra(RESULT)

                //                    if (!mButtonSrcLang.getText().equals(result) &&
                //                            !mTranslatedResult.getText().toString().isEmpty()) {
                //                        mButtonSrcLang.setText(mButtonTrgLang.getText());
                //                        mButtonTrgLang.setText(result);
                //                        mCustomEditText.setText(mTranslatedResult.getText());

                //                        JsonObject languagesMap = JsonUtils.getJsonObjectFromAssetsFile(getContext(), "langs.json");
                //
                //                        String srcLangAPI = languagesMap.get(mButtonSrcLang.getText().toString().toLowerCase()).getAsString();
                //                        String trgLangAPI = languagesMap.get(mButtonTrgLang.getText().toString().toLowerCase()).getAsString();
                //
                //                        SharedPreferences.Editor editor = mSettings.edit();
                //                        editor.putString(CUR_SELECTED_ITEM_SRC_LANG, srcLangAPI);
                //                        editor.putString(CUR_SELECTED_ITEM_TRG_LANG, trgLangAPI);
                //                        editor.apply();

                //                    } else if (mButtonSrcLang.getText().equals(result)){
                //                        mButtonSwitchLang.performClick();
                //                    } else {
                mButtonSrcLang!!.text = result
                //                    }
                if (!mCustomEditText.text.toString().isEmpty()) {
                    showLoadingDictionary()
                    hideSuccess()

                    val sourceLang = mButtonSrcLang?.getText().toString().toLowerCase()
                    val targetLang = mButtonTrgLang?.getText().toString().toLowerCase()
                    val inputText = mCustomEditText.getText().toString()
                    viewModel.translate(
                        sourceLang, targetLang, inputText
                    )
                }
            }
            TRG_LANG_ACTIVITY_REQUEST_CODE -> if (resultCode == AppCompatActivity.RESULT_OK) {
                val result = data!!.getStringExtra(RESULT)
                //                    if (mButtonTrgLang.getText().equals(result)) {
                //                        mButtonSrcLang.setText(mButtonTrgLang.getText());
                //                        mCustomEditText.setText(mTranslatedResult.getText());
                //                    }
                mButtonTrgLang!!.text = result
                if (!mCustomEditText.text.toString().isEmpty()) {
                    showLoadingDictionary()
                    hideSuccess()

                    val sourceLang = mButtonSrcLang?.getText().toString().toLowerCase()
                    val targetLang = mButtonTrgLang?.getText().toString().toLowerCase()
                    val inputText = mCustomEditText.getText().toString()
                    viewModel.translate(
                        sourceLang, targetLang, inputText
                    )
                }
            }
            else -> {
            }
        }
    }

    override fun setHintOnInput() {
        mCustomEditText.hint = resources.getString(R.string.translate_hint)
    }

    override fun showError() {
        hideLoadingTargetVoice()
        hideLoadingSourceVoice()
        showIconTargetVoice()
        showIconSourceVoice()
        stopAnimationMicroWaves()
        setHintOnInput()
        showActiveInput()
        setRecognizingSourceText(false)
    }

    private fun initCustomEditText() {
        mSettings?.let {
            mCustomEditText.setText(it.getString(EDITTEXT_DATA, ""))
        }
        if (!mCustomEditText.text.toString().isEmpty()) {
            showClear()
        } else {
            hideClear()
        }
        mCustomEditText.setRawInputType(InputType.TYPE_CLASS_TEXT)

        mCustomEditText.setOnEditorActionListener { _, actionId, _ ->
            val curEditTextContent = mCustomEditText.text.toString().trim { it <= ' ' }
            val maybeExistedItem = createPredictedTranslatedItem()

            val sourceLang = mButtonSrcLang?.getText().toString().toLowerCase()
            val targetLang = mButtonTrgLang?.getText().toString().toLowerCase()
            val inputText = mCustomEditText.getText().toString()

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!curEditTextContent.isEmpty()
                    && viewModel.mHistoryTranslatedItems?.contains(maybeExistedItem)?.not() == true) {
                    viewModel.translate(sourceLang, targetLang, inputText)
                } else {
                    viewModel.getTranslatedItemFromCache(maybeExistedItem)
//                    mTranslatedResult.text = translatedItems[id].trgMeaning
                }
                Log.d("keyboard state", "ACTION_DONE & customEditText is not empty")
            }
            false
        }

        mCustomEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (mContainerError.visibility == View.VISIBLE)
                    hideRetry()
                if (mCustomEditText.text?.isNotEmpty() == true && !mClearEditText.isShown) {
                    showClear()
                    mButtonGetPhotoOrSourceVoice.setImageResource(R.drawable.volume_up_indicator_dark512)
                } else if (mCustomEditText.text?.isEmpty() == true && mClearEditText.isShown) {
                    hideClear()
                    hideSuccess()
                    clearContainerSuccess()
                    mButtonGetPhotoOrSourceVoice.setImageResource(R.drawable.camera_dark512)

                    val inputText = mCustomEditText.getText().toString()
                    val sourceLang = mButtonSrcLang?.getText().toString()
                    val targetLang = mButtonTrgLang?.getText().toString()
                    val outputText = mTranslatedResult.getText().toString()
                    viewModel.saveToSharedPreferences(
                        sourceLang, targetLang, inputText, outputText
                    )
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        mCustomEditText.setOnClickListener{ this.handleRecognizerOnEdittext() }
    }

    private fun setHintRecognizer() {
        mCustomEditText.hint = resources.getString(R.string.recognizer_hint)
    }

    private fun showActiveBorderInput() {
        try {
            customEditText.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.selector_edittext_border_active
            )
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun hideActiveBorderInput() {
        try {
            customEditText.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.selector_edittext_border
            )
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun showActiveRecognizerInput() {
        try {
            customEditText.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.selector_edittext_recognizer_active
            )
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun hideCursorInput() {
        mCustomEditText.isCursorVisible = false
    }

    private fun showCursorInput() {
        mCustomEditText.isCursorVisible = true
    }

    private fun handleRecognizerOnEdittext() {
        viewModel.stopRecognizeText()
    }

    fun clearContainerSuccess() {
        mTranslatedResult.text = ""
        mTranslations.clear()
        adapter?.notifyDataSetChanged()
        viewModel.clearContainerSuccess()
    }

    private fun initEventListenerKeyboardVisibility() {
//        KeyboardVisibilityEvent.setEventListener(
//                activity!!
//        ) { isOpen ->
//            if (isOpen && isAdded) {
//                showActiveInput()
//            } else if (!isOpen && isAdded) {
//                hideActiveInput()
//                //                        if (!mCustomEditText.getText().toString().isEmpty() &&
//                //                                mSaver != null &&
//                //                                mSaver.getCurTranslatedItem() != null &&
//                //                                !mSaver.getCurTranslatedItem()
//                //                                        .getSrcMeaning()
//                //                                        .equals(mCustomEditText.getText().toString())) {
//                //                            showLoadingDictionary();
//                //                            viewModel.translate();
//                //                        }
//            }
//        }
    }

    private fun restoreVisibility(
        savedInstanceState: Bundle,
        view: View,
        key: String
    ) {
        when (savedInstanceState.getString(key)?.toInt()) {
            View.GONE -> view.visibility = View.GONE
            View.INVISIBLE -> view.visibility = View.INVISIBLE
            View.VISIBLE -> view.visibility = View.VISIBLE
            else -> {
            }
        }
    }

    private fun setupRecycler() {
        mTranslateRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = TranslatorRecyclerAdapter(
            mOnItemClickListener = { _, text -> this.clickOnSynonymItem(text) }
        )
        mTranslateRecyclerView.adapter = adapter
    }

    private fun clickOnGeneralContainer(): Boolean {
        hideKeyboard()
        //        UIUtils.showToast(getContext(), "clicked outside keyboard, keyboard hided");
        return true
    }

    private fun clickOnSwitchLangButton() {
        val oldSrcLang = getTextButtonSrcLang()
        val oldTrgLang = getTextButtonTrgLang()
        setTextButtonSrcLang(oldTrgLang)
        setTextButtonTrgLang(oldSrcLang)

        val srcLangAPI = mSettings!!.getString(CUR_SELECTED_ITEM_SRC_LANG, "") ?: ""
        val trgLangAPI = mSettings!!.getString(CUR_SELECTED_ITEM_TRG_LANG, "") ?: ""
        val inputText = mCustomEditText.getText().toString()

        mSettings?.let {
            it.edit().apply {
                putString(CUR_SELECTED_ITEM_SRC_LANG, trgLangAPI)
                putString(CUR_SELECTED_ITEM_TRG_LANG, srcLangAPI)
                apply()
            }

            setTextCustomEditText(getTextTranslatedResultView())
            if (!isEmptyCustomEditText()) {
                viewModel.translate(srcLangAPI, trgLangAPI, inputText)
            } else {
                viewModel.getTranslatedItemFromCache(createPredictedTranslatedItem())
//                mTranslatedResult.text = translatedItems[id].trgMeaning
            }
        }
    }

    private fun onSelectLanguageButtonClick(isSource: Boolean) {
        val intent = Intent(context, SelectLanguageActivity::class.java).apply {
            putExtra("TYPE", if (isSource) "SOURCE" else "TARGET")
        }
        startActivityForResult(
            intent,
            if (isSource) SRC_LANG_ACTIVITY_REQUEST_CODE
            else TRG_LANG_ACTIVITY_REQUEST_CODE
        )
    }

    private fun clickOnRetryButton() {
        showLoadingDictionary()
        hideSuccess()

        val sourceLang = mButtonSrcLang?.getText().toString().toLowerCase()
        val targetLang = mButtonTrgLang?.getText().toString().toLowerCase()
        val inputText = mCustomEditText.getText().toString()
        viewModel.translate(sourceLang, targetLang, inputText)
    }

    private fun clickOnFullscreenButton() {
        val intent = Intent(context, com.romankaranchuk.translator.ui.fullscreen.FullscreenActivity::class.java)
        intent.putExtra(TRANSLATED_RESULT, getTextTranslatedResultView())
        startActivity(intent)
    }

    private fun clickOnClearEditText() {
        clearCustomEditText()
    }

    private fun clickOnRecognizePhotoOrVocalizeSourceText() {
        if (!isEmptyCustomEditText()) {
            showLoadingSourceVoice()
            hideIconSourceVoice()

            viewModel.vocalizeText(
                text = mCustomEditText.text.toString(),
                language = Language.ENGLISH
            )
        } else {
            com.romankaranchuk.translator.utils.UIUtils.showToast(context, context!!.resources.getString(R.string.try_to_get_photo))
        }
    }

    private fun clickOnRecognizeSourceText() {
        if (!isRecordAudioGranted()) {
            requestRecordAudioPermissions()
        }
        if (!isRecognizingSourceText() && isRecordAudioGranted()) {
            setRecognizingSourceText(true)
            showAnimationMicroWaves()

            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission.RECORD_AUDIO
                ) != PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(permission.RECORD_AUDIO),
                    RECOGNIZING_REQUEST_PERMISSION_CODE
                )
            } else {
                viewModel.startRecognizeText()
                activateVoiceRecognizer()
            }
        } else {
            setRecognizingSourceText(false)
            viewModel.stopRecognizeText()
        }
    }

    private fun clickOnSetFavoriteButton() {
        //         final TranslatedItem item = mSaver.getCurTranslatedItem();
        //         if (!item.isFavorite()) {
        //             item.isFavoriteUp(true);
        //             mSaver.setCurTranslatedItem(item);
        //             mRepository.saveTranslatedItem(TranslatedItemEntry.TABLE_NAME_FAVORITES, item);
        //             view.setImageResource(R.drawable.bookmark_black_shape_gold512);
        //         } else {
        //             item.isFavoriteUp(false);
        //             mSaver.setCurTranslatedItem(item);
        //             mRepository.deleteTranslatedItem(TranslatedItemEntry.TABLE_NAME_FAVORITES, item);
        //             view.setImageResource(R.drawable.bookmark_black_shape_dark512);
        //         }
        //        mRepository.updateTranslatedItem(TranslatedItemEntry.TABLE_NAME_HISTORY, item);
        //        UIUtils.showToast(mContext, "set favorite was clicked");
        com.romankaranchuk.translator.utils.UIUtils.showToast(context, context!!.resources.getString(R.string.set_favorite_message))
    }


    private fun clickOnShareButton() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, context!!.resources.getString(R.string.share_subject))
        intent.putExtra(Intent.EXTRA_TEXT, getTextTranslatedResultView())
        startActivity(
            Intent.createChooser(
                intent,
                context!!.resources.getString(R.string.chooser_title)
            )
        )
    }

    private fun clickOnSynonymItem(text: String) {
        if (!text.isEmpty()) {
            showLoadingDictionary()
            hideSuccess()

            val sourceLang = mButtonSrcLang?.getText().toString().toLowerCase()
            val targetLang = mButtonTrgLang?.getText().toString().toLowerCase()
            val inputText = mCustomEditText.getText().toString()
            viewModel.translate(sourceLang, targetLang, inputText)
        }
    }

    private fun clickOnVocalizeTargetText() {
        if (!isEmptyTranslatedResultView()) {
            showLoadingTargetVoice()
            hideIconTargetVoice()


            viewModel.vocalizeText(
                text = mTranslatedResult.text.toString(),
                language = Language.RUSSIAN
            )
        } else {
            com.romankaranchuk.translator.utils.UIUtils.showToast(
                context,
                context!!.resources.getString(R.string.try_vocalize_empty_result)
            )
        }
    }

    fun handleDictionaryResponse(dictDefinition: com.romankaranchuk.translator.data.database.model.DictDefinition) {
        Log.d("myLogs", dictDefinition.toString())

        val translations: MutableList<com.romankaranchuk.translator.data.database.model.Translation> = ArrayList()
        var index: Int
        for (POS in dictDefinition.partsOfSpeech) {
            translations.addAll(POS.translations)
            index = 1
            for (translation in POS.translations) {
                translation.number = index++.toString()
            }
        }
        adapter?.updateAll(translations, dictDefinition.partsOfSpeech)

        hideLoadingDictionary()
        hideRetry()
        showSuccess()
    }

    private fun handleDictionaryError() {
//        error.printStackTrace()
        hideLoadingDictionary()
        hideRetry()
        hideSuccess()
    }

    private fun bindViewModel() {
        viewModel.translationsLiveData.observe(viewLifecycleOwner) {
            adapter?.updateAll(it.first, it.second)
        }
        viewModel.translateLiveData.observe(viewLifecycleOwner) {
            when(it) {
                is ContentResult.Error -> {
//                    it.error.printStackTrace()
                    showRetry()
                    hideSuccess()
                    hideLoadingDictionary()
                }
                is ContentResult.Loading -> {

                }
                is ContentResult.Success -> {
                    mTranslatedResult.text = it.content.text?.get(0)
                    showLoadingDictionary()
                    hideSuccess()

                    val sourceLang = mButtonSrcLang?.getText().toString().toLowerCase()
                    val targetLang = mButtonTrgLang?.getText().toString().toLowerCase()
                    val inputText = mCustomEditText.getText().toString()
                    viewModel.loadDefinition(
                        inputText = inputText,
                        sourceLang = sourceLang,
                        targetLang = targetLang
                    )
                }
            }
        }
        viewModel.definitionLiveData.observe(viewLifecycleOwner) {
            when(it) {
                is ContentResult.Error -> {
                    handleDictionaryError()
                }
                is ContentResult.Loading -> {

                }
                is ContentResult.Success -> {
                    handleDictionaryResponse(it.content)
                }
            }
        }
    }
}