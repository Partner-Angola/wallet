package com.joeware.android.gpulumera.nft.ui.camera

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.hardware.Camera
import android.media.AudioManager
import android.media.SoundPool
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseFragment
import com.joeware.android.gpulumera.camera.CamRatio
import com.joeware.android.gpulumera.camera.FlashMode
import com.joeware.android.gpulumera.common.C
import com.joeware.android.gpulumera.common.CandyDisplayHelper
import com.joeware.android.gpulumera.databinding.FragmentNftCameraBinding
import com.joeware.android.gpulumera.manager.SaveImageManager.OnPictureSavedListener
import com.joeware.android.gpulumera.nft.model.GalleryItem
import com.joeware.android.gpulumera.nft.model.GalleryMode
import com.joeware.android.gpulumera.nft.model.NFTCameraSkinVO
import com.joeware.android.gpulumera.nft.ui.dialog.NftCameraBetaAlertDialog
import com.joeware.android.gpulumera.nft.ui.gallery.NftGalleryActivity
import com.joeware.android.gpulumera.nft.ui.market.NftCameraMarketDialog
import com.joeware.android.gpulumera.nft.ui.mint.NftMintingDialog
import com.joeware.android.gpulumera.nft.ui.dialog.NftCameraQuestDialog
import com.joeware.android.gpulumera.ui.CandyCameraManager
import com.joeware.android.gpulumera.util.PrefUtil
import com.joeware.android.gpulumera.util.RotateTransformation
import com.jpbrothers.android.engine.base.EngineBaseGlobal
import com.jpbrothers.android.engine.shaders.GLShader
import com.jpbrothers.android.engine.shaders.GLShaderGroup
import com.jpbrothers.android.engine.view.CameraManager
import com.jpbrothers.android.engine.view.CameraManager.CaptureCallback
import com.jpbrothers.android.engine.view.GLTextureAll
import com.jpbrothers.base.animation.base.JPAni
import com.jpbrothers.base.animation.base.Techniques
import com.jpbrothers.base.util.Util
import com.jpbrothers.base.util.WeekRefHandler
import com.jpbrothers.base.util.log.JPLog
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.java.standalone.KoinJavaComponent.inject
import java.util.*
import kotlin.math.abs
import kotlin.math.min


class NftCameraFragment : BaseFragment() {

    private lateinit var binding: FragmentNftCameraBinding
    private val viewModel: NftCameraViewModel by sharedViewModel()
    private val prefUtil: PrefUtil by inject(PrefUtil::class.java)

