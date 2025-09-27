package ru.astondevs.util;

import ru.astondevs.entity.Role;

import java.util.List;
import java.util.Scanner;

public class ConsoleHelper {

    private final Scanner scanner;

    public ConsoleHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    public int readValidInt(String prompt) {
        int number = 0;
        boolean valid = false;
        do {
            System.out.print(prompt);
            String line = scanner.nextLine();
            try {
                number = Integer.parseInt(line);
                if (number <= 0) {
                    System.out.println("❌ Число должно быть положительным!");
                } else {
                    valid = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Введите корректное число!");
            }
        } while (!valid);
        return number;
    }

    public String readValidLine(String prompt) {
        String line;
        do {
            System.out.print(prompt);
            line = scanner.nextLine();
            if (line.isBlank()) {
                System.out.println("❌ Поле не может быть пустым!");
            }
        } while (line.isBlank());
        return line;
    }

    public Long readValidRoleId(List<Role> roles) {
        if (roles.isEmpty()) {
            System.out.println("❌ В базе данных нет ролей!");
            return 0L;
        }

        List<Long> roleIds = roles.stream().map(Role::getId).toList();
        long number;
        boolean valid = false;

        do {
            System.out.println("Список ролей:");
            roles.forEach(r -> System.out.println(r.getId() + " - " + r.getName()));

            number = readValidInt("Введите идентификатор роли пользователя: ");
            if (!roleIds.contains(number)) {
                System.out.println("❌ Роли с таким идентификатором не существует!");
            } else {
                valid = true;
            }
        } while (!valid);

        return number;
    }

    public boolean isNull(Object obj, String message) {
        if (obj == null) {
            System.out.println("❌ " + message);
            return true;
        }
        return false;
    }

    public boolean isEmpty(List<?> list, String message) {
        if (list.isEmpty()) {
            System.out.println("❌ " + message);
            return true;
        }
        return false;
    }
}

