package models

abstract class User(
                     val userId: String,
                     val fullName: String,
                     val email: String,
                     val username: String,
                     val password: String,
                     val phoneNumber: String
                   ):

  def login(inputUsername: String, inputPassword: String): Boolean =
    inputUsername == username && inputPassword == password

  def logout(): Unit =
    println(s"$username logged out")

  def validateEmail(): Either[String, Unit] =
    if email.trim.isEmpty then
      Left("Email cannot be empty")
    else if !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$") then
      Left("Invalid email format")
    else
      Right(())