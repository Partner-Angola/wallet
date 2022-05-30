package com.joeware.android.gpulumera.nft.ui.camera

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.PointF
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.WindowManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.camera.CamRatio
import com.joeware.android.gpulumera.camera.CameraChooser
import com.joeware.android.gpulumera.camera.CameraEvent
import com.joeware.android.gpulumera.camera.FlashMode
import com.joeware.android.gpulumera.common.C
import com.joeware.android.gpulumera.edit.logo.LogoManager
import com.joeware.android.gpulumera.manager.SaveImageManager
import com.joeware.android.gpulumera.util.CandyMediaScanner
import com.joeware.android.gpulumera.util.PrefUtil
import com.joeware.android.gpulumera.util.SingleLiveEvent
import com.joeware.android.gpulumera.util.Theme
import com.joeware.android.jni.ImageNativeLibrary
import com.jpbrothers.android.engine.base.util.DeviceUtil
import com.jpbrothers.android.engine.camera.CameraIdInfo
import com.jpbrothers.android.engine.camera.CameraTmpBridge
import com.jpbrothers.android.engine.shaders.*
import com.jpbrothers.base.ui.TextImageMaker
import com.jpbrothers.base.util.RxEventFactory
import com.jpbrothers.base.util.WeekRefHandler
import com.jpbrothers.base.util.log.JPLog
import io.reactivex.Completable
import org.koin.java.standalone.KoinJavaComponent.inject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*
import kotlin.math.abs


class NftCameraViewModel(val context: Context) : CandyViewModel() {

    private val prefUtil: PrefUtil by inject(PrefUtil::class.java)
    private val IMAGES_FOLDER_NAME = "CANDY_NFT"

    private val _clickBack = SingleLiveEvent<Void>()
    private val _mintNFT = SingleLiveEvent<Void>()
    private val _takePhoto = SingleLiveEvent<Void>()
    private val _clickFlim = SingleLiveEvent<Void>()
    private val _clickGiftBox = SingleLiveEvent<Void>()
    var isIngCapture : Boolean = false

    val clickBack: LiveData<Void> get() = _clickBack
    val mintNFT: LiveData<Void> get() = _mintNFT
    val takePhoto: LiveData<Void> get() = _takePhoto
    val clickFlim: LiveData<Void> get() = _clickFlim
    val clickGiftBox: LiveData<Void> get() = _clickGiftBox

    private val cameraChooser = CameraChooser()
    private var currentCameraId = prefUtil.nftCameraId

    init {
        cameraChooser.initCameraChooser(initCameraList())
    }

    fun clickBack() {
        _clickBack.call()
    }

    fun mintNFT() {
        _mintNFT.call()
    }

    fun takePhoto() {
        _takePhoto.call()
    }

    fun clickFlim() {
        _clickFlim.call()
    }

    fun clickGiftBox() {
        _clickGiftBox.call()
    }

    /***************************************************************************************************************
     * 카메라 촬영 관련 로직
     ***************************************************************************************************************/

    var glitchBitmap: Bitmap? = null
    var lookupBitmap: Bitmap? = null
    var lightBitmap: Bitmap? = null
    var grainBitmap: Bitmap? = null
    var dateTaken: Long = 0

