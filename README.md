# Order Management System (OMS)

## Proje Hakkında
**Order Management System**, müşteri siparişlerini yönetmek için geliştirilmiş bir Java tabanlı masaüstü uygulamasıdır.  
Uygulama; **PostgreSQL** veritabanı ile entegre çalışmakta ve sipariş, müşteri ve ürün yönetimini kapsamaktadır.  

Proje **Eclipse IDE** üzerinde geliştirilmiştir.  
UI katmanı tamamen **Swing** ile manuel olarak yazılmıştır. Bu nedenle UI kodları, form tasarım araçları kullanılmadığı için daha karmaşık bir yapıdadır. Senior developer bakış açısıyla bu tercih, öğrenme odaklı bir yaklaşım olmakla birlikte, **maintainability (bakım kolaylığı)** açısından bazı zorluklar doğurabilmektedir.

---

## Özellikler
- 📦 Sipariş ekleme, güncelleme ve silme  
- 👤 Müşteri yönetimi  
- 🏷️ Ürün yönetimi  
- 🔗 PostgreSQL veritabanı bağlantısı  
- 🖥️ Swing tabanlı UI (manuel yazılmıştır)  

---

## Teknolojiler
- **Java 17+**  
- **PostgreSQL 14+**  
- **Eclipse IDE**  
- **JDBC** (Database bağlantısı için)  
- **Swing** (UI için)  

---

## Kurulum
1. **Repository’yi klonlayın**  
   ```bash
   git clone https://github.com/username/OrderManagementSystem.git
   ```

2. **Veritabanını oluşturun**  
   ```sql
   CREATE DATABASE order_management;
   ```

3. **Tabloları oluşturun** (`schema.sql` dosyası projede mevcuttur)  

4. **Veritabanı bağlantısını yapılandırın**  
   `DatabaseConnection.java` dosyasında aşağıdaki bilgileri kendi ortamınıza göre güncelleyin:  
   ```java
   private static final String URL = "jdbc:postgresql://localhost:5432/order_management";
   private static final String USER = "postgres";
   private static final String PASSWORD = "your_password";
   ```

5. **Projeyi Eclipse üzerinden çalıştırın**  
   - Eclipse → `File > Import > Existing Projects into Workspace`  
   - Projeyi seçip çalıştırın.  

---

## Developer Notları
- Swing UI kodları **form designer** kullanılmadan yazıldığı için **karmaşık ve uzun görünebilir**.  
- UI tarafında *refactor* yapılması önerilir (ör: `MVC` veya `Observer Pattern` entegrasyonu).  
- PostgreSQL bağlantıları `DAO` katmanı üzerinden yönetilmektedir.  

---

## Gelecek İyileştirmeler
- UI tarafında **JavaFX veya modern frameworklere geçiş**  
- **ORM (Hibernate veya JPA)** entegrasyonu  
- Daha modüler **katmanlı mimari**  

---

## About the Project 
**Order Management System** is a Java-based desktop application designed to manage customer orders.  
It integrates with a **PostgreSQL** database and covers order, customer, and product management modules.  

The project was developed in **Eclipse IDE**.  
UI layer is written entirely with **Swing** (manual implementation). This makes the UI code relatively complex, since no form builder was used. From a senior developer perspective, this is an acceptable choice for learning, but it reduces **maintainability** and increases **technical debt** for larger projects.

---

## Features
- 📦 Add, update, delete orders  
- 👤 Manage customers  
- 🏷️ Manage products  
- 🔗 PostgreSQL database integration  
- 🖥️ Swing-based UI (written manually)  

---

## Tech Stack
- **Java 17+**  
- **PostgreSQL 14+**  
- **Eclipse IDE**  
- **JDBC**  
- **Swing**  

---

## Installation
1. **Clone the repository**  
   ```bash
   git clone https://github.com/username/OrderManagementSystem.git
   ```

2. **Create the database**  
   ```sql
   CREATE DATABASE order_management;
   ```

3. **Run schema.sql** to create necessary tables  

4. **Configure DB connection** in `DatabaseConnection.java`:  
   ```java
   private static final String URL = "jdbc:postgresql://localhost:5432/order_management";
   private static final String USER = "postgres";
   private static final String PASSWORD = "your_password";
   ```

5. **Run the project in Eclipse**  
   - Eclipse → `File > Import > Existing Projects into Workspace`  
   - Select the project and run.  

---

## Developer Notes
- UI code is **manually written with Swing**, therefore looks more complex.  
- Refactoring with **MVC pattern** or introducing **JavaFX** could improve maintainability.  
- Database access is handled via **DAO layer**.  

---

## Future Improvements
- UI migration to **JavaFX** or modern UI frameworks  
- ORM integration (Hibernate/JPA)  
- Refactoring into a more modular **layered architecture**  
