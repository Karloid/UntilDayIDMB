package com.krld.dmb;

import android.content.*;
import android.graphics.*;
import android.util.Log;
import android.view.*;

import java.text.DecimalFormat;
import java.util.*;
import java.math.*;

public class DaysView extends View {

    private static final String MY_TAG = "DMB_TAG";
    public static final int DELAY = 200;
    private Thread runner;
    private List<MyDay> myDays;

    private static final int RECT_SIZE = 43;

    private static final int MARGIN = 5;

    private int completedDayColor;

    private int notCompletedDayColor;

    private static final int DAYS_COUNT = 365;

    private static final int TEXT_COLOR = Color.BLACK;

    private static final int PROGRESS_LINE_MARGIN_Y = 20;

    private float x;

    private float y;

    private int currentDayColor;

    DaysView(Context context) {
        super(context);
        init();

    }

    public void startThread() {
        Log.d(MY_TAG, "start runner thread ****");
        runner = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                    refresh();
                }
            }
        });
        runner.start();
    }

    public void refresh() {
        init();
        postInvalidate();
    }

    private void init() {
        notCompletedDayColor = Color.GRAY;
        completedDayColor = Color.GREEN;
        currentDayColor = Color.RED;
        myDays = new ArrayList<MyDay>();

        MyDay.now = Calendar.getInstance();
        Calendar iterateCalendar = Calendar.getInstance();
        iterateCalendar.set(Calendar.YEAR, 2013);
        iterateCalendar.set(Calendar.MONTH, 6);
        iterateCalendar.set(Calendar.DAY_OF_MONTH, 5);
        iterateCalendar.set(Calendar.MINUTE, 0);
        iterateCalendar.set(Calendar.HOUR, 0);
        iterateCalendar.set(Calendar.SECOND, 1);
        iterateCalendar.set(Calendar.MILLISECOND, 1);
        MyDay.startDate = (Calendar) iterateCalendar.clone();
        for (int i = 0; i <= DAYS_COUNT; i++) {
            myDays.add(new MyDay(iterateCalendar));
            iterateCalendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        MyDay.endDate = (Calendar) iterateCalendar.clone();

    }

    @Override
    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        drawCalendar(canvas, paint);
    }

    private void drawCalendar(Canvas c, Paint p) {
        // TODO: Implement this method
        p.setTextSize(RECT_SIZE / 2);
        p.setTextAlign(Paint.Align.CENTER);
        boolean currentDaySaved = false;
        int currentDayIndex = 0;

        int col = 0;
        int row = 0;
        int maxCol = getWidth() / (RECT_SIZE);
        int translateY = 20;
        c.translate(0, translateY);
        List<MyDay> myDaysCloned = (List<MyDay>) ((ArrayList) myDays).clone();
        for (MyDay myDay : myDaysCloned) {
            if (myDay.isCompleted()) {
                if (myDay.day.get(Calendar.DAY_OF_YEAR) == MyDay.now.get(Calendar.DAY_OF_YEAR) &&
                        myDay.day.get(Calendar.YEAR) == MyDay.now.get(Calendar.YEAR)) {
                    p.setColor(currentDayColor);
                } else {
                    p.setColor(completedDayColor);
                }
            } else {

                if (!currentDaySaved) {
                    currentDayIndex = myDaysCloned.indexOf(myDay);
                    currentDaySaved = true;
                }
                p.setColor(notCompletedDayColor);

            }
            if (col != 0 && myDay.day.get(Calendar.DAY_OF_MONTH) == 1) {
                col = 0;
                row++;
                c.drawLine(MARGIN, row * (RECT_SIZE) + MARGIN / 2,
                        getWidth() - MARGIN, row * (RECT_SIZE) + MARGIN / 2, p);

                c.drawText((myDay.day.get(Calendar.MONTH) + 1) + "." + myDay.day.get(Calendar.YEAR),
                        getWidth() - 40, row * RECT_SIZE, p);

            }

            c.drawRect(col * RECT_SIZE + MARGIN,
                    row * RECT_SIZE + MARGIN, col * RECT_SIZE + RECT_SIZE
                    , row * RECT_SIZE + RECT_SIZE, p);

            p.setColor(TEXT_COLOR);
            c.drawText(myDay.day.get(Calendar.DAY_OF_MONTH) + "", col * RECT_SIZE
                            + MARGIN + RECT_SIZE / 2,
                    row * RECT_SIZE + MARGIN + (RECT_SIZE / 3) * 2, p
            );

            col++;
            if (col == maxCol) {
                col = 0;
                row++;
            }
        }
        p.setColor(completedDayColor);
        c.translate(0, -translateY);
        c.drawLine(MARGIN, RECT_SIZE / 2 + MARGIN / 2,
                getWidth() - MARGIN, RECT_SIZE / 2 + MARGIN / 2, p);

        c.drawText((myDaysCloned.get(0).day.get(Calendar.MONTH) + 1) + "." + myDaysCloned.get(0).day.get(Calendar.YEAR),
                getWidth() - 40, RECT_SIZE / 2, p);


        double progressPercent;

        long completedMills = MyDay.now.getTimeInMillis() - MyDay.startDate.getTimeInMillis();
        long allMillis = MyDay.endDate.getTimeInMillis() - MyDay.startDate.getTimeInMillis();
        progressPercent = (((double) completedMills) / allMillis) * 100;
        Log.d(MY_TAG, "completedMills: " + completedMills + " allMillis: " + allMillis);

        int progressPixel = (int) Math.round(((float) getWidth() / 100f) * progressPercent);

        p.setColor(notCompletedDayColor);
        p.setStrokeWidth(15);

        c.drawLine(0, getHeight() - PROGRESS_LINE_MARGIN_Y, getWidth(),
                getHeight() - PROGRESS_LINE_MARGIN_Y, p);
        c.drawText(DAYS_COUNT - currentDayIndex + "", getWidth() - 40, getHeight() - PROGRESS_LINE_MARGIN_Y - 20, p);
        p.setColor(completedDayColor);
        c.drawLine(0, getHeight() - PROGRESS_LINE_MARGIN_Y, progressPixel,
                getHeight() - PROGRESS_LINE_MARGIN_Y, p);
        c.drawText(currentDayIndex + "", 40, getHeight() - PROGRESS_LINE_MARGIN_Y - 20, p);
        p.setTextSize(35);
        c.drawText(new DecimalFormat("#0.000000").format(progressPercent) + "%", getWidth() / 2, getHeight() - PROGRESS_LINE_MARGIN_Y - 20, p);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //  refresh();
        x = event.getX();
        y = event.getY();
        return super.onTouchEvent(event);
    }


    public void stopThread() {
        Log.d(MY_TAG, "stop runner thread ----");
        runner.interrupt();
    }
}
