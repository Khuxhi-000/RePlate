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
5. **Real-time Lifecycle Tracking:** Monitor donations from `AVAILABLE` → `RESERVED` → `PICKED_UP` → `DELIVERED` or `EXPIRED`.
6. **Concurrent Background Monitoring:** Run a background daemon thread to continuously identify and update expired donations.
7. **Transparent Audit History:** Maintain a permanent log of all distribution transactions.
8. **Generate Dynamic Impact Reports:** Dynamically compute meals saved, active donors, NGOs, and delivery metrics.
9. **Demonstrate Core Java Concepts:** Apply OOP principles, multithreading, synchronization, collections, 2D arrays, custom exceptions, file persistence, and robust validation.

### Scope:
- **Simulated Local Network:** Tested with regional cities: **Chandigarh, Mohali, Ludhiana, Barnala**.
- **Platform:** 100% Core Java terminal application running on any standard JDK (Java 8+).
- **Zero Heavy Dependencies:** No external databases, no web servers, no Spring Boot, and no third-party APIs. Simple, clean, and fast.

---

## 4. Team Member Division & Responsibilities

| Member | Module / Role | Primary Classes Developed | Responsibilities |
|---|---|---|---|
| **Member 1** | **User & Account Management** | `User`, `Donor`, `NGO`, `Volunteer`, `Admin`, `UserManager` | User registration, login authentication, role-based polymorphic dispatch, duplicate username validation, profile management, and account security. |
| **Member 2** | **Food & Donation Management** | `FoodItem`, `FoodDonation`, `FoodInventory`, `DonationManager`, `FoodSearch` | Donation listing, surplus quantity management, expiry tracking, linear search by name/category/location, sorting by quantity/expiry, and ListIterator filtering. |
| **Member 3** | **Request, Matching & Logistics** | `DonationRequest`, `RequestManager`, `DonationMatcher`, `VolunteerAssignment`, `AssignmentManager`, `CityDistanceMatrix` | NGO request placement, PriorityQueue processing for urgent needs, multi-criteria donation matching, 2D distance matrix lookup, quantity allocation, and volunteer dispatching. |
| **Member 4** | **Distribution, Concurrency & System** | `Pickup`, `Delivery`, `DistributionManager`, `DonationHistory`, `ImpactReport`, `FileManager`, `MainMenu`, `DonationExpiryChecker`, `ValidationException`, `DonationExpiredException`, `InsufficientQuantityException` | Pickup/delivery confirmation, digital receipt generation, background thread lifecycle & synchronization, file I/O persistence, dynamic impact metrics, robust input validation, and main menu coordination. |

---

## 5. System Architecture & Class Catalog

The project contains **34 application classes/types + 1 comprehensive automated test class (35 total)** organized across 6 clean packages:

