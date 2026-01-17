# Multichat-project - кросс-серверный чат для проекта Cristalix

## Описание

Проект является мостом, который объединяет чаты:
- Сервер Minecraft 1.12.2
- Сервер Minecraft 1.20.5
- Telegram чат

## Архитектура

Система построена с использованием Apache Kafka в качестве шины сообщений.

Каждый сервер слушает свой топик.
Когда на него приходит сообщение, он форматирует его в соответствии с источником передачи и отправляет в чат.

## Настройка плагинов

В каждом плагине для майнкрафта при первом запуске создастся конфиг в котором нужно будет вписать адрес к Kafka серверу

## Настройка телеграм бота

Данные для бота хранятся в переменных средах для удобства.

Скрипт запуска бота с использованием сред окружения:
```bash
export BOT_TOKEN=
export BOT_CHAT_ID=
export BOT_USERNAME=
export BOT_CHAT_THREAD_1_12_2=
export BOT_CHAT_THREAD_1_20_5=
export KAFKA_BOOTSTRAP_SERVERS=

java -jar telegram-bot.jar
```

## Запуск Kafka Server с использованием KRaft

Порты, используемые для сервера, в примере используются стандартные (9092, 9093). Их можно изменить.

### 1. Подготовить сервер (открыть нужные порты)

Если используется UFW:
```bash    
sudo ufw allow 9092/tcp  
sudo ufw allow 9093/tcp
```
### 2. Скачать и распаковать Kafka

```shell
wget https://archive.apache.org/dist/kafka/3.0.0/kafka_2.13-3.0.0.tgz
tar -xzf kafka_2.13-3.0.0.tgz
```
### 3. Настроить KRaft кластер

```shell
cd kafka_2.13-3.0.0
./bin/kafka-storage.sh random-uuid
./bin/kafka-storage.sh format -t <СКОПИРОВАТЬ_СЮДА> -c ./config/kraft/server.properties
```
### 4. Настроить конфиг

```shell
nano config/kraft/server.properties
```

```properties
listeners=PLAINTEXT://:9092,CONTROLLER://:9093
advertised.listeners=PLAINTEXT://ВАШ_IP:9092
```
### 5. Создать скрипт запуска

```shell
echo './bin/kafka-server-start.sh ./config/kraft/server.properties' > start.sh
chmod +x start.sh
```
### 6. Запустить сервер

```shell
./start.sh
```
