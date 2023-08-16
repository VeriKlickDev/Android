package com.ui.twilioVideo

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.*
import android.os.Build
import android.widget.EditText
import com.veriKlick.R
import tvi.webrtc.HardwareVideoDecoderFactory
import tvi.webrtc.HardwareVideoEncoderFactory
import tvi.webrtc.VideoFrame
import tvi.webrtc.YuvConverter
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer


//twilio ext

fun isH264Supported(): Boolean {
    val hardwareVideoEncoderFactory =
        HardwareVideoEncoderFactory(null, true, true)
    val hardwareVideoDecoderFactory =
        HardwareVideoDecoderFactory(null)

    val h264EncoderSupported = hardwareVideoEncoderFactory.supportedCodecs.any {
        it.name.equals("h264", ignoreCase = true)
    }
    val h264DecoderSupported = hardwareVideoDecoderFactory.supportedCodecs.any {
        it.name.equals("h264", ignoreCase = true)
    }

    return h264EncoderSupported && h264DecoderSupported
}



/**
 * Converts a [tvi.webrtc.VideoFrame] to a Bitmap. This method must be called from a thread with a
 * valid EGL context when the frame buffer is a [VideoFrame.TextureBuffer].
 */
fun VideoFrame.toBitmap(): Bitmap? {
    // Construct yuv image from image data
    val yuvImage = if (buffer is VideoFrame.TextureBuffer) {
        val yuvConverter = YuvConverter()
        val i420Buffer = yuvConverter.convert(buffer as VideoFrame.TextureBuffer)
        yuvConverter.release()
        i420ToYuvImage(i420Buffer, i420Buffer.width, i420Buffer.height)
    } else {
        val i420Buffer = buffer.toI420()
        val returnImage = i420ToYuvImage(i420Buffer, i420Buffer.width, i420Buffer.height)
        i420Buffer.release()
        returnImage
    }
    val stream = ByteArrayOutputStream()
    val rect = Rect(0, 0, yuvImage.width, yuvImage.height)

    // Compress YuvImage to jpeg
    yuvImage.compressToJpeg(rect, 100, stream)

    // Convert jpeg to Bitmap
    val imageBytes = stream.toByteArray()
    var bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val buffer = ByteBuffer.wrap(imageBytes)
        val src =
            ImageDecoder.createSource(buffer)
        try {
            ImageDecoder.decodeBitmap(src)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    } else {
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
    val matrix = Matrix()

    // Apply any needed rotation
    matrix.postRotate(rotation.toFloat())
    bitmap = Bitmap.createBitmap(
        bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true
    )
    return bitmap
}

private fun i420ToYuvImage(i420Buffer: VideoFrame.I420Buffer, width: Int, height: Int): YuvImage {
    val yuvPlanes = arrayOf(
        i420Buffer.dataY, i420Buffer.dataU, i420Buffer.dataV
    )
    val yuvStrides = intArrayOf(
        i420Buffer.strideY, i420Buffer.strideU, i420Buffer.strideV
    )
    if (yuvStrides[0] != width) {
        return fastI420ToYuvImage(yuvPlanes, yuvStrides, width, height)
    }
    if (yuvStrides[1] != width / 2) {
        return fastI420ToYuvImage(yuvPlanes, yuvStrides, width, height)
    }
    if (yuvStrides[2] != width / 2) {
        return fastI420ToYuvImage(yuvPlanes, yuvStrides, width, height)
    }
    val bytes = ByteArray(
        yuvStrides[0] * height + yuvStrides[1] * height / 2 + yuvStrides[2] * height / 2
    )
    var tmp = ByteBuffer.wrap(bytes, 0, width * height)
    copyPlane(yuvPlanes[0], tmp)
    val tmpBytes = ByteArray(width / 2 * height / 2)
    tmp = ByteBuffer.wrap(tmpBytes, 0, width / 2 * height / 2)
    copyPlane(yuvPlanes[2], tmp)
    for (row in 0 until height / 2) {
        for (col in 0 until width / 2) {
            bytes[width * height + row * width + col * 2] =
                tmpBytes[row * width / 2 + col]
        }
    }
    copyPlane(yuvPlanes[1], tmp)
    for (row in 0 until height / 2) {
        for (col in 0 until width / 2) {
            bytes[width * height + row * width + col * 2 + 1] =
                tmpBytes[row * width / 2 + col]
        }
    }
    return YuvImage(bytes, ImageFormat.NV21, width, height, null)
}

private fun fastI420ToYuvImage(
    yuvPlanes: Array<ByteBuffer>,
    yuvStrides: IntArray,
    width: Int,
    height: Int
): YuvImage {
    val bytes = ByteArray(width * height * 3 / 2)
    var i = 0
    for (row in 0 until height) {
        for (col in 0 until width) {
            bytes[i++] = yuvPlanes[0].get(col + row * yuvStrides[0])
        }
    }
    for (row in 0 until height / 2) {
        for (col in 0 until width / 2) {
            bytes[i++] = yuvPlanes[2].get(col + row * yuvStrides[2])
            bytes[i++] = yuvPlanes[1].get(col + row * yuvStrides[1])
        }
    }
    return YuvImage(bytes, ImageFormat.NV21, width, height, null)
}

private fun copyPlane(src: ByteBuffer, dst: ByteBuffer) {
    src.position(0).limit(src.capacity())
    dst.put(src)
    dst.position(0).limit(dst.capacity())
}


object DialogConnectTwilioRoom {
    fun createConnectDialog(
        participantEditText: EditText?,
        callParticipantsClickListener: DialogInterface.OnClickListener?,
        cancelClickListener: DialogInterface.OnClickListener?,
        context: Context?
    ): AlertDialog
    {
        val alertDialogBuilder= AlertDialog.Builder(context!!)
        alertDialogBuilder.setIcon(R.drawable.ic_video_call_white_24dp)
        alertDialogBuilder.setTitle(context.getString(R.string.txt_connect_to_room))
        alertDialogBuilder.setPositiveButton(context.getString(R.string.txt_connect), callParticipantsClickListener)
        alertDialogBuilder.setNegativeButton(context.getString(R.string.txt_cancel), cancelClickListener)
        alertDialogBuilder.setCancelable(false)
        participantEditText?.let { this.setRoomNameFieldInDialog(it, alertDialogBuilder) }
        return alertDialogBuilder.create()
    }

    private fun setRoomNameFieldInDialog(
        roomNameEditText: EditText, alertDialogBuilder: android.app.AlertDialog.Builder
    ) {
        roomNameEditText.hint = "room name"
        roomNameEditText.setText("RM7ab7f1105b0e85d81be17655f6af7add")
        alertDialogBuilder.setView(roomNameEditText)
    }
}

