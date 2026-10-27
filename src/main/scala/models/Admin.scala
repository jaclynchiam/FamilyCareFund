package models

object Admin extends User(
  userId = "A001",
  fullName = "Administrator",
  email = "admin@familycarefund.org",
  username = "admin",
  password = "1234",
  phoneNumber = ""
):

  def editFAQ(
               faq: FAQ,
               newQuestion: String,
               newAnswer: String
             ): FAQ =
    faq.copy(
      question = newQuestion,
      answer = newAnswer
    )

  def viewHouseholdUser(
                         users: List[HouseholdUser],
                         households: List[Household]
                       ): List[(HouseholdUser, Household)] =
    users.flatMap { user =>
      households
        .find(_.householdId == user.householdId)
        .map(household => (user, household))
    }