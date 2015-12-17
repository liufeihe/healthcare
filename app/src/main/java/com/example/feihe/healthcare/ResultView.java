package com.example.feihe.healthcare;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class ResultView extends View {

    private float width;//每个小格的宽度
    private float height;//每个小格的高度
    private int blockNum=10;//把整个画布分成100*100的小格
    private float[] bmiLimit=new float[]{18.5f,24f,28f};

    private String key = "";
    private int length = 0;
    private float[] datalist;

    private float averageY;
    private float averageX;
    private float maxY;
    private float minY;
    private float maxX;
    private float minX;
    private float radius = 10;

    public ResultView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public void  setData(String key, float[] data){
        if(data.length==0)
            length = 0;
        else{
            this.key = key;
            length = data.length;
            datalist = new float[length];
            for(int i=0; i < length; i++)
                datalist[i] = data[i];
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width=w/blockNum;
        height=h/blockNum;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        width = getMeasuredWidth()/blockNum;
        height = getMeasuredHeight()/blockNum;

        //在maxX,maxY,minX,minY内显示数据
        averageY = height*blockNum/2;
        averageX = width*blockNum/2;
        maxY = height*(blockNum/2+3);
        minY = height*(blockNum/2-3);
        maxX = width*(blockNum-1);
        minX = width;

        //画背景
        Paint background = new Paint();
        background.setColor(getResources().getColor(R.color.colorBackground));
        Rect rect = new Rect();
        rect.left = 0;
        rect.top = 0;
        rect.right = getMeasuredWidth();
        rect.bottom = getMeasuredHeight();
        canvas.drawRect(rect, background);

        Paint textPaint = new Paint();
        textPaint.setTextSize(height * 0.4f);
        textPaint.setTextScaleX(width / height);
        textPaint.setColor(Color.BLACK);
        if (length == 0){
            canvas.drawText("没有数据", averageX*0.8f, averageY,textPaint);
            return;
        }

        //画坐标轴y
        Paint axis = new Paint();
        axis.setColor(Color.BLACK);
        canvas.drawLine(minX, minY, minX, maxY, axis);
        canvas.drawLine(minX + 1, minY, minX + 1, maxY, axis);
        canvas.drawLine(minX + 2, minY, minX + 2, maxY, axis);

        //计算最大、最小和均值
        float[] arrayData = new float[length];
        float sum = 0;
        float maxData = datalist[0];
        float minData = maxData;
        for (int i = 0; i < length; i++) {
            arrayData[i] = datalist[i];
            sum += arrayData[i];
            if (arrayData[i] > maxData)
                maxData = arrayData[i];
            if (arrayData[i] < minData)
                minData = arrayData[i];
        }
        float average = sum/length;


        if(key.equalsIgnoreCase(MainActivity.BMIKGM2)){
            //画BMI的18.5-24-28
            canvas.drawText("18.5-24为正常，大于24过重，大于28肥胖", width*1.5f, height*1.5f,textPaint);
            canvas.drawText("均值为："+Math.round(average * 10)/10, width*3.5f, height,textPaint);
        }
        else{
            canvas.drawText("均值为："+Math.round(average * 10)/10, width*3.5f, height*1.5f,textPaint);
        }

        float dataWidth = (maxX-minX)/length;
        float dataHeight = (maxY-minY)/(maxData-minData);
        if(maxData != minData){
            //画最大值和最小值
            textPaint.setColor(Color.RED);
            canvas.drawText(String.valueOf((float) Math.round(maxData * 10) / 10), 0, minY, textPaint);
            textPaint.setColor(Color.GREEN);
            canvas.drawText(String.valueOf((float) Math.round(minData * 10) / 10), 0, maxY, textPaint);

            averageY = maxY - (average-minData)*dataHeight;
            //画最大值线
            Paint linePaint1 = new Paint();
            linePaint1.setAntiAlias(true);
            linePaint1.setStyle(Paint.Style.STROKE);
            linePaint1.setStrokeWidth(2);
            PathEffect effects1 = new DashPathEffect(new float[]{8,8,8,8},1);
            linePaint1.setPathEffect(effects1);
            Path path1 = new Path();
            path1.moveTo(minX, minY);
            path1.lineTo(maxX, minY);
            linePaint1.setColor(Color.RED);
            canvas.drawPath(path1, linePaint1);//数字越大，y值越小
            //画最小值线
            Paint linePaint2 = new Paint();
            linePaint2.setAntiAlias(true);
            linePaint2.setStyle(Paint.Style.STROKE);
            linePaint2.setStrokeWidth(2);
            PathEffect effects2 = new DashPathEffect(new float[]{8,8,8,8},1);
            linePaint2.setPathEffect(effects2);
            Path path2 = new Path();
            path2.moveTo(minX, maxY);
            path2.lineTo(maxX, maxY);
            linePaint2.setColor(Color.GREEN);
            canvas.drawPath(path2, linePaint2);
        }

        float prex = 0;
        float prey = 0;
        float x = 0;
        float y = 0;
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        for (int i = 0; i < length; i++) {
            float data = arrayData[i];
            x = minX + dataWidth * (i + 1);
            if(maxData == minData)
                y = averageY;//数据都相同时，则画在均线上
            else{
                if(data == minData)
                    y = maxY;
                else if(data == maxData)
                    y = minY;
                else
                    y = maxY - (data-minData)*dataHeight;//大的数字画在上面
            }

            //画最后一个数字的线和数字
            if(i == length-1){
                Paint linePaint = new Paint();
                linePaint.setAntiAlias(true);
                linePaint.setStyle(Paint.Style.STROKE);
                linePaint.setStrokeWidth(2);
                PathEffect effects = new DashPathEffect(new float[]{8,8,8,8},1);
                linePaint.setPathEffect(effects);
                Path path = new Path();
                path.moveTo(minX, y);
                path.lineTo(x, y);
                linePaint.setColor(Color.BLACK);
                canvas.drawPath(path, linePaint);
                textPaint.setColor(Color.BLACK);
                canvas.drawText(String.valueOf((float) Math.round(data * 10) / 10), 0, y, textPaint);
            }

            paint.setColor(Color.BLACK);
            canvas.drawCircle(x, y, radius, paint);
            //画点之间的线，只有一个点则不画
            if (i >= 1) {
                if (data == arrayData[i - 1])
                    paint.setColor(Color.BLACK);
                else if (data > arrayData[i - 1])
                    paint.setColor(Color.RED);
                else
                    paint.setColor(Color.GREEN);
                canvas.drawLine(prex, prey, x, y, paint);
                canvas.drawLine(prex + 1, prey, x + 1, y, paint);
                canvas.drawLine(prex + 2, prey, x + 2, y, paint);
            }
            prex = x;
            prey = y;
        }
    }
}
