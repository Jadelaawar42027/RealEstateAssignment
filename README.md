# Real Estate Application (OOP in Java)

Computer Programming 2 – group assignment

- Student 1 – Name: ____ / Student ID: ____
- Student 2 – Name: ____ / Student ID: ____

## Compile and run

Run every command from the `RealEstateApp` folder.

**Windows (Command Prompt)**

```
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
java -cp out edu.uni.realestate.app.Main
java -cp out edu.uni.realestate.app.AgentDemo
```

**macOS / Linux (Terminal)**

```
javac -d out $(find src -name "*.java")
java -cp out edu.uni.realestate.app.Main
java -cp out edu.uni.realestate.app.AgentDemo
```

The code compiles with zero errors and zero warnings, also with `-Xlint:all` and with `--release 17`.

`Main` is the application's only entry point. `AgentDemo` also has a `main` method because the assignment asks for a runnable test class with no keyboard input; it is a test driver, not part of the application.

## Design overview

```
src/edu/uni/realestate/
  model/    Property, House, Apartment, HouseRental, HousePurchase,
            ApartmentRental, ApartmentPurchase, RentalTerms, PurchaseTerms,
            GarageType, LaundryType, InputHelper (package-private)
  service/  Agent
  app/      Main, AgentDemo
```

Dependencies point one way: `app → service → model`. No model class imports from `service` or `app`.

