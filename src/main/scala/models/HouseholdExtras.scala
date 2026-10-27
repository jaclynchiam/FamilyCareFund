package models

import upickle.default.*

// Linked to HouseholdUser (via username), NOT Household - none of these
// fields are read by EligibilityRules. Kept separate so Household only
// ever contains what eligibility logic actually needs.
case class HouseholdExtras(
                            username: String,
                            vehicleOwnership: String,
                            receivesFinancialAssistance: Boolean,
                            assistanceType: Option[String],
                            savingsRange: String,
                            hasDisability: Boolean,
                            needsCare: Boolean
                          )

object HouseholdExtras:
  given ReadWriter[HouseholdExtras] = macroRW