package com.tjut;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;

@Log4j2
public class DmRegHandler {

    public interface DmReg extends Library{
        DmReg INSTANCE = Native.load("DmReg.dll", DmReg.class);
        /**
         * SetDllPathA  字符串(Ascii码表示插件所在的路径),整数(0表示STA，1表示MTA)
         */
        void SetDllPathA(String format, int args);
        /**
         * SetDllPathW  字符串(Unicode码表示插件所在的路径),整数(0表示STA，1表示MTA)
         */
        void SetDllPathW(String format, int args);
    }
    public static void exec() {
        new DmRegHandler().handler();
    }
    public void handler() {
        //获取dm.dll的绝对路径。
        String absolutePath = FileUtil.getAbsolutePath("dm.dll");
        log.info("大漠插件路径:{}", absolutePath);
        DmReg.INSTANCE.SetDllPathA(absolutePath, 0);
    }




}
