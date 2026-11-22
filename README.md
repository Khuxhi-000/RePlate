# RePlate — Food Rescue & Surplus Redistribution Network
> **Course:** Programming in Java (Sem 3)  
> **Project Type:** Core Java Terminal-Based Menu-Driven Application  
> **Team Size:** 4 University Students  

---

## 1. Project Title & Overview
**RePlate** is a simple, practical, and cleanly architected Core Java terminal application designed to curb food wastage by directly bridging the gap between food donors (such as college messes, restaurants, and hotels) and beneficiaries (NGOs, orphanages, and community shelters) via coordinated volunteers.

---

## 2. Problem Statement
Large quantities of freshly prepared and safe surplus food are discarded daily by university messes, event banquets, restaurants, and bakeries. Concurrently, local NGOs, orphanages, and relief shelters often experience shortages of meals for their beneficiaries. Due to the lack of an organized, real-time coordination system, edible food ends up in landfills. **RePlate** solves this by establishing a decentralized, fast, and transparent network where surplus food is recorded, matched to urgent requests, allocated, picked up, and delivered by volunteers.

---

## 3. Project Objectives & Scope

### Objectives:
1. **Reduce Food Wastage:** Provide donors with a quick channel to list surplus food before expiry.
2. **Connect Donors and NGOs:** Automate matching based on food category, quantity, and city location.
3. **Prioritize Urgent Demands:** Process urgent NGO requirements first using a priority-based queue.
4. **Coordinate Volunteer Logistics:** Assign available volunteers to handle pickup and delivery.
5. **Real-time Lifecycle Tracking:** Monitor donations from `AVAILABLE` → `RESERVED` → `PICKED_UP` → `DELIVERED`.
6. **Transparent Audit History:** Maintain a permanent log of all distribution transactions.
7. **Generate Dynamic Impact Reports:** Dynamically compute meals saved, active donors, NGOs, and delivery metrics.
8. **Demonstrate Core Java Concepts:** Apply OOP principles, collections, file persistence, and robust exception handling.

### Scope:
- **Simulated Local Network:** Tested with real-world regional cities: **Chandigarh, Mohali, Ludhiana, Barnala**.
- **Platform:** 100% Core Java terminal application running on any standard JDK (Java 8+).
- **Zero Heavy Dependencies:** No external databases, no web servers, no Spring Boot, and no third-party APIs. Simple, clean, and fast.

---

## 4. Team Member Division & Responsibilities

| Member | Module / Role | Primary Classes Developed | Responsibilities |
|---|---|---|---|
| **Member 1** | **User & Account Management** | `User`, `Donor`, `NGO`, `Volunteer`, `Admin`, `UserManager` | User registration, login authentication, role-based polymorphic dispatch, duplicate validation, and profile management. |
| **Member 2** | **Food & Donation Management** | `FoodItem`, `FoodDonation`, `FoodInventory`, `DonationManager`, `FoodSearch` | Donation listing, surplus quantity management, expiry tracking, linear search by name/category/location, and sorting by quantity/expiry. |
| **Member 3** | **Request & Matching** | `DonationRequest`, `RequestManager`, `DonationMatcher`, `VolunteerAssignment`, `AssignmentManager` | NGO request placement, PriorityQueue processing for urgent needs, multi-criteria donation matching, quantity allocation, and volunteer dispatching. |
| **Member 4** | **Distribution, Reports & System** | `Pickup`, `Delivery`, `DistributionManager`, `DonationHistory`, `ImpactReport`, `FileManager`, `MainMenu` | Pickup/delivery confirmation, digital receipt generation, file I/O persistence, dynamic impact metrics, robust input validation, and main menu coordination. |

---

## 5. System Architecture & Class Structure

The project contains **25 carefully designed classes, 4 enums, and 1 interface** organized across 5 clean packages:

