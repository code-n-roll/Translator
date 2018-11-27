package com.karanchuk.roman.testtranslate.presentation.ui.translator

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
import com.google.gson.Gson
import com.karanchuk.roman.testtranslate.R
import com.karanchuk.roman.testtranslate.common.Constants.*
import com.karanchuk.roman.testtranslate.common.extensions.bind
import com.karanchuk.roman.testtranslate.common.view.CustomEditText
import com.karanchuk.roman.testtranslate.common.view.EditTextLayout
import com.karanchuk.roman.testtranslate.data.database.TablePersistenceContract.TranslatedItemEntry
import com.karanchuk.roman.testtranslate.data.database.model.DictDefinition
import com.karanchuk.roman.testtranslate.data.database.model.TranslatedItem
import com.karanchuk.roman.testtranslate.data.database.model.Translation
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorLocalRepository
import com.karanchuk.roman.testtranslate.data.database.repository.TranslatorRepositoryImpl
import com.karanchuk.roman.testtranslate.presentation.TestTranslatorApp
import com.karanchuk.roman.testtranslate.presentation.ui.fullscreen.FullscreenActivity
import com.karanchuk.roman.testtranslate.presentation.ui.sourcelang.SourceLangActivity
import com.karanchuk.roman.testtranslate.presentation.ui.targetlang.TargetLangActivity
import com.karanchuk.roman.testtranslate.utils.UIUtils
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.util.*

/**
 * Created by roman on 8.4.17.
 */

class TranslatorFragment : Fragment(), TranslatorContract.View {

