# RePlate — Viva & Project Defense Guide
> **Subject:** Programming in Java  
> **Target Audience:** 2nd Year University Students & Evaluation Panel  

---

### Q1: What real-world problem does the project solve?
**Answer:**  
In university messes, restaurants, hotels, and banquets, large quantities of safe, edible surplus food are prepared daily but discarded after peak hours. Meanwhile, local NGOs, orphanages, and community shelters struggle with daily food requirements.  
Because there is no simple, direct communication channel, edible food ends up in landfills. **RePlate** solves this by offering a terminal-based coordination network that records surplus food, matches it to NGO requests based on food type, quantity, and location, assigns local volunteers for transport, and tracks the food until it is safely delivered or auto-expired.

---

### Q2: What are the main classes in the system and their roles?
**Answer:**  
The system is divided across 4 modular student areas (with 34 application classes/types):
1. **Module 1 (User Management):**
   - `User` (abstract base class), `Donor`, `NGO`, `Volunteer`, `Admin`, and `UserManager`.
2. **Module 2 (Food & Donation Management):**
   - `FoodItem`, `FoodDonation`, `FoodInventory`, `DonationManager`, and `FoodSearch`.
3. **Module 3 (Request, Matching & Logistics):**
   - `DonationRequest`, `RequestManager`, `DonationMatcher`, `VolunteerAssignment`, `AssignmentManager`, and `CityDistanceMatrix`.
4. **Module 4 (Distribution, Concurrency & System):**
   - `Pickup`, `Delivery`, `DistributionManager`, `DonationHistory`, `ImpactReport`, `FileManager`, `MainMenu`, `DonationExpiryChecker`, and custom exceptions (`ValidationException`, `DonationExpiredException`, `InsufficientQuantityException`).

---

### Q3: How does Inheritance work in this project?
**Answer:**  
Inheritance is implemented in the User hierarchy:
```
                User (Abstract Base Class)
               /     \          \         \
          Donor      NGO    Volunteer    Admin
```
- The base class `User` contains common attributes such as `userId`, `name`, `email`, `phone`, `address`, `username`, `password`, and `role`.
- Subclasses use the `super(...)` constructor to initialize these shared fields while introducing their own specialized fields:
  - `Donor` adds `organizationType` (e.g. "College Mess").
  - `NGO` adds `registrationNumber` and `peopleServedCount`.
  - `Volunteer` adds `vehicleType` and `isAvailable`.
  - `Admin` adds `adminCode`.
- This eliminates code duplication and enables polymorphic behavior.

---

### Q4: Where is Encapsulation used?
**Answer:**  
Encapsulation is applied across all model classes:
- All instance variables (fields) are declared `private` (e.g., `private int quantity`, `private String name`).
- Access is strictly controlled through `public` getter and setter methods.
- Methods include validation rules (e.g., ensuring quantity cannot be negative or status transitions follow the defined lifecycle). This protects internal object state from unauthorized or accidental modification.

---

### Q5: Where is Polymorphism used?
**Answer:**  
Polymorphism is demonstrated in two ways:
1. **Method Overriding (Dynamic / Runtime Polymorphism):**
   - In `User`, the abstract method `public abstract void displayRole();` is declared.
   - Each subclass overrides it (`Donor` displays organization type, `Volunteer` displays vehicle info, etc.).
   - When a user logs in, the reference is held as a `User user`:
     ```java
     User user = userManager.login(username, password);
     user.displayRole(); // Dynamically calls the subclass implementation!
     ```
2. **Interface Polymorphism:**
   - Classes `User`, `FoodDonation`, `DonationRequest`, and `VolunteerAssignment` implement the `Displayable` interface.
   - Any collection of `Displayable` objects can be processed polymorphically using `obj.displayDetails()`.

---

### Q6: Where is Abstraction used?
**Answer:**  
- `User` is an `abstract class` because creating a bare, generic user makes no sense in this domain—every user must have a specific role (`Donor`, `NGO`, `Volunteer`, or `Admin`).
- `User` declares the abstract method `displayRole()`, forcing each concrete subclass to define how its role is presented.
- Data persistence details in `FileManager` abstract away file reading/writing from the main UI classes.

---

