package models

object EligibilityRules:

  def incomeBelow(household: Household, limit: Double): Boolean =
    household.calculateTotalIncome() <= limit

  def hasDependents(household: Household, minimum: Int): Boolean =
    household.numberOfDependents() >= minimum

  def isRenting(household: Household): Boolean =
    household.housingStatus.equalsIgnoreCase("Renting")

  def hasEligibleStudent(household: Household): Boolean =
    household.members.exists(m =>
      m.employmentStatus.equalsIgnoreCase("Student") &&
        m.age >= 6 &&
        m.age <= 24
    )

  def hasSeniorMember(household: Household): Boolean =
    household.members.exists(_.age >= 60)

  def hasMinorChild(household: Household): Boolean =
    household.members.exists(m =>
      m.relationship.equalsIgnoreCase("Child") &&
        m.age < 18
    )