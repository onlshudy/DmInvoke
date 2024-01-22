package com.tjut.util;

import com.jacob.com.Variant;
import com.tjut.DmConfig;
import com.tjut.bean.Point;
import com.tjut.enums.KeyBorad;
import com.tjut.utils.CommonUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

@Repository
@Log4j2
public class WowUtil {

    private DmConfig config;

    private static final long ONE_MINLIONS = 2 * 1000;

    private static final float PI = 3.1415F;

    private static final int ANGLE = 360;

    private static int MYANGLE = 0;

    private static Variant[] windowAddress = new Variant[4];

    private static Variant[] address;
    private static long TimeMillis = System.currentTimeMillis();


    static {
        address = new Variant[]{
                new Variant(70,true),
                new Variant(83,true),
                new Variant(158,true),
                new Variant(95,true)
        };
    }


    private final static String DM_NUMB_SOFT = "env/dm/dm_soft.txt";
//      x1   变参指针: 返回窗口客户区左上角X坐标
//     * @param y1   变参指针: 返回窗口客户区左上角Y坐标
//     * @param x2   变参指针: 返回窗口客户区右下角X坐标
//     * @param y2   变参指针: 返回窗口客户区右下角Y坐标
    public WowUtil(@Autowired DmConfig config) {
        this.config = config;
        int hwnd = CommonUtils.FindWindow("魔兽世界");
        for (int i = 0; i < windowAddress.length ; i++){
            windowAddress[i] = new Variant(0,true);
        }
        config.getClientRect(hwnd,windowAddress[0],windowAddress[1],windowAddress[2],windowAddress[3]);
        log.info("魔兽世界窗口位置: x1: {},y1: {},x2: {},y2: {}",windowAddress[0].getInt(),windowAddress[1].getInt(),windowAddress[2].getInt(),windowAddress[3].getInt());
    }

    //绑定窗口
//    public void BindWindow(String windowName){
//        int success = Dispatch.call(dm,"Reg",license,version).getInt();
//    }

    //识别坐标
    public boolean OcrAddressNumber(Point point){
        config.setDict(0,DM_NUMB_SOFT);

        String ocr = config.ocr(windowAddress[0].getInt()+address[0].getIntRef(), windowAddress[1].getInt()+address[1].getIntRef(), windowAddress[0].getInt()+address[2].getIntRef(), windowAddress[1].getInt()+address[3].getIntRef(), "cccccc-000000|ffffff-000000|bbbbbb-000000|999999-000000|dddddd-000000|aaaaaa-000000|eeeeee-000000", 0.85);
        if (ocr.length() != 8){
            log.warn("获取坐标错误：{}",ocr);
            return false;
        }else {
            point.setAddX(Float.parseFloat(ocr.substring(0, 4))/100);
            point.setAddY(Float.parseFloat(ocr.substring(ocr.length() - 4))/100);
            log.info("角色当前位置：x {}，y {}",point.getAddX(),point.getAddY());
            return true;
        }
    }

    //寻路
    public void run(Point point){
        //获取当前坐标；
        Point start = new Point();
        OcrAddressNumber(start);
        System.out.println("x:"+start.getAddX()+"__y:"+start.getAddY());
        //角度
        double angle = calculateAngle(start.getAddX(), start.getAddY(), point.getAddX(), point.getAddY());
        log.info("开始寻路的角度:{}",angle);
        config.keyDown(KeyBorad.W.getVk_code());
        boolean flag = true;
        while (!arrive(point.getAddX(), point.getAddY(), start.getAddX(), start.getAddY())){
            Point temp = new Point();
            try {
                Thread.sleep(300);
                TimeMillis-=300;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (!OcrAddressNumber(temp)){
                continue;
            }
            double angle1 = calculateAngle(start.getAddX(), start.getAddY(), temp.getAddX(), temp.getAddY());
            double end = calculateAngle(temp.getAddX(), temp.getAddY(), point.getAddX(), point.getAddY());

            log.info("寻路时的角度:{}",angle1);
            log.info("寻路时终点的角度:{}",end);
            if (TimeMillis < System.currentTimeMillis()){
                if (angle1 == -1){
                    continue;
                }
                KeyBorad keyBorad = AdjustAngle(angle, angle1,end);
                if (!ObjectUtils.isEmpty(keyBorad)){
                    if (flag){
                        double agnle = Math.abs((end - angle1)/ANGLE) ;
                        log.info("旋转角度比例:{}",agnle);
                        TimeMillis = (long) (System.currentTimeMillis() + agnle*ONE_MINLIONS);
                        flag = false;
                    }
                    config.keyDown(keyBorad.getVk_code());
                    try {
                        Thread.sleep(100);
                        TimeMillis-=100;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (System.currentTimeMillis() >= TimeMillis ){
                        config.keyUp(keyBorad.getVk_code());
                        flag = true;
                    }
                }
            }

            start = temp;
        }
    }

    public boolean arrive(float x1,float y1,float x2, float y2){
        if (Math.abs(x1 - x2) <= 0.5 && Math.abs(y1 - y2) <= 0.5){
            config.keyUp(KeyBorad.W.getVk_code());
            log.info("已到达位置:x {},y{}",x2,y2);
            return true;
        }
        return false;
    }

    private static double calculateAngle(double x1, double y1, double x2, double y2) {
        double deltaX = x2 - x1;
        double deltaY = y2 - y1;
        // 使用反正切函数计算角度
        double angleInRadians = Math.atan2(deltaY, deltaX);
        // 将弧度转换为度数
        double angleInDegrees = Math.toDegrees(angleInRadians);
        // 角度范围为 [0, 360)，可以根据实际需求调整
        if (angleInDegrees < 0) {
            angleInDegrees += 360;
        }
        if (Math.abs(deltaX) < 0.1 && Math.abs(deltaY) < 0.1){
            return -1;
        }
        return angleInDegrees;
    }


    private static KeyBorad AdjustAngle(double start, double end,double ends){
        double angle = Math.abs(end - ends);
        if (Math.abs(angle - 0) <= 20){
            return null;
        }
        if (end > ends ){
            return KeyBorad.A;
        }else {
            return KeyBorad.D;
        }

    }

}