### Q7: Where and why is Multithreading used?
**Answer:**  
- **Purpose:** In real life, surplus food has a finite shelf-life. If uncollected, it must be marked as `EXPIRED` automatically without requiring manual donor intervention or freezing the main menu.
- **Implementation:** Class `service.DonationExpiryChecker` extends `java.lang.Thread`.
- **How it works:**
  1. Instantiated in `MainMenu` with a check interval (e.g., 10 seconds).
  2. Marked as a background daemon thread (`setDaemon(true)`).
  3. Started with `.start()`, which triggers its `run()` method in a separate execution stack.
  4. Periodically calls `donationManager.checkAndUpdateExpiredDonations()`.
  5. Transitions between `RUNNABLE` and `TIMED_WAITING` via `Thread.sleep(intervalMillis)`.
  6. When the user exits the application (Choice 6), `stopChecker()` calls `interrupt()` to awaken the thread from `sleep()`, and `join(2000)` to ensure it terminates cleanly before process exit.

---

### Q8: What is the Thread Lifecycle and how does RePlate demonstrate it?
**Answer:**  
1. **NEW:** When `new DonationExpiryChecker(...)` is called.
2. **RUNNABLE:** When `.start()` is called and the OS thread scheduler allocates CPU time for `run()`.
3. **TIMED_WAITING:** When the thread invokes `Thread.sleep(10000)`.
4. **BLOCKED:** When waiting to acquire the monitor lock on a `synchronized` method in `FoodInventory` or `DonationManager`.
5. **TERMINATED:** When `run()` exits after detecting `isInterrupted() == true` or `running == false`.

---

### Q9: Where is Synchronization used and why is it necessary?
**Answer:**  
- **Shared Resource:** The in-memory collection of food donations (`FoodInventory.donationList` and `donationMap`).
- **The Problem (Race Condition):** The main thread (donor adding/updating food, NGO allocating surplus food) and the background thread (`DonationExpiryChecker` scanning and modifying status) access the same collection at the same time. Without synchronization, concurrent modifications could lead to dirty reads, inconsistent quantities, or `ConcurrentModificationException`.
- **The Solution:**
  - Methods in `FoodInventory` (`addDonation`, `getDonationById`, `getAllDonations`, `getAvailableDonations`, `expireOutdatedDonations`) are marked `synchronized`.
  - Mutating methods in `DonationManager` (`checkAndUpdateExpiredDonations`, `updateDonationQuantity`, `updateDonationStatus`, `cancelDonation`) are marked `synchronized`.
  - Synchronizing on the `this` monitor ensures that only one thread can modify or iterate through donations at any given time, preventing race conditions.

---

### Q10: Where are 2D Arrays used in the project?
**Answer:**  
- In class `util.CityDistanceMatrix`.
- We maintain a `4 x 4` 2D integer array (`int[][] DISTANCE_MATRIX`) mapping pairwise distances between regional cities: Chandigarh, Mohali, Ludhiana, and Barnala:
  ```java
  private static final int[][] DISTANCE_MATRIX = {
      {   0,  10, 105, 150 }, // Chandigarh
      {  10,   0,  95, 140 }, // Mohali
      { 105,  95,   0,  65 }, // Ludhiana
      { 150, 140,  65,   0 }  // Barnala
  };
  ```
- **Usage:**
  - `CityDistanceMatrix.getDistance(cityA, cityB)` looks up cell `[i][j]` in $O(1)$ time.
  - `DonationMatcher` uses `CityDistanceMatrix.isWithinRange(loc1, loc2, 25)` to evaluate whether an NGO is within practical transfer range (e.g. Chandigarh & Mohali = 10 km).
  - Admin Menu Option 7 prints the 2D grid in clean tabular format.

---

### Q11: Where are Collections used and why were they chosen?
**Answer:**  
1. **`ArrayList`:** Used for sequential records (`ArrayList<User>`, `ArrayList<FoodDonation>`, `ArrayList<DonationRequest>`). Provides dynamic resizing and fast indexed access.
2. **`HashMap`:** Used for key-value lookups (`HashMap<Integer, User>`, `HashMap<String, User>`). Provides **$O(1)$ constant time** lookup for instant login authentication and ID searches.
3. **`PriorityQueue`:** Used in `RequestManager` to automatically serve urgent NGO food demands first (`URGENT` > `HIGH` > `MEDIUM` > `LOW`) using natural ordering defined by `Comparable<DonationRequest>`.
4. **`Iterator`:** Used in `FoodInventory.expireOutdatedDonations()` to safely iterate over donations during background checks without index shift errors.
5. **`ListIterator`:** Used in `FoodSearch.filterByLocationWithIterator()` to demonstrate bidirectional sequential collection traversal.
6. **`Comparator`:** Used in `FoodSearch.sortByQuantity()` and `FoodSearch.sortByExpiry()` to sort collections without modifying model classes.

