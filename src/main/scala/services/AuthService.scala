package services

import models.*
import storage.{FileStorage, UserRecord}

object AuthService:

  private def toHouseholdUser(r: UserRecord): HouseholdUser =
    HouseholdUser(r.userId, r.fullName, r.email, r.username, r.password, r.phoneNumber, r.householdId)

  // Tries Admin first (single hardcoded account), then every registered
  // HouseholdUser - each User subtype's own login() checks its own
  // credentials, so this just finds whichever one matches.
  def login(username: String, password: String): Option[User] =
    val householdUsers: List[User] = FileStorage.loadUsers().map(toHouseholdUser)
    (Admin :: householdUsers).find(_.login(username.trim, password.trim))

  def register(
                fullName: String,
                email: String,
                username: String,
                password: String,
                confirmPassword: String,
                phoneNumber: String
              ): Either[String, HouseholdUser] =

    // Clean normal user information
    val cleanFullName = fullName.trim
    val cleanEmail = email.trim
    val cleanUsername = username.trim
    val cleanPhone = phoneNumber.trim

    // Validate user input
    if cleanFullName.isEmpty then
      Left("Full name cannot be empty")

    else if cleanUsername.isEmpty then
      Left("Username cannot be empty")

    else if cleanUsername == Admin.username then
      Left("This username is reserved")

    else if password.trim.isEmpty then
      Left("Password cannot be empty")

    else if password != confirmPassword then
      Left("Passwords do not match")

    else if FileStorage.usernameExists(cleanUsername) then
      Left("Username already taken")

    else
      val userId = java.util.UUID.randomUUID().toString.take(8)

      val user = HouseholdUser(
        userId = userId,
        fullName = cleanFullName,
        email = cleanEmail,
        username = cleanUsername,
        password = password,
        phoneNumber = cleanPhone,
        householdId = userId
      )

      user.validateEmail() match
        case Left(err) =>
          Left(err)

        case Right(_) =>
          if !user.register() then
            Left("Please fill in all required fields")
          else
            FileStorage.saveUser(user)
            Right(user)