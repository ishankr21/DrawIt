package com.example.drawit

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.graphics.*
import android.util.TypedValue
import android.view.MotionEvent

class DrawingView(context: Context,attrs: AttributeSet):View(context,attrs) {

    private var mDrawPath:CustomPath?=null
    private var mCanvasBitmap:Bitmap?=null
    private var mDrawPaint:Paint?=null
    private var mCanvasPaint:Paint?=null
    private var mBrushSize:Float=0.toFloat()
    private var colour=Color.BLUE
    private var canvas:Canvas?=null
    private val mPaths=ArrayList<CustomPath>()
    private val mUndoPaths=ArrayList<CustomPath>()

    init {
        setUpDrawing()
    }

    private fun setUpDrawing() {
        mDrawPaint= Paint()
        mDrawPath=CustomPath(colour,mBrushSize)
        mDrawPaint!!.color=colour
        mDrawPaint!!.style=Paint.Style.STROKE
        mDrawPaint!!.strokeJoin=Paint.Join.ROUND
        mDrawPaint!!.strokeCap=Paint.Cap.ROUND
        mCanvasPaint= Paint(Paint.DITHER_FLAG)





    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap= Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas= Canvas(mCanvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!,0f,0f,mCanvasPaint)
        for(path in mPaths)
        {
            mDrawPaint!!.strokeWidth=path.brushSize
            mDrawPaint!!.color=path.colour
            canvas.drawPath(path,mDrawPaint!!)
        }
        if(!mDrawPath!!.isEmpty)
        {
            mDrawPaint!!.strokeWidth=mDrawPath!!.brushSize
            mDrawPaint!!.color=mDrawPath!!.colour
        canvas.drawPath(mDrawPath!!,mDrawPaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        val touchx=event?.x
        val touchy=event?.y
        when(event?.action)
        {
             MotionEvent.ACTION_DOWN -> {
                 mDrawPath!!.colour=colour
                 mDrawPath!!.brushSize=mBrushSize
                 mDrawPath!!.reset()
                 if (touchx != null) {
                     if (touchy != null) {
                         mDrawPath!!.moveTo(touchx,touchy)
                     }
                 }

             }
            MotionEvent.ACTION_MOVE->{
                if (touchy != null) {
                    if (touchx != null) {
                        mDrawPath!!.lineTo(touchx,touchy)
                    }
                }
            }
            MotionEvent.ACTION_UP->
            {
                mPaths.add(mDrawPath!!)
                mDrawPath=CustomPath(colour,mBrushSize)

            }
            else -> return false

        }
        invalidate()
        return true

    }
    fun onClickUndo()
    {
        if(mPaths.size>0)
        {
            mUndoPaths.add(mPaths.removeAt(mPaths.size-1))
            invalidate()
        }
    }
    fun redo()
    {
        if(mUndoPaths.size>0)
        {
            mPaths.add(mUndoPaths[mUndoPaths.size-1])
            mUndoPaths.removeAt(mUndoPaths.size-1)
            invalidate()
        }
    }
    fun setBrushSize(newSize:Float)
    {
        mBrushSize=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,newSize,resources.displayMetrics)
        mDrawPaint!!.strokeWidth=mBrushSize
    }
    fun setColor(newColor: String)
    {
        colour=Color.parseColor(newColor)
        mDrawPaint!!.color=colour
    }
    internal inner class CustomPath(var colour:Int,var brushSize:Float):Path() {


    }
}