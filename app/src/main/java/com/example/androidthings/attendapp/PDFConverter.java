package com.example.androidthings.attendapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;

import com.example.androidthings.attendapp.models.PdfDetails;
import com.example.androidthings.attendapp.models.RollNumber;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PDFConverter {

    //    A4 size is 210 x 297 mm, which gives in points:
//
//        210 / 25.4 * 72 = 595.3 pt
//        297 / 25.4 * 72 = 841.9 pt

    int pageWidth = 595;
    int pageHeight = 842;
    float left = 24f;
    float top = 24f;
    float fromLeft1 = 20f;
    float fromTop = 23f;
    float lineSpace = 5f;

    public PdfDocument.Page createPdf(PdfDetails pdfDetails, ArrayList<RollNumber> rollNumbers, PdfDocument document, int pageNumber, int serialNumber) {


        PdfDocument.PageInfo pageInfo1 = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
        PdfDocument.Page page = document.startPage(pageInfo1);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        getPageTitle(pdfDetails, canvas, paint, left, top,pageNumber);

        float incrementTop = 7;
        float totalFromTop;
        float totalFromTop1;
        float incrementTop1 = 7;
        float totalFromTop2;
        float incrementTop2 = 7;

        int sNo;
        for (int i = 0; i < rollNumbers.size(); i++) {

            sNo = i + 1;
            totalFromTop = fromTop * incrementTop;
            ++incrementTop;

            if (rollNumbers.size() <= 30) {
                if (i == 0) {
                    canvas.drawText("SNo", fromLeft1 / 4f, fromTop * 6, paint);
                    canvas.drawText("ROLL NUMBERS", fromLeft1 * 3f, fromTop * 6, paint);
                    canvas.drawText("At", (594f) - 20f, fromTop * 6, paint);
                    //horizontal line label
                    canvas.drawLine(1f, fromTop * 6f + lineSpace, 594f, fromTop * 6f + lineSpace, paint);
                }
                if (sNo != 30) {
                    canvas.drawLine(1f, totalFromTop + lineSpace, 594f, totalFromTop + lineSpace, paint);
                }
                //No. Line
                canvas.drawLine(fromLeft1 + 15, top * 5f, fromLeft1 + 15, 841f, paint);
                //At vertical Line
                canvas.drawLine((594f) - 30f, top * 5f, (594f) - 30f, 841f, paint);
                canvas.drawText(String.valueOf(serialNumber), fromLeft1 / 2f, totalFromTop, paint);
                canvas.drawText(rollNumbers.get(i).getRollNumber(), fromLeft1 * 3, totalFromTop, paint);
                if (rollNumbers.get(i).getAttendance().equals(Constants.PRESENT)) {
                    canvas.drawText("P", (594f) - 20f, totalFromTop, paint);
                } else {
                    canvas.drawText("X", (594) - 20f, totalFromTop, paint);
                }
            }

            if (rollNumbers.size() >= 31) {
                if (i == 0) {
                    canvas.drawText("SNo", fromLeft1 / 4f, fromTop * 6, paint);
                    canvas.drawText("ROLL NUMBERS", fromLeft1 * 3f, fromTop * 6, paint);
                    canvas.drawText("At", (594f / 2f) - 20f, fromTop * 6, paint);
                    //horizontal line label
                    canvas.drawText("SNo", (594f / 2f) + fromLeft1 / 4f, fromTop * 6, paint);
                    canvas.drawText("ROLL NUMBERS", (594f / 2f) + fromLeft1 * 3f, fromTop * 6, paint);
                    canvas.drawText("At", (594f) - 20f, fromTop * 6, paint);
                    canvas.drawLine(1f, fromTop * 6f + lineSpace, 594f, fromTop * 6f + lineSpace, paint);
                }
                //  sNo line l1
                canvas.drawLine(fromLeft1 + 15, top * 5f, fromLeft1 + 15, 841f, paint);
                //at Line l1
                canvas.drawLine((594f / 2f) - 30f, top * 5f, (594f / 2f) - 30f, 841f, paint);
                //divider line
                canvas.drawLine(594f / 2f, top * 5f, 594f / 2f, 841f, paint);
                //  sNo line l2
                canvas.drawLine((594f / 2f) + fromLeft1 + 15f, top * 5f, (594f / 2f) + fromLeft1 + 15f, 841f, paint);
                // At line l2
                canvas.drawLine((594f) - 30f, top * 5f, (594f) - 30f, 841f, paint);

                if (sNo != 30 | sNo != 30 && sNo != 60) {
                    canvas.drawLine(1f, totalFromTop + lineSpace, 594f, totalFromTop + lineSpace, paint);
                }
                if (sNo <= 30) {
                    totalFromTop1 = fromTop * incrementTop1;
                    ++incrementTop1;
                    canvas.drawText(String.valueOf(serialNumber), fromLeft1 / 2f, totalFromTop1, paint);
                    canvas.drawText(rollNumbers.get(i).getRollNumber(), fromLeft1 * 3, totalFromTop1, paint);

                    if (rollNumbers.get(i).getAttendance().equals(Constants.PRESENT)) {
                        canvas.drawText("P", (594f / 2f) - 20f, totalFromTop1, paint);
                    } else {
                        canvas.drawText("X", (594f / 2f) - 20f, totalFromTop1, paint);
                    }
                }
                if (sNo >= 31) {
                    totalFromTop2 = fromTop * incrementTop2;
                    ++incrementTop2;
                    canvas.drawText(String.valueOf(serialNumber), (594f / 2f) + fromLeft1 / 2f, totalFromTop2, paint);
                    canvas.drawText(rollNumbers.get(i).getRollNumber(), (594f / 2f) + fromLeft1 * 3, totalFromTop2, paint);
                    if (rollNumbers.get(i).getAttendance().equals(Constants.PRESENT)) {
                        canvas.drawText("P", (594f) - 20f, totalFromTop2, paint);
                    } else {
                        canvas.drawText("X", (594) - 20f, totalFromTop2, paint);
                    }
                }
            }
            serialNumber++;
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        canvas.drawRect(1f, 1f, 1f, 841f, paint);
        canvas.drawRect(1f, 1f, 594f, 841f, paint);
        canvas.drawLine(1f, top * 5f, 594f, top * 5f, paint);
        return page;
    }

    private void getPageTitle(PdfDetails pdfDetails, Canvas canvas1, Paint paint, float left, float top,int pageNumber) {
        if (pageNumber==1){

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
            String currentDateAndTime = sdf.format(new Date());
            canvas1.drawText("Pdf Generated: ", pageWidth-100f, top * 1.7f, paint);
            canvas1.drawText(currentDateAndTime, pageWidth-130f, top * 2.4f, paint);
        }
        canvas1.drawText("Page No: " +pageNumber, pageWidth-70f, top * 0.7f, paint);
        canvas1.drawText("Teacher Name: " + pdfDetails.getTeacherName(), 24f, top *0.7f, paint);
        canvas1.drawText("Batch : " + pdfDetails.getBatchName(), 24f, top * 1.7f, paint);
        canvas1.drawText("Subject: " + pdfDetails.getSubject(), left, top * 2.7f, paint);
        canvas1.drawText("Duration: " + pdfDetails.getDuration(), left, top * 3.7f, paint);
        canvas1.drawText("Date: " + pdfDetails.getDate(), left, top * 4.7f, paint);
    }


}
