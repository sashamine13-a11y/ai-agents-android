# AI Agents — Мультиагентное Android-приложение

Приложение для работы с нейросетями через API. Поддерживает мультиагентную архитектуру: несколько специализированных AI-агентов работают вместе для решения сложных задач.

## Поддерживаемые модели (работают из РФ без VPN)

| Провайдер | Модель | Бесплатный лимит | Регистрация |
|-----------|--------|------------------|-------------|
| **Gemini** | gemini-1.5-flash | 60 запросов/мин | [aistudio.google.com](https://aistudio.google.com/app/apikey) |
| **DeepSeek** | deepseek-chat | ~$5 кредитов | [platform.deepseek.com](https://platform.deepseek.com) |
| **Qwen** | qwen-turbo | 1 млн токенов/мес | [dashscope.aliyun.com](https://dashscope.aliyun.com) |
| **Mistral** | mistral-tiny | 1 запрос/сек | [console.mistral.ai](https://console.mistral.ai) |

## Агенты

- **CodeAgent** — пишет и редактирует код
- **PlannerAgent** — разбивает задачи на шаги
- **FileAgent** — работает с файлами
- **ResearchAgent** — ищет информацию
- **ReviewAgent** — ревьюит код
- **TranslateAgent** — переводит тексты

## Сборка через GitHub Actions

1. Форкните репозиторий
2. Перейдите в **Actions** → **Build APK** → **Run workflow**
3. Через ~5 минут скачайте APK из артефактов

## Локальная сборка

```bash
# 1. Клонируйте репо
git clone <url>
cd ai-agents-android

# 2. Создайте local.properties (опционально — для тестов)
cp local.properties.example local.properties

# 3. Соберите
./gradlew assembleDebug
```

APK будет в `app/build/outputs/apk/debug/app-debug.apk`

## Настройка

При первом запуске откройте **Настройки** (иконка шестерёнки) и введите хотя бы один API ключ.

## Мульти-агентный режим

Включите переключатель **«Мульти»** — PlannerAgent разобьёт задачу на шаги и запустит нужных агентов автоматически.
