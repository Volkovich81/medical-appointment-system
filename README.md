# Medical Appointment System

**Medical Appointment System** — это REST API для автоматизации записи пациентов к врачам. В итоговой версии система будет обеспечивать полный цикл управления мед. пациентами: регистрацию пациентов, создание электронных медицинских карт и выписку рецептов, с последующим развертыванием через CI/CD.

**Стек:** Java 17 · Spring Boot 4 · Maven

* * *

## Лабораторная работа №1: Basic REST service

**Задание:**
- Создать Spring Boot приложение
- Реализовать REST API для одной сущности (Patient)
- GET с @RequestParam и @PathVariable
- Реализовать слои: Controller → Service → Repository
- DTO и mapper
- Настроить Checkstyle

* * *

## Лабораторная работа №2: JPA (Hibernate/Spring Data)

**Задание:**
- Подключить PostgreSQL к проекту
- Создать 5 сущностей со связями:
    - `Patient`, `Doctor`, `Specialization`, `Appointment`, `MedicalRecord`
    - `OneToMany`: Patient → Appointments, Doctor → Appointments
    - `ManyToMany`: Doctor ↔ Specialization
    - `OneToOne`: Patient ↔ MedicalRecord
- Реализовать CRUD операции
- Настроить CascadeType и FetchType
- Решить проблему N+1 через JOIN FETCH
- Показать разницу между @Transactional и без него
- Сделать ER-диаграмму

* * *

## Лабораторная работа №3: Data Caching

**Задание:**
- Реализовать сложный GET-запрос с фильтрацией по вложенной сущности с использованием @Query (JPQL)
- Реализовать аналогичный запрос через native query
- Добавить пагинацию (Pageable)
- Реализовать in-memory индекс на основе HashMap<K, V> для ранее запрошенных данных
- Ключ формировать из параметров запроса (составной ключ)
- Обеспечить корректную работу индекса через equals() и hashCode()
- Реализовать инвалидацию индекса при изменении данных

* * *

## Лабораторная работа №4: Error Logging/Handling

**Задание:**
- Реализовать глобальную обработку ошибок через `@ControllerAdvice`
- Добавить валидацию входных данных через `@Valid`
- Реализовать единый формат ошибки для всех endpoint
- Настроить логирование через Logback:
  - уровни логирования (DEBUG, INFO, WARN, ERROR)
  - ротация логов (максимальный размер 10MB, хранение 30 дней)
- Реализовать аспект (AOP) для логирования времени выполнения сервисных методов
- Подключить Swagger/OpenAPI с описанием endpoint и DTO

* * *

## SonarCloud

https://sonarcloud.io/summary/new_code?id=Volkovich81_medical-appointment-system