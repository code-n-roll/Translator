package com.romankaranchuk.translator.ui.translator

//import ru.yandex.speechkit.Language
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
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.romankaranchuk.translator.R
import com.romankaranchuk.translator.TranslatorApplication
import com.romankaranchuk.translator.common.Constants.*
import com.romankaranchuk.translator.data.database.model.DictDefinition
import com.romankaranchuk.translator.data.database.model.TranslatedItem
import com.romankaranchuk.translator.data.database.model.Translation
import com.romankaranchuk.translator.data.database.repository.TranslatorRepository
import com.romankaranchuk.translator.data.database.repository.TranslatorRepositoryImpl
import com.romankaranchuk.translator.data.database.storage.TextDataStorage
import com.romankaranchuk.translator.databinding.FragmentTranslatorBinding
import com.romankaranchuk.translator.di.util.Injectable
import com.romankaranchuk.translator.ui.fullscreen.FullscreenActivity
import com.romankaranchuk.translator.ui.translator.selectlang.SelectLanguageActivity
import com.romankaranchuk.translator.utils.UIUtils
import com.romankaranchuk.translator.utils.network.ContentResult
import org.xmlpull.v1.XmlPullParserException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject


class TranslatorFragment @Inject constructor() : Fragment(), TranslatorContract.View, Injectable {

    companion object {
        const val SRC_LANG_ACTIVITY_REQUEST_CODE = 1
        const val TRG_LANG_ACTIVITY_REQUEST_CODE = 2
        val TAG = TranslatorFragment::class.java.simpleName.toString()
    }

    var mButtonSwitchLang: ImageButton? = null
    var mButtonSrcLang: Button? = null
    var mButtonTrgLang: Button? = null

//    private var mView: View? = null
    private var mMainActivityContainer: FrameLayout? = null
    private var mTranslations: MutableList<Translation> = mutableListOf()
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
    private lateinit var mRepository: TranslatorRepositoryImpl

    @Inject lateinit var textDataStorage: TextDataStorage
    @Inject lateinit var translatorRepository: TranslatorRepository
    @Inject lateinit var mSettings: SharedPreferences

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<TranslatorViewModel> { viewModelFactory }

    private var _binding: FragmentTranslatorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTranslatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        findViewsOnActivity()
        findViewsOnActionBar()

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

//        val localDataSource = TranslatorLocalRepository.getInstance(requireContext())
//        mRepository = TranslatorRepositoryImpl.getInstance(localDataSource)
//
//        UIUtils.changeSoftInputModeWithOrientation(activity!!)
//        setupStartedUI()

        Timber.d("onViewCreated")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        if (savedInstanceState != null) {
//            restoreVisibility(savedInstanceState, binding.layoutTranslationResult?.connectionErrorContent?.root?, CONT_ERROR_VISIBILITY)
//            restoreVisibility(savedInstanceState, binding.layoutTranslationResult?.connectionSuccessfulContent?, CONT_SUCCESS_VISIBILITY)
//            restoreVisibility(savedInstanceState, mProgressDictionary, PROGRESS_BAR_VISIBILITY)
//        }
        Timber.d("onActivityCreated")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.apply {
            putString(CONT_ERROR_VISIBILITY, binding.layoutTranslationResult?.connectionErrorContent?.root?.visibility.toString())
            putString(CONT_SUCCESS_VISIBILITY, binding.layoutTranslationResult?.connectionSuccessfulContent?.root?.visibility.toString())
            putString(PROGRESS_BAR_VISIBILITY, binding.layoutTranslationResult?.fragmentTranslatorProgressbar?.visibility.toString())
        }
        Timber.d("onSaveInstanceState")
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
        return binding.layoutTranslationResult?.connectionSuccessfulContent?.textviewTranslateResult?.text.toString().isEmpty()
    }

    override fun getTextTranslatedResultView(): String {
        return binding.layoutTranslationResult?.connectionSuccessfulContent?.textviewTranslateResult?.text.toString()
    }

    override fun isEmptyCustomEditText(): Boolean {
        return binding.layoutTranslationInput?.edittext?.text.toString().isEmpty()
    }

    override fun isRecognizingSourceText(): Boolean {
        return isRecognizingSourceText
    }

    override fun isRecordAudioGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun setTextCustomEditText(text: String) {
        binding.layoutTranslationInput?.edittext?.setText(text)
    }

