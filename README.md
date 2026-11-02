# FamilyCareFund (Subsidy Eligibility Calculator)

A standalone ScalaFX desktop application supporting UN Sustainable Development Goal 1 (No Poverty).
FamilyCareFund helps low-income households check their eligibility for six government subsidies,
prepare the documents they need, and request help from NGO partners if they need assistance applying.

## Project Summary

Households register an account, then fill in their household information: themselves plus any other
household members (age, employment status, monthly income, whether they are a dependent), housing
situation, vehicle ownership, financial assistance status, savings range, and special circumstances
(disability / care needs).

The system then checks this household against six subsidies:

- Cash Subsidy
- Basic Needs Subsidy
- Housing Subsidy
- Education Subsidy
- Senior Citizen Subsidy
- Single-Parent Family Subsidy


After checking eligibility, the household can view details for any subsidy they qualify for
(why they qualify, required documents, how to apply), generate and print an application checklist,
or request NGO assistance if they need help applying.

Admin (a pre-seeded account, not self-registered) can view all registered households and manage FAQs.


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
## Requirements

- Scala 3.3+
- sbt 1.9+
- Java 17+ (JDK with JavaFX-compatible JVM)

## Setup & Run

1. Unzip this project, keeping the folder structure intact.
2. From the project root, run:
   ```
   sbt run
   ```
3. On first launch, the app seeds an admin account automatically:
   - Username: `admin`
   - Password: `1234`
4. Households register their own account from the Login screen ("New user? Register").

Data is stored as JSON files under `src/main/resources/data/` (`users.json`, `households.json`,
`extras.json`, `faqs.json`), created automatically the first time each is written to.

## Running Tests

```
sbt test
```

## Key Features

1. **Register / Login** - Users can register their own accounts and log in. Admin logs in using the built-in admin account.
2. **Household Information** - Enter personal and family member details, including age, employment status and income, as well as housing situation, vehicle ownership, savings and financial assistance information. Add, edit and remove family members.
3. **Eligibility Checker** - Checks the household against all 6 government subsidies and displays eligible or not eligible results.
4. **Subsidy Information** - Browse all 6 available subsidies, including those the household may not qualify for, and view their details, eligibility criteria and requirements.
5. **Generate Checklist** - Automatically generates an application checklist based on the required documents for an eligible subsidy.
6. **Print Checklist** - Print the required document checklist.
7. **NGO Assistance** - Submit a request for help with subsidy applications, which is recorded for Admin to follow up.
8. **Admin Dashboard** - View registered households, check NGO assistance requests and manage FAQs.


