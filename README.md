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

## SonarCloud

https://sonarcloud.io/summary/new_code?id=Volkovich81_medical-appointment-system