    override fun clearCustomEditText() {
        binding.layoutTranslationInput?.edittext?.text?.clear()
    }

    private fun setupStartedUI() {
        if (isRecordAudioGranted()) {
            binding.getAudioSpelling?.setImageResource(R.drawable.tool_dark512)
        } else {
            binding.getAudioSpelling?.setImageResource(R.drawable.tool_light512)
        }

        if (binding.layoutTranslationInput?.edittext?.text.toString().isEmpty()) {
            binding.getSourceVoice?.setImageResource(R.drawable.camera_dark512)
        } else {
            binding.getSourceVoice?.setImageResource(R.drawable.volume_up_indicator_dark512)
        }

        mSettings?.let {
            binding.layoutTranslationResult?.connectionSuccessfulContent?.textviewTranslateResult?.text = it.getString(TRANSL_RESULT, "")
            if (java.lang.Boolean.parseBoolean(it.getString(IS_FAVORITE, ""))) {
                binding.layoutTranslationResult?.connectionSuccessfulContent?.setFavorite?.setImageResource(R.drawable.bookmark_black_shape_gold512)
            } else {
                binding.layoutTranslationResult?.connectionSuccessfulContent?.setFavorite?.setImageResource(R.drawable.bookmark_black_shape_dark512)
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
//        binding.generalContainer.setOnTouchListener { view, event -> this.clickOnGeneralContainer() }
//        binding.layoutTranslationResult?.connectionErrorContent?.buttonConnectionErrorRetry?.setOnClickListener{ this.clickOnRetryButton() }
//        binding.layoutTranslationResult?.connectionSuccessfulContent?.fullscreenTranslatedWord?.setOnClickListener{ this.clickOnFullscreenButton() }
//        mClearEditText.setOnClickListener{ this.clickOnClearEditText() }
//        binding.getSourceVoice?.setOnClickListener{ this.clickOnRecognizePhotoOrVocalizeSourceText() }
//        binding.getAudioSpelling?.setOnClickListener{ this.clickOnRecognizeSourceText() }
//        mButtonGetTargetVoice.setOnClickListener{ this.clickOnVocalizeTargetText() }
//        mButtonSetFavorite.setOnClickListener{ clickOnSetFavoriteButton() }
//        binding.layoutTranslationResult?.connectionSuccessfulContent?.shareTranslatedWord?.setOnClickListener{ this.clickOnShareButton() }
    }

    private fun findViewsOnActivity() {
//        mNavigation = activity!!.findViewById(R.id.navigation)
//        mMainActivityContainer = activity!!.findViewById(R.id.main_activity_container)
    }

    private fun findViewsOnActionBar() {
        mButtonSwitchLang = binding.translatorToolbar?.findViewById(R.id.center_actionbar_button)
        mButtonSrcLang = binding.translatorToolbar?.findViewById(R.id.left_actionbar_button)
        mButtonTrgLang = binding.translatorToolbar?.findViewById(R.id.right_actionbar_button)
        val title = resources.getString(R.string.title_choose_lang)
        mSettings?.let {
            mButtonSrcLang!!.text = it.getString(SRC_LANG, title)
            mButtonTrgLang!!.text = it.getString(TRG_LANG, title)
        }
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
            binding.getAudioSpelling?.setImageResource(R.drawable.tool_dark512)
        } else {
            UIUtils.showToast(
                context,
                resources.getString(R.string.record_audio_not_granted)
            )
            binding.getAudioSpelling?.setImageResource(R.drawable.tool_light512)
        }
    }

    override fun hideKeyboard() {
        val `in` = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        `in`.hideSoftInputFromWindow(requireView().windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun showClear() {
        binding.layoutTranslationInput?.clearEdittext?.visibility = View.VISIBLE
    }

    override fun hideClear() {
        binding.layoutTranslationInput?.clearEdittext?.visibility = View.INVISIBLE
    }

    override fun showLoadingTargetVoice() {
        binding.layoutTranslationResult?.connectionSuccessfulContent?.getTargetVoiceProgress?.visibility = View.VISIBLE
    }

    override fun hideLoadingTargetVoice() {
        binding.layoutTranslationResult?.connectionSuccessfulContent?.getTargetVoiceProgress?.visibility = View.INVISIBLE
    }

    override fun showIconTargetVoice() {
        binding.layoutTranslationResult?.connectionSuccessfulContent?.getTargetVoice?.visibility = View.VISIBLE
    }

    override fun hideIconTargetVoice() {
        binding.layoutTranslationResult?.connectionSuccessfulContent?.getTargetVoice?.visibility = View.INVISIBLE
    }

    override fun showLoadingSourceVoice() {
        binding.layoutTranslationInput?.getSourceVoiceProgress?.visibility = View.VISIBLE
    }

    override fun hideLoadingSourceVoice() {
        binding.layoutTranslationInput?.getSourceVoiceProgress?.visibility = View.INVISIBLE
    }

    override fun showIconSourceVoice() {
        binding.getSourceVoice?.visibility = View.VISIBLE
    }

    override fun hideIconSourceVoice() {
        binding.getSourceVoice?.visibility = View.INVISIBLE
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
        if (!binding.layoutTranslationInput?.edittext?.text.toString().isEmpty()) {
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
        binding.includeContentMicroWaves?.circleFirst?.alpha = 1f

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

        mAnimatorSecond!!.setTarget(binding.includeContentMicroWaves?.circleSecond)
        mAnimatorSecondBack!!.setTarget(binding.includeContentMicroWaves?.circleSecond)
        mAnimatorThird!!.setTarget(binding.includeContentMicroWaves?.circleThird)
        mAnimatorThirdBack!!.setTarget(binding.includeContentMicroWaves?.circleThird)
        mAnimatorForth!!.setTarget(binding.includeContentMicroWaves?.circleForth)
        mAnimatorForthBack!!.setTarget(binding.includeContentMicroWaves?.circleForth)

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

        binding.includeContentMicroWaves?.circleFirst?.alpha = 0f
        binding.includeContentMicroWaves?.circleSecond?.alpha = 0f
        binding.includeContentMicroWaves?.circleThird?.alpha = 0f
        binding.includeContentMicroWaves?.circleForth?.alpha = 0f
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

    override fun createPredictedTranslatedItem(): TranslatedItem {
        val curEditTextContent = binding.layoutTranslationInput?.edittext?.text.toString().trim { it <= ' ' }
        val srcLangAPI = mSettings!!.getString(CUR_SELECTED_ITEM_SRC_LANG, "")
        val trgLangAPI = mSettings!!.getString(CUR_SELECTED_ITEM_TRG_LANG, "")

        return TranslatedItem(
            srcLangAPI, trgLangAPI, null, null,
            curEditTextContent, null, null, null
        )
    }

    override fun showLoadingDictionary() {
        binding.layoutTranslationResult?.fragmentTranslatorProgressbar?.visibility = View.VISIBLE
    }

    override fun hideLoadingDictionary() {
        binding.layoutTranslationResult?.fragmentTranslatorProgressbar?.visibility = View.INVISIBLE
    }

    override fun showRetry() {
        binding.layoutTranslationResult?.connectionErrorContent?.root?.visibility = View.VISIBLE
    }

    override fun hideRetry() {
        binding.layoutTranslationResult?.connectionErrorContent?.root?.visibility = View.INVISIBLE
    }

    override fun showSuccess() {
        binding.layoutTranslationResult?.connectionSuccessfulContent?.root?.visibility = View.VISIBLE
    }

    override fun hideSuccess() {
        binding.layoutTranslationResult?.connectionSuccessfulContent?.root?.visibility = View.INVISIBLE
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
                //                            !binding.layoutTranslationResult?.connectionSuccessfulContent?.textviewTranslateResult?.getText().toString().isEmpty()) {
                //                        mButtonSrcLang.setText(mButtonTrgLang.getText());
                //                        mButtonTrgLang.setText(result);
                //                        binding.layoutTranslationInput?.edittext?.setText(binding.layoutTranslationResult?.connectionSuccessfulContent?.textviewTranslateResult?.getText());

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
                if (!binding.layoutTranslationInput?.edittext?.text.toString().isEmpty()) {
                    showLoadingDictionary()
                    hideSuccess()

                    val sourceLang = mButtonSrcLang?.getText().toString().toLowerCase()
                    val targetLang = mButtonTrgLang?.getText().toString().toLowerCase()
                    val inputText = binding.layoutTranslationInput?.edittext?.getText().toString()
                    viewModel.translate(sourceLang, targetLang, inputText)
                    viewModel.loadDefinition(inputText, sourceLang, targetLang)
                }
            }
            TRG_LANG_ACTIVITY_REQUEST_CODE -> if (resultCode == AppCompatActivity.RESULT_OK) {
                val result = data!!.getStringExtra(RESULT)
                //                    if (mButtonTrgLang.getText().equals(result)) {
                //                        mButtonSrcLang.setText(mButtonTrgLang.getText());
                //                        binding.layoutTranslationInput?.edittext?.setText(binding.layoutTranslationResult?.connectionSuccessfulContent?.textviewTranslateResult?.getText());
                //                    }
                mButtonTrgLang!!.text = result
                if (!binding.layoutTranslationInput?.edittext?.text.toString().isEmpty()) {
                    showLoadingDictionary()
                    hideSuccess()

                    val sourceLang = mButtonSrcLang?.getText().toString().toLowerCase()
                    val targetLang = mButtonTrgLang?.getText().toString().toLowerCase()
                    val inputText = binding.layoutTranslationInput?.edittext?.getText().toString()
                    viewModel.translate(sourceLang, targetLang, inputText)
                    viewModel.loadDefinition(inputText, sourceLang, targetLang)
                }
            }
            else -> {
            }
        }
    }

    override fun setHintOnInput() {
        binding.layoutTranslationInput?.edittext?.hint = resources.getString(R.string.translate_hint)
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
            binding.layoutTranslationInput?.edittext?.setText(it.getString(EDITTEXT_DATA, ""))
        }
        if (!binding.layoutTranslationInput?.edittext?.text.toString().isEmpty()) {
            showClear()
        } else {
            hideClear()
        }
        binding.layoutTranslationInput?.edittext?.setRawInputType(InputType.TYPE_CLASS_TEXT)

        binding.layoutTranslationInput?.edittext?.setOnEditorActionListener { _, actionId, _ ->
            val curEditTextContent = binding.layoutTranslationInput?.edittext?.text.toString().trim { it <= ' ' }
            val maybeExistedItem = createPredictedTranslatedItem()

            val sourceLang = mButtonSrcLang?.getText().toString().toLowerCase()
            val targetLang = mButtonTrgLang?.getText().toString().toLowerCase()
            val inputText = binding.layoutTranslationInput?.edittext?.getText().toString()

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!curEditTextContent.isEmpty()
                    && viewModel.mHistoryTranslatedItems?.contains(maybeExistedItem)?.not() == true) {
                    viewModel.translate(sourceLang, targetLang, inputText)
                } else {
                    viewModel.getTranslatedItemFromCache(maybeExistedItem)
//                    binding.layoutTranslationResult?.connectionSuccessfulContent?.textviewTranslateResult?.text = translatedItems[id].trgMeaning
                }
                Timber.d("ACTION_DONE & customEditText is not empty")
            }
            false
        }

        binding.layoutTranslationInput?.edittext?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.layoutTranslationResult?.connectionErrorContent?.root?.visibility == View.VISIBLE)
                    hideRetry()
                if (binding.layoutTranslationInput?.edittext?.text?.isNotEmpty() == true && binding.layoutTranslationInput?.clearEdittext?.isShown == false) {
                    showClear()
                    binding.getSourceVoice?.setImageResource(R.drawable.volume_up_indicator_dark512)
                } else if (binding.layoutTranslationInput?.edittext?.text?.isEmpty() == true && binding.layoutTranslationInput?.clearEdittext?.isShown == true) {
                    hideClear()
                    hideSuccess()
                    clearContainerSuccess()
                    binding.getSourceVoice?.setImageResource(R.drawable.camera_dark512)

                    val inputText = binding.layoutTranslationInput?.edittext?.getText().toString()
                    val sourceLang = mButtonSrcLang?.getText().toString()
                    val targetLang = mButtonTrgLang?.getText().toString()
                    val outputText = binding.layoutTranslationResult?.connectionSuccessfulContent?.textviewTranslateResult?.getText().toString()
                    viewModel.saveToSharedPreferences(
                        sourceLang, targetLang, inputText, outputText
                    )
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding.layoutTranslationInput?.edittext?.setOnClickListener{ this.handleRecognizerOnEdittext() }
    }

