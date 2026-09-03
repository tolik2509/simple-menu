# simple-menu

Легковесная библиотека для создания GUI-меню на серверах Minecraft (Bukkit/Paper). Работает через стандартный `InventoryHolder`.

[![](https://jitpack.io/v/tolik2509/simple-menu.svg)](https://jitpack.io/#tolik2509/simple-menu)

## 🚀 Подключение (Maven)

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>io.github.tolik2509</groupId>
        <artifactId>simple-menu</artifactId>
        <version>v1.0.0</version>
    </dependency>
</dependencies>
```

## 💻 Использование

Пример регистрации и запуска менеджера в вашем плагине:

```java
private MenuTickManager tickManager;

@Override
public void onEnable() {
        SimpleMenuAPI.init(this);

        // Инициализация менеджера обновлений
        this.tickManager = new MenuTickManager();

        // Запуск синхронной задачи сервера (каждый тик)
        Bukkit.getScheduler().runTaskTimer(this, tickManager, 0L, 1L);
    }

//🕒 Менеджер обновлений (Тики)
public void openAnimatedMenu(Player player, AbstractMenu menu) {
        tickManager.register(player, menu);
        }
```

## 📄 Лицензия
MIT License.