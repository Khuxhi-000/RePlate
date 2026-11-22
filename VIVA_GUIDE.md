# RePlate — Viva & Project Defense Guide
> **Subject:** Programming in Java  
> **Target Audience:** 2nd Year University Students & Evaluation Panel  

---

### Q1: What real-world problem does the project solve?
**Answer:**  
In university messes, restaurants, hotels, and banquets, large quantities of safe, edible surplus food are prepared daily but discarded after peak hours. Meanwhile, local NGOs, orphanages, and community shelters struggle with daily food requirements.  
Because there is no simple, direct communication channel, edible food ends up in landfills. **RePlate** solves this by offering a terminal-based coordination network that records surplus food, matches it to NGO requests based on food type, quantity, and location, assigns local volunteers for transport, and tracks the food until it is safely delivered.

---

### Q2: What are the main classes in the system and their roles?
**Answer:**  
The system is divided across 4 student modules:
1. **Module 1 (User Management):**
   - `User` (abstract base class), `Donor`, `NGO`, `Volunteer`, `Admin`, and `UserManager`.
2. **Module 2 (Food & Donation Management):**
   - `FoodItem`, `FoodDonation`, `FoodInventory`, `DonationManager`, and `FoodSearch`.
3. **Module 3 (Request & Matching):**
   - `DonationRequest`, `RequestManager`, `DonationMatcher`, `VolunteerAssignment`, and `AssignmentManager`.
4. **Module 4 (Distribution & Reports):**
   - `Pickup`, `Delivery`, `DistributionManager`, `DonationHistory`, `ImpactReport`, `FileManager`, and `MainMenu`.

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

### Q7: Where are Collections used in the project?
**Answer:**  
We use three core collections from the Java Collections Framework:
1. `ArrayList`
2. `HashMap`
3. `PriorityQueue`

---

### Q8: Why is `ArrayList` used?
**Answer:**  
- `ArrayList<User>`, `ArrayList<FoodDonation>`, and `ArrayList<DonationRequest>` are used because we need resizable, ordered lists.
- They allow us to easily add new records at runtime, iterate through them sequentially to display lists, and pass them to search/sort algorithms.

---

### Q9: Why is `HashMap` used?
**Answer:**  
- In a system with many users and donations, searching linearly through an `ArrayList` takes $O(n)$ time.
- By using `HashMap<Integer, User>` and `HashMap<String, User>`, we achieve **$O(1)$ constant time lookup** when:
  - Checking credentials during login (`usernameMap.get(username)`).
  - Finding a user or donation by ID (`userMap.get(userId)`, `donationMap.get(id)`).
  - Checking for duplicate usernames during registration.

---

### Q10: Why is `PriorityQueue` used?
**Answer:**  
- An NGO's food request has a priority level: `LOW`, `MEDIUM`, `HIGH`, or `URGENT`.
- In a disaster or emergency food shortage, urgent requests must be served before older low-priority requests.
- `DonationRequest` implements `Comparable<DonationRequest>`, ordering requests by priority descending.
- The `PriorityQueue<DonationRequest>` automatically ensures that `peek()` or `poll()` always returns the most urgent pending food request first, without having to manually sort the entire list repeatedly.

---

### Q11: Where is Exception Handling used?
**Answer:**  
1. **Custom Exception (`ValidationException`):**
   - Created in `util.ValidationException` to signal validation failures such as negative quantities or empty text inputs.
2. **Standard Exceptions Handled defensively:**
   - `NumberFormatException` in `InputValidator`: caught when a user enters alphabets where a menu choice or quantity is required. The program notifies the user and re-prompts instead of crashing.
   - `IOException` in `FileManager`: caught during file reading and writing if a file is missing or locked.
   - Null checks and boundary checks throughout managers to prevent `NullPointerException` and `IndexOutOfBoundsException`.

---