    private fun setHintRecognizer() {
        binding.layoutTranslationInput?.edittext?.hint = resources.getString(R.string.recognizer_hint)
    }

    private fun showActiveBorderInput() {
        try {
            binding.layoutTranslationInput?.edittext?.background = ContextCompat.getDrawable(
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
            binding.layoutTranslationInput?.edittext?.background = ContextCompat.getDrawable(
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
            binding.layoutTranslationInput?.edittext?.background = ContextCompat.getDrawable(
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
        binding.layoutTranslationInput?.edittext?.isCursorVisible = false
    }

    private fun showCursorInput() {
        binding.layoutTranslationInput?.edittext?.isCursorVisible = true
    }

    private fun handleRecognizerOnEdittext() {
        viewModel.stopRecognizeText()
    }

    fun clearContainerSuccess() {
        binding.layoutTranslationResult?.connectionSuccessfulContent?.textviewTranslateResult?.text = ""
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
//                //                        if (!binding.layoutTranslationInput?.edittext?.getText().toString().isEmpty() &&
//                //                                mSaver != null &&
//                //                                mSaver.getCurTranslatedItem() != null &&
//                //                                !mSaver.getCurTranslatedItem()
//                //                                        .getSrcMeaning()
//                //                                        .equals(binding.layoutTranslationInput?.edittext?.getText().toString())) {
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
        binding.layoutTranslationResult?.connectionSuccessfulContent?.containerDictDefin?.layoutManager = LinearLayoutManager(requireContext())
        adapter = TranslatorRecyclerAdapter(
            mOnItemClickListener = { _, text -> this.clickOnSynonymItem(text) }
        )
        binding.layoutTranslationResult?.connectionSuccessfulContent?.containerDictDefin?.adapter = adapter
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
        val inputText = binding.layoutTranslationInput?.edittext?.getText().toString()

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
//                binding.layoutTranslationResult?.connectionSuccessfulContent?.textviewTranslateResult?.text = translatedItems[id].trgMeaning
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
        val inputText = binding.layoutTranslationInput?.edittext?.getText().toString()
        viewModel.translate(sourceLang, targetLang, inputText)
    }

    private fun clickOnFullscreenButton() {
        val intent = Intent(context, FullscreenActivity::class.java)
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

//            viewModel.vocalizeText(
//                text = binding.layoutTranslationInput?.edittext?.text.toString(),
//                language = Language.ENGLISH
//            )
        } else {
            UIUtils.showToast(context, requireContext().resources.getString(R.string.try_to_get_photo))
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
        UIUtils.showToast(context, requireContext().resources.getString(R.string.set_favorite_message))
    }


    private fun clickOnShareButton() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, requireContext().resources.getString(R.string.share_subject))
        intent.putExtra(Intent.EXTRA_TEXT, getTextTranslatedResultView())
        startActivity(
            Intent.createChooser(
                intent,
                requireContext().resources.getString(R.string.chooser_title)
            )
        )
    }

    private fun clickOnSynonymItem(text: String) {
        if (!text.isEmpty()) {
            showLoadingDictionary()
            hideSuccess()

            val sourceLang = mButtonSrcLang?.getText().toString().toLowerCase()
            val targetLang = mButtonTrgLang?.getText().toString().toLowerCase()
            val inputText = binding.layoutTranslationInput?.edittext?.getText().toString()
            viewModel.translate(sourceLang, targetLang, inputText)
        }
    }

    private fun clickOnVocalizeTargetText() {
        if (!isEmptyTranslatedResultView()) {
            showLoadingTargetVoice()
            hideIconTargetVoice()


//            viewModel.vocalizeText(
//                text = binding.layoutTranslationResult?.connectionSuccessfulContent?.textviewTranslateResult?.text.toString(),
//                language = Language.RUSSIAN
//            )
        } else {
            UIUtils.showToast(
                context,
                requireContext().resources.getString(R.string.try_vocalize_empty_result)
            )
        }
    }

    fun handleDictionaryResponse(dictDefinition: DictDefinition) {
        Timber.d(dictDefinition.toString())

        val translations: MutableList<Translation> = ArrayList()
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
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.viewState.collect {
//
//                }
//            }
//        }
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
                    binding.layoutTranslationResult?.connectionSuccessfulContent?.textviewTranslateResult?.text = it.content.text?.get(0)
                    showLoadingDictionary()
                    hideSuccess()
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