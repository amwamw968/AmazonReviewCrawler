package com.poof.crawler.reviews;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * @author wilkey 
 * @mail admin@wilkey.vip
 * @Date 2017年1月10日 下午4:28:19
 */
public class Mergexls {

	public static void mergeExcelFiles(File file, List<FileInputStream> list) throws IOException {
		HSSFWorkbook book = new HSSFWorkbook();
		HSSFSheet sheet = book.createSheet(file.getName());

		for (FileInputStream fin : list) {
			HSSFWorkbook b = new HSSFWorkbook(fin);
			for (int i = 0; i < b.getNumberOfSheets(); i++) {
				copySheets(book, sheet, b.getSheetAt(i));
			}
		}

		try {
			writeFile(book, file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected static void writeFile(HSSFWorkbook book, File file) throws Exception {
		FileOutputStream out = new FileOutputStream(file);
		book.write(out);
		out.close();
	}

	private static void copySheets(HSSFWorkbook newWorkbook, HSSFSheet newSheet, HSSFSheet sheet) {
		copySheets(newWorkbook, newSheet, sheet, true);
	}

	private static void copySheets(HSSFWorkbook newWorkbook, HSSFSheet newSheet, HSSFSheet sheet, boolean copyStyle) {
		int newRownumber = newSheet.getLastRowNum() + 1;
		int maxColumnNum = 0;
		Map<Integer, HSSFCellStyle> styleMap = (copyStyle) ? new HashMap<Integer, HSSFCellStyle>() : null;

		for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
			HSSFRow srcRow = sheet.getRow(i);
			HSSFRow destRow = newSheet.createRow(i + newRownumber);
			if (srcRow != null) {
				copyRow(newWorkbook, sheet, newSheet, srcRow, destRow, styleMap);
				if (srcRow.getLastCellNum() > maxColumnNum) {
					maxColumnNum = srcRow.getLastCellNum();
				}
			}
		}
		for (int i = 0; i <= maxColumnNum; i++) {
			newSheet.setColumnWidth(i, sheet.getColumnWidth(i));
		}
	}

	public static void copyRow(HSSFWorkbook newWorkbook, HSSFSheet srcSheet, HSSFSheet destSheet, HSSFRow srcRow, HSSFRow destRow, Map<Integer, HSSFCellStyle> styleMap) {
		destRow.setHeight(srcRow.getHeight());
		for (int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); j++) {
			HSSFCell oldCell = srcRow.getCell(j);
			HSSFCell newCell = destRow.getCell(j);
			if (oldCell != null) {
				if (newCell == null) {
					newCell = destRow.createCell(j);
				}
				copyCell(newWorkbook, oldCell, newCell, styleMap);
			}
		}
	}

	public static void copyCell(HSSFWorkbook newWorkbook, HSSFCell oldCell, HSSFCell newCell, Map<Integer, HSSFCellStyle> styleMap) {
		if (styleMap != null) {
			int stHashCode = oldCell.getCellStyle().hashCode();
			HSSFCellStyle newCellStyle = styleMap.get(stHashCode);
			if (newCellStyle == null) {
				newCellStyle = newWorkbook.createCellStyle();
				newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
				styleMap.put(stHashCode, newCellStyle);
			}
			newCell.setCellStyle(newCellStyle);
		}
		switch (oldCell.getCellType()) {
		case HSSFCell.CELL_TYPE_STRING:
			newCell.setCellValue(oldCell.getRichStringCellValue());
			break;
		case HSSFCell.CELL_TYPE_NUMERIC:
			newCell.setCellValue(oldCell.getNumericCellValue());
			break;
		case HSSFCell.CELL_TYPE_BLANK:
			newCell.setCellType(HSSFCell.CELL_TYPE_BLANK);
			break;
		case HSSFCell.CELL_TYPE_BOOLEAN:
			newCell.setCellValue(oldCell.getBooleanCellValue());
			break;
		case HSSFCell.CELL_TYPE_ERROR:
			newCell.setCellErrorValue(oldCell.getErrorCellValue());
			break;
		case HSSFCell.CELL_TYPE_FORMULA:
			newCell.setCellFormula(oldCell.getCellFormula());
			break;
		default:
			break;
		}
	}

}
