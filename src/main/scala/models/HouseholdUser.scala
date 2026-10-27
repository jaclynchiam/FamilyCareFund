package models

class HouseholdUser(
                     userId: String,
                     fullName: String,
                     email: String,
                     username: String,
                     password: String,
                     phoneNumber: String,
                     val householdId: String
                   ) extends User(
  userId,
  fullName,
  email,
  username,
  password,
  phoneNumber
):

  def register(): Boolean =
    username.trim.nonEmpty &&
      password.trim.nonEmpty &&
      fullName.trim.nonEmpty &&
      validateEmail().isRight