    private lateinit var cameraManager: CameraManager
    private lateinit var gpuImage: GLTextureAll
    private var isStopPreview = true
    /**
     * 셔터 사운드
     */
    private var mSoundPool: SoundPool? = null
    private var mPolarSound = 0

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentNftCameraBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        return binding.root
    }

    override fun setObserveData() {
        viewModel.takePhoto.observe(this, Observer {
            actionShot()
        })
        viewModel.clickFlim.observe(this, Observer{
            // randomSkin()
            startActivity(Intent(requireContext(), NftGalleryActivity::class.java))
        })
        viewModel.clickGiftBox.observe(this, Observer{
            NftCameraQuestDialog.showDialog(childFragmentManager)
        })
        viewModel.switchCameraId.observe(this, Observer { cameraId ->
            cameraManager.changeCameraRatio(cameraId, null)
            gpuImage.invalidate()
        })
        viewModel.mintNFT.observe(this, Observer {
            if(imgPath.isNotEmpty()) {
                NftMintingDialog.showDialog(childFragmentManager, arrayListOf(GalleryItem(-1, imgPath, "", "", 0L, "", GalleryMode.PICTURE, 0L, 0, 0, 0, false)))
            }
        })
        viewModel.clickBack.observe(this, Observer { onBackPreview()})
        viewModel.clickFlash.observe(this, Observer {
            changeFlash(prefUtil.nftCameraFlash)?.subscribe(object : SingleObserver<FlashMode?> {
                override fun onSubscribe(d: Disposable) {
                    JPLog.d("changeFlash onSubscribe")
                }

                override fun onSuccess(flashMode: FlashMode) {
                    JPLog.d("changeFlash onSuccess : $flashMode")
                    prefUtil.nftCameraFlash = flashMode
                    setFlash(flashMode)
                }

                override fun onError(e: Throwable) {
                    JPLog.e("changeFlash onError : $e")
                }
            })
        })
        viewModel.back.observe(this, Observer { onBackPreview() })
    }

    override fun init() {
        binding.btnMakeNft.setOnClickListener {
            if(imgPath.isNotEmpty()) {
                onBackPreview()
                NftMintingDialog.showDialog(childFragmentManager, arrayListOf(GalleryItem(-1, imgPath, "", "", 0L, "", GalleryMode.PICTURE, 0L, 0, 0, 0, false)))
            }
        }

        initBaseSetting()
        setShutterWaveView()
        initCamera()
        initCameraSkin()
        changeCameraSkin()
        // 세팅완료 후 작업들

        // 카메라 오버레이 없애준다.
        playAniCameraOverlay(false)
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Do custom work here
                    if(binding.layoutIvResult.visibility == View.VISIBLE){
                        viewModel.backToCamera()
                    }else {
                        // if you want onBackPressed() to be called as normal afterwards
                        if (isEnabled) {
                            isEnabled = false
                            requireActivity().onBackPressed()
                        }
                    }
                }
            }
        )
        if(prefUtil.isShowBetaAlert) NftCameraBetaAlertDialog.showDialog(parentFragmentManager)
    }

    /**
     * 시작 기본 세팅.
     */
    private fun initBaseSetting() {
        mSoundPool = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
        mPolarSound = mSoundPool!!.load(context, R.raw.polaroid, 1)
        loadNFTCameraSkinDateFromJson()
        binding.lyCaemraOverlay.visibility = View.VISIBLE
        binding.layoutIvResult.visibility = View.INVISIBLE

        binding.ivResult.setOnClickListener { viewModel.backToCamera() }
        binding.btnRetake.setOnClickListener { viewModel.backToCamera() }
        binding.lyTop.setOnClickListener { NftCameraMarketDialog.showDialog(childFragmentManager) }
    }

    private fun setShutterWaveView() {
        binding.waveView.setAnimDuration(3000)
        binding.waveView.progressValue = 100
        binding.waveView.waveShiftRatio = 0.5f
        binding.tvShotPower.text = "100%"
    }

    override fun onResume() {
        super.onResume()
        resumeCamera()
    }

    override fun onPause() {
        super.onPause()
        pauseCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraManager.release()
        gpuImage.release()
    }

    private fun actionShot() {
        if(viewModel.isIngCapture)
            return
        viewModel.isIngCapture = true;

        mSoundPool?.let { it.play(mPolarSound, 1f, 1f, 0, 0, 1f) }
        var process = binding.waveView.progressValue - 10
        if (process <= 0) process = 100
        binding.waveView.progressValue = process
        binding.tvShotPower.text = "$process%"

        cameraManager.capture(object : CaptureCallback {
            override fun onImageCaptureMuteOnShot(origin: Bitmap?, bitmap: Bitmap?): Boolean {
                return false
            }

            override fun onImageCaptureColl(origin: Bitmap?, bitmap: Bitmap?): Boolean {
                return false
            }
        }) { data, camera ->
            data?.let {
                processInstaFilterApply(it)
            }
        }
    }

    /**
     * 촬영 후 뒤로 갈경우 무조건 호출.
     */
    private fun onBackPreview() {
        binding.ivResult.setImageBitmap(null)
        binding.lyCaemraOverlay.visibility = View.GONE
        binding.layoutIvResult.visibility = View.GONE
        binding.lyCaemraOverlay.clearAnimation()
        playAniCameraSkin(false)
        viewModel.isIngCapture = false
        cameraManager.restartPreview()


        JPAni.with(Techniques.SlideInUp).duration(500).withListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator?) {
                binding.lyBottom.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator?) {
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        }).playOn(binding.lyBottom)

        JPAni.with(Techniques.SlideOutDown).duration(500).withListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                binding.lyBottomNft.visibility = View.INVISIBLE
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        }).playOn(binding.lyBottomNft)
    }

    /***************************************************************************************************************
     * 카메라 세팅 관련 시작.
     ***************************************************************************************************************/
    private fun initCamera() {
        cameraManager = CandyCameraManager(requireContext(), prefUtil.nftCameraId).apply {
            setPreview(GLTextureAll(requireContext()))
            setHandler(WeekRefHandler())
            isDisablePreview = false
            setCheckCameraOpenOverlap(false)
            setCameraOpenCompleteCallback {
                adjustCameraParam()
                if (cameraManager.cameraHelper.isOpened) {
                    cameraManager.startPreview()
                    gpuImage.shader = GLShaderGroup(GLShader())
                    /**
                     * Camera Flash 모드 가져오기
                     */
                    try {
                        val supportedFlashModes = cameraManager.cameraHelper.supportedFlashModes
                        if (supportedFlashModes != null && !(supportedFlashModes.size == 1 && supportedFlashModes[0] == "off")) {   // 플래시 지원
                            if (supportedFlashModes.contains(prefUtil.nftCameraFlash.toString())) {
                                cameraManager.cameraHelper.flashMode = prefUtil.nftCameraFlash.cameraMode
                            }
                            viewModel.setFlashAvailable(true)
                            setFlashPara(prefUtil.nftCameraFlash)
                            setFlash(prefUtil.nftCameraFlash)
                        } else {    // 플래시 미지원
                            viewModel.setFlashAvailable(false)
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                } else {
                    isStopPreview = true
                    cameraManager.onStop()
                }
            }
        }

        addAdjustCameraView()
        viewModel.initWide()
    }

    private fun addAdjustCameraView(){
        binding.lyCamera.post {
            gpuImage = cameraManager.preview.apply {
                id = R.id.surfaceView
                layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT).apply {
                    leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                    topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                    bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                    width = binding.lyCamera.width
                    height = binding.lyCamera.width
                }
                finalWidth = binding.lyCamera.width
                finalHeight = binding.lyCamera.width
            }
            val frame = ImageView(requireContext()).apply {
                background = ContextCompat.getDrawable(requireContext(), R.drawable.nft_camera_frame)
                layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
            }
            binding.lyCamera.addView(gpuImage)
            binding.lyCamera.addView(frame)
        }

    }

    private fun resumeCamera() {
        JPLog.e("david NFTFragment call resumeCamera ${cameraManager.cameraHelper.isOpened}")
        if (isStopPreview) {
            try {
                if (!cameraManager.cameraHelper.isOpened) {
                    cameraManager.openCamera(cameraManager.cameraHelper.cameraId)
                }
                gpuImage.resetPreview()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            isStopPreview = false
        }
    }

    private fun pauseCamera() {
        isStopPreview = true
        cameraManager.onStop()
        cameraManager.setStopFlag(true)
    }

    private fun adjustCameraParam() {
        val camera : Camera = cameraManager.cameraHelper.camera
        val cameraId : Int = cameraManager.cameraHelper.cameraId
        val parameters : Camera.Parameters = camera.parameters

        /**
         * Camera Focus Mode (포커스 모드 설정)
         */
        val supportedFocusModes = cameraManager.cameraHelper.supportedFocusModes
        try {
            if (supportedFocusModes.isNotEmpty() && cameraId == 0) {
                when {
                    supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) -> {
                        parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                    }
                    supportedFocusModes.contains( Camera.Parameters.FOCUS_MODE_AUTO) -> {
                        parameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
                    }
                    supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_INFINITY) -> {
                        parameters.focusMode = Camera.Parameters.FOCUS_MODE_INFINITY
                    }
                }
                camera.parameters = parameters
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        /**
         * Camera PreviewSize (카메라 프리뷰 사이즈)
         */
        val previewSizes: List<Camera.Size> = cameraManager.cameraHelper.supportedPreviewSizes
        val pictureSizes: List<Camera.Size> = cameraManager.cameraHelper.supportedPictureSizes

        val previewSize = viewModel.getOptimalPreviewSize(previewSizes, C.DIS_SIZE.x, C.DIS_SIZE.y, CamRatio.PIC_1X1)
        val picSize = activity?.let { viewModel.getOptimalPicSize(it, pictureSizes, 1.0, min(EngineBaseGlobal.MAX_PIC_SIZE, EngineBaseGlobal.MAX_TEX_SIZE)) }

        previewSize?.let { parameters.setPreviewSize(it.width, it.height) }
        picSize?.let { parameters.setPictureSize(it.width, it.height) }


        /**
         * Camera Orientation (카메라 회전 방향 설정)
         */
        try {
            val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                requireActivity().display
            } else {
                requireActivity().windowManager.defaultDisplay
            }
            when (display?.rotation) {
                Surface.ROTATION_0 -> camera.setDisplayOrientation(180)
                Surface.ROTATION_90 -> camera.setDisplayOrientation(0)
                Surface.ROTATION_180 -> camera.setDisplayOrientation(270)
                Surface.ROTATION_270 -> camera.setDisplayOrientation(180)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        camera.parameters = parameters
    }
    

    /**
     * 추후 서버 데이터 파싱으로 변경 시작.
     * 스킨컬러 변경에 쓰이는 ImageView List
     */
    private val skinImgViewList: MutableList<ImageView> = mutableListOf()

    /***************************************************************************************************************
     * 카메라의 스킨 컬러를 랜덤을로 변경 한다.
     ***************************************************************************************************************/
    private fun randomSkin() {
        val rnd = Random()
        // 최소 1~3개 값이 255 이상을 유지.
        var array: JSONArray? = null
        try {
            val jsonObj = JSONObject(Util.loadJSONFromAsset(context, "marterial_colors.json"))
            array = jsonObj.getJSONArray("colors")

            loop@ for (i in 0 until skinImgViewList.count()){
                val backColor: Int = Color.parseColor(array[rnd.nextInt(array.length())].toString())
                when (i) {
                    skinImgViewList.size-2 -> {
                        // 마지막 바디
                        skinImgViewList[i].setColorFilter(backColor, PorterDuff.Mode.SRC_IN)
                        binding.root.setBackgroundColor(backColor); binding.waveView.waveColor = backColor;binding.lyCaemraOverlay.setBackgroundColor(backColor)
                    }
                    skinImgViewList.size -1-> {
                        // 제일 마지막 라인 바디일경우 컬러 필터 X
                        break@loop
                    }
                    else -> {
                        skinImgViewList[i].setColorFilter(backColor, PorterDuff.Mode.SRC_IN)
                    }
                }
            }
        } catch (e: java.lang.Exception) {
        }
    }

    /***************************************************************************************************************
     * 카메라 플래시 변경 로직.
     ***************************************************************************************************************/
    private fun changeFlash(currentFlashMode: FlashMode): Single<FlashMode?>? {
        return Single.create { emitter: SingleEmitter<FlashMode?> ->
            var changeFlashMode = currentFlashMode
            if (cameraManager.cameraHelper.cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                if (currentFlashMode === FlashMode.OFF) {
                    changeFlashMode = FlashMode.TORCH
                } else if (currentFlashMode === FlashMode.TORCH) {
                    changeFlashMode = FlashMode.OFF
                }
            } else {
                if (currentFlashMode === FlashMode.OFF) {
                    changeFlashMode = FlashMode.AUTO
                } else if (currentFlashMode === FlashMode.AUTO) {
                    changeFlashMode = FlashMode.TORCH
                } else if (currentFlashMode === FlashMode.TORCH) {
                    changeFlashMode = FlashMode.OFF
                }
            }
            setFlashPara(changeFlashMode)
            emitter.onSuccess(changeFlashMode)
        }.subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
    }

    private fun setFlashPara(mode: FlashMode): Boolean {
        if (cameraManager.cameraHelper != null && cameraManager.cameraHelper.supportedFlashModes != null) {
            if (cameraManager.cameraHelper.supportedFlashModes.contains(mode.cameraMode)) {
                cameraManager.cameraHelper.flashMode = mode.cameraMode
                return true
            }
        }
        return false
    }

    private fun setFlash(mode: FlashMode) {
        when (mode) {
            FlashMode.OFF -> {
                binding.btnFlash.setImageResource(R.drawable.nft_btn_flash)
                binding.btnFlash.alpha = 0.4f
            }
            FlashMode.AUTO -> {
                binding.btnFlash.setImageResource(R.drawable.nft_btn_flash_auto)
                binding.btnFlash.alpha = 1.0f
            }
            FlashMode.TORCH -> {
                binding.btnFlash.setImageResource(R.drawable.nft_btn_flash_torch)
                binding.btnFlash.alpha = 1.0f
            }
        }
    }


    /***************************************************************************************************************
     * 카메라 스킨 변경 관련 시작.
     ***************************************************************************************************************/
    private var cameraSkinIdx = 0
    private lateinit var cameraNFTCameraSkin: NFTCameraSkinVO
    private var nftCameraSkinInfoList = arrayListOf<NFTCameraSkinVO>()

    private fun loadNFTCameraSkinDateFromJson() {
        // 카메라 스킨 더미 데이터 불러오기
        try {
            val jsonObj = JSONObject(Util.loadJSONFromAsset(context, "camera_skins.json"))
            val jsonArray = jsonObj.getJSONArray("skins");
            var index = 0
            nftCameraSkinInfoList.clear()
            while (index < jsonArray.length()) {
                val nftCameraSkinVO: NFTCameraSkinVO = Gson().fromJson(jsonArray[index].toString(), NFTCameraSkinVO::class.java)
                nftCameraSkinInfoList.add(nftCameraSkinVO)
                index++
            }

        } catch (e: java.lang.Exception) {

        }
    }

    private fun initCameraSkin(){
        var backColor = Color.parseColor("#a8f2eb")
        val unwrappedDrawable: Drawable? = AppCompatResources.getDrawable(requireContext(), R.drawable.bg_rounded_top)
        val wrappedDrawable: Drawable? = unwrappedDrawable?.let { DrawableCompat.wrap(it) }
        wrappedDrawable?.let { DrawableCompat.setTint(it, backColor) }

        binding.root.background = wrappedDrawable; binding.waveView.waveColor = backColor;binding.lyCaemraOverlay.setBackgroundColor(backColor)
    }

    private fun changeCameraSkin() {
        binding.lyTop.removeAllViews()
        cameraNFTCameraSkin = nftCameraSkinInfoList[cameraSkinIdx];
        setNFTCameraSkin("${cameraNFTCameraSkin.name}${cameraNFTCameraSkin.resName}", cameraNFTCameraSkin.skinRatio, cameraNFTCameraSkin.percentWidth)
        cameraSkinIdx = 0
//        cameraSkinIdx++;
//        if (cameraSkinIdx == nftCameraSkinInfoList.size)
//            cameraSkinIdx = 0
//        randomSkin()
    }

    private fun setNFTCameraSkin(skinResName: String, ratio: String, percentWidth: Float) {
        skinImgViewList.clear()
        context?.let {
            for (i in 0 until cameraNFTCameraSkin.resLength) {
                generateSkin((it.resources.getIdentifier("$skinResName${i + 1}", "drawable", it.packageName)), ratio, percentWidth)
            }
            generateSkin((it.resources.getIdentifier("${skinResName}line", "drawable", it.packageName)), ratio, percentWidth)
        }
    }

    private fun generateSkin(resId: Int, ratio: String, percentWidth: Float) {
        val img = ImageView(context)
        img.setImageResource(resId)
        img.layoutParams = ViewGroup.LayoutParams(0, 0)
        img.id = ViewCompat.generateViewId()
        binding.lyTop.addView(img)

        val set = ConstraintSet()
        set.clone(binding.lyTop)
        set.setDimensionRatio(img.id, ratio)
        set.constrainDefaultWidth(img.id, ConstraintSet.MATCH_CONSTRAINT_PERCENT)
        set.constrainPercentWidth(img.id, percentWidth)
        set.connect(img.id, ConstraintSet.BOTTOM, binding.lyTop.id, ConstraintSet.BOTTOM)
        set.connect(img.id, ConstraintSet.TOP, binding.lyTop.id, ConstraintSet.TOP)
        set.connect(img.id, ConstraintSet.LEFT, binding.lyTop.id, ConstraintSet.LEFT)
        set.connect(img.id, ConstraintSet.RIGHT, binding.lyTop.id, ConstraintSet.RIGHT)
        set.applyTo(binding.lyTop)
        skinImgViewList.add(img)
    }


    /***************************************************************************************************************
     * 카메라 촬영 관련 로직 byte[] -> originBitmap -> filteredBitmap
     ***************************************************************************************************************/

    var imgPath=""

    private fun processInstaFilterApply(data: ByteArray) {
        val options = RequestOptions.skipMemoryCacheOf(false).diskCacheStrategy(DiskCacheStrategy.NONE)
        if (!cameraManager.cameraHelper.isFaceCamera) options.transform(RotateTransformation(context, 90f, false))
        else options.transform(RotateTransformation(context, 270f, C.isFlipLeft))

        // 카메라 확대 애니메이션
        playAniCameraSkin(true)

        // 카메라 화면 오버레이 애니메이션.
        playAniCameraOverlay(true)

        Glide.with(requireActivity()).asBitmap().load(data).override(SIZE_ORIGINAL).apply(options)
            .into(object : SimpleTarget<Bitmap?>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                        var result = resource.copy(resource.config, resource.isMutable)
                        context?.let {
                            viewModel.getApplyInstaFilter(it, it.resources, cameraManager.cameraHelper.isFaceCamera, result, object : OnPictureSavedListener {
                                override fun onShowLandingPage(finalResult: Bitmap) {}
                                override fun onCapturedFilteredBitmap(finalResult: Bitmap) {
                                    playAniResultView(finalResult)
                                }
                                override fun onPictureSaved(uri: Uri, result: Bitmap) {}
                                override fun onCallSaved(uri: Uri, hasPath: Boolean) {}
                                override fun onArrivedUri(originUri: Uri, uri: Uri) {}
                            })
                        }
                    }
                })

    }

    /***************************************************************************************************************
     * 스킨 애니메이션 관련 코드
     ***************************************************************************************************************/
    var pivotXSkin : Float = 0.0f

    /**
     * 상단 카메라 스킨 확대 축소 animation
     */
    private fun playAniCameraSkin(isScaleUp : Boolean){
        // 카메라 확대 애니메이션
        val targetPercent =  1.0f / ((C.DIS_SIZE.x * cameraNFTCameraSkin.percentWidth) / binding.layoutIvResult.width.toFloat())
        val animatorSet = AnimatorSet()

        var viewHeight: Int = skinImgViewList[skinImgViewList.size - 1].measuredHeight
        val durationSkin = 1000L

        viewHeight = (viewHeight * targetPercent).toInt()
        pivotXSkin = abs(viewHeight / (CandyDisplayHelper.getInstance(context).convertDPtoPX(140.0f) * targetPercent))

        val scaleX :ObjectAnimator
        val scaleY :ObjectAnimator
        val transY :ObjectAnimator

        if(isScaleUp) {
            scaleX = ObjectAnimator.ofFloat(binding.lyTop, "scaleX", 1.0f, targetPercent).apply {duration = durationSkin}
            scaleY = ObjectAnimator.ofFloat(binding.lyTop, "scaleY", 1.0f, targetPercent).apply {duration = durationSkin}
            binding.lyTop.pivotY = binding.lyTop.measuredHeight.toFloat() * pivotXSkin
            animatorSet.playTogether(scaleX, scaleY)
        }else{
            scaleX = ObjectAnimator.ofFloat(binding.lyTop, "scaleX", targetPercent, 1.0f).apply {duration = durationSkin/2}
            scaleY = ObjectAnimator.ofFloat(binding.lyTop, "scaleY", targetPercent, 1.0f).apply {duration = durationSkin/2}
            transY = ObjectAnimator.ofFloat(binding.lyTop, "translationY", 0f).apply {duration = durationSkin/2}
            animatorSet.playTogether(scaleX, scaleY, transY)
        }
        animatorSet.start()
    }

    /**
     * 카메라 오버레이 animation
     */
    private fun playAniCameraOverlay(isShow : Boolean){
        if(isShow){
            JPAni.with(Techniques.FadeIn).duration(2000).withListener(
                object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator?) {
                        binding.lyCaemraOverlay.visibility = View.VISIBLE
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        binding.lyCaemraOverlay.visibility = View.VISIBLE
                    }

                    override fun onAnimationCancel(animation: Animator?) {}
                    override fun onAnimationRepeat(animation: Animator?) {}
                }).playOn(binding.lyCaemraOverlay)
        }else{
            JPAni.with(Techniques.FadeOut).duration(1500).playOn(binding.lyCaemraOverlay)
        }
    }

    /**
     * 카메라 촬영 결과물 보여주기 animation
     */
    private fun playAniResultView(result : Bitmap){

        JPAni.with(Techniques.SlideInUp).duration(2000).withListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator?) {
                binding.lyBottomNft.visibility = View.VISIBLE

            }

            override fun onAnimationEnd(animation: Animator?) {
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        }).playOn(binding.lyBottomNft)

        JPAni.with(Techniques.SlideOutDown).duration(2000).withListener(object : Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                binding.lyBottom.visibility = View.INVISIBLE
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        }).playOn(binding.lyBottom)

        binding.layoutIvResult.visibility = View.INVISIBLE
        JPAni.with(Techniques.SlideInDown).duration(2000).withListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                binding.layoutIvResult.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator?) {
                JPAni.with(Techniques.FadeIn).duration(2000).withListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator?) {
                        binding.ivResult.setImageBitmap(result)
                        imgPath = viewModel.saveMediaToStorage(result)
                    }
                    override fun onAnimationEnd(animation: Animator?) {}
                    override fun onAnimationCancel(animation: Animator?) {}
                    override fun onAnimationRepeat(animation: Animator?) {}
                }).playOn(binding.ivResult)
            }
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}
        }).playOn(binding.layoutIvResult)
    }


}