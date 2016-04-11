/*
 * Copyright (C) 2016 Ferid Cafer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ferid.app.classroom.utility;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * Created by ferid.cafer on 7/13/2015.
 */
public class ExcelStyleManager {

    private static volatile HSSFCellStyle cellStyleHeader;
    private static volatile HSSFCellStyle cellStyleContent;

    public ExcelStyleManager() {
        cellStyleHeader = null;
        cellStyleContent = null;
    }

    /**
     * Get header cell style
     * @param wb
     * @return
     */
    private static HSSFCellStyle getHeaderCellStyleInstance(HSSFWorkbook wb) {
        if (cellStyleHeader == null) {
            synchronized (ExcelStyleManager.class) {
                if (cellStyleHeader == null) {
                    cellStyleHeader = wb.createCellStyle();
                }
            }
        }

        return cellStyleHeader;
    }

    /**
     * Get content cell style
     * @param wb
     * @return
     */
    private static HSSFCellStyle getContentCellStyleInstance(HSSFWorkbook wb) {
        if (cellStyleContent == null) {
            synchronized (ExcelStyleManager.class) {
                if (cellStyleContent == null) {
                    cellStyleContent = wb.createCellStyle();
                }
            }
        }

        return cellStyleContent;
    }

    /**
     * Header cell style (dates)
     * @param wb HSSFWorkbook
     * @return HSSFCellStyle
     */
    public static HSSFCellStyle getHeaderCellStyle(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = getHeaderCellStyleInstance(wb);

        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 8);
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        cellStyle.setFont(font);

        cellStyle.setWrapText(true);

        cellStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());

        return cellStyle;
    }

    /**
     * Content cell style (presence)
     * @return HSSFCellStyle
     */
    public static HSSFCellStyle getContentCellStyle(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = getContentCellStyleInstance(wb);

        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 8);
        cellStyle.setFont(font);

        cellStyle.setWrapText(true);

        cellStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());

        return cellStyle;
    }
}