    companion object {
        const val SRC_LANG_ACTIVITY_REQUEST_CODE = 1
        const val TRG_LANG_ACTIVITY_REQUEST_CODE = 2
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
    val mCustomEditText: CustomEditText by bind(R.id.edittext)
    val mTranslatedResult: TextView by bind(R.id.textview_translate_result)
    private val mContainerEditText: EditTextLayout by bind(R.id.container_edittext)
    private val mContainerSuccess: RelativeLayout by bind(R.id.connection_successful_content)
    private val mContainerError: RelativeLayout by bind(R.id.connection_error_content)

    private val mCircleFirst: ImageButton by bind(R.id.circle_first)
    private val mCircleSecond: ImageButton by bind(R.id.circle_first)
    private val mCircleThird: ImageButton by bind(R.id.circle_first)
    private val mCircleForth: ImageButton by bind(R.id.circle_first)

    private var mButtonSwitchLang: ImageButton? = null
    private var mView: View? = null
    private var mActionBar: ActionBar? = null
    private var mNavigation: BottomNavigationView? = null
    var mButtonSrcLang: Button? = null
    var mButtonTrgLang: Button? = null
    private var mMainActivityContainer: FrameLayout? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var mHistoryTranslatedItems: List<TranslatedItem>? = null
    private var mTranslations: ArrayList<Translation>? = null
    private var mRepository: TranslatorRepositoryImpl? = null
    private var mSettings: SharedPreferences? = null
    private var mBottomPadding: Int = 0

    private var mAnimatorSet: AnimatorSet? = null
    private var mAnimatorSecond: Animator? = null
    private var mAnimatorSecondBack: Animator? = null
    private var mAnimatorThird: Animator? = null
    private var mAnimatorThirdBack: Animator? = null
    private var mAnimatorForth: Animator? = null
    private var mAnimatorForthBack: Animator? = null

    private lateinit var mPresenter: TranslatorContract.Presenter
    private val mGestureDetector: GestureDetector? = null
    private var isRecognizingSourceText: Boolean = false

    override fun setTextButtonSrcLang(text: String) {
        mButtonSrcLang!!.text = text
    }

    override fun setTextButtonTrgLang(text: String) {
        mButtonTrgLang!!.text = text
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_translator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mSettings = activity!!.getSharedPreferences(PREFS_NAME, 0)
        mView = view

        setPresenter(TranslatorPresenter(this, context))
        mPresenter.attachView(context)

        initActionBar()
        findViewsOnActivity()
        findViewsOnActionBar()

        hideLoadingDictionary()
        hideLoadingTargetVoice()
        hideLoadingSourceVoice()
        hideRetry()

        val localDataSource = TranslatorLocalRepository.getInstance(context!!)
        mRepository = TranslatorRepositoryImpl.getInstance(localDataSource)
        mHistoryTranslatedItems = mRepository!!.getTranslatedItems(TranslatedItemEntry.TABLE_NAME_HISTORY)

        mTranslatedResult.text = mSettings!!.getString(TRANSL_RESULT, "")
        if (java.lang.Boolean.parseBoolean(mSettings!!.getString(IS_FAVORITE, ""))) {
            mButtonSetFavorite.setImageResource(R.drawable.bookmark_black_shape_gold512)
        } else {
            mButtonSetFavorite.setImageResource(R.drawable.bookmark_black_shape_dark512)
        }
        if (mSettings!!.getString(TRANSL_CONTENT, "")!!.isEmpty()) {
            hideSuccess()
        }

        if (isRecordAudioGranted) {
            mButtonGetAudioSpelling.setImageResource(R.drawable.tool_dark512)
        } else {
            mButtonGetAudioSpelling.setImageResource(R.drawable.tool_light512)
        }

        initTranslateRecyclerView()
        UIUtils.changeSoftInputModeWithOrientation(activity!!)
        initCustomEditText()
        initEventListenerKeyboardVisibility()
        initListeners()

        if (mCustomEditText.text.toString().isEmpty()) {
            mButtonGetPhotoOrSourceVoice.setImageResource(R.drawable.camera_dark512)
        } else {
            mButtonGetPhotoOrSourceVoice.setImageResource(R.drawable.volume_up_indicator_dark512)
        }

        //        mGestureDetector = new GestureDetector(getContext(), mContainerEditText);
        //        mCustomEditText.setOnTouchListener(mViewOnTouchListener);
    }

    override fun isEmptyTranslatedResultView(): Boolean {
        return mTranslatedResult.text.toString().isEmpty()
    }

    override fun getTextTranslatedResultView(): String {
        return mTranslatedResult.text.toString()
    }

    override fun setTextCustomEditText(text: String) {
        mCustomEditText.setText(text)
    }

    override fun isEmptyCustomEditText(): Boolean {
        return mCustomEditText.text.toString().isEmpty()
    }

    override fun clearCustomEditText() {
        mCustomEditText.text.clear()
    }

    override fun isRecognizingSourceText(): Boolean {
        return isRecognizingSourceText
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
        mGeneralContainer.setOnTouchListener { view, event -> this.clickOnGeneralContainer() }
        mButtonSrcLang!!.setOnClickListener { this.clickOnSrcLangButton() }
        mButtonSwitchLang!!.setOnClickListener { this.clickOnSwitchLangButton() }
        mButtonTrgLang!!.setOnClickListener{ this.clickOnTrgLangButton() }
        mButtonRetry.setOnClickListener{ this.clickOnRetryButton() }
        mButtonFullscreen.setOnClickListener{ this.clickOnFullscreenButton() }
        mClearEditText.setOnClickListener{ this.clickOnClearEditText() }
        mButtonGetPhotoOrSourceVoice.setOnClickListener{ this.clickOnRecognizePhotoOrVocalizeSourceText() }
        mButtonGetAudioSpelling.setOnClickListener{ this.clickOnRecognizeSourceText() }
        mButtonGetTargetVoice.setOnClickListener{ this.clickOnVocalizeTargetText() }
        mButtonSetFavorite.setOnClickListener{ clickOnSetFavoriteButton() }
        mButtonShare.setOnClickListener{ this.clickOnShareButton() }
    }

    private fun findViewsOnActivity() {
        mNavigation = activity!!.findViewById(R.id.navigation)
        mMainActivityContainer = activity!!.findViewById(R.id.main_activity_container)
    }

    private fun findViewsOnActionBar() {
        val mActionBarView = mActionBar!!.customView
        mButtonSwitchLang = mActionBarView.findViewById(R.id.center_actionbar_button)
        mButtonSrcLang = mActionBarView.findViewById(R.id.left_actionbar_button)
        mButtonTrgLang = mActionBarView.findViewById(R.id.right_actionbar_button)
        val title = resources.getString(R.string.title_choose_lang)
        mButtonSrcLang!!.text = mSettings!!.getString(SRC_LANG, title)
        mButtonTrgLang!!.text = mSettings!!.getString(TRG_LANG, title)
    }

    override fun isRecordAudioGranted(): Boolean {
        return ContextCompat.checkSelfPermission(context!!,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    override fun requestRecordAudioPermissions() {
        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO),
                RECOGNIZING_REQUEST_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode != RECOGNIZING_REQUEST_PERMISSION_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (grantResults.size == 1 && grantResults[0] == PERMISSION_GRANTED) {
            //            mPresenter.recognizeSourceText();
            mButtonGetAudioSpelling.setImageResource(R.drawable.tool_dark512)
        } else {
            UIUtils.showToast(context,
                    resources.getString(R.string.record_audio_not_granted))
            mButtonGetAudioSpelling.setImageResource(R.drawable.tool_light512)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (savedInstanceState != null) {
            restoreVisibility(savedInstanceState, mContainerError, CONT_ERROR_VISIBILITY)
            restoreVisibility(savedInstanceState, mContainerSuccess, CONT_SUCCESS_VISIBILITY)
            restoreVisibility(savedInstanceState, mProgressDictionary, PROGRESS_BAR_VISIBILITY)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter.detachView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.apply {
            putString(CONT_ERROR_VISIBILITY, mContainerError.visibility.toString())
            putString(CONT_SUCCESS_VISIBILITY, mContainerSuccess.visibility.toString())
            putString(PROGRESS_BAR_VISIBILITY, mProgressDictionary.visibility.toString())
        }
    }

    override fun hideKeyboard() {
        val `in` = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        `in`.hideSoftInputFromWindow(mView!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
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

        mAnimatorSecond = AnimatorInflater.loadAnimator(context,
                R.animator.micro_waves_second)
        mAnimatorSecondBack = AnimatorInflater.loadAnimator(context,
                R.animator.micro_waves_second_back)

        mAnimatorThird = AnimatorInflater.loadAnimator(context,
                R.animator.micro_waves_third)
        mAnimatorThirdBack = AnimatorInflater.loadAnimator(context,
                R.animator.micro_waves_third_back)

        mAnimatorForth = AnimatorInflater.loadAnimator(context,
                R.animator.micro_waves_forth)
        mAnimatorForthBack = AnimatorInflater.loadAnimator(context,
                R.animator.micro_waves_forth_back)

        mAnimatorSecond!!.setTarget(mCircleSecond)
        mAnimatorSecondBack!!.setTarget(mCircleSecond)
        mAnimatorThird!!.setTarget(mCircleThird)
        mAnimatorThirdBack!!.setTarget(mCircleThird)
        mAnimatorForth!!.setTarget(mCircleForth)
        mAnimatorForthBack!!.setTarget(mCircleForth)

        mAnimatorSet = AnimatorSet()

        mAnimatorSet?.let {
            it.play(mAnimatorSecond).before(mAnimatorSecondBack)
            it.play(mAnimatorSecondBack).after(resources.getInteger(R.integer.dur_second_to_back).toLong())

            it.play(mAnimatorThird).after(mAnimatorSecond)
            it.play(mAnimatorThird).before(mAnimatorThirdBack)
            it.play(mAnimatorThirdBack).after(resources.getInteger(R.integer.dur_third_to_back).toLong())

            it.play(mAnimatorForth).after(mAnimatorThird)
            it.play(mAnimatorForth).before(mAnimatorForthBack)
            it.play(mAnimatorForthBack).after(resources.getInteger(R.integer.dur_forth_to_back).toLong())

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
        val `in` = TestTranslatorApp.instance.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        `in`.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    override fun getTextButtonSrcLang(): String {
        return mButtonSrcLang!!.text.toString()
    }

    override fun getTextButtonTrgLang(): String {
        return mButtonTrgLang!!.text.toString()
    }

    fun clickOnButtonSwitchLang() {
        mButtonSwitchLang!!.performClick()
    }

    override fun createPredictedTranslatedItem(): TranslatedItem {
        val curEditTextContent = mCustomEditText.text.toString().trim { it <= ' ' }
        val srcLangAPI = mSettings!!.getString(CUR_SELECTED_ITEM_SRC_LANG, "")
        val trgLangAPI = mSettings!!.getString(CUR_SELECTED_ITEM_TRG_LANG, "")

        return TranslatedItem(srcLangAPI, trgLangAPI, null, null,
                curEditTextContent, null, null, null)
    }

    override fun getTranslatedItemFromCache(maybeExistedItem: TranslatedItem) {
        val id = mHistoryTranslatedItems!!.indexOf(maybeExistedItem)
        if (id != -1) {
            val dictDefinitionJSON = mHistoryTranslatedItems!![id].dictDefinitionJSON
            val existedItem = Gson().fromJson(dictDefinitionJSON, DictDefinition::class.java)
            mPresenter.handleDictionaryResponse(existedItem)
            mTranslatedResult.text = mHistoryTranslatedItems!![id].trgMeaning
            Log.d("myLogs", mHistoryTranslatedItems!![id].trgMeaning)
        }
    }

    override fun setPresenter(presenter: TranslatorContract.Presenter) {
        mPresenter = presenter
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
        mBottomPadding = UIUtils.hideBottomNavViewGetBottomPadding(activity!!,
                mMainActivityContainer, mNavigation)
        showActiveBorderInput()
    }

    override fun hideActiveInput() {
        hideCursorInput()
        UIUtils.showBottomNavViewSetBottomPadding(activity!!, mMainActivityContainer,
                mNavigation, mBottomPadding)
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
                    mPresenter.requestTranslatorAPI()
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
                    mPresenter.requestTranslatorAPI()
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
        mCustomEditText.setText(mSettings!!.getString(EDITTEXT_DATA, ""))
        if (!mCustomEditText.text.toString().isEmpty()) {
            showClear()
        } else {
            hideClear()
        }
        mCustomEditText.setRawInputType(InputType.TYPE_CLASS_TEXT)

        mCustomEditText.setOnEditorActionListener { _, actionId, _ ->
            val curEditTextContent = mCustomEditText.text.toString().trim { it <= ' ' }
            val maybeExistedItem = createPredictedTranslatedItem()

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!curEditTextContent.isEmpty() && !mHistoryTranslatedItems!!.contains(maybeExistedItem)) {
                    if (mPresenter.requestTranslatorAPI()) {
                        showLoadingDictionary()
                        hideSuccess()
                    }
                } else {
                    getTranslatedItemFromCache(maybeExistedItem)
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
                if (mCustomEditText.text.isNotEmpty() && !mClearEditText.isShown) {
                    showClear()
                    mButtonGetPhotoOrSourceVoice.setImageResource(R.drawable.volume_up_indicator_dark512)
                } else if (mCustomEditText.text.isEmpty() && mClearEditText.isShown) {
                    hideClear()
                    hideSuccess()
                    clearContainerSuccess()
                    mButtonGetPhotoOrSourceVoice.setImageResource(R.drawable.camera_dark512)
                    mPresenter.saveToSharedPreferences()
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
            mContainerEditText.background = Drawable.createFromXml(resources,
                    resources.getLayout(R.layout.edittext_border_active))
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun hideActiveBorderInput() {
        try {
            mContainerEditText.background = Drawable.createFromXml(resources,
                    resources.getLayout(R.layout.edittext_border))
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun showActiveRecognizerInput() {
        try {
            mContainerEditText.background = Drawable.createFromXml(resources,
                    resources.getLayout(R.layout.edittext_recognizer_active))
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
        mPresenter.resetRecognizer()
    }

    fun clearContainerSuccess() {
        mTranslatedResult.text = ""
        mTranslations!!.clear()
        mPresenter.clearContainerSuccess()
    }

    private fun initActionBar() {
        mActionBar = (activity as AppCompatActivity).supportActionBar
        mActionBar?.let {
            it.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            it.setDisplayShowCustomEnabled(true)
            it.setCustomView(R.layout.actionbar_translator)
            it.setShowHideAnimationEnabled(false)
            it.elevation = 0f
            it.title = ""
            it.show()
        }
    }

    private fun initEventListenerKeyboardVisibility() {
        KeyboardVisibilityEvent.setEventListener(
                activity!!
        ) { isOpen ->
            if (isOpen && isAdded) {
                showActiveInput()
            } else if (!isOpen && isAdded) {
                hideActiveInput()
                //                        if (!mCustomEditText.getText().toString().isEmpty() &&
                //                                mSaver != null &&
                //                                mSaver.getCurTranslatedItem() != null &&
                //                                !mSaver.getCurTranslatedItem()
                //                                        .getSrcMeaning()
                //                                        .equals(mCustomEditText.getText().toString())) {
                //                            showLoadingDictionary();
                //                            mPresenter.requestTranslatorAPI();
                //                        }
            }
        }
    }


    private fun restoreVisibility(savedInstanceState: Bundle,
                                  view: View?,
                                  key: String) {
        when (Integer.parseInt(savedInstanceState.getString(key))) {
            View.GONE -> view!!.visibility = View.GONE
            View.INVISIBLE -> view!!.visibility = View.INVISIBLE
            View.VISIBLE -> view!!.visibility = View.VISIBLE
            else -> {
            }
        }
    }

    private fun initTranslateRecyclerView() {
        mLayoutManager = LinearLayoutManager(activity)
        mTranslateRecyclerView.layoutManager = mLayoutManager

        val dictDefString = mSettings!!.getString(TRANSL_CONTENT, "")


        mTranslations = ArrayList()

        var dictDefinition: DictDefinition? = null
        if (!dictDefString!!.isEmpty()) {
            dictDefinition = Gson().fromJson(dictDefString, DictDefinition::class.java)
            if (dictDefinition != null) {
                for (POS in dictDefinition.partsOfSpeech) {
                    mTranslations!!.addAll(POS.translations)
                }
            }
        }
        if (dictDefinition != null) {
            mTranslateRecyclerView.adapter = TranslatorRecyclerAdapter(
                    mTranslations,
                    dictDefinition.partsOfSpeech
            ) { _, text -> this.clickOnSynonymItem(text) }
        } else {
            mTranslateRecyclerView.adapter = TranslatorRecyclerAdapter(
                    mTranslations, null
            ) { _, text -> this.clickOnSynonymItem(text) }
        }
    }

    private fun clickOnGeneralContainer(): Boolean {
        hideKeyboard()
        //        UIUtils.showToast(getContext(), "clicked outside keyboard, keyboard hided");
        return true
    }

    private fun clickOnSrcLangButton() {
        val intent = Intent(context, SourceLangActivity::class.java)
        startActivityForResult(intent, SRC_LANG_ACTIVITY_REQUEST_CODE)
    }

    private fun clickOnSwitchLangButton() {
        val oldSrcLang = textButtonSrcLang
        val oldTrgLang = textButtonTrgLang
        textButtonSrcLang = oldTrgLang
        textButtonTrgLang = oldSrcLang

        val srcLangAPI = mSettings!!.getString(CUR_SELECTED_ITEM_SRC_LANG, "")
        val trgLangAPI = mSettings!!.getString(CUR_SELECTED_ITEM_TRG_LANG, "")

        mSettings!!.edit().apply {
            putString(CUR_SELECTED_ITEM_SRC_LANG, trgLangAPI)
            putString(CUR_SELECTED_ITEM_TRG_LANG, srcLangAPI)
            apply()
        }

        setTextCustomEditText(textTranslatedResultView)
        if (!isEmptyCustomEditText && mPresenter.requestTranslatorAPI()) {
            showLoadingDictionary()
            hideSuccess()
        } else {
            getTranslatedItemFromCache(createPredictedTranslatedItem())
        }
    }

    private fun clickOnTrgLangButton() {
        val intent = Intent(context, TargetLangActivity::class.java)
        startActivityForResult(intent, TRG_LANG_ACTIVITY_REQUEST_CODE)
    }

    private fun clickOnRetryButton() {
        showLoadingDictionary()
        hideSuccess()
        mPresenter.requestTranslatorAPI()
    }

    private fun clickOnFullscreenButton() {
        val intent = Intent(context, FullscreenActivity::class.java)
        intent.putExtra(TRANSLATED_RESULT, textTranslatedResultView)
        startActivity(intent)
    }

    private fun clickOnClearEditText() {
        clearCustomEditText()
    }

    private fun clickOnRecognizePhotoOrVocalizeSourceText() {
        if (!isEmptyCustomEditText) {
            showLoadingSourceVoice()
            hideIconSourceVoice()
            mPresenter.vocalizeSourceText()
        } else {
            UIUtils.showToast(context, context!!.resources.getString(R.string.try_to_get_photo))
        }
    }

    private fun clickOnRecognizeSourceText() {
        if (!isRecordAudioGranted) {
            requestRecordAudioPermissions()
        }
        if (!isRecognizingSourceText() && isRecordAudioGranted) {
            setRecognizingSourceText(true)
            showAnimationMicroWaves()
            mPresenter.recognizeSourceText()
        } else {
            setRecognizingSourceText(false)
            mPresenter.resetRecognizer()
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
        UIUtils.showToast(context, context!!.resources.getString(R.string.set_favorite_message))
    }


    private fun clickOnShareButton() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, context!!.resources.getString(R.string.share_subject))
        intent.putExtra(Intent.EXTRA_TEXT, textTranslatedResultView)
        startActivity(Intent.createChooser(intent, context!!.resources.getString(R.string.chooser_title)))
    }

    private fun clickOnSynonymItem(text: String) {
        if (!text.isEmpty()) {
            showLoadingDictionary()
            hideSuccess()
            mPresenter.requestTranslatorAPI()
        }
    }

    private fun clickOnVocalizeTargetText() {
        if (!isEmptyTranslatedResultView) {
            showLoadingTargetVoice()
            hideIconTargetVoice()
            mPresenter.vocalizeTargetText()
        } else {
            UIUtils.showToast(context, context!!.resources.getString(R.string.try_vocalize_empty_result))
        }
    }
}