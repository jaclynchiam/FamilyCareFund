package models

sealed trait Subsidy:
  def name: String
  def description: String
  def benefits: String
  def eligibilityCriteria: String
  def requiredDocuments: List[String]

  def isEligible(household: Household): Boolean


// 1. Cash Subsidy
case object CashSubsidy extends Subsidy:

  val name: String =
    "Cash Subsidy"

  val description: String =
    "Provides direct financial support to households experiencing financial difficulties."

  val benefits: String =
    "One-time RM1,500 - RM6,000 cash payment"

  val eligibilityCriteria: String =
    "Household monthly income <= RM3,000 AND at least 1 dependent"

  val requiredDocuments: List[String] =
    List(
      "Identification document",
      "Proof of household income",
      "Proof of household members/dependents",
      "Asset/financial declaration"
    )

  override def isEligible(household: Household): Boolean =
    EligibilityRules.incomeBelow(household, 3000) &&
      EligibilityRules.hasDependents(household, 1)


// 2. Basic Needs Subsidy
case object BasicNeedsSubsidy extends Subsidy:

  val name: String =
    "Basic Needs Subsidy"

  val description: String =
    "Helps eligible households cover essential daily expenses, such as food and basic necessities."

  val benefits: String =
    "RM500 per month"

  val eligibilityCriteria: String =
    "Household monthly income <= RM2,500 AND at least 2 dependents"

  val requiredDocuments: List[String] =
    List(
      "Identification document",
      "Proof of household income",
      "Proof of household members/dependents"
    )

  override def isEligible(household: Household): Boolean =
    EligibilityRules.incomeBelow(household, 2500) &&
      EligibilityRules.hasDependents(household, 2)


// 3. Housing Subsidy
case object HousingSubsidy extends Subsidy:

  val name: String =
    "Housing Subsidy"

  val description: String =
    "Provides support for eligible households that have rental or housing-related expenses."

  val benefits: String =
    "Up to RM800 per month toward rental costs"

  val eligibilityCriteria: String =
    "Household monthly income <= RM3,500 AND renting AND at least 1 dependent"

  val requiredDocuments: List[String] =
    List(
      "Identification document",
      "Proof of income",
      "Rental agreement",
      "Proof of residence",
      "Household member documents"
    )

  override def isEligible(household: Household): Boolean =
    EligibilityRules.incomeBelow(household, 3500) &&
      EligibilityRules.isRenting(household) &&
      EligibilityRules.hasDependents(household, 1)


// 4. Education Subsidy
case object EducationSubsidy extends Subsidy:

  val name: String =
    "Education Subsidy"

  val description: String =
    "Provides support for school and education-related expenses for children or students."

  val benefits: String =
    "Up to RM1,000 per student per year"

  val eligibilityCriteria: String =
    "Household monthly income <= RM4,000 AND at least 1 student aged 6-24"

  val requiredDocuments: List[String] =
    List(
      "Identification document",
      "Proof of household income",
      "Student identification",
      "School/university enrolment document",
      "Proof of relationship"
    )

  override def isEligible(household: Household): Boolean =
    EligibilityRules.incomeBelow(household, 4000) &&
      EligibilityRules.hasEligibleStudent(household)


// 5. Senior Citizen Subsidy
case object SeniorCitizenSubsidy extends Subsidy:

  val name: String =
    "Senior Citizen Subsidy"

  val description: String =
    "Provides support to households that contain an elderly household member."

  val benefits: String =
    "RM300 monthly allowance"

  val eligibilityCriteria: String =
    "Household monthly income <= RM3,500 AND at least 1 member aged 60 or above"

  val requiredDocuments: List[String] =
    List(
      "Identification document",
      "Proof of income",
      "Senior citizen's identification",
      "Proof of household relationship"
    )

  override def isEligible(household: Household): Boolean =
    EligibilityRules.incomeBelow(household, 3500) &&
      EligibilityRules.hasSeniorMember(household)


// 6. Single-Parent Family Subsidy
case object SingleParentSubsidy extends Subsidy:

  val name: String =
    "Single-Parent Family Subsidy"

  val description: String =
    "Provides additional support for households headed by a single parent."

  val benefits: String =
    "RM500 per month"

  val eligibilityCriteria: String =
    "Household income <= RM4,000 AND single-parent household AND at least 1 child below 18"

  val requiredDocuments: List[String] =
    List(
      "Identification document",
      "Proof of income",
      "Child's identification",
      "Proof of parent-child relationship",
      "Supporting document for single-parent status"
    )

  override def isEligible(household: Household): Boolean =
    EligibilityRules.incomeBelow(household, 4000) &&
      household.isSingleParent &&
      EligibilityRules.hasMinorChild(household)


object Subsidy:
  val all: List[Subsidy] =
    List(CashSubsidy, BasicNeedsSubsidy, HousingSubsidy, EducationSubsidy, SeniorCitizenSubsidy, SingleParentSubsidy)