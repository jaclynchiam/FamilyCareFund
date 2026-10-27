package models

object EligibilityChecker:

  def checkEligibility(
                        household: Household,
                        subsidies: List[Subsidy]
                      ): List[Subsidy] =
    subsidies.filter(_.isEligible(household))

  def checkAll(
                household: Household,
                subsidies: List[Subsidy]
              ): List[EligibilityResult[Subsidy]] =
    subsidies.map { subsidy =>
      val eligible = subsidy.isEligible(household)

      EligibilityResult(
        subsidy,
        eligible,
        if eligible then "Eligible" else "Not Eligible"
      )
    }