```
src/
├── main/
│   └── MainMenu.java                 # Terminal interface, application runner & thread manager
├── manager/
│   ├── UserManager.java              # Manages user accounts and volunteer availability
│   ├── DonationManager.java          # Thread-safe operations on surplus donations & status updates
│   ├── RequestManager.java           # Manages NGO requests and PriorityQueue
│   ├── AssignmentManager.java        # Coordinates volunteer delivery assignments
│   └── DistributionManager.java      # Handles pickup, delivery confirmation, and receipts
├── model/
│   ├── Displayable.java              # Interface for uniform console details
│   ├── UserRole.java                 # Enum: DONOR, NGO, VOLUNTEER, ADMIN
│   ├── FoodCategory.java             # Enum: COOKED_FOOD, PACKAGED_FOOD, FRUITS, VEGETABLES, BAKERY, OTHER
│   ├── DonationStatus.java           # Enum: AVAILABLE, RESERVED, PICKED_UP, DELIVERED, CANCELLED, EXPIRED
│   ├── RequestPriority.java          # Enum: LOW, MEDIUM, HIGH, URGENT
│   ├── User.java                     # Abstract base class with encapsulated fields
│   ├── Donor.java                    # Subclass representing mess / restaurant
│   ├── NGO.java                      # Subclass representing charity / shelter
│   ├── Volunteer.java                # Subclass representing delivery volunteer
│   ├── Admin.java                    # Subclass representing system administrator
│   ├── FoodItem.java                 # Represents food item and category
│   ├── FoodDonation.java             # Represents donation listing with times and status
│   ├── FoodInventory.java            # Thread-safe in-memory storage wrapper for donations
│   ├── DonationRequest.java          # Represents NGO demand (implements Comparable)
│   ├── VolunteerAssignment.java      # Represents active transport assignment
│   ├── Pickup.java                   # Represents pickup milestone
│   └── Delivery.java                 # Represents delivery milestone
├── service/
│   ├── DonationMatcher.java          # Matching engine using 2D distance matrix & custom exceptions
│   ├── FoodSearch.java               # Linear search, ListIterator, and Comparator sorting
│   ├── DonationHistory.java          # Audit trail and event log
│   ├── ImpactReport.java             # Dynamic calculation of redistribution metrics using StringBuilder
│   └── DonationExpiryChecker.java    # Background Thread periodically expiring outdated donations
├── test/
│   └── SystemTest.java               # 23-step automated test verifying all syllabus requirements
└── util/
    ├── CityDistanceMatrix.java       # 2D Array: Inter-city distance matrix & transit feasibility
    ├── FileManager.java              # Flat-file CSV persistence with try-catch-finally & multi-catch
    ├── InputValidator.java           # Robust terminal input with validation loops
    ├── ValidationException.java      # Custom checked exception for input validation
    ├── DonationExpiredException.java # Custom checked exception for expired donations
    └── InsufficientQuantityException.java # Custom checked exception for surplus shortage
```

---

## 6. Comprehensive Syllabus Concepts Demonstrated

### 1. Multithreading & Thread Lifecycle
- **Background Expiry Checker:** `service.DonationExpiryChecker` extends `java.lang.Thread`.
- **Concurrency:** Runs as a background daemon thread (`setDaemon(true)`) started via `.start()` during application launch.
- **Thread Lifecycle States:**
  - `NEW`: Instantiated in `MainMenu` constructor.
  - `RUNNABLE`: Executing the expiration check loop in `run()`.
  - `TIMED_WAITING`: Sleeping between check cycles via `Thread.sleep(intervalMillis)`.
  - `TERMINATED`: Exits cleanly when interrupted or stopped via `.join()`.
- **Clean Interruption & Shutdown:** `stopChecker()` calls `interrupt()` to immediately break out of `Thread.sleep()` and `join(2000)` to wait for graceful completion before the terminal shuts down.

### 2. Synchronization & Critical Section Protection
- **Shared Resource:** The collection of food donations inside `FoodInventory` / `DonationManager`.
- **Critical Sections:** Reading and updating donation records while the background thread concurrently scans for expired donations.
- **Race Condition Prevention:**
  - Methods in `FoodInventory` (`addDonation`, `getDonationById`, `getAllDonations`, `getAvailableDonations`, `expireOutdatedDonations`) are marked `synchronized`.
  - Mutating methods in `DonationManager` (`checkAndUpdateExpiredDonations`, `updateDonationQuantity`, `cancelDonation`, `updateDonationStatus`) are marked `synchronized`.
  - Returns thread-safe defensive copies (`new ArrayList<>(donationList)`) under the lock to prevent `ConcurrentModificationException`.

### 3. Two-Dimensional (2D) Arrays
- **Inter-City Distance Grid:** `util.CityDistanceMatrix` stores a `4 x 4` 2D integer array (`int[][] DISTANCE_MATRIX`) mapping distances between Chandigarh, Mohali, Ludhiana, and Barnala.
- **Practical Application:** `DonationMatcher` uses `CityDistanceMatrix.isWithinRange(loc1, loc2, 25)` to evaluate whether an NGO request is close enough to a donor for practical food transport (e.g. Chandigarh to Mohali = 10 km).
- **Tabular Grid Display:** Admin menu option 7 prints the formatted 2D distance matrix.

