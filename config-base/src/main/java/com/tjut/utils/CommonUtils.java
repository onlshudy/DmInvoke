package com.tjut.utils;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

@Log4j2
public class CommonUtils {
    private final static User32 user32 = User32.INSTANCE;

    public static int FindWindow(String windowName){
        final int[] ret = {0};
        user32.EnumWindows(new WinUser.WNDENUMPROC() {
            int count = 0;
            public boolean callback(WinDef.HWND hWnd, Pointer arg1) {
                char[] windowText = new char[512];
                user32.GetWindowText(hWnd, windowText, 512);
                String wText = Native.toString(windowText);
                WinDef.RECT rectangle = new WinDef.RECT();
                user32.GetWindowRect(hWnd, rectangle);
                if (wText.isEmpty() || !(User32.INSTANCE.IsWindowVisible(hWnd)
                        && rectangle.left > -32000)) {
                    return true;
                }
                if (windowName.equals(wText)){
                    long pointerValue = Pointer.nativeValue(hWnd.getPointer());
                    ret[0] = (int) pointerValue;
                    log.info("寻找到{}窗口：{}.",windowName, ret[0]);
                }
                return true;
            }
        }, null);
        if (ret[0] == 0){
            log.info("未找到{}窗口.",windowName);
        }
        return ret[0];
    }

    public static void main(String[] args) {
        FindWindow("魔兽世界");
    }
}
