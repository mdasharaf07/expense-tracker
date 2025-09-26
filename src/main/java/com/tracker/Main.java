package com.tracker;
import com.tracker.gui.ExpenseAppGUI;
import com.tracker.util.DatabaseConnection;

import javax.swing.*;
import java.sql.Connection;

public class Main {
    public static void main(String[] args)  {
        try {
            Connection cn = DatabaseConnection.getDBConnection();
            System.out.println("Connected to database successfully");
        }
        catch(Exception e) {
            System.out.println("Connection Failed! Check output console");
            System.exit(1);
        }
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
        SwingUtilities.invokeLater(
            ()->{
                try {
                    new ExpenseAppGUI().setVisible(true);
                }
                catch (Exception e) {
                    System.err.println(e.getMessage());
                }

            }
        );
    }
}