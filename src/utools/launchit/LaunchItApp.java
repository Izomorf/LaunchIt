/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utools.launchit;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import utools.launchit.db.LaunchItDatabase;
import utools.launchit.ui.LaunchItMainScreen;

/**
 *
 * @author ruinmaxk
 */
public class LaunchItApp {
    private static Logger log = Logger.getLogger(LaunchItApp.class.getName());
    
    private static FileHandler fh;
    
    public LaunchItApp() {
        LaunchItDatabase.getInstance();
    }
    
    public static void main(String[] args) {
        log.info("Hello!");
        LaunchItDatabase.getInstance();
        
        try {
            log.addHandler(getLogFileHandler());
        } catch (SecurityException e) {
            log.log(Level.SEVERE,
                "Не удалось создать файл лога из-за политики безопасности.", e);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LaunchItMainScreen().setVisible(true);
            }
        });
    }
    
    public static FileHandler getLogFileHandler() {
        if (fh == null) {
            try {
                fh = new FileHandler("LogApp");
                fh.setFormatter(new SimpleFormatter());
            } catch (IOException e) {
                log.log(Level.SEVERE,
                    "Не удалось создать файл лога из-за ошибки ввода-вывода.", e);
            }
        }
        return fh;
    }
}
