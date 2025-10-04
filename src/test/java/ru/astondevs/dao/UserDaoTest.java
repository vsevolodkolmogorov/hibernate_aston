package ru.astondevs.dao;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.astondevs.entity.Role;
import ru.astondevs.entity.User;
import ru.astondevs.errors.UserNotFoundedException;
import ru.astondevs.util.ConnectionManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDaoTest {

    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("test_dao_db" + System.currentTimeMillis())
                    .withUsername("test_dao")
                    .withPassword("test");

    private final UserDao userDao = UserDao.getInstance();
    private final RoleDao roleDao = RoleDao.getInstance();

    @BeforeAll
    void setup() throws Exception {
        Properties props = new Properties();
        props.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        props.setProperty("hibernate.connection.username", postgres.getUsername());
        props.setProperty("hibernate.connection.password", postgres.getPassword());
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.setProperty("hibernate.hbm2ddl.auto", "none");

        ConnectionManager.init(props);

        try (Connection conn = DriverManager.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword())) {

            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(conn));

            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master-test.yaml",
                    new ClassLoaderResourceAccessor(),
                    database
            );

            liquibase.dropAll();
            liquibase.update(new Contexts(), new LabelExpression());
        }
    }

    @Test
    @DisplayName("save() - сохранение пользователя и получать id")
    void save_ShouldPersistUser() {
        Role role = roleDao.findById(1L);
        User user = new User("Alice", "alice@mail.com", 25, role);

        User saved = userDao.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(userDao.findById(saved.getId())).isNotNull();
    }

    @Test
    @DisplayName("findById() - поиск пользователя по ID")
    void findById_ShouldReturnUser() {
        Role role = roleDao.findById(1L);
        User user = new User("Bob", "bob@mail.com", 30, role);
        User saved = userDao.save(user);

        User found = userDao.findById(saved.getId());

        assertThat(found.getEmail()).isEqualTo("bob@mail.com");
        assertThat(found.getRole().getName()).isEqualTo(role.getName());
    }

    @Test
    @DisplayName("findById() - поиск несуществующего пользователя")
    void findById_ShouldReturnNull_WhenNotExist() {
        assertThat(userDao.findById(999L)).isNull();
    }

    @Test
    @DisplayName("findAll() - должен вернуть список всех пользователей")
    void findAll_ShouldReturnList() {
        Role role = roleDao.findById(1L);
        userDao.save(new User("A", "a@mail.com", 20, role));
        userDao.save(new User("B", "b@mail.com", 22, role));

        List<User> users = userDao.findAll();

        assertThat(users).isNotEmpty();
        assertThat(users.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("update() - обновить данные пользователя")
    void update_ShouldChangeUserData() {
        Role role = roleDao.findById(1L);
        User user = userDao.save(new User("Old", "old@mail.com", 40, role));

        user.setName("NewName");
        User updated = userDao.update(user.getId(), user);

        assertThat(updated.getName()).isEqualTo("NewName");
    }

    @Test
    @DisplayName("update() - обновить данные несуществующего пользователя")
    void update_ShouldThrow_WhenUserNotFound() {
        assertThatThrownBy(() -> userDao.update(999L, new User()))
                .isInstanceOf(UserNotFoundedException.class);
    }

    @Test
    @DisplayName("delete() - удалить пользователя по id")
    void delete_ShouldRemoveUser() {
        Role role = roleDao.findById(1L);
        User user = userDao.save(new User("Charlie", "charlie@mail.com", 28, role));

        userDao.delete(user.getId());

        assertThat(userDao.findById(user.getId())).isNull();
    }

    @Test
    @DisplayName("delete() - удалить несуществующего пользователя")
    void delete_ShouldNotFail_WhenUserNotExist() {
        userDao.delete(999L);
    }
}