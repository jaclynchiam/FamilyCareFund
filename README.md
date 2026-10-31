<!--# FamilyCareFund (Subsidy Eligibility Calculator)

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

Each subsidy is a `case object` extending the `Subsidy` sealed trait, and implements its own
`isEligible` rule (see `models/Subsidy.scala`), using shared comparison logic from
`models/EligibilityRules.scala`. This is the project's main demonstration of inheritance and subtype
polymorphism.

After checking eligibility, the household can view details for any subsidy they qualify for
(why they qualify, required documents, how to apply), generate and print an application checklist,
or request NGO assistance if they need help applying.

Admin (a pre-seeded account, not self-registered) can view all registered households and manage FAQs.

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

## Project Structure

```
src/main/scala/
├── Main.scala                    # ScalaFX entry point (JFXApp3) and every screen
├── models/
│   ├── User.scala                # abstract User (login, logout, email validation)
│   ├── HouseholdUser.scala       # extends User - self-registers
│   ├── Admin.scala               # extends User - pre-seeded, manages subsidies/FAQs/users
│   ├── Household.scala           # income, housing, members, single-parent flag, etc.
│   ├── HouseholdMember.scala     # one household member's details
│   ├── HouseholdExtras.scala     # vehicle/savings/assistance info, linked to HouseholdUser
│   ├── Subsidy.scala             # sealed trait + 6 concrete subsidies
│   ├── EligibilityRules.scala    # shared eligibility check helpers
│   ├── EligibilityChecker.scala  # runs a household against a list of subsidies
│   ├── EligibilityResult.scala   # generic result wrapper (item, eligible, message)
│   ├── ApplicationChecklist.scala
│   ├── FAQ.scala
│   └── Validatable.scala         # trait for self-validating models
├── services/
│   └── AuthService.scala         # login (Admin + HouseholdUser) and registration
└── storage/
    └── FileStorage.scala         # JSON persistence for users, households, extras, FAQs
src/main/resources/
└── data/                         # JSON data files (created automatically on first run)
    ├── users.json
    ├── households.json
    ├── extras.json
    └── faqs.json
```

## Key Features
1. **Register / Login** - household self-registration; Admin uses a fixed pre-seeded account
2. **Household Information** - collect household member and household-wide details, with add/edit/remove for members
3. **Eligibility Checker** - checks the household against all 6 subsidies and shows eligible vs not eligible
4. **Subsidy Information** - browse all 6 subsidies (even ones the household doesn't qualify for), view details
5. **Generate Checklist** - auto-generated from the required documents of an eligible subsidy
6. **Print Checklist** - prints the required document checklist, with the results shown in the CLI.
7. **NGO Assistance** - request help applying; recorded against the household for Admin to follow up
8. **Admin Panel** - view registered households, manage FAQs
-->