```
src/
├── main/
│   └── MainMenu.java             # Terminal interface and application coordinator
├── manager/
│   ├── UserManager.java          # Manages user accounts and volunteer availability
│   ├── DonationManager.java      # Manages food donations and inventory updates
│   ├── RequestManager.java       # Manages NGO requests and PriorityQueue
│   ├── AssignmentManager.java    # Coordinates volunteer delivery assignments
│   └── DistributionManager.java  # Handles pickup, delivery confirmation, and receipts
├── model/
│   ├── Displayable.java          # Interface for uniform console details
│   ├── UserRole.java             # Enum: DONOR, NGO, VOLUNTEER, ADMIN
│   ├── FoodCategory.java         # Enum: COOKED_FOOD, PACKAGED_FOOD, FRUITS, VEGETABLES, BAKERY, OTHER
│   ├── DonationStatus.java       # Enum: AVAILABLE, RESERVED, PICKED_UP, DELIVERED, CANCELLED, EXPIRED
│   ├── RequestPriority.java      # Enum: LOW, MEDIUM, HIGH, URGENT
│   ├── User.java                 # Abstract base class with encapsulated fields
│   ├── Donor.java                # Subclass representing mess / restaurant
│   ├── NGO.java                  # Subclass representing charity / shelter
│   ├── Volunteer.java            # Subclass representing delivery volunteer
│   ├── Admin.java                # Subclass representing system administrator
│   ├── FoodItem.java             # Represents food item and category
│   ├── FoodDonation.java         # Represents donation listing with times and status
│   ├── FoodInventory.java        # In-memory storage wrapper for donations
│   ├── DonationRequest.java      # Represents NGO demand (implements Comparable)
│   ├── VolunteerAssignment.java  # Represents active transport assignment
│   ├── Pickup.java               # Represents pickup milestone
│   └── Delivery.java             # Represents delivery milestone
├── service/
│   ├── DonationMatcher.java      # Matching and quantity allocation engine
│   ├── FoodSearch.java           # Linear search and Comparator sorting
│   ├── DonationHistory.java      # Audit trail and event log
│   └── ImpactReport.java         # Dynamic calculation of redistribution metrics
└── util/
    ├── FileManager.java          # Flat file CSV I/O handling
    ├── InputValidator.java       # Safe terminal input, prevents crashes
    └── ValidationException.java  # User-defined checked exception
```

---

## 6. Course Concepts Demonstrated

### 1. Object-Oriented Programming (OOP)
- **Encapsulation:** All model entities (`User`, `FoodDonation`, `DonationRequest`, etc.) maintain `private` fields accessible only through getter and setter methods with state validation.
- **Inheritance:** `User` serves as the abstract parent class extended by `Donor`, `NGO`, `Volunteer`, and `Admin`. Common attributes (id, name, email, phone, location, password) are inherited cleanly without duplication.
- **Polymorphism:**
  - *Dynamic Method Overriding:* When a logged-in user enters the system, `user.displayRole()` dynamically triggers the subclass-specific role message.
  - *Interface Polymorphism:* Calling `displayDetails()` on any `Displayable` reference uniformly prints structured details.
- **Abstraction:** `User` is an `abstract` class containing the abstract method `public abstract void displayRole()`. Subclasses must provide their own role descriptions.
- **Interface:** `Displayable` defines `void displayDetails()`, ensuring unified presentation for users, donations, requests, and assignments.

### 2. Java Collections Framework
| Collection | Usage in RePlate | Why Chosen (Viva Justification) |
|---|---|---|
| `ArrayList<T>` | `ArrayList<User>`, `ArrayList<FoodDonation>`, `ArrayList<DonationRequest>`, `ArrayList<VolunteerAssignment>` | Maintains dynamic, sequentially ordered records. Elements can be added easily, iterated over, and sorted. |
| `HashMap<K, V>` | `HashMap<Integer, User>`, `HashMap<String, User>`, `HashMap<Integer, FoodDonation>` | Provides near-instant **$O(1)$** lookup by ID or username. Ideal for fast login credential checking and direct record retrieval. |
| `PriorityQueue<T>` | `PriorityQueue<DonationRequest>` | Automatically arranges NGO requests by urgency (`URGENT` > `HIGH` > `MEDIUM` > `LOW`) using natural ordering from `Comparable<DonationRequest>`. Urgent requests are dequeued and served first. |
| `Comparator<T>` | `FoodSearch.sortByQuantity()`, `FoodSearch.sortByExpiry()` | Provides customized, on-the-fly sorting algorithms for surplus food inventory without altering the underlying class. |

### 3. Exception Handling
- **Custom Exception:** `ValidationException` is thrown when business invariants are violated (e.g., negative food quantities, empty names).
- **Defensive Error Handling:** Uses `try-catch` blocks for:
  - `NumberFormatException` when users type letters instead of integers in menus.
  - `IOException` when reading or writing files in `FileManager`.
  - Application **never crashes** on invalid user inputs; instead, informative hints are printed and input is re-prompted.

