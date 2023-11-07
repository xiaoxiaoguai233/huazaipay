package org.xxpay.mch.common.util;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * excel读写工具类
 */
public class POIUtil {

    private static Logger logger = Logger.getLogger(POIUtil.class);
    private final static String xls = "xls";
    private final static String xlsx = "xlsx";

    /**
     * 读入excel文件，解析后返回
     *
     * @param file
     * @throws IOException
     */
    public static List<List<String>> readExcel(MultipartFile file) throws IOException {
        //检查文件  
        checkFile(file);
        //获得Workbook工作薄对象  
        Workbook workbook = getWorkBook(file);
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回  
        List<List<String>> list = new ArrayList<>();
        if (workbook != null) {
            for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
                //获得当前sheet工作表  
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if (sheet == null) {
                    continue;
                }
                //获得当前sheet的开始行  
                int firstRowNum = sheet.getFirstRowNum();
                //获得当前sheet的结束行  
                int lastRowNum = sheet.getLastRowNum();
                //循环所有行
                for (int rowNum = firstRowNum; rowNum <= lastRowNum; rowNum++) {
                    //获得当前行  
                    Row row = sheet.getRow(rowNum);
                    if (row == null) {
                        continue;
                    }
                    //获得当前行的开始列  
                    int firstCellNum = row.getFirstCellNum();
                    //获得当前行的列数  
                    int lastCellNum = row.getPhysicalNumberOfCells();
                    //String[] cells = new String[row.getPhysicalNumberOfCells()];
                    List<String> cellList = new LinkedList<>();
                    //循环当前行  
                    for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
                        Cell cell = row.getCell(cellNum);
                        //cells[cellNum] = getCellValue(cell);
                        cellList.add(getCellValue(cell));
                    }
                    list.add(cellList);
                }
            }
            workbook.close();
        }
        return list;
    }

    public static void checkFile(MultipartFile file) throws IOException {
        //判断文件是否存在  
        if (null == file) {
            logger.error("文件不存在！");
            throw new FileNotFoundException("文件不存在！");
        }
        //获得文件名  
        String fileName = file.getOriginalFilename();
        //判断文件是否是excel文件  
        if (!fileName.endsWith(xls) && !fileName.endsWith(xlsx)) {
            logger.error(fileName + "不是excel文件");
            throw new IOException(fileName + "不是excel文件");
        }
    }

    public static Workbook getWorkBook(MultipartFile file) {
        //获得文件名  
        String fileName = file.getOriginalFilename();
        //创建Workbook工作薄对象，表示整个excel  
        Workbook workbook = null;
        try {
            //获取excel文件的io流  
            InputStream is = file.getInputStream();
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象  
            if (fileName.endsWith(xls)) {
                //2003  
                workbook = new HSSFWorkbook(is);
            } else if (fileName.endsWith(xlsx)) {
                //2007  
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        return workbook;
    }

    public static String getCellValue(Cell cell) {
        String cellValue = "";
        if (cell == null) {
            return cellValue;
        }
        //把数字当成String来读，避免出现1读成1.0的情况  
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
        //判断数据的类型  
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC: //数字  
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_STRING: //字符串  
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN: //Boolean  
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA: //公式  
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_BLANK: //空值   
                cellValue = "";
                break;
            case Cell.CELL_TYPE_ERROR: //故障  
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }

    public static boolean campaignsListCsvDown(String header) {
        try {
            //CSV文件生成
            HSSFWorkbook workbook = new HSSFWorkbook();

            HSSFSheet sheet = workbook.createSheet("campaignsListCsvDown");

            HSSFCellStyle style = workbook.createCellStyle();           // 样式对象
            style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);  // 垂直
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);             // 水平

            HSSFRow row = sheet.createRow((short) 0);
            HSSFRow row1 = sheet.createRow((short) 1);

            // 写入头部信息
            String[] split = header.split(",");
            for (int index = 0; index < split.length; index++) {
                row.createCell(index).setCellValue(split[index]);
            }

            //从第3行开始循环写数据
            int rowNum = 2;
//            for (Campaigns campaigns : campaignsList) {
//                HSSFRow row2 = sheet.createRow(rowNum);
//                row2.createCell(0).setCellValue(campaigns.getCampaignsName());
//                row2.createCell(1).setCellValue(DateFormatHelper.formatToMdy(campaigns.getStartTime()));
//                row2.createCell(2).setCellValue(campaigns.getDayNum() + "天");
//                row2.createCell(3).setCellValue(campaigns.getStoreNum() + "家");
//
//                row2.createCell(4).setCellValue("$" + campaigns.getSpend());
//                row2.createCell(5).setCellValue(campaigns.getRateChange() + "%");
//                row2.createCell(6).setCellValue("不涉及");
//
//                if (campaigns.getCostAvg() > 0) {
//                    row2.createCell(7).setCellValue(campaigns.getCostAvg());
//                } else {
//                    row2.createCell(7).setCellValue("不涉及");
//                }
//
//                rowNum++;
//            }


            File file = new File("D://test" + ".xls");
            String fileName = file.getPath();

            workbook.write(new FileOutputStream(file));


            return true;
        } catch (Exception e) {
//            log.error("", e);
            return false;
        }
    }

    public static void main(String[] args) {
        campaignsListCsvDown("标ti,内容,其他,搜索");
    }


}