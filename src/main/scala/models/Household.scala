package models

import upickle.default.*

case class Household(
                      householdId: String,
                      housingStatus: String,
                      members: List[HouseholdMember],
                      isSingleParent: Boolean,
                      needsNgoAssistance: Boolean,
                      additionalPropertyCount: Int
                    ) extends Validatable:

  def addMember(member: HouseholdMember): Household =
    copy(members = members :+ member)

  def removeMember(member: HouseholdMember): Household =
    copy(members = members.filterNot(_ == member))

  def calculateTotalIncome(): Double =
    members.map(_.monthlyIncome).sum

  def numberOfDependents(): Int =
    members.count(_.isDependent())

  override def validate(): Either[String, Unit] =
    if members.isEmpty then
      Left("Add at least one household member")
    else if additionalPropertyCount < 0 then
      Left("Additional property count cannot be negative")
    else if calculateTotalIncome() < 0 then
      Left("Total monthly income cannot be negative")
    else
      Right(())

object Household:
  given ReadWriter[Household] = macroRW