    fun getApplyInstaFilter(context: Context, resources: Resources, isFaceCamera : Boolean , origin: Bitmap, listener: SaveImageManager.OnPictureSavedListener) {
        Completable.complete().subscribe({

            /**
             * 다양한 필터 적용을 위해 테마를 랜덤으로 설정한다.
             */
            val random = Random()
            if (C.IS_NFT_RANDOM_FILTER) {
                when (random.nextInt(8)) {
                    Theme.CLASSIC.ordinal -> C.THEME = Theme.CLASSIC
                    Theme.CANDYMATIC.ordinal -> C.THEME = Theme.CANDYMATIC
                    Theme.FLASH.ordinal -> C.THEME = Theme.FLASH
                    Theme.INSTAX.ordinal -> C.THEME = Theme.INSTAX
                    Theme.MELLOW.ordinal -> C.THEME = Theme.MELLOW
                    Theme.RAICA.ordinal -> C.THEME = Theme.RAICA
                    Theme.SQUARE.ordinal -> C.THEME = Theme.SQUARE
                    Theme.TOY.ordinal -> C.THEME = Theme.TOY
                }
            }

            // check recycled Bitmap
            if (glitchBitmap != null) {
                glitchBitmap!!.recycle()
                glitchBitmap = null
            }
            if (lookupBitmap != null) {
                lookupBitmap!!.recycle()
                lookupBitmap = null
            }
            if (grainBitmap != null) {
                grainBitmap!!.recycle()
                grainBitmap = null
            }
            if (lightBitmap != null) {
                lightBitmap!!.recycle()
                lightBitmap = null
            }
            val masterShader = GLMasterShader()
            val filterGroups = GLSkinSmoothShaderGroup(masterShader)

            // add Vignette filter
            if (random.nextInt(4) == 0 && (C.THEME === Theme.CLASSIC || C.THEME === Theme.RAICA || C.THEME === Theme.MELLOW)) {
                masterShader.addShader(GLMasterShader.UnitShader.VIGNETTE, PointF(0.5f, 0.5f), floatArrayOf(0.05f, 0.05f, 0.05f), 0.3f, 0.85f)
            }

            // add Lookup filter
            var idx = 0
            var header = "classic_"
            var lookupIntensity = 1.0f
            var exIntensity = 1.0f
            var vibIntensity = 1.0f
            var saIntensity = 1.0f
            var contIntensity = 1.0f
            if (C.THEME === Theme.CLASSIC || C.THEME === Theme.MELLOW) {
                idx = random.nextInt(C.THEME_CLASSIC_COUNT) + 1
                header = "classic_"
            } else if (C.THEME === Theme.INSTAX) {
                idx = random.nextInt(C.THEME_INSTAX_COUNT) + 1
                header = "instax_"
            } else if (C.THEME === Theme.RAICA) {
                lookupIntensity = (random.nextInt(30).toFloat() + 70) * 0.01f // 0.7 to 1.0
                exIntensity = (random.nextInt(90) - 20).toFloat() * 0.01f // -0.2 to 0.70
                vibIntensity = (random.nextInt(70).toFloat() - 40) * 0.01f // -0.4 to 0.3
                saIntensity = (random.nextInt(20) + 90).toFloat() * 0.01f // 0.90, to 991.10
                contIntensity = (random.nextInt(10) + 95).toFloat() * 0.01f // 0.95 to 1.05
                if (random.nextBoolean()) {
                    idx = random.nextInt(C.THEME_CLASSIC_COUNT) + 1
                    header = "classic_"
                } else {
                    idx = random.nextInt(C.THEME_INSTAX_COUNT) + 1
                    header = "instax_"
                    lookupIntensity -= 0.30f
                    exIntensity = -abs(exIntensity) - 0.30f
                    vibIntensity = -abs(vibIntensity) - 0.10f
                    saIntensity = 0.65f
                    contIntensity = 0.85f
                }
                //        } else if (C.THEME == Theme.MELLOW) {
//            idx = 32;
//            header = "instax_";
//            exIntensity = -1.0f;
//            lookupIntensity = 0.6f;
//            vibIntensity = 0.0f;// -0.4 to 0.3
//            saIntensity = 1f;// 0.90, to 1.10
//            contIntensity = 1.0f;
            } else if (C.THEME === Theme.SQUARE) {
                idx = random.nextInt(C.THEME_SQUARE_COUNT) + 1
                header = "square_"
            } else if (C.THEME === Theme.FLASH) {
                idx = random.nextInt(C.THEME_FLASH_COUNT)
                header = "flash_"
                exIntensity = -0.5f
                saIntensity = 1.2f
                contIntensity = 1.2f
                vibIntensity = (random.nextInt(10).toFloat() - 40) * 0.01f // -0.3 to -0.4            //            saIntensity = 0.6f;
            } else if (C.THEME === Theme.TOY) {
                idx = random.nextInt(C.THEME_TOY_COUNT)
                header = "toy_"
                lookupIntensity = (random.nextInt(30).toFloat() + 70) * 0.01f // 0.7 to 1.0
                exIntensity = (random.nextInt(90) - 20).toFloat() * 0.01f // -0.2 to 0.70
                vibIntensity = (random.nextInt(70).toFloat() - 40) * 0.01f // -0.4 to 0.3
                saIntensity = (random.nextInt(20) + 90).toFloat() * 0.01f // 0.90, to 991.10
                contIntensity = (random.nextInt(10) + 95).toFloat() * 0.01f // 0.95 to 1.05
            } else if (C.THEME === Theme.CANDYMATIC) {
                idx = random.nextInt(C.THEME_CANDYMATIC_COUNT) + 1
                header = "candymatic_"
                lookupIntensity = (random.nextInt(50).toFloat() + 50) * 0.01f // 0.7 to 1.0
                exIntensity = (random.nextInt(90) - 10).toFloat() * 0.01f // -0.2 to 0.70
                vibIntensity = (random.nextInt(70).toFloat() - 40) * 0.01f // -0.4 to 0.3
                saIntensity = (random.nextInt(20) + 90).toFloat() * 0.01f // 0.90, to 991.10
                contIntensity = (random.nextInt(10) + 95).toFloat() * 0.01f // 0.95 to 1.05
                //            lookupIntensity = ((float) random.nextInt(30) + 70) * 0.01f; // 0.7 to 1.0
//            exIntensity = (float) ((random.nextInt(90)) - 20) * 0.01f; // -0.2 to 0.70
//            vibIntensity = ((float) (random.nextInt(70)) - 40) * 0.01f;// -0.4 to 0.3
//            saIntensity = (float) ((random.nextInt(20)) + 90) * 0.01f;// 0.90, to 991.10
//            contIntensity = (float) ((random.nextInt(10)) + 95) * 0.01f; // 0.95 to 1.05


//            lookupIntensity = lookupIntensity - 0.30f;
//            exIntensity = -Math.abs(exIntensity) - 0.30f;
//            vibIntensity = -Math.abs(vibIntensity) - 0.10f;
//            saIntensity = 0.65f;
//            contIntensity = 0.85f;
            }
            try {
                val lookupId = resources.getIdentifier(header + idx, "raw", context?.packageName)
                lookupBitmap = ShaderChooseUtil.getInstance().decodeResource(resources, lookupId)
                masterShader.addShader(GLMasterShader.UnitShader.LOOKUP, lookupBitmap)
                masterShader.setIntensity(lookupIntensity)
            } catch (e: java.lang.Exception) {
            }
            if (C.THEME === Theme.RAICA || C.THEME === Theme.FLASH) {
                // SnapQuck  모드 일경우.
                masterShader.addShader(GLMasterShader.UnitShader.EXPOSURE)
                masterShader.exposure = exIntensity // -0.8 ~ -1.0
                // -1 to 1;
                masterShader.addShader(GLMasterShader.UnitShader.VIBRANCE)
                masterShader.setVibrance(vibIntensity)

                // -1 to 1;
                masterShader.addShader(GLMasterShader.UnitShader.SATURATION)
                masterShader.setSaturation(saIntensity)

                //        // -1 to 1;
                masterShader.addShader(GLMasterShader.UnitShader.CONTRAST)
                masterShader.contrast = contIntensity
            }

            // add glitch filter
            try {
                if (random.nextBoolean() || C.THEME === Theme.FLASH) {
                    val glitchId = "texture_1"
                    val glitch = resources.getIdentifier(glitchId, "raw", context?.packageName)
                    glitchBitmap = ShaderChooseUtil.getInstance().decodeResource(resources, glitch)
                    if (glitchBitmap != null && !glitchBitmap!!.isRecycled) {
                        var mIntensity = 0.2f
                        if (C.THEME === Theme.FLASH) {
                            mIntensity += (random.nextInt(30) + 40).toFloat() * 0.01f // 0.4 to 0.7 ->
                        }
                        val glitchShader = GLGlitchShader(glitchBitmap, mIntensity)
                        filterGroups.addFilter(glitchShader)
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            // add grain filter
            try {
                //            if (random.nextInt(3) != 0) {
                if (C.THEME === Theme.RAICA || C.THEME === Theme.SQUARE || C.THEME === Theme.FLASH || C.THEME === Theme.CANDYMATIC) {
                    val grain = GLGrainNonTextureShader((random.nextInt(8) + 12).toFloat() * 0.01f)
                    filterGroups.addFilter(grain)
                    //            }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            // add light filter


            // add light filter


//        if (C.THEME != Theme.RAICA && C.THEME != Theme.MELLOW) {
            if (C.THEME !== Theme.RAICA && C.THEME !== Theme.TOY) {
                var lidx = -1
                try {
                    lidx = random.nextInt(C.LIGHT_COUNT) + 1
//                if (C.ISHALLOWEEN) {
//                    lidx = lidx + 20
//                }
                    val lightId = resources.getIdentifier("light_$lidx", "raw", context?.packageName)
                    lightBitmap = ShaderChooseUtil.getInstance().decodeResource(resources, lightId)
                    if (!isFaceCamera) {
                        val na = ImageNativeLibrary(lightBitmap)
                        na.flipBitmapHorizontal()
                        lightBitmap = na.bitmapAndFree
                    }
                    if (lightBitmap != null && !lightBitmap!!.isRecycled) {
                        masterShader.addShader(GLMasterShader.UnitShader.SCREENBLEND, lightBitmap)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            try {
                if (isFaceCamera) {
                    // beauty filter 추가
                    val mSkinSmoothFilter = GLFilterBilateralTwoInput()
                    val mSkinSmoothPreFilter = GLFilterBilateralTextureSamplingH(0.003f, 0.0f, 2.135f)
                    val mSkinSmoothPreFilter2 = GLFilterBilateralTextureSamplingV(0.0f, 0.004f, 2.135f)
                    mSkinSmoothFilter.skinEnable = true
                    mSkinSmoothFilter.setBeautyLevel(range(50, 0.0f, 1.0f))
                    filterGroups.addFilterFirst(mSkinSmoothFilter)
                    filterGroups.addFilterFirst(mSkinSmoothPreFilter2)
                    filterGroups.addFilterFirst(mSkinSmoothPreFilter)
                }
            } catch (e: java.lang.Exception) {
            }
            val group: GLShaderGroup = filterGroups //mGPUImage.getShader().clone();
            try {
                val logoManager = LogoManager.getInstance(context)
                val logoSaveInfo = logoManager!!.logoSaveInfo
                var dateDrawable: TextImageMaker.LogoDrawable? = null
                if (C.isTimeStamp) {
                    if (logoManager.dateDrawableList != null && logoManager.dateDrawableList.size > 0) {
                        dateDrawable = logoManager.dateDrawableList[0]
                        if (dateDrawable != null) {
                            logoSaveInfo.dateLogo = dateDrawable
                            logoSaveInfo.isTagDate = true
                        }
                    }
                } else {
                    logoSaveInfo.dateLogo = null
                    logoSaveInfo.isTagDate = false
                }


                // 1:1을 지원 하지 않는 경우 크롭 해준다.
                if(origin.width != origin.height) {
                    val dstBmp: Bitmap
                    if (origin.width >= origin.height) {
                        dstBmp = Bitmap.createBitmap(
                            origin,
                            origin.width / 2 - origin.height / 2,
                            0,
                            origin.height,
                            origin.height
                        );
                    } else {
                        dstBmp = Bitmap.createBitmap(
                            origin,
                            0,
                            origin.height / 2 - origin.width / 2,
                            origin.width,
                            origin.width
                        );
                    }
                    origin.recycle()
                    SaveImageManager.getInstance(context, handler).getFullCapturedBitmap(
                        dstBmp,
                        { finalResult: Bitmap? -> listener.onCapturedFilteredBitmap(finalResult) }, group, logoSaveInfo
                    )
                } else {
                    SaveImageManager.getInstance(context, handler).getFullCapturedBitmap(
                        origin,
                        { finalResult: Bitmap? -> listener.onCapturedFilteredBitmap(finalResult) }, group, logoSaveInfo
                    )
                }
            } catch (e: java.lang.Exception) {
            }
        },{})
    }

    private val handler: WeekRefHandler = WeekRefHandler { }

    private fun range(percentage: Int, start: Float, end: Float): Float {
        return (end - start) * percentage / 5.0f + start
    }
    /***************************************************************************************************************
     * 화면 사이즈에 최적화된 프리뷰 사이즈 가져오기.
     ***************************************************************************************************************/
    fun getOptimalPreviewSize(sizes: List<Camera.Size>?, width: Int, height: Int, mode: CamRatio): Camera.Size? {
        if (sizes == null) {
            return null
        }
        val baseWidth: Int
        val baseHeight: Int //W > H
        val deviceLevel = DeviceUtil.getDeviceLevel()
        if (deviceLevel >= 3) {
            baseWidth = 1440
            baseHeight = 1080
        } else if (deviceLevel == 2) {
            baseWidth = 1280
            baseHeight = 960
        } else {
            baseWidth = 960
            baseHeight = 720
        }
        var size: Camera.Size? = null
        val it: Iterator<*> = sizes.iterator()
        // 1:1 체크 하기.
        var isUnSupported1x1 = true
        do {
            if (!it.hasNext()) {
                break
            }
            val size2 = it.next() as Camera.Size
            if (size2.width == size2.height) {
                isUnSupported1x1 = false
                JPLog.e("david ::: is support 1:1")
            }
        } while (true)
        RxEventFactory.get().post(CameraEvent.UnSupported1x1(isUnSupported1x1))
        val iterator: Iterator<*> = sizes.iterator()
        val i = if (mode === CamRatio.PIC_FULL) 16 else if (mode === CamRatio.PIC_4X3) 4 else 1
        val j = if (mode === CamRatio.PIC_FULL) 9 else if (mode === CamRatio.PIC_4X3) 3 else 1
        do {
            if (!iterator.hasNext()) {
                break
            }
            val size2 = iterator.next() as Camera.Size
            if ((baseWidth < size2.width || baseHeight < size2.height) && i * size2.height == j * size2.width && (size == null || size.width * size.height > size2.width * size2.height)) size =
                size2
        } while (true)
        if (size == null) {
            val iterator1: Iterator<*> = sizes.iterator()
            do {
                if (!iterator1.hasNext()) {
                    break
                }
                val size1 = iterator1.next() as Camera.Size
                if (baseWidth >= size1.width && baseHeight >= size1.height && i * size1.height == j * size1.width && (size == null || size.width * size.height < size1.width * size1.height)) size =
                    size1
            } while (true)
        }
        return size
    }

    /***************************************************************************************************************
     * 화면 사이즈에 최적화된 사진 사이즈 가져오기.
     ***************************************************************************************************************/
    fun getOptimalPicSize(activity: Activity, sizes: List<Camera.Size>?, targetRatio: Double, max: Int): Camera.Size? {
        var max = max
        if (activity == null) {
            return null
        }
        if (sizes == null) {
            return null
        }
        if (max == 0) {
            max = 3000
        }
        var optimalSize: Camera.Size? = null
        var minDiff = Double.MAX_VALUE
        val display = activity!!.windowManager.defaultDisplay
        var targetHeight = display.height.coerceAtMost(display.width)
        if (targetHeight <= 0) {
            // We don't know the size of SurefaceView, use screen height
            val windowManager = activity!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            targetHeight = windowManager.defaultDisplay.height
        }

        // Try to find an size match aspect ratio and size
        val mName = Build.MODEL.toUpperCase()
        JPLog.e("getOptimalPicSize modelName $mName")
        val tmpWidth = C.DIS_SIZE.x
        val tmpHeight = C.DIS_SIZE.x
        val adaptList = ArrayList<Camera.Size>()
        for (i in sizes.indices) {
            val size = sizes[i]
            val ratio = size.width.toDouble() / size.height
            JPLog.e("getOptimalPicSize p ratio " + size.width + "x" + size.height + " : " + ratio + " " + tmpWidth + " " + tmpHeight)
            if (abs(ratio - targetRatio) < 0.05 && size.width <= max) {
                JPLog.e("getOptimalPicSize call ratio " + size.width + "x" + size.height + " : " + ratio)
                adaptList.add(size)
                if (size.width < tmpWidth || size.height < tmpHeight) {
                    break
                } else if (size.width == tmpWidth || size.height == tmpHeight) {
                }
            }
        }
        if (optimalSize == null && adaptList.size > 0) {
            optimalSize = if (C.isPicQuality == C.PicQuality.HIGH) {
                adaptList[0]
            } else if (C.isPicQuality == C.PicQuality.MEDIUM) {
                val firstSize = adaptList[0]
                if (firstSize.width < tmpWidth || firstSize.height < tmpHeight) {
                    adaptList[0]
                } else {
                    val index = adaptList.size / 2
                    // Daniel 181120 size가 2인 경우 여기서 인덱를 하나 빼버리면 무조건 0번 인덱스 해상도만 선택됨.
                    // 노멀화잘인 경우 adapList의 중간값을 선택하도록 수정
                    //                    if (adaptList.size() % 2 == 0 && index > 0) {
                    //                        index -= 1;
                    //                    }
                    adaptList[index]
                }
            } else {
                val firstSize = adaptList[0]
                if (firstSize.width < tmpWidth || firstSize.height < tmpHeight) {
                    adaptList[0]
                } else {
                    adaptList[adaptList.size - 1]
                }
            }
            JPLog.e("getOptimalPicSize result call ratio " + optimalSize.width + "x" + optimalSize.height + " : " + optimalSize.width.toDouble() / optimalSize.height + " " + adaptList.size + " " + C.isPicQuality)
        }

        // Daniel 181120 - 유음촬영인 경우 width, height사이즈 반대로 들어오는데, 비교를 거꾸로 하고있었음
        // (optimalSize.width < tmpWidth || optimalSize.height < tmpHeight) 부분 수정함
        if (optimalSize != null && (optimalSize.width < tmpHeight || optimalSize.height < tmpWidth) && C.isPicQuality != C.PicQuality.NORMAL) {
            var temp: Camera.Size? = null
            for (i in sizes.indices) {
                val size = sizes[i]
                val ratio = size.width.toDouble() / size.height
                if (size.width == optimalSize.width && size.height == optimalSize.height) {
                    break
                } else {
                    if (Math.abs(ratio - targetRatio) < 0.05) {
                        temp = size
                    }
                }
            }
            if (temp != null) {
                optimalSize = temp
                JPLog.e("getOptimalPicSize is low display size - new Size " + temp.width + " " + temp.height)
            }
        }

        // 비율과 일치하는 사이즈가 없을경우.
        if (optimalSize == null) {
            JPLog.e("No preview size match the aspect ratio")
            minDiff = Double.MAX_VALUE
            for (size in sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size
                    minDiff = Math.abs(size.height - targetHeight).toDouble()
                }
            }
        }
        return optimalSize
    }

    /***************************************************************************************************************
     * 사진 저장(임시) 추후 내부 디렉토리 지정 방식으로 변경 에정.
     ***************************************************************************************************************/
    fun saveMediaToStorage(bitmap: Bitmap): String {
        //Generating a file name
        val filename = "${System.currentTimeMillis()}.jpg"

        //Output stream
        var fos: OutputStream? = null
        var imagePath = ""
        //For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //getting the contentResolver
            context?.contentResolver?.also { resolver ->

                //Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM+"/"+C.GALLERY_FOLDER_NAME)
                }

                //Inserting the contentValues to contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                //Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
                imagePath = getPathFromUri(imageUri).toString()
            }
        } else {
            //These for devices running on android < Q
            //So I don't think an explanation is needed here
            fixMediaDir()
            val imagesDir = "${Environment.getExternalStorageDirectory()}/${Environment.DIRECTORY_DCIM}/${C.GALLERY_FOLDER_NAME}"
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
            imagePath = image.absolutePath
        }

        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val mediaScanner = CandyMediaScanner.newInstance(context)
            try {
                mediaScanner.mediaScanning(imagePath)
            } catch (e: java.lang.Exception) {

            }
        }
        return imagePath
    }

    private fun fixMediaDir() {
        try {
            val sdcard = Environment.getExternalStorageDirectory() ?: return
            val dcim = File(sdcard, Environment.DIRECTORY_DCIM) ?: return
            val camera = File(dcim, C.GALLERY_FOLDER_NAME)
            if (camera.exists()) {
                return
            }
            camera.mkdir()
        }catch (e : java.lang.Exception){}
    }

    private fun getPathFromUri(uri: Uri?): String? {
        val cursor: Cursor? = uri?.let { context.contentResolver.query(it, null, null, null, null) }
        cursor?.moveToNext()
        val path: String? = cursor?.getString(cursor?.getColumnIndex("_data"))
        cursor?.close()
        return path
    }

    /***************************************************************************************************************
     * 카메라 전후면 스위치
     ***************************************************************************************************************/
    private val _switchCameraId = MutableLiveData<Int>()
    private val _isSupportWide = MutableLiveData<Boolean>()
    private val _isWide = MutableLiveData(false)

    val switchCameraId: LiveData<Int> get() = _switchCameraId
    val isSupportWide: LiveData<Boolean> get() = _isSupportWide
    val isWide: LiveData<Boolean> get() = _isWide
    
    fun switchCam(isChange: Boolean, isSwitchWide: Boolean) {
        try {
            val info = if (cameraChooser.isSupportWideAngle()) {
                cameraChooser.getCameraInfo(currentCameraId, isChange, isSwitchWide)
            } else {
                cameraChooser.getRotateCameraInfo(currentCameraId)
            }
            currentCameraId = info.id.toInt()
            _isWide.postValue(info.isWide)
            _switchCameraId.postValue(currentCameraId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun initWide() {
        _isSupportWide.postValue(cameraChooser.isSupportWideAngle())
    }

    /***************************************************************************************************************
     * 카메라 플래시.
     ***************************************************************************************************************/
    private val _flashMode = MutableLiveData<FlashMode>()
    private val _isSupportFlash = MutableLiveData<Boolean>()
    private val _clickFlash = SingleLiveEvent<Void>()

    val flashMode: LiveData<FlashMode> get() = _flashMode
    val isSupportFlash: LiveData<Boolean> get() = _isSupportFlash
    val clickFlash: LiveData<Void> get() = _clickFlash

    fun setFlashAvailable(isSupport : Boolean){
        _isSupportFlash.postValue(isSupport)
    }

    fun setFlashMode(flashMode :FlashMode){
        _flashMode.postValue(flashMode)
    }


    fun switchFlash() {
        _clickFlash.call()
    }

    private fun initCameraList(): ArrayList<CameraIdInfo> {
        val cameraTmpBridge = CameraTmpBridge()
        val frontCameraList = cameraTmpBridge.getCameraList(context, true)
        val backCameraList = cameraTmpBridge.getCameraList(context, false)
        val frontWide = cameraTmpBridge.getWideAngleId(context, frontCameraList)
        val backWide = cameraTmpBridge.getWideAngleId(context, backCameraList)
        return arrayListOf<CameraIdInfo>().apply {
            var idx = 0
            var isAdd = true
            while (isAdd) {
                isAdd = false
                if (frontCameraList.size > idx) {
                    val id = frontCameraList[idx]
                    add(CameraIdInfo(id, true, frontWide == id))
                    isAdd = true
                }
                if (backCameraList.size > idx) {
                    val id = backCameraList[idx]
                    add(CameraIdInfo(id, false, backWide == id))
                    isAdd = true
                }
                idx++
            }
        }
    }

    private val _back = SingleLiveEvent<Void>()

    val back: LiveData<Void> get() = _back

    fun backToCamera() = _back.call()
}