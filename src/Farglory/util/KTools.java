/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Farglory.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.FileItem;

/**
 *
 * @author Kyle
 */
public class KTools
{

    private Logger log = Logger.getLogger(this.getClass()); //TOMCAT提供的LOG紀錄

    public KTools()
    {
    }

    public String Array2String(String[] arr)
    {
        String str = "";

        if (arr.length > 0)
        {
            for (int i = 0; i < arr.length; i++)
            {
                if (i != 0)
                {
                    str += ",";
                }
                str += arr[i];
            }
        }

        return str;
    }

    public String List2String(List<Object> arr)
    {
        String str = "";

        if (arr.size() > 0)
        {
            for (int i = 0; i < arr.size(); i++)
            {
                if (i != 0)
                {
                    str += ",";
                }
                str += arr.get(i);
            }
        }

        return str;
    }

    public boolean ConvertImg2PNG(String fromPath, String savePath, int newWidth, int newHeight, boolean chkRealSize)
    {
        BufferedImage imageS = null;
        BufferedImage imageR = null;
        File fileOut = null;
        boolean check = true;

        try
        {
            InputStream Uploadimg = new FileInputStream(fromPath);
            imageS = ImageIO.read(Uploadimg);

            double sw = imageS.getWidth();
            double sh = imageS.getHeight();
            double rw, rh;

            if (chkRealSize == true)
            {
                if (sw > sh)
                {
                    rw = (newWidth / sw) * sw;
                    rh = (newWidth / sw) * sh;
                }
                else
                {
                    rw = (newHeight / sh) * sw;
                    rh = (newHeight / sh) * sh;
                }
            }
            else
            {
                rw = newWidth;
                rh = newHeight;
            }

            Image new_img = imageS.getScaledInstance((int) rw, (int) rh, Image.SCALE_SMOOTH);
            
            imageR = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            
            Graphics2D g = imageR.createGraphics();
            imageR = g.getDeviceConfiguration().createCompatibleImage(newWidth, newHeight, Transparency.TRANSLUCENT);
            g= imageR.createGraphics();
            
            g.drawImage(new_img, (int)((newWidth/2)-(rw/2)), (int)((newHeight/2)-(rh/2)), (int) rw, (int) rh, null);
            
            //PNG轉JPG 才會用到Alpha圖層
            //g.setComposite(AlphaComposite.Src);
            //g.drawImage(new_img, 0, 0, (int) rw, (int) rh, null);

            //關閉圖片
            g.dispose();
            imageS.flush();
            imageR.flush();
            new_img.flush();
            Uploadimg.close();

            String FileName = fromPath.substring(fromPath.lastIndexOf("/") + 1).trim();
            String headFileName = FileName.substring(0 , FileName.lastIndexOf("."));
            String auxFileName = ".PNG";
            
            fileOut = new File(savePath);
            if (!fileOut.exists())
            {
                fileOut.mkdirs();
                log.info("資料匣" + savePath + "已創建");
            }

            fileOut = new File(savePath + headFileName + auxFileName);
            ImageIO.write(imageR, "PNG", fileOut);

            log.debug("Change Img Size OK");
        }
        catch (Exception ex)
        {
            log.error("chgImagePx Has Error = " + ex);
            check = false;
        }
        return check;
    }

    //改變圖片大小
    //上傳檔案用
    //直接寫入
    //fromPath : 原始圖檔路徑
    //savePath : 輸出檔案路徑(不含檔名)
    //希望改變的寬高
    //igoRealSize : 判斷圖片比例? Y/N
    public boolean chgImagePx2(String fromPath, String savePath, int newWidth, int newHeight, boolean chkRealSize)
    {
        BufferedImage imageS = null;
        BufferedImage imageR = null;
        File fileOut = null;
        Color backColor = Color.white;
        boolean check = true;

        try
        {
            InputStream Uploadimg = new FileInputStream(fromPath);
            imageS = ImageIO.read(Uploadimg);

            double sw = imageS.getWidth();
            double sh = imageS.getHeight();
            double rw, rh;

            if (chkRealSize == true)
            {
                if (sw > sh)
                {
                    rw = (newWidth / sw) * sw;
                    rh = (newWidth / sw) * sh;
                }
                else
                {
                    rw = (newHeight / sh) * sw;
                    rh = (newHeight / sh) * sh;
                }
            }
            else
            {
                rw = newWidth;
                rh = newHeight;
            }

            //放大图像不会导致失真，而缩小图像将不可避免的失真。
            //Java中也同样是这样。
            //但java提供了4个缩放的微调选项。
            //image.SCALE_SMOOTH //平滑优先
            //image.SCALE_FAST//速度优先
            //image.SCALE_AREA_AVERAGING //区域均值
            //image.SCALE_REPLICATE //像素复制型缩放
            //image.SCALE_DEFAULT //默认缩放模式
            Image new_img = imageS.getScaledInstance((int) rw, (int) rh, Image.SCALE_SMOOTH);
            imageR = new BufferedImage((int) rw, (int) rh, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = (Graphics2D) imageR.getGraphics();
            g.setBackground(backColor);
            g.clearRect(0, 0, (int) rw, (int) rh);
            g.drawImage(new_img, 0, 0, (int) rw, (int) rh, null);

            //關閉圖片
            g.dispose();
            imageS.flush();
            imageR.flush();
            new_img.flush();
            Uploadimg.close();

            String FileName = fromPath.substring(fromPath.lastIndexOf("/") + 1).trim();
            String auxFileName = FileName.substring(FileName.lastIndexOf(".") + 1, FileName.length());

            fileOut = new File(savePath);
            if (!fileOut.exists())
            {
                fileOut.mkdirs();
                log.info("資料匣" + savePath + "已創建");
            }

            fileOut = new File(savePath + FileName);
            ImageIO.write(imageR, auxFileName, fileOut);

            log.debug("Change Img Size OK");
        }
        catch (Exception ex)
        {
            log.error("chgImagePx Has Error = " + ex);
            check = false;
        }
        return check;
    }

    //改變圖片大小
    //輸出 BufferedImage
    //後續呼叫者處理
    //file : 要改變的圖檔
    //FilePath : 輸出檔案路徑(不含檔名)
    //希望改變的寬高
    public BufferedImage chgImagePx(FileItem file, int newWidth, int newHeight)
    {
        BufferedImage imageS = null;
        BufferedImage imageR = null;
        File fileOut = null;
        Color backColor = Color.white;
        boolean check = true;

        try
        {
            InputStream Uploadimg = file.getInputStream();
            imageS = ImageIO.read(Uploadimg);

            double sw = imageS.getWidth();
            double sh = imageS.getHeight();
            double rw, rh;

            if (sw > sh)
            {
                rw = (newWidth / sw) * sw;
                rh = (newWidth / sw) * sh;
            }
            else
            {
                rw = (newHeight / sh) * sw;
                rh = (newHeight / sh) * sh;
            }

            imageR = new BufferedImage((int) rw, (int) rh, BufferedImage.TYPE_INT_RGB);

            Graphics2D g = (Graphics2D) imageR.getGraphics();

            g.setBackground(backColor);
            g.clearRect(0, 0, (int) rw, (int) rh);

            g.drawImage(imageS, 0, 0, (int) rw, (int) rh, null);

            //關閉圖片
            g.dispose();
            imageS.flush();
            imageR.flush();

            log.debug("OK");
        }
        catch (Exception ex)
        {
            log.error("chgImagePx Has Error = " + ex);
            check = false;
        }
    return imageR;
  }

}
