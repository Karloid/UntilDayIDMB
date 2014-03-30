package com.krld.dmb;

import android.content.*;
import android.graphics.*;
import android.util.Log;
import android.view.*;

import java.text.DecimalFormat;
import java.util.*;

public class DaysView extends View {

    private static final String MY_TAG = "DMB_TAG";
    public static final int DELAY = 200;
    public static final int TEXT_LINE_PROGRESS_SIZE = 10;
    private Thread runner;
    private List<MyDay> myDays;

    private static final int RECT_SIZE = 43;

    private static final int MARGIN = 5;

    private int completedDayColor;

    private int notCompletedDayColor;

    private static final int DAYS_COUNT = 365;

    private static final int TEXT_COLOR = Color.BLACK;

    private static final int PROGRESS_LINE_MARGIN_Y = 45;

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
        MyDay.endDate.set(Calendar.HOUR, 23);
        MyDay.endDate.set(Calendar.MINUTE, 59);
        MyDay.endDate.set(Calendar.SECOND, 59);

    }

    @Override
    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        drawCalendar(canvas, paint);
    }

    private void drawCalendar(Canvas c, Paint p) {
        // TODO: Implement this method

        boolean currentDaySaved = false;
        int currentDayIndex = 0;

        drawCurrentDayProgress(c, p);

        p.setTextSize(RECT_SIZE / 2);
        p.setTextAlign(Paint.Align.CENTER);

        int col = 0;
        int row = 0;
        int maxCol = getWidth() / (RECT_SIZE);
        int translateY = 20;
        c.translate(0, translateY);
        List<MyDay> myDaysCopied = (List<MyDay>) ((ArrayList) myDays).clone();
        for (MyDay myDay : myDaysCopied) {
            if (myDay.isCompleted()) {
                if (myDay.day.get(Calendar.DAY_OF_YEAR) == MyDay.now.get(Calendar.DAY_OF_YEAR) &&
                        myDay.day.get(Calendar.YEAR) == MyDay.now.get(Calendar.YEAR)) {
                    p.setColor(currentDayColor);
                } else {
                    p.setColor(completedDayColor);
                }
            } else {

                if (!currentDaySaved) {
                    currentDayIndex = myDaysCopied.indexOf(myDay);
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

        c.drawText((myDaysCopied.get(0).day.get(Calendar.MONTH) + 1) + "." + myDaysCopied.get(0).day.get(Calendar.YEAR),
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
        int daysCompleted = currentDayIndex - 1;
        int daysEstimated = DAYS_COUNT - daysCompleted;
        c.drawText(daysEstimated + "", getWidth() - 40, getHeight() - PROGRESS_LINE_MARGIN_Y - 20, p);
        p.setColor(completedDayColor);
        c.drawLine(0, getHeight() - PROGRESS_LINE_MARGIN_Y, progressPixel,
                getHeight() - PROGRESS_LINE_MARGIN_Y, p);
        c.drawText(daysCompleted + "", 40, getHeight() - PROGRESS_LINE_MARGIN_Y - 20, p);
        p.setTextSize(35);
        c.drawText(new DecimalFormat("#0.000000").format(progressPercent) + "%", getWidth() / 2, getHeight() - PROGRESS_LINE_MARGIN_Y - 20, p);


    }

    private void drawCurrentDayProgress(Canvas c, Paint p) {

        int completedSeconds;
        int allSeconds;
        double partCompleted;

        completedSeconds = MyDay.now.get(Calendar.HOUR_OF_DAY) * 60 * 60 + MyDay.now.get(Calendar.MINUTE) * 60 + MyDay.now.get(Calendar.SECOND);
        allSeconds = 24 * 60 * 60;
        partCompleted = (completedSeconds * 1f) / (allSeconds * 1f);


        drawProgressLine(c, p, partCompleted, getHeight() - 25, "day");

        completedSeconds = MyDay.now.get(Calendar.MINUTE) * 60 + MyDay.now.get(Calendar.SECOND);
        allSeconds = 60 * 60;
        partCompleted = (completedSeconds * 1f) / (allSeconds * 1f);


        drawProgressLine(c, p, partCompleted, getHeight() - 13, "hour");

        completedSeconds = MyDay.now.get(Calendar.SECOND);
        allSeconds = 60;
        partCompleted = (completedSeconds * 1f) / (allSeconds * 1f);


        drawProgressLine(c, p, partCompleted, getHeight() - 1, "minute");


    }

    private void drawProgressLine(Canvas c, Paint p, double partCompleted, int lineY, String sign) {
        p.setAlpha(150);
        p.setStrokeWidth(2);
        p.setColor(Color.GRAY);
        c.drawLine(0, lineY, getWidth(), lineY, p);
        p.setColor(Color.GREEN);
        c.drawLine(0, lineY, (int) (getWidth() * partCompleted), lineY, p);
        p.setTextAlign(Paint.Align.RIGHT);
        p.setTextSize(TEXT_LINE_PROGRESS_SIZE);
        c.drawText(sign, getWidth(), lineY - 2, p);
        p.setStrokeWidth(1);
        p.setAlpha(255);

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