---

### Q12: What custom exceptions exist and where are `throw` and `throws` used?
**Answer:**  
We have three custom checked exceptions in package `util`:
1. `ValidationException`: Thrown when input validation fails (e.g. negative quantity, invalid email, invalid 10-digit phone number, invalid NGO registration format).
2. `DonationExpiredException`: Thrown by `DonationMatcher.allocateQuantity()` if an NGO tries to claim a donation that has expired.
3. `InsufficientQuantityException`: Thrown if an NGO requests more quantity than is available.
- **Syntax Demonstrated:**
  - `throw new DonationExpiredException(...)` explicitly creates and throws the exception object.
  - `public int allocateQuantity(...) throws DonationExpiredException, InsufficientQuantityException` declares the exceptions in the method signature.
  - Handled in `MainMenu` with a multi-catch block: `catch (DonationExpiredException | InsufficientQuantityException e)`.

---

### Q13: Where is `finally` used and what is the difference between `final`, `finally`, and `finalize`?
**Answer:**  
- **Where used:** In `FileManager.appendHistory(...)`, an explicit `finally` block guarantees that the `BufferedWriter` and `FileWriter` streams are closed even if an `IOException` occurs during writing.
- **Viva Difference:**
  - `final`: Keyword to declare constants (variables), prevent method overriding (methods), or prevent class inheritance (classes).
  - `finally`: Block that always executes after `try-catch`, typically for resource cleanup (closing files, sockets).
  - `finalize`: Deprecated method in `Object` called by Garbage Collector before reclaiming memory.

---

### Q14: Why is `StringBuilder` used instead of `String` concatenation?
**Answer:**  
- `String` in Java is immutable. Every `+` concatenation creates a new String object in the String pool and discards the old one, wasting memory.
- `StringBuilder` is mutable. It appends text to an internal resizable character buffer without creating intermediate objects.
- In RePlate, `StringBuilder` is used in:
  1. `DistributionManager.displayReceipt()` to assemble multi-line receipts.
  2. `ImpactReport.displayImpactReport()` to build the community statistics dashboard.
  3. `FoodDonation.toCsv()` for fast CSV serialization.

---

### Q15: How does input validation work in the project?
**Answer:**  
Handled centrally in `util.InputValidator`:
- **Email:** Uses regex `^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$`.
- **Phone:** Uses regex `^[6-9]\\d{9}$` (valid 10-digit Indian mobile number).
- **NGO Reg No:** Uses regex `^NGO-[A-Z]{2,4}-[0-9]{3,6}$` (e.g., `NGO-CH-105`).
- **Numbers/Choices:** Catches `NumberFormatException`, displays clear feedback, and loops until valid input is given. The terminal never crashes on bad input.

---

### Q16: How does data persistence work?
**Answer:**  
- `FileManager` stores plain text CSV files under the `data/` directory:
  - `users.txt`
  - `donations.txt`
  - `requests.txt`
  - `assignments.txt`
  - `history.txt`
- On startup, `BufferedReader` reads each line, parses CSV tokens, reconstructs the Java objects, and populates the in-memory collections.
- Whenever a donation is added, request matched, or delivery completed, the updated collections are persisted using `BufferedWriter`.
- All data survives application restarts.

---

### Q17: How does the complete Donor → NGO → Volunteer workflow work?
**Answer:**  
1. **Donor adds food:** Details (category, quantity, prep/expiry time, location) saved as `AVAILABLE`.
2. **NGO requests food:** NGO specifies category, quantity, location, and priority.
3. **DonationMatcher:** Checks category, available quantity, expiry status, and 2D matrix transfer distance. Allocates quantity and updates status.
4. **Volunteer Assignment:** System finds an available volunteer, creates `VolunteerAssignment`, and sets volunteer availability to busy.
5. **Pickup:** Volunteer marks pickup completed (`PICKED_UP`).
6. **Delivery & Receipt:** Volunteer marks delivery completed (`DELIVERED`). Volunteer is released back to available, receipt is generated via `StringBuilder`, and transaction is logged in `history.txt`.

---

### Q18: What are future improvements for this project?
**Answer:**  
1. **SMS/Push Alerts:** Integrate JavaMail API or SMS gateway to ping volunteers on assignment.
2. **Database Integration:** Migrate from flat text files to relational JDBC (MySQL/PostgreSQL) using connection pooling.
3. **GUI Front-End:** Build a JavaFX desktop dashboard while reusing the existing clean service and model classes.
