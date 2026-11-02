# FamilyCareFund (Subsidy Eligibility Calculator)

<!--A standalone ScalaFX desktop application supporting UN Sustainable Development Goal 1 (No Poverty).
FamilyCareFund helps low-income families check their eligibility for six government subsidies, view subsidy requirements, generate application checklists based on required documents, and request NGO assistance if they need help with the application process.-->

FamilyCareFund is a desktop application developed to support UN Sustainable Development Goal 1 (No Poverty). It helps low-income families check their eligibility for six government subsidies, view subsidy requirements, generate application checklists, and request assistance from NGOs if they need help with the application process.

---

## Table of Contents

- [Background](#background)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [How to Use](#how-to-use)
- [Data Storage](#data-storage)
- [Eligibility Logic](#eligibility-logic)
- [Screenshots](#screenshots)
- [Limitations and Future Work](#limitations-and-future-work)
- [Author](#author)

---

## Background

Many low-income households are entitled to government subsidies but never apply, either because they do not know the subsidies exist, are unsure whether they qualify, or find the application process confusing. FamilyCareFund addresses this by letting a household enter its details once, see which subsidies it is eligible for, understand exactly what to prepare, and ask an NGO for help if needed.

---

## Features

### 1. Household Information Management
- Enter your own details (age, employment, income, etc.)
- Add household members
- Edit or remove members
- Enter housing and financial information
- Save the household information

### 2. Subsidy Eligibility Checking
- Click **Check My Eligibility**
- Review the household information
- Click **Confirm & Check Eligibility**
- The system checks the household against all 6 subsidies
- Each subsidy is shown as **Eligible** or **Not Eligible**

### 3. Subsidy Information and Application Checklist
- Browse the 6 available subsidies
- Click a subsidy to view its:
  - Description
  - Benefits
  - Eligibility criteria
  - Required documents
  - How to apply
- For a subsidy the household is eligible for, generate an application checklist

### 4. NGO Assistance and Admin Management
- **User side:** submit an NGO assistance request. The NGO then contacts the household and helps with the application.
- **Admin side:** log in to
  - view registered households
  - see each household's NGO assistance status (Requested / Not requested)
  - manage FAQs

---

## Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Scala 3.3.4 |
| UI | ScalaFX 21.0.0-R32 (on JavaFX 21.0.2) |
| JSON | uPickle 3.3.1 |
| Build tool | sbt |
| Storage | Local JSON files |

---

## Project Structure

```
FamilyCareFund/
├── build.sbt
├── project/
│   └── build.properties
├── docs/
└── src/
    └── main/
        ├── resources/
        │   └── data/
        │       ├── users.json
        │       ├── households.json
        │       ├── extras.json
        │       └── faqs.json
        └── scala/
            ├── Main.scala
            ├── models/
            │   ├── Admin.scala
            │   ├── ApplicationChecklist.scala
            │   ├── EligibilityChecker.scala
            │   ├── EligibilityResult.scala
            │   ├── EligibilityRules.scala
            │   ├── FAQ.scala
            │   ├── Household.scala
            │   ├── HouseholdExtras.scala
            │   ├── HouseholdMember.scala
            │   ├── HouseholdUser.scala
            │   ├── Subsidy.scala
            │   ├── User.scala
            │   └── Validatable.scala
            ├── services/
            │   └── AuthService.scala
            └── storage/
                └── FileStorage.scala
```

### Code organisation

| Package / file | Responsibility |
|----------------|----------------|
| `Main.scala` | Application entry point and ScalaFX user interface |
| `models/` | Domain classes: users, admins, households, members, subsidies, FAQs, and the eligibility logic |
| `models/EligibilityChecker.scala`, `EligibilityRules.scala`, `EligibilityResult.scala` | Check a household against the subsidy rules and return the result |
| `models/ApplicationChecklist.scala` | Builds the application checklist for an eligible subsidy |
| `models/Validatable.scala` | Shared validation behaviour for input data |
| `services/AuthService.scala` | Login and authentication for households and admins |
| `storage/FileStorage.scala` | Reads and writes the JSON data files |
| `resources/data/` | Persistent data: users, households, extra household information, and FAQs |

---

## Getting Started

### Prerequisites
- **JDK 17 or later** (required by JavaFX 21)
- **sbt** ([installation guide](https://www.scala-sbt.org/download.html))

### Run the application

```bash
git clone https://github.com/jaclynchiam/FamilyCareFund.git
cd FamilyCareFund
sbt run
```

The first run downloads the dependencies, so it may take a few minutes. The build picks the correct JavaFX files for Windows, macOS or Linux automatically.

---

## How to Use

1. **Register or log in** to your household account.
2. **Fill in your household information:** your own details, then add each household member, and enter housing and financial information. Save when done.
3. **Check eligibility:** click *Check My Eligibility*, review your information, then click *Confirm & Check Eligibility*.
4. **Read the results:** each of the 6 subsidies is marked Eligible or Not Eligible.
5. **View subsidy details** under *Subsidies*, and generate an application checklist for any subsidy you are eligible for.
6. **Request NGO assistance** if you need help with an application.

### Admin
Log in with an admin account to view registered households, see who has requested NGO assistance, and manage FAQs.

> **Admin login:** `[TODO: add default admin credentials, or explain how an admin account is created]`

---

## Data Storage

FamilyCareFund stores its data in JSON files under `src/main/resources/data/`, handled by `FileStorage`:

| File | Contents |
|------|----------|
| `users.json` | Registered user accounts |
| `households.json` | Household details and members |
| `extras.json` | Additional household information (e.g. housing and financial details) |
| `faqs.json` | FAQs shown to users and managed by admins |

`[TODO: confirm what each file stores, and whether passwords are hashed.]`

---

## Eligibility Logic

Eligibility is decided by `EligibilityChecker`, which applies the criteria defined in `EligibilityRules` to the household's saved information and returns an `EligibilityResult` for each subsidy.

| # | Subsidy | Key eligibility criteria |
|---|---------|--------------------------|
| 1 | `[TODO]` | `[TODO]` |
| 2 | `[TODO]` | `[TODO]` |
| 3 | `[TODO]` | `[TODO]` |
| 4 | `[TODO]` | `[TODO]` |
| 5 | `[TODO]` | `[TODO]` |
| 6 | `[TODO]` | `[TODO]` |

---

## Screenshots

`[TODO: add screenshots of the household form, eligibility results, subsidy details, and admin dashboard]`

---


---

<!--## Author

**Jaclyn**, Computer Science, Sunway University
GitHub: [@jaclynchiam](https://github.com/jaclynchiam)-->