### 4. Java Collections Framework
| Collection / Concept | Usage in RePlate | Why Chosen (Viva Justification) |
|---|---|---|
| `ArrayList<T>` | `ArrayList<User>`, `ArrayList<FoodDonation>`, `ArrayList<DonationRequest>` | Maintains dynamic, sequentially ordered records with fast index access. |
| `HashMap<K, V>` | `HashMap<Integer, User>`, `HashMap<String, User>`, `HashMap<Integer, FoodDonation>` | Provides near-instant **$O(1)$** lookup for login validation and direct ID queries. |
| `PriorityQueue<T>` | `PriorityQueue<DonationRequest>` | Automatically serves `URGENT` NGO demands before `LOW` priority requests using natural ordering from `Comparable`. |
| `Iterator<T>` | `FoodInventory.expireOutdatedDonations()` | Safely iterates through donation collections to check expiry without risking index shifts or concurrent conflicts. |
| `ListIterator<T>` | `FoodSearch.filterByLocationWithIterator()` | Demonstrates bidirectional sequential collection traversal for exact city filtering. |
| `Comparator<T>` | `FoodSearch.sortByQuantity()`, `FoodSearch.sortByExpiry()` | Provides on-the-fly sorting algorithms without altering the model classes. |
| `Comparable<T>` | `DonationRequest implements Comparable<DonationRequest>` | Defines natural ordering based on urgency level (`RequestPriority`). |

### 5. Custom Exceptions & Exception Handling
- **Custom Exceptions:**
  - `ValidationException`: Thrown on invalid email, phone number, registration ID, or negative quantity.
  - `DonationExpiredException`: Thrown when attempting to match or allocate an expired donation.
  - `InsufficientQuantityException`: Thrown when requested surplus exceeds available packets.
- **Language Keywords:** Demonstrates `throw`, `throws`, custom constructor chaining (`super(message)`), and custom getters (`getRequested()`, `getAvailable()`).
- **Defensive Error Handling:**
  - `Multi-catch`: `catch (IOException | NumberFormatException e)` in `FileManager` and `catch (DonationExpiredException | InsufficientQuantityException e)` in `MainMenu`.
  - `finally block`: Explicit `finally` block in `FileManager.appendHistory` ensuring file writer streams are always closed even if an I/O error occurs.
  - Application never crashes on invalid user input.

### 6. Strings, StringBuilder & Wrapper Classes
- **`StringBuilder`:** Employed in `FoodDonation.toCsv()`, `DistributionManager.displayReceipt()`, and `ImpactReport.displayImpactReport()` for efficient multi-line string construction.
- **Wrapper Classes & Autoboxing:** Explicit use of `Integer.valueOf()`, `Integer.parseInt()`, `Boolean.valueOf()`, and autoboxing/unboxing between primitive types and collection generics.

---

## 7. How to Compile and Run

Navigate to the project root directory:
```powershell
cd "d:\2nd Year-Sem 3\PJ\RePlate"
```

### Step 1: Compile All Sources
```powershell
javac -d bin (Get-ChildItem -Recurse -Filter *.java src | Select-Object -ExpandProperty FullName)
```

### Step 2: Run the Application
```powershell
java -cp bin main.MainMenu
```

### Step 3: Run the Automated System Verification Test
```powershell
java -cp bin test.SystemTest
```

---

## 8. Sample Pre-Loaded Credentials

| Role | Username | Password | Location / Description |
|---|---|---|---|
| **Admin** | `admin` | `admin123` | System Administrator |
| **Donor** | `abcmess` | `pass123` | ABC College Mess, Chandigarh |
| **Donor** | `greenleaf` | `pass123` | Green Leaf Restaurant, Mohali |
| **NGO** | `helpinghands` | `pass123` | Helping Hands NGO, Chandigarh |
| **NGO** | `hopefdn` | `pass123` | Hope Foundation, Ludhiana |
| **Volunteer** | `rahul` | `pass123` | Rahul (Bike), Chandigarh |
| **Volunteer** | `aman` | `pass123` | Aman (Car), Mohali |
