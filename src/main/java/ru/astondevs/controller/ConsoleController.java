package ru.astondevs.controller;

import ru.astondevs.dao.RoleDao;
import ru.astondevs.dto.UserDto;
import ru.astondevs.service.UserService;
import ru.astondevs.util.ConsoleHelper;

import java.util.List;
import java.util.Scanner;

public class ConsoleController {
    private final UserService userService = new UserService();
    private final RoleDao roleDao = RoleDao.getInstance();
    private final Scanner scanner = new Scanner(System.in);
    private final ConsoleHelper consoleHelper = new ConsoleHelper(scanner);
    private UserDto user = null;

    public void run() {
        printBanner();
        while (true) {
            handleView();
            System.out.print("> ");
            String command = scanner.nextLine();

            switch (command.toLowerCase()) {
                case "/create" -> handleCreate();
                case "/clear" -> handleClear();
                case "/view" -> handleView();
                case "/save" -> handleSave();
                case "/update" -> handleUpdate();
                case "/delete" -> handleDelete();
                case "/find_all" -> handleFindAll();
                case "/find_by_id" -> handleFindById();
                case "/help" -> handleHelp();
                case "/exit" -> { return; }
                default -> System.out.print("Неизвестная команда! \n/");
            }
        }
    }

    private void handleCreate() {
        String name = consoleHelper.readValidLine("Введите имя: ");
        String email = consoleHelper.readValidLine("Введите почту: ");
        int age = consoleHelper.readValidInt("Введите возраст: ");
        Long roleId = consoleHelper.readValidRoleId(roleDao.findAll());

        user = new UserDto(name, email, age, roleId);

        System.out.print("Пользователь " + user.getName() + " создан!\n");
        System.out.print("Для сохранения пользователя в базу данных используйте - /save\n");
    }

    private void handleClear() {
        user = null;
        System.out.print("Пользователь очищен!\n");
    }

    private void handleView() {
        System.out.print("Актуальный пользователь: ");
        System.out.println(user != null ? user.toString() :
                null + "\nЧтобы создать пользователя используйте команду - /create");
    }

    private void handleSave() {
        if (consoleHelper.isNull(user, "Пользователь не создан!")) return;
        UserDto userCreated = userService.save(user);
        System.out.print("Пользователь " + userCreated.getName() + " сохранен в базу данных!\n");
        user = null;
    }

    private void handleUpdate() {
        if (consoleHelper.isNull(user, "Пользователь не создан!")) return;
        int updateId = consoleHelper.readValidInt("Введите id пользователя: ");
        UserDto userUpdated = userService.update((long) updateId, user);
        System.out.print("Пользователь " + userUpdated.getName() + " обновлен!\n");
        user = null;
    }

    private void handleDelete() {
        int deleteId = consoleHelper.readValidInt("Введите id пользователя: ");
        userService.delete((long) deleteId);
        System.out.print("Пользователь удален!\n");
    }

    private void handleFindAll() {
        System.out.print("Список пользователей: \n");
        List<UserDto> list = userService.findAll();

        if (consoleHelper.isEmpty(list, "В базе данных нет пользователей! \n")) return;

        for (UserDto user : list) {
            System.out.print("Пользователь: " + user.toString() + "\n");
        }
    }

    private void handleFindById() {
        System.out.print("Введите id пользователя: ");
        String userId = scanner.nextLine();
        UserDto userFound = userService.findById(Long.parseLong(userId));
        if (consoleHelper.isNull(userFound, "Пользователь не найден!")) return;
        System.out.print("Пользователь: " + userFound + "\n");
    }

    private static void printBanner() {
        System.out.println("====================================================");
        System.out.println("   👋 Привет! Это консольное приложение user-service");
        System.out.println("====================================================\n");
        handleHelp();
        System.out.println("====================================================");
        System.out.println("              Удачи в работе! 🚀");
        System.out.println("====================================================\n");
    }

    private static void handleHelp() {
        System.out.println("📌 Команды:\n");
        System.out.println("🔹 Работа с пользователем:");
        System.out.println("   /create   - создать пользователя");
        System.out.println("   /view     - посмотреть текущего пользователя");
        System.out.println("   /clear    - очистить данные пользователя\n");
        System.out.println("🔹 Работа с базой данных:");
        System.out.println("   /save     - сохранить пользователя в БД");
        System.out.println("   /update   - обновить пользователя в БД");
        System.out.println("   /delete   - удалить пользователя из БД");
        System.out.println("   /find_all  - показать всех пользователей");
        System.out.println("   /find_by_id - найти пользователя по ID\n");
        System.out.println("🔹 Список команд: /help");
        System.out.println("🚪 Для выхода напишите: /exit\n");
    }
}