### Q12: Where is File Handling used?
**Answer:**  
- Data persistence is implemented in `util.FileManager` without requiring an external database.
- Uses `BufferedReader` and `FileReader` to load data into memory when the program starts.
- Uses `BufferedWriter` and `FileWriter` to write updated records whenever donations, requests, or assignments change.
- Text/CSV files are stored under `data/`:
  - `users.txt`
  - `donations.txt`
  - `requests.txt`
  - `assignments.txt`
  - `history.txt`
- The application automatically creates the `data/` directory and files if they are not already present.

---

### Q13: Why are Enums used?
**Answer:**  
Instead of using loose Strings or magic numbers (which can cause typos and bugs), we use type-safe Java `enum`s:
- `FoodCategory`: Limits input to `COOKED_FOOD`, `PACKAGED_FOOD`, `FRUITS`, `VEGETABLES`, `BAKERY`, `OTHER`.
- `DonationStatus`: Enforces clear lifecycle states: `AVAILABLE`, `RESERVED`, `PICKED_UP`, `DELIVERED`, `CANCELLED`, `EXPIRED`.
- `RequestPriority`: Associates discrete urgency levels with numerical priority values for PriorityQueue comparison.
- `UserRole`: Controls role-based navigation for `DONOR`, `NGO`, `VOLUNTEER`, `ADMIN`.

---

### Q14: How does `DonationMatcher` work?
**Answer:**  
`DonationMatcher` contains the matching algorithm (`findBestMatch`):
1. Filters donations where status is `AVAILABLE` and quantity $> 0$.
2. Checks that the `FoodCategory` matches the request category.
3. Checks location compatibility (exact match, or compatible adjacent cities like Chandigarh and Mohali).
4. Verifies the donation is not expired.
5. Checks if the available quantity meets the requested quantity (prefers full matches, allows partial matches).
6. Allocates the required quantity, deducts it from the donation, and sets the donation status to `RESERVED` if its quantity reaches 0.

---

### Q15: How does the Donor → NGO → Volunteer workflow work?
**Answer:**  
1. **Donor adds food:** Donor inputs food name, category, quantity, preparation time, expiry time, and location.
2. **NGO requests food:** NGO specifies category, quantity needed, delivery city, and priority.
3. **Matcher matches & allocates:** `DonationMatcher` finds a compatible donation and subtracts the allocated quantity.
4. **Volunteer assignment:** System assigns an available volunteer (`isAvailable = false`) and creates a `VolunteerAssignment`.
5. **Pickup:** Volunteer logs into their dashboard and marks the food as picked up (`status = PICKED_UP`).
6. **Delivery & Receipt:** Volunteer marks delivery completed (`status = DELIVERED`). The volunteer's status is reset to available (`isAvailable = true`), a receipt is generated, and the event is permanently logged in `history.txt`.

---

### Q16: How does data persist after closing the application?
**Answer:**  
- Every time a user registers, donation is added, or delivery completes, the corresponding manager triggers `FileManager.save...()`, which writes CSV records to the `data/` text files.
- When the application is launched again (`new MainMenu()`), each manager reads from the files in `data/` and repopulates the in-memory collections (`ArrayList` and `HashMap`).
- This guarantees zero data loss between application restarts.

---

### Q17: What happens when invalid input is entered?
**Answer:**  
- The terminal never crashes.
- If a user inputs text when a number is expected, `InputValidator.readInt` catches `NumberFormatException`, displays `>> Error: Invalid number format. Please enter an integer.`, and re-prompts the user.
- If a user enters an out-of-range menu number (e.g., choice 9 in a 1-6 menu), it warns them and loops until a valid choice is entered.
- Empty fields and negative quantities are caught by `ValidationException`.

---

### Q18: What could be improved in a future version?
**Answer:**  
1. **Multi-threading:** Add a background worker thread (`ScheduledExecutorService`) that periodically checks expiry timestamps and marks outdated donations as `EXPIRED`.
2. **Automated Notifications:** Implement SMS or email notifications for volunteers when a new delivery is assigned.
3. **Distance Calculation:** Calculate real-time distances between cities or areas using simulated GPS coordinates.
4. **GUI / Web Dashboard:** Build a desktop JavaFX interface or a lightweight web interface while retaining the same clean business logic classes.
