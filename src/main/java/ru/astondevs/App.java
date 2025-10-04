package ru.astondevs;

import ru.astondevs.controller.ConsoleController;
import ru.astondevs.util.ConnectionManager;
import ru.astondevs.util.LiquibaseRunner;


public class App {

    public static void main(String[] args) {
        LiquibaseRunner.runMigrations();
        ConnectionManager.init();
        ConsoleController consoleController = new ConsoleController();
        consoleController.run();
    }
}
