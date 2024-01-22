package com.tjut;

import com.jacob.activeX.ActiveXComponent;
import com.tjut.bean.Point;
import com.tjut.config.springConfig;
import com.tjut.util.WowUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
public class Application {
    public static void main(String[] args) {
        DmRegHandler.exec();
        ApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(springConfig.class);
//        DmConfig bean = applicationContext.getBean(DmConfig.class);
//        ActiveXComponent dm = bean.getDm();
        WowUtil bean = applicationContext.getBean(WowUtil.class);
//        bean.OcrAddressNumber();
        bean.run(new Point(45.67F, 68.67F,""));

    }
}