### 4. File Persistence (No DB required)
- Text and CSV files stored in the `data/` folder:
  - `data/users.txt`
  - `data/donations.txt`
  - `data/requests.txt`
  - `data/assignments.txt`
  - `data/history.txt`
- On startup, `FileManager` reads data using `BufferedReader` and `FileReader`.
- On state updates (donation added, request matched, pickup/delivery completed), records are persisted using `BufferedWriter` and `FileWriter`.
- Missing directories and files are automatically created.

### 5. Enums
- `FoodCategory`: Limits food to `COOKED_FOOD`, `PACKAGED_FOOD`, `FRUITS`, `VEGETABLES`, `BAKERY`, `OTHER`.
- `DonationStatus`: Enforces lifecycle states: `AVAILABLE`, `RESERVED`, `PICKED_UP`, `DELIVERED`, `CANCELLED`, `EXPIRED`.
- `RequestPriority`: Maps urgency levels with integer ranks for PriorityQueue comparison.
- `UserRole`: Controls role-based dashboard access.

---

## 7. How to Compile and Run

### Prerequisites:
- Java Development Kit (JDK 8 or higher) installed.

### Option A: Using Windows PowerShell / Terminal
```powershell
# 1. Clone / Navigate to project directory
cd "d:\2nd Year-Sem 3\PJ\RePlate"

# 2. Compile all source files into bin directory
javac -d bin src/model/*.java src/util/*.java src/service/*.java src/manager/*.java src/main/*.java src/test/*.java

# 3. Run the Main Application
java -cp bin main.MainMenu
```

### Option B: Run Automated System Verification Test
```powershell
java -cp bin test.SystemTest
```

---

## 8. Sample End-to-End Workflow

```
Donor adds surplus food (e.g. 50 pkts Cooked Food in Chandigarh)
         ↓
NGO creates request (e.g. 30 pkts Cooked Food in Chandigarh, HIGH priority)
         ↓
DonationMatcher finds compatible donation (ID: 101)
         ↓
System allocates 30 pkts (Remaining: 20 pkts)
         ↓
Volunteer (Rahul) is assigned
         ↓
Volunteer marks Pickup Completed (Status → PICKED_UP)
         ↓
Volunteer marks Delivery Completed (Status → DELIVERED)
         ↓
Volunteer is released back to AVAILABLE status
         ↓
Receipt is generated and transaction logged in history.txt
         ↓
Impact Report updates dynamically
```

### Sample Receipt Output:
```
========================================
      FOOD REDISTRIBUTION RECEIPT
========================================
Donation ID : 101
Assignment  : #301
Donor       : ABC College Mess
NGO         : Helping Hands NGO

Food        : Rice (COOKED_FOOD)
Quantity    : 30 packets

Delivered By: Rahul
Received By : Sister Mary
Time        : 07:30 PM
Pickup      : Completed
Delivery    : Completed

Thank you for helping reduce food waste.
========================================
```

---

## 9. Sample Pre-Loaded Credentials

| Role | Username | Password | Details |
|---|---|---|---|
| **Admin** | `admin` | `admin123` | System Administrator |
| **Donor** | `abcmess` | `pass123` | ABC College Mess, Chandigarh |
| **Donor** | `greenleaf` | `pass123` | Green Leaf Restaurant, Mohali |
| **NGO** | `helpinghands` | `pass123` | Helping Hands NGO, Chandigarh |
| **NGO** | `hopefdn` | `pass123` | Hope Foundation, Ludhiana |
| **Volunteer**| `rahul` | `pass123` | Rahul (Bike), Chandigarh |
| **Volunteer**| `aman` | `pass123` | Aman (Car), Mohali |

*(New accounts can also be registered at any time from the Main Menu).*

---

## 10. Future Improvements
1. **SMS / Email Alerts:** Integrate automated notifications when new donations match an NGO's wishlist.
2. **Batch Expiry Checker Thread:** Use Java multi-threading (`ScheduledExecutorService`) to automatically mark uncollected donations as `EXPIRED` once their expiry timestamp passes.
3. **Distance Calculation:** Add simulated coordinate distance calculations between cities to recommend the closest volunteer.
4. **Export Reports to PDF/CSV:** Allow NGOs and administrators to export monthly tax exemption certificates and redistribution summaries.