- **Inheritance carries the property type.** `Property` (abstract) holds square feet, bedrooms, bathrooms, the id and availability. `House` and `Apartment` (both abstract) add their own fields. The four concrete classes extend `House` or `Apartment`.
- **Composition carries the deal type.** `HouseRental` and `ApartmentRental` each own a `private final RentalTerms terms`; `HousePurchase` and `ApartmentPurchase` each own a `PurchaseTerms`. Rental and purchase fields are written once.
- **Encapsulation.** Every field is private and has a public getter. Every setter is private, validates its value, throws `IllegalArgumentException`, and is called only from the constructor. There are no public setters and no `setAvailable`. The only state change after creation is `close()`, which is one-way and throws `IllegalStateException` if the property is already closed.
- **Chained display.** Each `display()` calls `super.display()` first and then prints only its own lines; concrete classes finish with `terms.display()`. Money is printed with `%.2f`.
- **Static `promptInit`.** Each class's `promptInit(Scanner)` calls its parent's (and its terms class's) `promptInit`, adds its own keys, and returns a `Map<String, String>`. It never creates the object. It has no `@Override` because static methods are hidden, not overridden. All console reading goes through the package-private `InputHelper` (`readInt`, `readDouble`, `readYesNo`, `readChoice`), which re-prompts until the input is valid and never crashes on non-numeric input.
- **Agent knows no attributes.** `Agent` only calls `promptInit`, constructors, `display()`, `close()`, `isAvailable()` and `getId()`. `getProperties()` returns `Collections.unmodifiableList(properties)`.

### How Agent asks "house or apartment" without InputHelper

`InputHelper` is package-private, so `Agent` (in `service`) cannot use it. Making it public would break the access rule in the assignment, and adding a public input method to the model would put more UI code in the model. So `Agent.addProperty(Scanner)` uses its own small private method, `askChoice`, which re-prompts with the `Scanner` until the answer is valid. Its options are not hard-coded: they come from the registry keys (`house`/`apartment`, `rental`/`purchase`), so a new type appears in the prompt automatically.

### Bonus: registry instead of a switch

`Agent` keeps two parallel registries, both keyed by `"<type> <deal>"`:

```java
Map<String, Function<Scanner, Map<String, String>>> PROMPTS       // "house rental" -> HouseRental::promptInit
Map<String, Function<Map<String, String>, Property>> CONSTRUCTORS // "house rental" -> HouseRental::new
```

A static `register(key, prompt, constructor)` method fills both, so they cannot get out of step. `addProperty(Scanner)` builds the key from the two answers, looks up the prompt method, runs it, passes the map to the constructor, and adds the result. There is no `switch` or `if` chain on type. Adding a new type (for example a `CondoRental`) only needs one line:

```java
register("condo rental", CondoRental::promptInit, CondoRental::new);
```

### Note on ids

The provided `Property` assigns the id last, so a failed `Property` check (for example `baths=0`) does not use up an id. Subclasses call `super(data)` first, though, so if `House`, `Apartment` or a terms class rejects a value, the `Property` part has already taken an id and that number is skipped. We kept `Property` exactly as provided. A fix would be to validate the whole map before calling the constructor.

## AgentDemo output

Output of `java -cp out edu.uni.realestate.app.AgentDemo`:

```

===== 1. One object of each concrete class, built from a Map =====
PROPERTY #1 [AVAILABLE]
  Square feet: 1400
  Bedrooms:    3
  Bathrooms:   2
  Stories:     2
  Garage:      ATTACHED
  Fenced yard: yes
  Monthly rent: 1850.00 EUR
  Furnished:    no
  Utilities:    about 120 EUR/month

PROPERTY #2 [AVAILABLE]
  Square feet: 1400
  Bedrooms:    3
  Bathrooms:   2
  Stories:     2
  Garage:      DETACHED
  Fenced yard: no
  Price:        250000.00 EUR
  Annual taxes: 3000.00 EUR

PROPERTY #3 [AVAILABLE]
  Square feet: 750
  Bedrooms:    0
  Bathrooms:   1
  Balcony:     yes
  Laundry:     COIN
  Monthly rent: 950.00 EUR
  Furnished:    yes
  Utilities:    included

PROPERTY #4 [AVAILABLE]
  Square feet: 750
  Bedrooms:    2
  Bathrooms:   1
  Balcony:     no
  Laundry:     OFFSITE
  Price:        180000.00 EUR
  Annual taxes: 1500.00 EUR

countAvailable() = 4

===== (a) Invalid values are rejected =====
Rejected sqft=0 (HouseRental): Square feet must be at least 1
Rejected beds=-1 (ApartmentRental): Bedrooms cannot be negative
Rejected baths=0 (HouseRental): Bathrooms must be at least 1
Rejected stories=0 (HousePurchase): Stories must be at least 1
Rejected garage="garage" (HousePurchase): Garage must be attached, detached or none (got "garage")
Rejected laundry missing (ApartmentPurchase): Laundry type is required
Rejected rent=-5 (ApartmentRental): Monthly rent must be greater than 0
Rejected utilities="   " (HouseRental): Utilities description cannot be blank
Rejected price=0 (ApartmentPurchase): Price must be greater than 0
Rejected taxes=-100 (HousePurchase): Annual taxes cannot be negative
Rejected sqft="abc" (HouseRental): For input string: "abc"
Properties held by the agent: 4

===== (b) Closing a property twice is rejected =====
closeDeal(1)  first time  -> true
closeDeal(1)  second time -> false
closeDeal(4)              -> true
closeDeal(99) unknown id  -> false
Calling close() directly again throws IllegalStateException: Property #1 is already RENTED

===== (c) Closed properties leave the available list but stay in the full list =====
countAvailable() = 2  (was 4)

--- displayProperties(false): available only ---
PROPERTY #2 [AVAILABLE]
  Square feet: 1400
  Bedrooms:    3
  Bathrooms:   2
  Stories:     2
  Garage:      DETACHED
  Fenced yard: no
  Price:        250000.00 EUR
  Annual taxes: 3000.00 EUR

PROPERTY #3 [AVAILABLE]
  Square feet: 750
  Bedrooms:    0
  Bathrooms:   1
  Balcony:     yes
  Laundry:     COIN
  Monthly rent: 950.00 EUR
  Furnished:    yes
  Utilities:    included

--- displayProperties(true): all ---
PROPERTY #1 [RENTED]
  Square feet: 1400
  Bedrooms:    3
  Bathrooms:   2
  Stories:     2
  Garage:      ATTACHED
  Fenced yard: yes
  Monthly rent: 1850.00 EUR
  Furnished:    no
  Utilities:    about 120 EUR/month

PROPERTY #2 [AVAILABLE]
  Square feet: 1400
  Bedrooms:    3
  Bathrooms:   2
  Stories:     2
  Garage:      DETACHED
  Fenced yard: no
  Price:        250000.00 EUR
  Annual taxes: 3000.00 EUR

PROPERTY #3 [AVAILABLE]
  Square feet: 750
  Bedrooms:    0
  Bathrooms:   1
  Balcony:     yes
  Laundry:     COIN
  Monthly rent: 950.00 EUR
  Furnished:    yes
  Utilities:    included

PROPERTY #4 [SOLD]
  Square feet: 750
  Bedrooms:    2
  Bathrooms:   1
  Balcony:     no
  Laundry:     OFFSITE
  Price:        180000.00 EUR
  Annual taxes: 1500.00 EUR

===== (d) getProperties() is read-only =====
getProperties().clear() throws UnsupportedOperationException; size is still 4
```

## Reflection

**1. What would break if `setBedrooms` were public?**
Any code holding a `Property` could change the bedroom count after the listing was created. That breaks the rule that editing details is out of scope. The check in `setBedrooms` (no negative values) would still run, but the "created once, then read-only" guarantee would be gone. For example, `Main` could take an object from `agent.getProperties()` and turn a 3-bedroom `HouseRental` into a studio after a tenant had already agreed to it. Keeping the setter private means the validation runs only inside the constructor, and `close()` is the only change allowed afterwards.

**2. Why `RentalTerms` and not inheritance?**
Java classes have single inheritance. `HouseRental` already extends `House`, so it cannot also extend a `Rental` class, and `ApartmentRental` has the same problem with `Apartment`. Without composition, `monthlyRent`, `furnished` and `utilities`, their three validating setters, their getters, the display lines and the prompts would be copied into both `HouseRental` and `ApartmentRental`. `price` and `annualTaxes` would also be copied into both purchase classes. With `private final RentalTerms terms`, the rules live in one class: changing the rent rule means editing only `RentalTerms.setMonthlyRent`.

**3. Static hiding vs. overriding.**
Overriding is resolved at runtime from the object's actual class. Hiding is resolved at compile time from the class name in the call. `HouseRental.promptInit` and `House.promptInit` are two separate static methods, and `HouseRental.promptInit` simply calls `House.promptInit(in)` by name, which is why `@Override` is a compile error on them. An instance method cannot work because `promptInit` must run *before* the object exists: its map is what the constructor (`HouseRental::new`) needs, so there is no instance yet to call a method on. A static method can also be passed as `HouseRental::promptInit` into the `Agent` registry without any object.

**4. A mistake a normal constructor would catch but the map cannot.**
With a constructor like `HouseRental(int sqft, int beds, ..., double rent, ...)`, the compiler would reject `new HouseRental("big", ...)` (wrong type) or a missing argument. With `Map<String, String>`, a typo such as `data.put("bath", "2")` instead of `"baths"` compiles fine. It only fails at runtime, when `Integer.parseInt(data.get("baths"))` gets `null` and throws `NumberFormatException`. Putting `"two"` instead of `"2"` is also accepted by the compiler and only rejected at runtime by `Integer.parseInt`. In the same way, any extra or misspelled key is silently ignored.

**5. Moving to a GUI or web page.**
Today every model class reads from a `Scanner`: `Property`, `House`, `Apartment`, `RentalTerms`, `PurchaseTerms` and the four concrete classes all have `promptInit(Scanner)`, and `InputHelper` prints prompts to `System.out`. `Agent.addProperty(Scanner)` reads input too. For a GUI, all of these would have to change, which shows the model depends on the user interface. A better design moves all prompting into a new `PropertyForm` class in the `app` package, with `InputHelper` moved beside it. `PropertyForm` would ask the questions and build the `Map<String, String>`. The model would keep only constructors, validation, getters, `close()` and display, and `Agent` would offer just `addProperty(Property)`. A future `GuiPropertyForm` or web controller could then build the same map from text fields, while the model classes and their validation stay unchanged.
