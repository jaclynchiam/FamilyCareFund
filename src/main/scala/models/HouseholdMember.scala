package models

import upickle.default.*

case class HouseholdMember(
                            name: String,
                            age: Int,
                            relationship: String,
                            employmentStatus: String,
                            monthlyIncome: Double,
                            dependent: Boolean
                          ) extends Validatable:

  // Helper method matching UML diagram
  def isDependent(): Boolean = dependent

  override def validate(): Either[String, Unit] =
    if name == null || name.trim.isEmpty then
      Left("Member name cannot be empty.")
    else if age < 0 then
      Left(s"Invalid age for $name: cannot be negative.")
    else if monthlyIncome < 0 then
      Left(s"Invalid income for $name: cannot be negative.")
    else
      Right(())

object HouseholdMember:
  given ReadWriter[HouseholdMember] = macroRW